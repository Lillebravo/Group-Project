# Group-Project
Träningsapp där varje set kan progressa individuellt, fullständig historik sparas, och användare kan bygga avancerade träningsprogram med rutiner och automatiska påminnelser.

Träningsapp - Databasstruktur
📋 Översikt
Denna databas hanterar ett komplett träningssystem där användare kan:

Skapa och följa träningsrutiner
Logga träningspass med detaljerad information per set
Spåra progression över tid (vikter, reps, PRs)
Skapa egna övningar och markera favoriter
Få påminnelser för schemalagda träningspass


🗂️ Tabeller
users
Användarkonton i systemet.
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
