# Sučelje

Opis sučelja po tasku, bez koda. Ponašanje određuje [specifikacija](specifikacija.md); ovdje su imena, oblici i ugovori koje testovi mogu koristiti.

## T1 Shema i seed

Slojevi: 5 Domena, 6 Repozitorij, 7 Baza (arhitektura). Paket: `com.example.timeoff.domain` za domenu i repozitorije.

### Baza (Flyway)

`V1__schema.sql`:

| Tablica | Stupci | Ograničenja |
| --- | --- | --- |
| `users` | `id`, `username`, `name`, `password_hash`, `admin`, `hire_date` | `username` jedinstven; sve obavezno; `admin` default `false` |
| `teams` | `id`, `name`, `lead_id` | `name` jedinstven; `lead_id` obavezan, FK na `users` (Q6, Q7: tim ima točno jednog voditelja) |
| `team_members` | `team_id`, `user_id` | primarni ključ (`team_id`, `user_id`); FK na `teams` i `users` |
| `time_off_requests` | `id`, `owner_id`, `type`, `start_date`, `end_date`, `reason`, `status`, `decision_comment`, `decided_by_id`, `decided_at`, `cancel_reason`, `cancelled_by_id`, `cancelled_at`, `created_at` | `owner_id`, `type`, `start_date`, `end_date`, `status`, `created_at` obavezni; `end_date >= start_date` (R8: završni datum nije prije početnog); `type` jedna od 3 vrijednosti; `status` jedna od 4; FK `decided_by_id`, `cancelled_by_id` na `users` |

- Datumi su `date`, vremena `timestamptz`.
- Voditelj je upisan i u `team_members` svakog tima koji vodi (Q3: voditelj je član svakog tima koji vodi).
- Uloga voditelja se ne sprema: korisnik je voditelj ako vodi barem jedan tim. Admin je stupac `admin` (Q2: korisnik može imati više uloga).

`V2__seed.sql`: korisnici i timovi iz briefa, bez zahtjeva (Q55: seed bez primjera zahtjeva).

| `username` | `name` | Admin | Vodi | Član | `hire_date` |
| --- | --- | --- | --- | --- | --- |
| `ana` | Ana | ne | Platform | Platform | 2020-01-01 |
| `ivan` | Ivan | ne | | Platform | 2020-01-01 |
| `marko` | Marko | ne | | Platform | 2020-01-01 |
| `petra` | Petra | ne | | Platform | 2020-01-01 |
| `luka` | Luka | ne | Mobile | Mobile | 2020-01-01 |
| `sara` | Sara | ne | | Mobile | 2020-01-01 |
| `tom` | Tom | ne | | Mobile | 2026-06-01 |
| `eva` | Eva | da | | | 2020-01-01 |

- Lozinka je `password` za sve korisnike, u bazi kao BCrypt hash (`BCryptPasswordEncoder` iz Spring Securityja) (D16: seed, lozinka `password` za sve).
- Datumi zaposlenja su iz D16 (seed: 2020-01-01 za sve osim Toma, 2026-06-01). Tom zato za 2026. ima razmjerni saldo: 20 × 7 / 12 = 11,67, zaokruženo 12 dana (Q15: tko počne raditi usred godine dobiva razmjerno manje dana; D5: broje se mjeseci od mjeseca zaposlenja do prosinca).

### Domena

Java tipovi: datumi (`hireDate`, `startDate`, `endDate`) su `LocalDate`, vremena (`createdAt`, `now`, `decidedAt`, `cancelledAt`) su `Instant`. Svako polje ima getter `getX()` (npr. `getStatus()`, `getHireDate()`); iznimka je `isAdmin()`.

**`Role`**: nema enuma. Uloge se izvode: `isAdmin()` iz stupca, `isTeamLead()` iz timova koje vodi.

**`TimeOffType`** (enum): `VACATION`, `SICK_LEAVE`, `DAY_OFF`.

**`Status`** (enum) s tablicom dopuštenih prijelaza:

| Iz | U |
| --- | --- |
| `PENDING` | `APPROVED`, `REJECTED`, `CANCELLED` |
| `APPROVED` | `CANCELLED` |
| `REJECTED` | nema |
| `CANCELLED` | nema |

- `canTransitionTo(target)`: `true` samo za red iz tablice.

**`Team`**: `id`, `name`, `lead` (`User`), `members` (kolekcija `User`). U T1 timovi nastaju samo seedom.

**`User`**: `id`, `username`, `name`, `passwordHash`, `admin`, `hireDate`, `teams` (kolekcija `Team` u kojima je član).

| Metoda | Vraća |
| --- | --- |
| konstruktor (`username`, `name`, `passwordHash`, `admin`, `hireDate`) | novi korisnik bez timova |
| `isAdmin()` | je li admin |
| `leads(team)` | je li voditelj tog tima |
| `isMemberOf(team)` | je li član tog tima |
| `isTeamLead()` | vodi li barem jedan tim |

**`TimeOffRequest`**: polja kao stupci tablice; `decidedBy` i `cancelledBy` su `User`.

| Metoda | Radi |
| --- | --- |
| konstruktor (`owner`, `type`, `startDate`, `endDate`, `reason`, `createdAt`) | stanje `PENDING` |
| `isSubmittedBy(user)` | je li `user` vlasnik |
| `isPending()` | je li stanje `PENDING` |
| `overlaps(start, end)` | preklapa li se s rasponom, oba kraja uključiva |
| `approve(actor, comment, now)` | `APPROVED`; upisuje `decisionComment`, `decidedBy`, `decidedAt` |
| `reject(actor, comment, now)` | `REJECTED`; isto |
| `cancel(actor, reason, now)` | `CANCELLED`; upisuje `cancelReason`, `cancelledBy`, `cancelledAt` |

- `approve`, `reject` i `cancel` provjeravaju samo prijelaz iz tablice `Status`. Nedopušten prijelaz baca `InvalidStatusTransitionException` (servis je kasnije pretvara u 409).
- Domena ne provjerava ovlasti, obavezan komentar (R5) ni obavezan razlog (D3); to su servisi u T5 i T6.
- Vrijeme (`now`, `createdAt`) dolazi izvana, da testovi ne ovise o satu.

### Repozitoriji (Spring Data JPA)

| Repozitorij | Metode osim standardnih (`findById`, `save`, `findAll`) |
| --- | --- |
| `UserRepository` | `findByUsername(username)` vraća `Optional<User>` |
| `TeamRepository` | nema |
| `TimeOffRequestRepository` | `findByOwner(user)` vraća `List<TimeOffRequest>` |

Ostali upiti dodaju se u taskovima koji ih trebaju.

### Što testovi mogu provjeriti

- Seed: 8 korisnika, 2 tima, voditelji, članstva, Eva kao admin bez tima, datumi zaposlenja i da hash odgovara lozinki `password`.
- Tablica `Status`: svaki red i svaki nedopušten prijelaz.
- `approve`, `reject`, `cancel`: novo stanje, upisana polja, iznimka iz završnog stanja.
- `overlaps`: dodir na rubu je preklapanje.
- Baza odbija `end_date < start_date`, nepoznat `type` i `status`, zahtjev bez vlasnika.

## T2 Login (JWT)

Slojevi: 2 Security, 3 API (arhitektura). Paketi: `com.example.timeoff.security` za token i filter, `com.example.timeoff.api` za controllere i greške.

### Endpointi

| Metoda i put | Prijava | Tijelo | Odgovor |
| --- | --- | --- | --- |
| `POST /api/auth/login` | ne treba | `{ "username": "ana", "password": "password" }` | 200 `{ "token": "<JWT>", "user": <Me> }` |
| `GET /api/me` | `Authorization: Bearer <JWT>` | nema | 200 `<Me>` |

`<Me>`: `{ "id": 1, "username": "ana", "name": "Ana", "admin": false, "teamLead": true }`. `teamLead` je `true` ako korisnik vodi barem jedan tim (Q2: korisnik može imati više uloga).

Sve ostale rute pod `/api/**` traže token. Bez prijave je dostupan još samo `GET /actuator/health`.

### Greške

Sve greške su `application/problem+json` (D11: format grešaka `application/problem+json`), s poljima `status`, `title` i `detail`.

| Slučaj | Status | `detail` |
| --- | --- | --- |
| Login bez `username` ili `password`, ili prazan | 400 | koje polje nedostaje |
| Login s nepostojećim korisnikom ili krivom lozinkom | 401 | `Invalid username or password` (isto za oba slučaja) |
| Ruta pod `/api/**` bez zaglavlja `Authorization` | 401 | `Authentication required` |
| Token s krivim potpisom, istekao ili neispravan | 401 | `Invalid or expired token` |
| Token ispravan, ali korisnik iz `sub` više ne postoji u bazi | 401 | `Invalid or expired token` |

### Token

| Svojstvo | Vrijednost |
| --- | --- |
| Algoritam | HS256 |
| Ključ | svojstvo `app.jwt.secret` (varijabla `JWT_SECRET`), najmanje 32 bajta; za lokalni rad i testove ima default u `application.properties`. HMAC ključ su bajtovi tog teksta u UTF-8, bez Base64 dekodiranja |
| Trajanje | svojstvo `app.jwt.ttl`, default `PT8H` |
| Claimovi | `sub` = id korisnika (kao tekst), `username`, `iat`, `exp` |

- Token nosi samo tko si. Uloge i timovi se čitaju iz baze kod svakog poziva, pa izmjena izravno u bazi (D6: korekcije se rade izravno u bazi) vrijedi odmah, bez nove prijave.
- Odjava ne postoji na serveru: frontend zaboravi token.
- Lozinka se provjerava s `BCryptPasswordEncoder` (D16: lozinka `password` za sve).

### Za sljedeće taskove

- Controller id trenutnog korisnika (`sub` iz tokena) dobiva preko komponente `CurrentUser` s metodom `id()`, koja vraća `Long`. Servis prima taj id i sam učita `User` iz `UserRepository` u svojoj transakciji (L12, točka 1). Servisi ne primaju token.
- Iznimke servisa API pretvara u statuse iz specifikacije (odjeljak 8): `InvalidStatusTransitionException` u 409. Ostale iznimke dodaju taskovi koji ih trebaju.

### Što testovi mogu provjeriti

- Login za svakog od 8 seedanih korisnika s `password` vraća 200, token i ispravan `<Me>` (Ana i Luka `teamLead: true`, Eva `admin: true`, ostali oboje `false`).
- Login s krivom lozinkom i s nepostojećim korisnikom: 401, isti `detail`.
- Login bez polja ili s praznim poljem: 400.
- `GET /api/me` s tokenom iz logina vraća tog korisnika.
- `GET /api/me` bez tokena, s tokenom krivog potpisa i s isteklim tokenom: 401, `application/problem+json`. Testovi mogu sami potpisati token ključem iz `app.jwt.secret` (npr. Nimbus `MACSigner`, već je na classpathu preko `spring-boot-starter-security-oauth2-resource-server`).
- Token jednog korisnika ne daje identitet drugoga: Ivanov token na `GET /api/me` vraća Ivana.

## T3 Radni dani i saldo

Slojevi: 3 API, 4 Servisi (arhitektura). Paket: `com.example.timeoff.service` za servise; controller u `com.example.timeoff.api`.

### Radni dani

`WorkingDays` (Spring komponenta):

| Metoda | Vraća | Pravilo |
| --- | --- | --- |
| `isWorkingDay(LocalDate date)` | `boolean` | ponedjeljak–petak su radni dani, subota i nedjelja nisu (R10: broje se samo radni dani) |
| `count(LocalDate start, LocalDate end)` | `int` | broj radnih dana od `start` do `end`, oba uključiva; 0 ako u rasponu nema radnog dana |

- Blagdani se ne preskaču (D1: blagdani su proširenje). Kad se dodaju, mijenja se samo `isWorkingDay`.
- `count` ne provjerava redoslijed datuma. Da `end` nije prije `start` (R8), provjerava kreiranje zahtjeva u T4.

### Saldo

`BalanceService` (Spring servis):

| Metoda | Vraća | Pravilo |
| --- | --- | --- |
| `entitlement(LocalDate hireDate, int year)` | `int` | 0 za godine prije godine zaposlenja; u godini zaposlenja 20 × broj mjeseci / 12, zaokruženo prema gore, mjeseci od mjeseca zaposlenja do prosinca uključivo (Q15, D5); inače 20 (R11) |
| `balance(Long userId, int year)` | `Balance` | saldo korisnika za tu godinu; učita korisnika i njegove zahtjeve u svojoj transakciji |

`Balance` (record u paketu `com.example.timeoff.service`): `year`, `entitlement`, `used`, `pending`, `remaining`, svi `int`, tim redom u konstruktoru.

| Polje | Značenje |
| --- | --- |
| `entitlement` | dani koje korisnik dobiva te godine, iz `entitlement(hireDate, year)` |
| `used` | zbroj radnih dana odobrenih (`APPROVED`) zahtjeva tipa `VACATION` te godine |
| `pending` | zbroj radnih dana zahtjeva tipa `VACATION` na čekanju (`PENDING`) te godine |
| `remaining` | `entitlement - used - pending` (R13: zahtjevi na čekanju troše saldo) |

- Zahtjev pripada godini svog početnog datuma; zahtjev je unutar jedne godine (specifikacija, odjeljak 5).
- `SICK_LEAVE` i `DAY_OFF` ne ulaze u saldo (R14). `REJECTED` i `CANCELLED` ne ulaze ni u `used` ni u `pending`.
- `remaining` može biti negativan ako je stanje izmijenjeno izravno u bazi (D6); ne zaokružuje se na 0.
- Provjeru "zahtjev ne smije premašiti `remaining`" (R13) radi kreiranje zahtjeva u T4.

### Današnji datum

Tekuća godina i "danas" čitaju se iz Spring beana `java.time.Clock`, u zoni iz `TZ` (D8: vremenska zona Europe/Zagreb). Datum se računa u zoni sata (`LocalDate.now(clock)`), ne u zoni računala: 31. 12. u 23:30 UTC je u Zagrebu već 1. 1., pa je tekuća godina sljedeća. Testovi ga mogu zamijeniti fiksnim satom (npr. `Clock.fixed(...)` u `@TestConfiguration` s `@Primary`). Isti sat koriste T4–T7 (npr. je li zahtjev počeo, Q23).

### Endpoint

| Metoda i put | Prijava | Odgovor |
| --- | --- | --- |
| `GET /api/me/balance` | `Authorization: Bearer <JWT>` | 200 `{ "year": 2026, "entitlement": 20, "used": 0, "pending": 0, "remaining": 20 }` |

- Vraća saldo prijavljenog korisnika za tekuću godinu (R12: korisnik vidi iskorištene, na čekanju i preostale dane tekuće godine).
- Bez tokena: 401, kao u T2.
- Saldo drugih korisnika (voditelj za članove svojih timova, admin za sve, Q16, Q17) dolazi u T7 (Vidljivost).

### Što testovi mogu provjeriti

- `WorkingDays.count`: tjedan od ponedjeljka do petka je 5; od petka do ponedjeljka je 2; samo subota i nedjelja je 0; jedan radni dan je 1.
- `entitlement`: zaposlen 2020-01-01, godina 2026: 20; Tom (2026-06-01), godina 2026: 12; zaposlen u siječnju te godine: 20; u prosincu: 2 (20 × 1 / 12 = 1,67); godina prije zaposlenja: 0.
- `balance`: odobreni i godišnji na čekanju se zbrajaju po radnim danima; bolovanje, slobodni dan, odbijeni i otkazani se ne broje; zahtjev iz druge godine se ne broji. Zahtjeve test sprema sam (repozitorij ili SQL), jer seed nema zahtjeva (Q55).
- `GET /api/me/balance` s fiksnim satom u 2026.: Tom dobiva `entitlement` 12, Ivan 20; bez tokena 401.

## T4 Kreiranje zahtjeva i validacija

Slojevi: 3 API, 4 Servisi (arhitektura). Paketi: `com.example.timeoff.service` za servis i iznimke, `com.example.timeoff.api` za controller.

### Endpoint

| Metoda i put | Prijava | Tijelo | Odgovor |
| --- | --- | --- | --- |
| `POST /api/requests` | `Authorization: Bearer <JWT>` | `{ "type": "VACATION", "startDate": "2026-11-02", "endDate": "2026-11-06", "reason": "Trip" }` | 201 `<Request>` |

- Vlasnik je uvijek prijavljeni korisnik; tijelo nema polje za vlasnika (Q29: zahtjev podnosi samo vlasnik). Isto vrijedi za voditelje i admine.
- `type`: `VACATION`, `SICK_LEAVE` ili `DAY_OFF`. Datumi u obliku `yyyy-MM-dd`. `reason` nije obavezan (brief); prazan ili samo razmaci sprema se kao `null`.
- Novi zahtjev je `PENDING`, `createdAt` je trenutak iz `Clock` (T3, Današnji datum).

`<Request>`: `{ "id": 1, "ownerId": 2, "type": "VACATION", "startDate": "2026-11-02", "endDate": "2026-11-06", "reason": "Trip", "status": "PENDING", "createdAt": "2026-10-30T09:00:00Z" }`. Polja odluke i otkazivanja dodaju T5 i T6.

### Validacija

Provjere idu ovim redom; prva koja padne određuje odgovor. Sve greške su `application/problem+json`, kao u T2.

| # | Provjera | Status | `detail` |
| --- | --- | --- | --- |
| 1 | Nedostaje `type`, `startDate` ili `endDate`; nepoznat `type`; datum nije datum; tijelo nije JSON | 400 | koje polje nije ispravno |
| 2 | `reason` dulji od 500 znakova | 400 | `reason size must be between 0 and 500` |
| 3 | `endDate` prije `startDate` (R8: završni datum nije prije početnog) | 400 | `End date must not be before start date` |
| 4 | `startDate` i `endDate` u različitim godinama (specifikacija 5: zahtjev je unutar jedne kalendarske godine) | 400 | `Request must be within one calendar year` |
| 5 | Nema nijednog radnog dana, `WorkingDays.count` = 0 (R10: zahtjev bez radnog dana je nevaljan) | 400 | `Request must contain at least one working day` |
| 6 | `startDate` prije `hireDate` korisnika (D4: zahtjev ne počinje prije datuma zaposlenja) | 409 | `Request must not start before hire date` |
| 7 | Preklapa se s korisnikovim zahtjevom u stanju `PENDING` ili `APPROVED`, bilo kojeg tipa; dodir na rubu je preklapanje (R9: nema preklapanja) | 409 | `Request overlaps an existing pending or approved request` |
| 8 | Samo `VACATION`: radni dani zahtjeva > `remaining` iz `BalanceService.balance(userId, godina startDate)` (R13: ne smije premašiti preostali saldo) | 409 | `Not enough vacation days: requested X, remaining Y` |

- Pravilo za 400 i 409: 400 ako je tijelo neispravno samo po sebi (provjere 1–5); 409 ako se sudara s podacima u bazi (provjere 6–8) (specifikacija 8).
- `REJECTED` i `CANCELLED` zahtjevi ne smetaju preklapanju (R9).
- Zahtjev koji troši točno preostali saldo je dopušten. `SICK_LEAVE` i `DAY_OFF` ne gledaju saldo (R14).
- Datumi u prošlosti i budućnosti su dopušteni, bez granice (Q19–Q22, Q25).

### Servis

`RequestService` (Spring servis):

| Metoda | Vraća | Radi |
| --- | --- | --- |
| `create(Long userId, NewRequest command)` | `TimeOffRequest` | učita korisnika, provjeri 3–8, spremi; sve u jednoj transakciji |

`NewRequest` (record u `com.example.timeoff.service`): `type` (`TimeOffType`), `startDate`, `endDate` (`LocalDate`), `reason` (`String`), tim redom.

Iznimke (paket `com.example.timeoff.service`), API ih pretvara u statuse:

| Iznimka | Status |
| --- | --- |
| `InvalidRequestException` | 400 |
| `RuleViolationException` | 409 |

- Provjere 1 i 2 radi API (oblik ulaza); 3–8 servis (arhitektura: je li ulaz dopušten, odlučuje servis).
- Svaka provjera je mala funkcija na jednom mjestu (pravila kodiranja, Načelo 5); blagdani i zahtjev preko granice godine (proširenja) mijenjali bi samo provjere 4 i 5 i `WorkingDays`.
- Popis vlastitih zahtjeva i dohvat jednog zahtjeva dolaze u T7 (Vidljivost).

### Što testovi mogu provjeriti

- Ispravan zahtjev svakog tipa: 201, `status` `PENDING`, `ownerId` prijavljenog korisnika, spremljen u bazi; `reason` se smije izostaviti.
- Bez tokena: 401.
- Svaka provjera 1–8 sa svojim statusom; zahtjev se tada ne sprema.
- R8: `endDate` = `startDate` na radni dan je dopušten (1 dan).
- R10: samo subota i nedjelja: 400.
- D4: Tom (zaposlen 2026-06-01) od 2026-05-29: 409; od 2026-06-01: 201.
- R9: preklapanje s `PENDING` i `APPROVED`, i s drugim tipom: 409; s `REJECTED` i `CANCELLED`: 201; dodir na rubu: 409; preklapanje s tuđim zahtjevom: 201.
- R13: s fiksnim satom u 2026. Tom (12 dana) traži 12 radnih dana: 201; 13: 409. Godišnji na čekanju troši saldo: drugi zahtjev preko ostatka: 409. `SICK_LEAVE` i `DAY_OFF` preko salda: 201.
- Saldo se računa za godinu `startDate`, ne tekuću: godišnji u 2027. troši saldo 2027.
- Datum u prošlosti (npr. 2026-01-05) je dopušten.
- Postojeće zahtjeve test sprema sam (repozitorij ili SQL), kao u T3.
