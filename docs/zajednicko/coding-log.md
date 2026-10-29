# Coding log

Jedini kanal između uloga (developer, tester, revieweri). Svaki problem ima svoj unos s oznakom `L`. Uloge se pozivaju na oznaku, npr. "vidi L4", i ništa ne prepisuju.

Pravila:

- Developer i tester dodaju novi unos na dno: naslov sa stanjem `otvoreno` i **Problem**.
- Rasprava ide ispod Problema, jedan redak po javljanju, najviše dva kruga. Sudac je [specifikacija](specifikacija.md): argument pokazuje na oznaku (R, Q, D), ne na mišljenje. Ako se ne slože ili specifikacija ne odgovara, odlučuje Rajko.
- Tehnička pitanja (sučelje, tipovi, testna infrastruktura) rješavaju developer i tester sami, u raspravi, i sami upisuju **Odluka**, **Zašto** i **Gdje**.
- Orkestrator ulazi samo kad se radi o ponašanju (specifikacija ne odgovara) ili kad se developer i tester ne slože ni nakon dva kruga. Tada **Odluku** i **Zašto** upisuje on, uz Rajkovu odluku.
- Odluka koja mijenja ponašanje ulazi i u specifikaciju kao nova oznaka D.
- Čita se pretragom po oznaci (`L4`, `T3`, `R13`), ne cijeli fajl.
- Riješeni i odbačeni unosi se na kraju taska sele u `coding-log-arhiva.md`.

Stanja: `otvoreno` (čeka odluku), `odlučeno` (čeka popravak), `riješeno` (popravljeno, Gdje kaže gdje), `odbačeno` (nije problem, Zašto kaže zašto).

Format:

```markdown
## Lx · otvoreno · Tx · R4

**Problem** (developer): Jedna ili dvije rečenice.

- tester: argument s oznakom.
- developer: odgovor.

**Odluka:**
**Zašto:**
**Gdje:**
```

## L1 · riješeno · T1 · Q49.1, D16

**Problem** (developer): Specifikacija ne kaže koja je lozinka seedanih korisnika.

**Odluka:** Svi imaju lozinku `password`, upisanu u README.
**Zašto:** Najjednostavniji način da se djeluje kao svaki korisnik.
**Gdje:** specifikacija D16, sučelje T1

## L2 · riješeno · T1 · D4, D5, Q15, D16

**Problem** (developer): Specifikacija ne kaže datume zaposlenja seedanih korisnika.

**Odluka:** Svi 2020-01-01, osim Toma 2026-06-01.
**Zašto:** Tom pokazuje razmjerni saldo u godini zaposlenja (12 dana).
**Gdje:** specifikacija D16, sučelje T1

## L3 · riješeno · T1 · D16

**Problem** (developer): Sučelje T1 za lozinku i datume zaposlenja u seedu navodi samo Q15 (tko počne raditi usred godine dobiva razmjerno manje dana) i D5 (u godini zaposlenja broje se mjeseci od mjeseca zaposlenja do prosinca), a ne D16 (seed: lozinka `password` za sve, datum zaposlenja 2020-01-01 za sve osim Toma, 2026-06-01).

**Odluka:** Developer u sučelje dodaje ono što developer i tester smatraju potrebnim za testove, u postojećem formatu sučelja.
**Zašto:** Sučelje je jedino što tester vidi; mora biti dovoljno za testove.
**Gdje:** sučelje T1, Baza: lozinka i datumi zaposlenja pozivaju se na D16; navedeno je da se hash radi s `BCryptPasswordEncoder`

## L4 · riješeno · T1 · sučelje (Domena, Repozitoriji)

**Problem** (tester): Sučelje ne navodi konstruktore `User` i `Team`, Java tipove za datume i vremena, ni povratni tip `findByUsername`. Testovi u `backend/src/test/java/com/example/timeoff/` pretpostavljaju: `new User(username, name, passwordHash, admin, hireDate)` s `LocalDate`; `Instant` za `createdAt`, `now`, `decidedAt` i `cancelledAt`; `findByUsername` vraća `Optional<User>`; getteri `getLead()`, `getMembers()`, `getTeams()`, `getName()`, `getDecisionComment()`, `getDecidedBy()`, `getDecidedAt()`, `getCancelReason()`, `getCancelledBy()`, `getCancelledAt()`, `getStatus()`, `getHireDate()`, `getPasswordHash()`. Developer neka potvrdi ili ispravi sučelje.

**Odluka:** Developer u sučelje dodaje ono što developer i tester smatraju potrebnim za testove, u postojećem formatu sučelja.
**Zašto:** Sučelje je jedino što tester vidi; mora biti dovoljno za testove.
**Gdje:** sučelje T1, Domena: Java tipovi (`LocalDate`, `Instant`), getter `getX()` za svako polje osim `isAdmin()`, konstruktor `User`, tipovi `lead`, `members`, `teams`; Repozitoriji: `findByUsername` vraća `Optional<User>`, `findByOwner` vraća `List<TimeOffRequest>`. Konstruktor `Team` se ne dodaje jer timovi u T1 nastaju samo seedom

## L5 · riješeno · T1 · sučelje (Baza)

**Problem** (tester): Test seeda i shema koristi Spring Boot `@ServiceConnection`, Testcontainers PostgreSQL `postgres:16` i `BCryptPasswordEncoder` iz Spring Securityja. Sučelje to ne navodi. Ako pom koristi drugu verziju ili drugačiji pristup, neka developer javi.

**Odluka:** Tester koristi postojeći `TestcontainersConfiguration` (`@Import(TestcontainersConfiguration.class)`, `postgres:17`), ne vlastiti kontejner. `BCryptPasswordEncoder` iz Spring Securityja ostaje.
**Zašto:** Projekt koristi PostgreSQL 17; vlastiti kontejner koristi stari Testcontainers paket (`org.testcontainers.containers`) koji projekt nema.
**Gdje:** `backend/src/test/java/com/example/timeoff/SeedAndSchemaTest.java`, `@Import(TestcontainersConfiguration.class)`; vlastiti kontejner i `@ServiceConnection` uklonjeni

## L6 · riješeno · T1 · pravila kodiranja (Stil 4)

**Problem** (reviewer, važno): Helper `insertRequest` u `SeedAndSchemaTest.java` ima pet argumenata u jednom retku (deklaracija, red 172 i tijelo, red 176). Pozivi u testovima R8 i T1 (redovi 106, 113, 120, 127) stavljaju argumente u dva retka umjesto svakog u svoj. Pravilo: "Više od tri argumenta: svaki u svoj redak."

- tester: prihvaćam. `insertRequest` i svi pozivi u R8 i T1 sada imaju svaki argument u svom retku.

**Odluka:** Argumenti u svoj redak, kako pravilo traži.
**Zašto:** Pravilo "Više od tri argumenta: svaki u svoj redak."
**Gdje:** `SeedAndSchemaTest.java`, `insertRequest` i pozivi u `dbRejectsEndDateBeforeStartDate_R8`, `dbAcceptsOneDayRequest_R8`, `dbRejectsUnknownType_T1`, `dbRejectsUnknownStatus_T1`

## L7 · riješeno · T1 · pravila kodiranja (Stil 4)

**Problem** (reviewer, važno): U `TimeOffRequestTest.java`, metoda `setUp` (redovi 23 i 24) zove `new User(...)` s pet argumenata u jednom retku. Pravilo: "Više od tri argumenta: svaki u svoj redak."

- tester: prihvaćam. Oba `new User(...)` poziva u `setUp` sada imaju svaki argument u svom retku.

**Odluka:** Argumenti u svoj redak, kako pravilo traži.
**Zašto:** Pravilo "Više od tri argumenta: svaki u svoj redak."
**Gdje:** `domain/TimeOffRequestTest.java`, `setUp`

## L8 · riješeno · T1 · pravila kodiranja (Stil 5)

**Problem** (reviewer, važno): Lanac poziva u `SeedAndSchemaTest.java` stoji u jednom retku: `userRepository.findAll().stream().filter(...).map(...)` u `onlyEvaIsAdmin_T1` (red 85) i `team(teamName).getMembers().stream().map(...).toList()` u `memberNames` (red 191). Pravilo: "Lanac poziva: svaki korak u svoj redak."

- tester: prihvaćam. `onlyEvaIsAdmin_T1` i `memberNames` sada imaju svaki korak lanca u svom retku.

**Odluka:** Svaki korak lanca u svoj redak, kako pravilo traži.
**Zašto:** Pravilo "Lanac poziva: svaki korak u svoj redak."
**Gdje:** `SeedAndSchemaTest.java`, `onlyEvaIsAdmin_T1` i `memberNames`
