# Review: pokretanje, konfiguracija, README

- Commit: eb785ba
- Datum: 2026-11-01
- Necommitano (git status --short): ` M README.md`, ` M docs/za-procjenu.md`, ` M docs/zajednicko/specifikacija.md`, ` M tester/e2e-izvjestaj.md`, `?? .claude/`, `?? docs/diagrams.md`, `?? integrator_report.md`, `?? llm-shiva/`, `?? shiva_report.md`.
- README.md koji sam čitao je necommitana verzija (radno stablo), ne ona iz eb785ba.

## BLOKIRA

Nema nalaza koji krše brief, odgovor naručitelja ili specifikaciju, niti onih koji sigurno ruše aplikaciju.

## VAŽNO

1. **Primjer iz `.env.example` prolazi provjeru ključa, pa kopiranje bez izmjene daje javno poznat JWT secret.**
   - Dokaz: `.env.example:3`: `JWT_SECRET=change-me-to-a-random-string-of-at-least-32-bytes`. Taj tekst ima 49 znakova (ASCII), a `SecurityConfig.java` odbija samo ključ kraći od `MIN_KEY_BYTES = 32`. `docker-compose.yml:29`: `JWT_SECRET: ${JWT_SECRET:?JWT_SECRET must be set in .env (see .env.example)}` odbija samo prazno/nepostavljeno.
   - Zašto: cilj L14 je da nitko ne može sam izraditi valjani token. Korisnik koji samo napravi `cp .env.example .env` (README.md:102) pokrene stack s tajnom koja je javno u repozitoriju. README to prepušta riječima "then set JWT_SECRET".
   - Pripada: developer/orkestrator (prazna vrijednost `JWT_SECRET=` u `.env.example` bi natjerala Compose da stane s postojećom porukom); README kao zaobilazno rješenje.

2. **Backend image pokrenut bez Composea koristi javni zadani secret, što L14 zabranjuje; komentar u properties je netočan.**
   - Dokaz: `backend/src/main/resources/application.properties:12-13`: `# Local and test default only; Docker sets JWT_SECRET.` / `app.jwt.secret=${JWT_SECRET:local-dev-secret-change-me-0123456789}`. JWT_SECRET postavlja samo Compose (`docker-compose.yml:29`); `backend/Dockerfile` ga ne traži.
   - Zašto: `docker build ./backend` pa `docker run` (uz bazu preko `SPRING_DATASOURCE_URL`) starta s poznatom tajnom, bez upozorenja. L14 u logu govori samo o Composeu.
   - Pripada: developer (komentar, ili bez zadane vrijednosti izvan testova); README treba reći da je podržan samo Compose.

3. **`backend/mvnw` je u git indeksu bez izvršnog bita, pa README naredba `./mvnw test` ne radi na svježem checkoutu na Linuxu/macOS-u.**
   - Dokaz: `git ls-files -s backend/mvnw`: `100644 bd8896bf2217b46faa0291585e01ac1a3441a958 0	backend/mvnw`. README.md:116: `(cd backend && ./mvnw test)`.
   - Zašto: "Permission denied" je prvi dojam recenzenta. Nisam pokretao; zaključak iz moda u indeksu.
   - Pripada: README (`sh mvnw test`) ili developer (`git update-index --chmod=+x backend/mvnw`).

4. **README linka na `docs/diagrams.md`, koji nije u gitu.**
   - Dokaz: README.md:152: `[diagrams.md](docs/diagrams.md)`; `git ls-files --error-unmatch docs/diagrams.md` ne nalazi fajl; `git status`: `?? docs/diagrams.md`. Fajl postoji na disku. Isto `specifikacija.md` sekcija 13: `[diagrams.md](../diagrams.md)` (necommitana izmjena).
   - Zašto: u commitu eb785ba link ne vodi nikamo; README, specifikacija i diagrams.md moraju u isti commit.
   - Pripada: log/orkestrator.

5. **README navodi model "Haiku" za E2E, a u commitanim dokumentima to nigdje ne piše.**
   - Dokaz: README.md:51: `This first run was done by an LLM (Haiku);`. `grep -rn Haiku docs tester README.md` pogađa samo README (osim fajla koji nisam smio čitati). `tester/e2e-izvjestaj.md` ne imenuje model.
   - NEPROVJERLJIVO iz dopuštenih izvora.
   - Pripada: tester (upisati model u e2e-izvjestaj.md).

6. **Brojka "~530 tests" nije provjerljiva bez pokretanja.**
   - Dokaz: statički brojim 219 `@Test` + 40 `@ParameterizedTest` (backend) i 64 `it(` + 4 `it.each(` (frontend); 327 deklaracija. Konačna brojka ovisi o širenju parametriziranih testova. README.md:46 i :85 `~530`.
   - NEPROVJERLJIVO. Pripada: README/tester (brojka iz stvarnog izlaza).

## SITNO

1. Nesuglasna imena varijabli: Compose postavlja `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` (`docker-compose.yml:25-27`), a `application.properties:3-5` definira `DB_URL`, `DB_USER`, `DB_PASSWORD`. Radi (Spring čita oba), ali `DB_*` nitko ne koristi i nisu dokumentirani. Pripada: developer/README.
2. Zona (D8) se postavlja samo u Composeu (`docker-compose.yml:8,30,42`: `TZ: Europe/Zagreb`); `ClockConfig.java` koristi `Clock.systemDefaultZone()`. Samostalan backend image računa "danas" u UTC-u. Isti uzrok kao VAŽNO 2. Pripada: README.
3. Baza je objavljena na hostu s trivijalnom lozinkom: `docker-compose.yml:9-10` `POSTGRES_PASSWORD: timeoff`, `ports: - "5432:5432"`. Nije potrebno za README (koristi `docker compose exec`) i sudara se s lokalnim Postgresom. Pripada: developer.
4. Nema healthchecka za backend; `/actuator/health` je izložen (`application.properties:10`) i otvoren (`SecurityConfig.java`: `.requestMatchers("/actuator/health", "/error").permitAll()`), ali nije u README tablici API-ja. `frontend` ima `depends_on: - backend` bez uvjeta. Pripada: developer/README.
5. README traži samo "Needs Docker" (redak 99), a Test blok traži i JDK 21 i Node (verzija nigdje u README-u; Dockerfile koristi `node:22-alpine`; nema `engines` u `frontend/package.json`). Pripada: README.
6. README.md:231 "End-to-end tests against the running Compose stack" djelomično proturječi retcima 46-52 (E2E putovi već odrađeni); stvarni sljedeći korak je skripta (Playwright). Pripada: README.
7. "~75 atomic questions" (README.md:12): `atomarna-pitanja.md` ima 77 sidara `id="q..."`, od toga 57 glavnih; "~75" vrijedi samo ako se broje podpitanja. Pripada: README.

## PRIHVAĆENO (svjesno izostavljeno)

- Carry-over, hrvatski blagdani, istovremeni zahtjevi, godišnja granica, preklapanje bolovanja: README "Deliberately out" i "Extensions (not built)", D5, D7. README sada izričito navodi da je D5 odstupanje od odgovora Q18 (i Q14.1, "do 30. lipnja"); odgovor naručitelja time nije ispunjen, ali je evidentirano.
- L28: nema salda podnositelja na Approvals ekranu (log L28, README Extensions).
- D17: otkazivanje tuđeg zahtjeva na čekanju kroz UI nije napravljeno, samo API (specifikacija D17).
- E2E: 344/345, jedna ispravljena stavka nije ponovo izvršena; README to iskreno kaže (podudara se s `tester/e2e-izvjestaj.md`).

## Provjereno i u redu

- Svih 13 ruta iz README tablice postoji u controllerima (login, me, me/balance, POST requests, approve/reject/cancel, approvals, approvals/cancellable, me/requests, requests, requests/{id}, users/{id}/balance, calendar s `month` obveznim i `teamId` neobveznim, teams). Nema ruta u controllerima koje README ne navodi osim `/actuator/health`.
- Svi README linkovi osim `docs/diagrams.md` su pod `git ls-files`: specifikacija, arhitektura, taskovi, pravila-kodiranja, coding-log, sucelje, way-of-working, TESTING_METHODOLOGY, e2e-scenariji, e2e-izvjestaj.
- D1–D17: točno 17 odluka u specifikaciji sekciji 10; sekcije 9 (proširenja) i 11 (odstupanja) postoje.
- Coding log: 43 naslova `## L`, jedan je predložak `Lx`, dakle 42 unosa kako README kaže. L9, L14, L28, L34 postoje i odgovaraju kontekstu.
- E2E: 83+28+42+76+115 = 344 PROSLO + 1 PALO = 345, 5 lanaca; podudara se s README-om.
- Seed (V2__seed.sql): 8 korisnika, uloge i timovi odgovaraju README tablici; Tom 2026-06-01, ostali 2020-01-01; eva admin bez tima; jedna lozinka (BCrypt); nema zahtjeva (D9).
- SQL u "Data corrections": sve tablice i stupci postoje u V1__schema.sql; 'CANCELLED' dopušten CHECK-om.
- Run blok redom: `cp .env.example .env` pa `docker compose up -d --build`; `.env` je u `.gitignore` i nije praćen (`git check-ignore -v`); Compose bez JWT_SECRET staje (`:?`). BOM na početku `.gitignore` ne kvari pravilo (`git check-ignore -v` pogađa `docs/old/`).
- Backend Dockerfile: dvofazni, `target/*.jar` pogađa jedan jar. Frontend Dockerfile: `API_URL` kao ARG i ENV u obje faze. Portovi 8080/3000 odgovaraju README-u.
- Testcontainers koristi `postgres:17`, kao Compose; Java 21 u `backend/pom.xml:30`.
- Nijedna prava tajna nije u repozitoriju osim razvojne zadane vrijednosti (VAŽNO 2) i DB lozinke `timeoff`.

## Nisam stigao / ne smijem

- Nisam pokrenuo testove, Docker ni aplikaciju: ne mogu potvrditi ~530 testova, da `docker compose up` prolazi ni da `next start` s `npm ci --omit=dev` učitava `next.config.ts` bez paketa `typescript`.
- Frontend izvorni kod nisam čitao osim `next.config.ts` i `package.json`.
- Sadržaj `diagrams.md` nisam uspoređivao s README tvrdnjom o dijagramima.
- Arhitekturu, way-of-working i taskove nisam provjeravao tvrdnju po tvrdnju, samo reference iz README-a.
