# Group-Project
**Träningsapp där varje set kan progressa individuellt, fullständig historik sparas, och användare kan bygga avancerade träningsprogram med rutiner och automatiska påminnelser.**

---

**Databasstruktur**
## Översikt
**Denna databas hanterar ett komplett träningssystem där användare kan:**

Skapa och följa träningsrutiner
Logga träningspass med detaljerad information per set
Spåra progression över tid (vikter, reps, PRs)
Skapa egna övningar och markera favoriter
Få påminnelser för schemalagda träningspass


🗂️ Tabeller
## **users**
**Användarkonton i systemet.**
```
- id (PK)
- email
- password_hash
- name
- created_at
- push_notifications_enabled (boolean)
- notification_method (enum: 'push', 'email', 'both')
```

**Syfte:** Grundläggande användarinformation och notiseringsinställningar.

---

### **exercises**
Alla övningar i systemet - både globala (fördefinierade) och användar-skapade custom övningar.
```
- id (PK)
- name (t.ex. "Bänkpress", "Squats")
- description (nullable)
- category (t.ex. "Chest", "Legs", "Back")
- created_at
```

**Syfte:** Gemensam databas för alla övningar. Custom övningar läggs också här men kopplas till användare via `user_exercise_preferences`.

---

### **user_exercise_preferences**
Kopplar användare till övningar och sparar personliga inställningar.
```
- id (PK)
- user_id (FK → users)
- exercise_id (FK → exercises)
- is_favourite (boolean) - markerad som favorit?
- is_custom (boolean) - skapade användaren denna övning?
- default_weight (nullable) - användarens standardvikt för övningen
- default_rest_time (nullable) - användarens standardvila
- created_at
```

**Syfte:** 
- Låter användare markera favorit-övningar
- Identifierar custom övningar (som bara syns för den användaren)
- Sparar användarens personliga standardvärden per övning

**Viktigt:** Om `is_custom = true` betyder det att bara denna användare ska se övningen. Globala övningar har ingen rad här (förutom om användaren markerat som favorit).

---

### **routines**
Träningsrutiner som innehåller flera workouts (t.ex. "Push/Pull/Legs").
```
- id (PK)
- user_id (FK → users)
- name (t.ex. "PPL 6 dagar/vecka", "Upper/Lower")
- description (nullable)
- created_at
```

**Syfte:** Organisera flera träningspass i en strukturerad rutin.

---

### **workouts**
Enskilda träningspass (t.ex. "Push A", "Pull", "Legs").
```
- id (PK)
- user_id (FK → users)
- name (t.ex. "Push A", "Full Body Monday")
- created_at
```

**Syfte:** Ett träningspass innehåller flera övningar. Kan ingå i flera routines.

---

### **routine_workouts** *(junction table)*
Kopplar routines till workouts och definierar ordning/schema.
```
- id (PK)
- routine_id (FK → routines)
- workout_id (FK → workouts)
- day_order (int) - vilken dag i cykeln (1, 2, 3...)
- week_day (nullable) - t.ex. "Monday", "Friday"
- reminder_time (time, nullable) - t.ex. "18:00"
- reminder_enabled (boolean, default true)
```

**Syfte:** 
- Definierar vilka workouts som ingår i en rutin
- Bestämmer ordning och schemaläggning
- Hanterar påminnelser för specifika pass

**Exempel:** PPL-rutinen har tre workouts (Push, Pull, Legs) där Push är dag 1 (måndag kl 18:00), Pull är dag 2 (onsdag kl 18:00), osv.

---

### **workout_exercises**
Kopplar övningar till ett specifikt workout.
```
- id (PK)
- workout_id (FK → workouts)
- exercise_id (FK → exercises)
- rest_time (int, sekunder) - vila mellan sets
- order (int) - vilken ordning i passet
```

**Syfte:** Definierar vilka övningar som ingår i ett träningspass och i vilken ordning.

**OBS:** Target sets, reps och vikter finns INTE här - de ligger i `workout_exercise_sets` eftersom varje set kan vara olika!

---

### **workout_exercise_sets**
Detaljerad information per set för varje övning i ett workout.
```
- id (PK)
- workout_exercise_id (FK → workout_exercises)
- set_number (int) - 1, 2, 3...
- target_reps (int) - planerat antal reps
- target_weight (decimal) - planerad vikt i kg
```

**Syfte:** Spara planerade värden per set. Detta är viktigt eftersom:
- Set 1 kan ha 13 reps medan set 2 och 3 har 12 reps
- Progression sker ofta ojämnt (första setet blir bättre först)
- Stödjer tekniker som dropsets (olika vikter per set)

**Exempel:**
```
Bänkpress i "Push A":
Set 1: 80kg x 13 reps (target)
Set 2: 80kg x 12 reps (target)
Set 3: 80kg x 12 reps (target)
```

**Uppdatering:** När användaren genomför passet och loggar faktiska värden uppdateras `target_reps` och `target_weight` här **endast om de blev bättre** (progression):
- Vikter uppdateras om olika
- Reps uppdateras om olika
- Sets uppdateras **endast om fler** (inte färre - färre sets = dålig dag, ska inte påverka planen)

---

### **workout_logs**
Sparar varje genomfört träningspass.
```
- id (PK)
- user_id (FK → users)
- workout_id (FK → workouts)
- routine_id (FK → routines, nullable) - var det del av en rutin?
- routine_day (int, nullable) - vilken dag i rutincykeln?
- started_at (datetime)
- completed_at (datetime)
- duration_minutes (int) - total tid för passet
- notes (text, nullable) - användarens anteckningar
- created_at
```

**Syfte:** 
- Permanent historik över genomförda träningspass
- Möjliggör statistik (hur ofta tränar användaren, genomsnittlig tid, etc.)
- Kopplar till routines för att spåra progress i specifika program

---

### **workout_exercise_logs**
Detaljerad logg per set för varje övning i ett genomfört pass.
```
- id (PK)
- workout_log_id (FK → workout_logs)
- exercise_id (FK → exercises)
- set_number (int)
- weight (decimal) - faktisk vikt användaren lyfte
- reps (int) - faktiskt antal reps
- estimated_1rm (decimal) - beräknas vid loggning med formel
- notes (text, nullable) - anteckningar per set
- created_at
```

**Syfte:**
- Sparar exakt vad användaren gjorde varje set
- Möjliggör historikvisning och progressionsspårning
- Används för att detektera PRs (personal records)
- `estimated_1rm` beräknas automatiskt: `vikt × (1 + reps/30)`

**Exempel på loggning:**
```
Bänkpress - 2024-10-15:
Set 1: 80kg x 13 reps (estimated 1RM: 114.7kg) ← PR!
Set 2: 80kg x 12 reps (estimated 1RM: 112kg)
Set 3: 80kg x 11 reps (estimated 1RM: 109.3kg)

🔄 Relationer och dataflöde
Planering (Skapar träningsplan):

Användare skapar en routine (t.ex. "PPL")
Lägger till workouts i rutinen via routine_workouts (Push, Pull, Legs)
Varje workout innehåller workout_exercises (Bänkpress, Rows, etc.)
Varje övning har flera workout_exercise_sets (Set 1: 80kg x 12, Set 2: 80kg x 12...)

Genomförande (Tränar):

Användare startar ett workout → skapar workout_log med started_at
För varje set de genomför → skapar workout_exercise_log
När passet är klart → uppdaterar completed_at och duration_minutes

Uppdatering (Automatisk progression):
Efter loggning jämför systemet faktiska värden med planerade:

Om vikter/reps bättre → uppdatera workout_exercise_sets
Om sets fler → lägg till nya rader i workout_exercise_sets
Om sets färre → gör INGEN uppdatering (behåll målet)

Historik och PR-spårning:

Använd workout_exercise_logs för att visa:

All historik för en specifik övning
PRs per rep-range (max vikt för 1 rep, 5 reps, 12 reps etc.)
Progress över tid (grafer)
Jämförelser mellan träningspass




🎯 Viktiga designbeslut
Varför sparas sets separat?
Eftersom progression inte är linjär:

Set 1 kan förbättras till 13 reps
Set 2 och 3 är fortfarande 12 reps
Nästa gång ska användaren se exakt denna uppdelning

Varför två tabeller för övningar?

exercises = alla övningar (globala + custom)
user_exercise_preferences = kopplar användare till övningar
Detta låter:

Användare skapa custom övningar (bara de ser dem)
Användare markera favoriter
Spara personliga defaults utan att påverka andra



Varför separera workouts från workout_logs?

workouts = planen (mall)
workout_logs = faktiskt genomförande (historik)
Detta låter:

Samma plan köras många gånger
Historik sparas permanent
Jämförelser mellan olika genomföranden



Progression-logik:

Vikter/Reps: Uppdatera om olika
Sets: Uppdatera endast om fler (progression), inte färre (dålig dag)
Pass-specifik: Bänk i "Push A" påverkar inte bänk i "Full Body"


📊 Exempel på queries
Visa senaste träningspasset för "Push A"
sqlSELECT wl.*, wel.* 
FROM workout_logs wl
JOIN workout_exercise_logs wel ON wl.id = wel.workout_log_id
WHERE wl.workout_id = [Push A ID]
  AND wl.user_id = [user ID]
ORDER BY wl.completed_at DESC
LIMIT 1

---

Hitta PR för bänkpress på 12 reps
sqlSELECT MAX(weight) as pr_weight, completed_at
FROM workout_exercise_logs wel
JOIN workout_logs wl ON wel.workout_log_id = wl.id
WHERE wel.exercise_id = [Bänkpress ID]
  AND wel.reps = 12
  AND wl.user_id = [user ID]
Visa användarens favorit-övningar
sqlSELECT e.* 
FROM exercises e
JOIN user_exercise_preferences uep ON e.id = uep.exercise_id
WHERE uep.user_id = [user ID]
  AND uep.is_favourite = true

---

Hämta alla övningar användaren ska se
sqlSELECT e.* 
FROM exercises e
LEFT JOIN user_exercise_preferences uep 
  ON e.id = uep.exercise_id AND uep.user_id = [user ID]
WHERE uep.is_custom = false OR uep.is_custom IS NULL OR uep.user_id = [user ID]
(Visar globala övningar + användarens egna custom övningar)

🚀 Framtida utbyggnad (ej implementerat än)

AI-driven auto-progression baserat på historik
Avancerad schemaläggning (var X:e dag, specifika veckor)
Snooze-funktion för påminnelser
Community-features (dela routines/workouts)
Kroppsstatistik (vikt, kroppsfett, mått)
Nutrition tracking
```

-- ============================================
-- SKAPA TABELLER
-- ============================================

-- 1. Users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    push_notifications_enabled BOOLEAN DEFAULT true,
    notification_method ENUM('push', 'email', 'both') DEFAULT 'push',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Exercises (globala övningar)
CREATE TABLE exercises (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. User Exercise Preferences
CREATE TABLE user_exercise_preferences (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    exercise_id INT NOT NULL,
    is_favourite BOOLEAN DEFAULT false,
    is_custom BOOLEAN DEFAULT false,
    default_weight DECIMAL(5,2),
    default_rest_time INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_exercise (user_id, exercise_id)
);

-- 4. Routines
CREATE TABLE routines (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Workouts
CREATE TABLE workouts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Routine Workouts (junction table)
CREATE TABLE routine_workouts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    routine_id INT NOT NULL,
    workout_id INT NOT NULL,
    day_order INT NOT NULL,
    week_day VARCHAR(20),
    reminder_time TIME,
    reminder_enabled BOOLEAN DEFAULT true,
    FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE,
    FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE
);

-- 7. Workout Exercises
CREATE TABLE workout_exercises (
    id INT PRIMARY KEY AUTO_INCREMENT,
    workout_id INT NOT NULL,
    exercise_id INT NOT NULL,
    rest_time INT DEFAULT 60,
    order_in_workout INT NOT NULL,
    FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
);

-- 8. Workout Exercise Sets
CREATE TABLE workout_exercise_sets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    workout_exercise_id INT NOT NULL,
    set_number INT NOT NULL,
    target_reps INT NOT NULL,
    target_weight DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (workout_exercise_id) REFERENCES workout_exercises(id) ON DELETE CASCADE
);

-- 9. Workout Logs
CREATE TABLE workout_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    workout_id INT NOT NULL,
    routine_id INT,
    routine_day INT,
    started_at DATETIME NOT NULL,
    completed_at DATETIME,
    duration_minutes INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE SET NULL
);

-- 10. Workout Exercise Logs
CREATE TABLE workout_exercise_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    workout_log_id INT NOT NULL,
    exercise_id INT NOT NULL,
    set_number INT NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    reps INT NOT NULL,
    estimated_1rm DECIMAL(5,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workout_log_id) REFERENCES workout_logs(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
);

-- ============================================
-- LÄGG TILL INDEX FÖR BÄTTRE PRESTANDA
-- ============================================

CREATE INDEX idx_user_exercise_prefs_user ON user_exercise_preferences(user_id);
CREATE INDEX idx_user_exercise_prefs_exercise ON user_exercise_preferences(exercise_id);
CREATE INDEX idx_routines_user ON routines(user_id);
CREATE INDEX idx_workouts_user ON workouts(user_id);
CREATE INDEX idx_workout_exercises_workout ON workout_exercises(workout_id);
CREATE INDEX idx_workout_exercises_exercise ON workout_exercises(exercise_id);
CREATE INDEX idx_workout_logs_user ON workout_logs(user_id);
CREATE INDEX idx_workout_logs_workout ON workout_logs(workout_id);
CREATE INDEX idx_workout_logs_completed ON workout_logs(completed_at);
CREATE INDEX idx_workout_exercise_logs_workout_log ON workout_exercise_logs(workout_log_id);
CREATE INDEX idx_workout_exercise_logs_exercise ON workout_exercise_logs(exercise_id);

-- ============================================
-- TESTDATA
-- ============================================

-- Lägg till testanvändare
INSERT INTO users (email, password_hash, name, push_notifications_enabled, notification_method) VALUES
('test@example.com', '$2y$10$abcdefghijklmnopqrstuv', 'Test Användare', true, 'push'),
('anna@example.com', '$2y$10$abcdefghijklmnopqrstuv', 'Anna Andersson', true, 'both'),
('erik@example.com', '$2y$10$abcdefghijklmnopqrstuv', 'Erik Eriksson', false, 'email');

-- Lägg till 20 globala övningar
INSERT INTO exercises (name, description, category) VALUES
-- CHEST (Bröst) - 8 övningar
INSERT INTO exercises (name, description, category) VALUES
('Barbell Bench Press', 'Lie on a bench and press barbell from chest upward', 'Chest'),
('Incline Barbell Bench Press', 'Bench press on inclined bench (30-45 degrees) for upper chest', 'Chest'),
('Decline Barbell Bench Press', 'Bench press on declined bench for lower chest', 'Chest'),
('Dumbbell Bench Press', 'Bench press with dumbbells for greater range of motion', 'Chest'),
('Incline Dumbbell Press', 'Dumbbell press on inclined bench', 'Chest'),
('Chest Dips', 'Dips between parallel bars with forward lean', 'Chest'),
('Cable Flyes', 'Chest flyes with cables for constant tension', 'Chest'),
('Dumbbell Flyes', 'Lying dumbbell flyes on flat bench', 'Chest'),

-- BACK (Rygg) - 10 övningar
('Conventional Deadlift', 'Lift barbell from floor to standing position', 'Back'),
('Sumo Deadlift', 'Deadlift with wide stance', 'Back'),
('Pull-ups', 'Hang from bar and pull body up until chin over bar', 'Back'),
('Chin-ups', 'Pull-ups with underhand grip', 'Back'),
('Barbell Rows', 'Bent over row with barbell', 'Back'),
('Dumbbell Rows', 'Single arm row with dumbbell', 'Back'),
('T-Bar Rows', 'Row with T-bar for thick back', 'Back'),
('Lat Pulldown', 'Pull bar down to chest in cable machine', 'Back'),
('Seated Cable Rows', 'Horizontal row in cable machine', 'Back'),
('Face Pulls', 'Cable pull to face for rear delts and upper back', 'Back'),

-- LEGS (Ben) - 12 övningar
('Barbell Back Squats', 'Deep squat with barbell on back', 'Legs'),
('Front Squats', 'Squats with barbell on front delts', 'Legs'),
('Bulgarian Split Squats', 'Single leg squats with rear foot elevated', 'Legs'),
('Leg Press', 'Press weight with legs in machine', 'Legs'),
('Romanian Deadlift', 'Stiff leg deadlift for hamstrings and glutes', 'Legs'),
('Leg Curls', 'Lying or seated hamstring curls', 'Legs'),
('Leg Extensions', 'Quad extensions in machine', 'Legs'),
('Walking Lunges', 'Forward lunges with dumbbells', 'Legs'),
('Hip Thrusts', 'Barbell hip thrusts for glutes', 'Legs'),
('Glute Kickbacks', 'Cable kickbacks for glutes', 'Legs'),
('Standing Calf Raises', 'Calf raises standing in machine', 'Legs'),
('Seated Calf Raises', 'Calf raises seated for soleus', 'Legs'),

-- SHOULDERS (Axlar) - 8 övningar
('Overhead Press', 'Standing shoulder press with barbell', 'Shoulders'),
('Seated Dumbbell Press', 'Seated shoulder press with dumbbells', 'Shoulders'),
('Arnold Press', 'Shoulder press with rotation of wrists', 'Shoulders'),
('Lateral Raises', 'Lift dumbbells out to sides for side delts', 'Shoulders'),
('Front Raises', 'Lift dumbbells forward for front delts', 'Shoulders'),
('Rear Delt Flyes', 'Bent over flyes for rear delts', 'Shoulders'),
('Cable Lateral Raises', 'Lateral raises with cable', 'Shoulders'),
('Upright Rows', 'Pull barbell up to chin', 'Shoulders'),

-- ARMS - Biceps (5 övningar)
('Barbell Curls', 'Bicep curls with barbell', 'Arms'),
('Dumbbell Curls', 'Alternating or simultaneous bicep curls', 'Arms'),
('Hammer Curls', 'Bicep curls with neutral grip', 'Arms'),
('Preacher Curls', 'Bicep curls on preacher bench', 'Arms'),
('Cable Curls', 'Bicep curls with cable for constant tension', 'Arms'),

-- ARMS - Triceps (5 övningar)
('Triceps Pushdown', 'Push down rope or bar in cable machine', 'Arms'),
('Overhead Triceps Extension', 'Overhead dumbbell extension for triceps', 'Arms'),
('Skull Crushers', 'Lying triceps extensions with barbell', 'Arms'),
('Close-Grip Bench Press', 'Bench press with narrow grip for triceps', 'Arms'),
('Triceps Dips', 'Dips on parallel bars for triceps', 'Arms'),

-- CORE (Mage/Bål) - 10 övningar
('Planks', 'Hold push-up position on forearms', 'Core'),
('Side Planks', 'Plank position on one side', 'Core'),
('Hanging Leg Raises', 'Hang from bar and raise legs', 'Core'),
('Cable Crunches', 'Kneeling crunches with cable', 'Core'),
('Russian Twists', 'Seated twists with weight', 'Core'),
('Ab Wheel Rollouts', 'Roll out with ab wheel', 'Core'),
('Dead Bugs', 'Lying alternating arm and leg extensions', 'Core'),
('Bicycle Crunches', 'Alternating elbow to knee crunches', 'Core'),
('Mountain Climbers', 'Running motion in plank position', 'Core'),
('Pallof Press', 'Anti-rotation press with cable', 'Core');

-- Lägg till några favoriter för testanvändare
INSERT INTO user_exercise_preferences (user_id, exercise_id, is_favourite, default_weight, default_rest_time) VALUES
(1, 1, true, 80.0, 180),   -- Barbell Bench Press
(1, 20, true, 100.0, 240), -- Barbell Back Squats
(1, 9, true, 120.0, 300),  -- Conventional Deadlift
(1, 11, true, 0.0, 120),   -- Pull-ups
(1, 33, true, 50.0, 150),  -- Overhead Press
(2, 1, true, 50.0, 120),   -- Barbell Bench Press
(2, 37, true, 15.0, 90);   -- Lateral Raises

-- Skapa custom övning för testanvändare
INSERT INTO exercises (name, description, category) VALUES
('My Special Curls', 'My own variation of bicep curls', 'Arms');

INSERT INTO user_exercise_preferences (user_id, exercise_id, is_custom, is_favourite) VALUES
(1, 61, true, true);

-- Uppdatera workouts med nya övningar
DELETE FROM workout_exercise_sets;
DELETE FROM workout_exercises;

-- Push A övningar (uppdaterad med rätt IDs)
INSERT INTO workout_exercises (workout_id, exercise_id, rest_time, order_in_workout) VALUES
(1, 1, 180, 1),   -- Barbell Bench Press
(1, 2, 150, 2),   -- Incline Barbell Bench Press
(1, 6, 120, 3),   -- Chest Dips
(1, 33, 150, 4),  -- Overhead Press
(1, 37, 60, 5),   -- Lateral Raises
(1, 43, 90, 6);   -- Triceps Pushdown

-- Pull A övningar
INSERT INTO workout_exercises (workout_id, exercise_id, rest_time, order_in_workout) VALUES
(2, 9, 300, 1),   -- Conventional Deadlift
(2, 11, 180, 2),  -- Pull-ups
(2, 13, 150, 3),  -- Barbell Rows
(2, 16, 120, 4),  -- Lat Pulldown
(2, 19, 90, 5),   -- Face Pulls
(2, 41, 60, 6);   -- Barbell Curls

-- Legs A övningar
INSERT INTO workout_exercises (workout_id, exercise_id, rest_time, order_in_workout) VALUES
(3, 20, 240, 1),  -- Barbell Back Squats
(3, 24, 180, 2),  -- Romanian Deadlift
(3, 23, 150, 3),  -- Leg Press
(3, 27, 120, 4),  -- Walking Lunges
(3, 28, 120, 5),  -- Hip Thrusts
(3, 30, 60, 6);   -- Standing Calf Raises

-- Lägg till sets för Push A övningar
-- Barbell Bench Press
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(1, 1, 12, 80.0),
(1, 2, 12, 80.0),
(1, 3, 12, 80.0);

-- Incline Barbell Bench Press
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(2, 1, 10, 70.0),
(2, 2, 10, 70.0),
(2, 3, 10, 70.0);

-- Chest Dips
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(3, 1, 12, 0.0),
(3, 2, 12, 0.0),
(3, 3, 12, 0.0);

-- Overhead Press
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(4, 1, 10, 50.0),
(4, 2, 10, 50.0),
(4, 3, 10, 50.0);

-- Lateral Raises
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(5, 1, 15, 10.0),
(5, 2, 15, 10.0),
(5, 3, 15, 10.0);

-- Triceps Pushdown
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(6, 1, 15, 30.0),
(6, 2, 15, 30.0),
(6, 3, 15, 30.0);

-- Lägg till sets för Pull A
-- Conventional Deadlift
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(7, 1, 5, 140.0),
(7, 2, 5, 140.0),
(7, 3, 5, 140.0);

-- Pull-ups
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(8, 1, 10, 0.0),
(8, 2, 10, 0.0),
(8, 3, 10, 0.0);

-- Barbell Rows
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(9, 1, 10, 70.0),
(9, 2, 10, 70.0),
(9, 3, 10, 70.0);

-- Lat Pulldown
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(10, 1, 12, 60.0),
(10, 2, 12, 60.0),
(10, 3, 12, 60.0);

-- Face Pulls
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(11, 1, 15, 20.0),
(11, 2, 15, 20.0),
(11, 3, 15, 20.0);

-- Barbell Curls
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(12, 1, 12, 30.0),
(12, 2, 12, 30.0),
(12, 3, 12, 30.0);

-- Lägg till sets för Legs A
-- Barbell Back Squats
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(13, 1, 10, 100.0),
(13, 2, 10, 100.0),
(13, 3, 10, 100.0);

-- Romanian Deadlift
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(14, 1, 10, 80.0),
(14, 2, 10, 80.0),
(14, 3, 10, 80.0);

-- Leg Press
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(15, 1, 12, 150.0),
(15, 2, 12, 150.0),
(15, 3, 12, 150.0);

-- Walking Lunges
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(16, 1, 20, 20.0),
(16, 2, 20, 20.0);

-- Hip Thrusts
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(17, 1, 12, 80.0),
(17, 2, 12, 80.0),
(17, 3, 12, 80.0);

-- Standing Calf Raises
INSERT INTO workout_exercise_sets (workout_exercise_id, set_number, target_reps, target_weight) VALUES
(18, 1, 15, 60.0),
(18, 2, 15, 60.0),
(18, 3, 15, 60.0);

-- Uppdatera loggade övningar med rätt exercise IDs
DELETE FROM workout_exercise_logs;

-- Logga Barbell Bench Press
INSERT INTO workout_exercise_logs (workout_log_id, exercise_id, set_number, weight, reps, estimated_1rm) VALUES
(1, 1, 1, 80.0, 13, 80.0 * (1 + 13/30)),
(1, 1, 2, 80.0, 12, 80.0 * (1 + 12/30)),
(1, 1, 3, 80.0, 12, 80.0 * (1 + 12/30));

-- Logga Incline Barbell Bench Press
INSERT INTO workout_exercise_logs (workout_log_id, exercise_id, set_number, weight, reps, estimated_1rm) VALUES
(1, 2, 1, 70.0, 10, 70.0 * (1 + 10/30)),
(1, 2, 2, 70.0, 10, 70.0 * (1 + 10/30)),
(1, 2, 3, 70.0, 9, 70.0 * (1 + 9/30));

-- Logga Chest Dips
INSERT INTO workout_exercise_logs (workout_log_id, exercise_id, set_number, weight, reps, estimated_1rm) VALUES
(1, 6, 1, 0.0, 12, 0.0),
(1, 6, 2, 0.0, 12, 0.0),
(1, 6, 3, 0.0, 11, 0.0);

-- ============================================
-- VERIFIERA INSTALLATION
-- ============================================

-- Se alla övningar per kategori
SELECT category, COUNT(*) as antal 
FROM exercises 
GROUP BY category 
ORDER BY antal DESC;

-- Se alla övningar (sorterade)
SELECT id, name, category FROM exercises ORDER BY category, name;

-- Total antal övningar
SELECT COUNT(*) as total_ovningar FROM exercises;

-- Se Push A workout med alla övningar
SELECT 
    w.name as workout, 
    e.name as exercise, 
    e.category,
    we.order_in_workout, 
    we.rest_time,
    COUNT(wes.id) as antal_sets
FROM workouts w
JOIN workout_exercises we ON w.id = we.workout_id
JOIN exercises e ON we.exercise_id = e.id
LEFT JOIN workout_exercise_sets wes ON we.id = wes.workout_exercise_id
WHERE w.id = 1
GROUP BY w.id, e.id, we.id
ORDER BY we.order_in_workout;

-- Se alla övningar per kategori (detaljerad)
SELECT 
    category,
    GROUP_CONCAT(name ORDER BY name SEPARATOR ', ') as exercises
FROM exercises
GROUP BY category;
