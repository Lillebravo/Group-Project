# 📌 Kodstandard för [Träningsapp]

Detta dokument beskriver vår gemensamma kodstandard för att säkerställa konsekvent, lättläst och underhållbar kod i vårt agila team.

---

## 1. Allmän kodstil

- **Formattering:**  
  - Använd [Prettier](https://prettier.io/) för automatisk formattering.  
  - Formattera koden **innan varje commit** (helst automatiskt via pre-commit hook).

- **Indentering:** 1 tab.  
- **Max linjelängd:** 100 tecken.  
- **Semikolon:** Ja.  
- **Citattecken:** Enkel `' '` för JavaScript/TypeScript. ´" "´ För Java.  
- **Imports:**  
  - Standardbibliotek först, externa paket sen, egna moduler sist.  
  - Använd relativa paths endast inom samma feature-folder.

---

## 2. Namngivning & Struktur

Filer & mappar: PascalCase → AuthController.java
Variabler & funktioner: camelCase → getUserData
Klasser & komponenter: PascalCase → UserCard
Konstanter: UPPER_CASE → MAX_RETRIES

För frontend
Klasser & ID: Kebab-case -> list-exercices

---

## 3. Git & Versionshantering

Branch-namn:

feature/namn → ny funktionalitet

fix/namn → buggfix

refactor/namn → omstrukturering
Ex: feature/login-page, fix/navbar-responsiveness

Commit-meddelanden:

Kort & tydlig rubrik i presens.

Exempel:

✅ feat: add login form with validation

✅ fix: correct typo in header

❌ fixed bug, ❌ stuff

Pull Requests:

En PR per avgränsad uppgift.

Minst en kodgranskning innan merge.

Ingen direkt push till main – alltid via PR.

---

## 4. Dokumentation & Kommentarer
// för kommentarer
Förklara vad större komplicerade klasser/funktioner gör
Behövs inga kommentarer för små enkla grejer 

## 5. Kodgranskning

All kod granskas av minst en annan teammedlem innan merge.
Fokus på: läsbarhet, säkerhet, felhantering, logik, standarder.
