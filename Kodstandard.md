# 📌 Kodstandard för [Projektets namn]

Detta dokument beskriver vår gemensamma kodstandard för att säkerställa konsekvent, lättläst och underhållbar kod i vårt agila team.

---

## 🧰 1. Allmän kodstil

- **Formattering:**  
  - Använd [Prettier](https://prettier.io/) för automatisk formattering.  
  - Använd [ESLint](https://eslint.org/) med vårt delade regelset (`.eslintrc.json`).  
  - Formattera koden **innan varje commit** (helst automatiskt via pre-commit hook).

- **Indentering:** 2 mellanslag (inte tabbar).  
- **Max linjelängd:** 100 tecken.  
- **Semikolon:** Ja.  
- **Citattecken:** Enkel `' '` för JavaScript/TypeScript.  
- **Imports:**  
  - Standardbibliotek först, externa paket sen, egna moduler sist.  
  - Använd relativa paths endast inom samma feature-folder.

Exempel:
```javascript
import fs from 'fs';
import express from 'express';
import { getUser } from '@/features/user/userService';
