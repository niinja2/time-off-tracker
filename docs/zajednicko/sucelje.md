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

## T5 Odobravanje i odbijanje

Slojevi: 3 API, 4 Servisi (arhitektura). Paketi: `com.example.timeoff.service` za servis, pravilo ovlasti i iznimke, `com.example.timeoff.api` za controller.

### Endpointi

| Metoda i put | Prijava | Tijelo | Odgovor |
| --- | --- | --- | --- |
| `POST /api/requests/{id}/approve` | `Authorization: Bearer <JWT>` | `{ "comment": "ok" }`; tijelo i `comment` nisu obavezni | 200 `<Request>` |
| `POST /api/requests/{id}/reject` | `Authorization: Bearer <JWT>` | `{ "comment": "Busy week" }`; `comment` obavezan | 200 `<Request>` |
| `GET /api/approvals` | `Authorization: Bearer <JWT>` | nema | 200 `[<Request>, ...]` |

- `GET /api/approvals`: zahtjevi na čekanju koje prijavljeni korisnik smije odobriti (Ovlasti, ispod), poredani po `startDate`, pa po `id`. Za ekran Approvals (specifikacija 12). Tko ne smije odobriti ništa, dobiva praznu listu, ne 403.
- Odluka bilježi tko je odlučio (`decidedBy` = prijavljeni korisnik, Q46) i kada (`decidedAt` iz `Clock`, D13).
- Prazan `comment` ili samo razmaci kod odobrenja sprema se kao `null`, kao `reason` u T4.

`<Request>` iz T4 dobiva polja: `ownerName`, `decisionComment`, `decidedById`, `decidedAt`. Dok zahtjev nije odlučen, zadnja tri su `null`.

`{ "id": 1, "ownerId": 2, "ownerName": "Ivan", "type": "VACATION", "startDate": "2026-11-02", "endDate": "2026-11-06", "reason": "Trip", "status": "APPROVED", "createdAt": "2026-10-30T09:00:00Z", "decisionComment": "ok", "decidedById": 1, "decidedAt": "2026-10-30T10:00:00Z" }`

### Ovlasti

Tko smije odobriti ili odbiti zahtjev. Pravila se gledaju redom, prvo koje odgovara odlučuje:

| # | Uvjet | Smije | Izvor |
| --- | --- | --- | --- |
| 1 | odlučuje admin | da, bilo koji zahtjev, i vlastiti, bilo kojeg tipa | R2, Q1.1, D15 |
| 2 | odlučuje vlasnik zahtjeva | ne | R3 |
| 3 | vlasnik je voditelj (vodi barem jedan tim) | ne; ide adminu, i kad je član tuđeg tima | R3, D2 |
| 4 | odlučuje voditelj tima u kojem je vlasnik član | da; dovoljan je bilo koji od tih timova | R1, Q3, Q5.1 |
| 5 | inače | ne; npr. vlasnik bez tima ide adminu | Q4 |

`ApprovalPolicy` (Spring komponenta): `canDecide(User actor, TimeOffRequest request)` vraća `boolean` po ovoj tablici. Isto pravilo koristi T6 za otkazivanje (D14: tko smije odobriti, smije i otkazati).

### Provjere

Redom; prva koja padne određuje odgovor. Greške su `application/problem+json`.

| # | Provjera | Status | `detail` |
| --- | --- | --- | --- |
| 1 | `id` nije broj; tijelo nije JSON; `comment` dulji od 500 znakova | 400 | koje polje nije ispravno |
| 2 | Odbijanje bez `comment`, s praznim ili samo s razmacima (R5: odbijanje traži komentar) | 400 | `comment must not be blank` |
| 3 | Zahtjev s tim `id` ne postoji | 404 | `Request not found` |
| 4 | Korisnik ne smije odlučiti (Ovlasti) | 403 | `You cannot approve or reject this request` |
| 5 | Zahtjev nije `PENDING` (R4: odobrava se i odbija samo zahtjev na čekanju) | 409 | `Request cannot change from X to Y` (iz `InvalidStatusTransitionException`, T1) |

- Provjere 1 i 2 radi API (oblik ulaza); 3–5 servis.
- Datumi zahtjeva se ne gledaju: zahtjev na čekanju smije se odobriti i kad su mu datumi prošli (Q35), i ne istječe (Q36).
- Kod odobrenja se preklapanje i saldo ne provjeravaju ponovo: zahtjev na čekanju ih je prošao pri kreiranju i već troši saldo (R9, R13).

### Servis

`ApprovalService` (Spring servis), svaka metoda u svojoj transakciji:

| Metoda | Vraća | Radi |
| --- | --- | --- |
| `approve(Long requestId, Long actorId, String comment)` | `TimeOffRequest` | provjere 3–5, pa `request.approve(actor, comment, now)` |
| `reject(Long requestId, Long actorId, String comment)` | `TimeOffRequest` | provjere 3–5, pa `request.reject(actor, comment, now)` |
| `pending(Long actorId)` | `List<TimeOffRequest>` | zahtjevi `PENDING` za koje `canDecide` vraća `true`, poredani kao gore |

Nove iznimke (paket `com.example.timeoff.service`), API ih pretvara u statuse:

| Iznimka | Status |
| --- | --- |
| `NotFoundException` | 404 |
| `ForbiddenException` | 403 |

### Što testovi mogu provjeriti

Seed (T1): Ana vodi Platform (Ivan, Marko, Petra), Luka vodi Mobile (Sara, Tom), Eva je admin bez tima.

- R1: Ana odobrava i odbija Ivanov zahtjev: 200, `status`, `decidedById` Ane, `decidedAt` iz sata, `decisionComment`.
- R1: Luka ne smije Ivanov zahtjev: 403. Ivan ne smije Markov: 403.
- R2: Eva odobrava Ivanov i Sarin zahtjev: 200.
- R3: Ivan ne smije svoj zahtjev: 403. Ana ne smije svoj: 403.
- R3, D2: Anin zahtjev Luka ne smije: 403; Eva smije: 200.
- Q1.1, D15: Eva odobrava svoj zahtjev, i godišnji i bolovanje: 200.
- Q4: zahtjev korisnika bez tima (osim Eve test može napraviti korisnika bez tima SQL-om) smije samo admin.
- R4: odobrenje ili odbijanje zahtjeva koji je `APPROVED`, `REJECTED` ili `CANCELLED`: 409.
- R5: odbijanje bez `comment` ili s praznim: 400; odobrenje bez tijela: 200, `decisionComment` `null`.
- Q35: zahtjev na čekanju iz prošlosti se smije odobriti: 200.
- Nepostojeći `id`: 404. Bez tokena: 401.
- Redoslijed: Luka odbija nepostojeći zahtjev bez komentara: 400 (provjera 2 prije 3); Luka odobrava Ivanov već odobreni zahtjev: 403 (ovlasti prije stanja).
- `GET /api/approvals`: Ana vidi zahtjeve na čekanju Ivana, Marka i Petre, ne svoje, ne Sarine, ne odlučene; Eva vidi sve na čekanju, i svoje; Ivan dobiva praznu listu.
- Postojeće zahtjeve test sprema sam (repozitorij ili SQL), kao u T3.

## T6 Otkazivanje

Slojevi: 3 API, 4 Servisi (arhitektura). Paketi: `com.example.timeoff.service` za servis, `com.example.timeoff.api` za controller.

### Endpoint

| Metoda i put | Prijava | Tijelo | Odgovor |
| --- | --- | --- | --- |
| `POST /api/requests/{id}/cancel` | `Authorization: Bearer <JWT>` | `{ "reason": "Plans changed" }`; tijelo i `reason` nisu obavezni za vlasnika | 200 `<Request>` |

- Otkazivanje bilježi tko je otkazao (`cancelledBy` = prijavljeni korisnik, Q30, Q31) i kada (`cancelledAt` iz `Clock`, D13).
- Prazan `reason` ili samo razmaci sprema se kao `null`, kao u T4 i T5.

`<Request>` iz T5 dobiva polja: `cancelReason`, `cancelledById`, `cancelledAt`. Dok zahtjev nije otkazan, sva tri su `null`.

`{ "id": 1, "ownerId": 2, "ownerName": "Ivan", "type": "VACATION", "startDate": "2026-11-02", "endDate": "2026-11-06", "reason": "Trip", "status": "CANCELLED", "createdAt": "2026-10-30T09:00:00Z", "decisionComment": "ok", "decidedById": 1, "decidedAt": "2026-10-30T10:00:00Z", "cancelReason": null, "cancelledById": 2, "cancelledAt": "2026-10-31T08:00:00Z" }`

### Ovlasti

Pravila se gledaju redom, prvo koje odgovara odlučuje (specifikacija 4):

| # | Uvjet | Smije | Razlog | Izvor |
| --- | --- | --- | --- | --- |
| 1 | otkazuje vlasnik zahtjeva (i admin svoj) | da | opcionalan | R6, R7 |
| 2 | `ApprovalPolicy.canDecide` (T5) vraća `true` | da | obavezan | Q30, Q31, Q32, D3, D14 |
| 3 | inače | ne | | D14 |

`CancelPolicy` (Spring komponenta): `canCancel(User actor, TimeOffRequest request)` i `reasonRequired(User actor, TimeOffRequest request)` vraćaju `boolean` po ovoj tablici. Red 2 zove `ApprovalPolicy`, ne ponavlja njegova pravila.

### Provjere

Redom; prva koja padne određuje odgovor. Greške su `application/problem+json`.

| # | Provjera | Status | `detail` |
| --- | --- | --- | --- |
| 1 | `id` nije broj; tijelo nije JSON; `reason` dulji od 500 znakova | 400 | koje polje nije ispravno |
| 2 | Zahtjev s tim `id` ne postoji | 404 | `Request not found` |
| 3 | Korisnik ne smije otkazati (Ovlasti) | 403 | `You cannot cancel this request` |
| 4 | Otkazuje netko tko nije vlasnik, bez `reason`, s praznim ili samo s razmacima (D3) | 400 | `reason must not be blank` |
| 5 | Zahtjev je `APPROVED` i počeo je: `startDate` je danas ili ranije (R7, Q23, Q30.1, Q31.1) | 409 | `Request has already started` |
| 6 | Zahtjev je `REJECTED` ili `CANCELLED` | 409 | `Request cannot change from X to Y` (iz `InvalidStatusTransitionException`, T1) |

- Provjera 1 je API (oblik ulaza); 2–6 servis. Provjera 4 je u servisu jer ovisi o tome tko otkazuje.
- "Danas" je `LocalDate.now(clock)` (T3).
- Zahtjev na čekanju smije se otkazati i kad su mu datumi prošli: specifikacija 4 "počeo" gleda samo kod odobrenih.

### Servis

`CancelService` (Spring servis), u transakciji:

| Metoda | Vraća | Radi |
| --- | --- | --- |
| `cancel(Long requestId, Long actorId, String reason)` | `TimeOffRequest` | provjere 2–6, pa `request.cancel(actor, reason, now)` |

Iznimke su postojeće: `NotFoundException` (404), `ForbiddenException` (403), `InvalidRequestException` (400), `RuleViolationException` (409), `InvalidStatusTransitionException` (409).

### Što testovi mogu provjeriti

Seed (T1): Ana vodi Platform (Ivan, Marko, Petra), Luka vodi Mobile (Sara, Tom), Eva je admin bez tima.

- R6: Ivan otkazuje svoj zahtjev na čekanju bez tijela: 200, `CANCELLED`, `cancelledById` Ivana, `cancelledAt` iz sata, `cancelReason` `null`.
- R7: Ivan otkazuje svoj odobreni zahtjev koji počinje sutra: 200. Koji počinje danas ili je počeo: 409.
- Q30, D3: Ana otkazuje Ivanov zahtjev s razlogom: 200, `cancelReason`; bez razloga ili s praznim: 400.
- Q31, Q32: Eva otkazuje Sarin odobreni zahtjev koji nije počeo, s razlogom: 200.
- Q30.1, Q31.1: Ana ili Eva otkazuju Ivanov odobreni zahtjev koji je počeo: 409.
- Eva otkazuje svoj zahtjev bez razloga: 200 (vlasnik, red 1).
- D14: Luka ne smije Ivanov zahtjev: 403. Marko ne smije Ivanov: 403. Anin zahtjev Luka ne smije: 403.
- Odbijeni ili otkazani zahtjev: 409.
- Zahtjev na čekanju iz prošlosti vlasnik smije otkazati: 200.
- Saldo: nakon otkazivanja godišnji više ne ulazi u `used` ni `pending` (T3).
- Nepostojeći `id`: 404. Bez tokena: 401.
- Redoslijed: Luka otkazuje Ivanov zahtjev bez razloga: 403 (ovlasti prije razloga); Ana otkazuje Ivanov odbijeni zahtjev bez razloga: 400 (razlog prije stanja).
- Postojeće zahtjeve test sprema sam (repozitorij ili SQL), kao u T3.

## T7 Vidljivost i kalendar

Slojevi: 3 API, 4 Servisi (arhitektura). Paketi: `com.example.timeoff.service` za servise, pravilo vidljivosti i zapise, `com.example.timeoff.api` za controllere.

### Endpointi

| Metoda i put | Prijava | Parametri | Odgovor | Ekran (specifikacija 12) |
| --- | --- | --- | --- | --- |
| `GET /api/me/requests` | `Authorization: Bearer <JWT>` | nema | 200 `[<Request>, ...]` | My time off |
| `GET /api/requests` | `Authorization: Bearer <JWT>` | nema | 200 `[<Request>, ...]` | nema; API za pravila vidljivosti (specifikacija 7; brief: API se zove izravno kao razni korisnici) |
| `GET /api/requests/{id}` | `Authorization: Bearer <JWT>` | nema | 200 `<Request>` | nema; isto |
| `GET /api/users/{id}/balance` | `Authorization: Bearer <JWT>` | nema | 200 `<Balance>` iz T3 | nema; saldo drugih (Q16, Q17), najavljen u T3 |
| `GET /api/calendar` | `Authorization: Bearer <JWT>` | `month` obavezan, `yyyy-MM`; `teamId` nije obavezan | 200 `[<CalendarEntry>, ...]` | Team calendar |
| `GET /api/teams` | `Authorization: Bearer <JWT>` | nema | 200 `[<Team>, ...]` | Team calendar, filter po timu (Q43, D12) |

- `GET /api/me/requests`: svi zahtjevi prijavljenog korisnika, u svim stanjima, puni `<Request>` iz T6 (Q44–Q46: zaposlenik vidi svoje zahtjeve sa svim detaljima). Poredani po `startDate` silazno, pa po `id` silazno (najnoviji prvi).
- `GET /api/requests`: svi zahtjevi, u svim stanjima, čiji je vlasnik za prijavljenog korisnika `FULL` (Vidljivost, ispod). Puni `<Request>`, poredani kao `GET /api/me/requests`. Zaposlenik dobiva samo svoje.
- `GET /api/requests/{id}`: jedan zahtjev, ako je vlasnik za prijavljenog korisnika `FULL`.
- `GET /api/users/{id}/balance`: saldo korisnika `id` za tekuću godinu (T3, Današnji datum), ako je taj korisnik za prijavljenog `FULL`. Oblik kao `GET /api/me/balance`.
- `GET /api/calendar`: zahtjevi `PENDING` i `APPROVED` čiji raspon ima barem jedan dan u mjesecu `month` (oba kraja uključiva), po pravilima kalendara (Vidljivost). Svaki zahtjev je jedan unos s cijelim rasponom datuma, i kad počinje prije ili završava poslije mjeseca. Poredani po `startDate`, pa po `name`. Grupiranje po danima radi frontend.
- `teamId`: ostaju samo unosi čiji je vlasnik član tog tima; vidljivost se ne mijenja. Tim u kojem prijavljeni ne vidi nikoga, ili tim koji ne postoji, daje praznu listu.
- `GET /api/teams`: admin dobiva sve timove; ostali timove u kojima su član (voditelj je član timova koje vodi, Q3). Poredani po `name`.

`<Request>` iz T6 dobiva polja `decidedByName` i `cancelledByName`: ime (`name`) korisnika iz `decidedById` i `cancelledById`, `null` dok zahtjev nije odlučen, odnosno otkazan (L32; specifikacija 7: zaposlenik vidi tko je odlučio i tko je otkazao, Q46, Q30, Q31). Vrijedi za svaki odgovor koji vraća `<Request>` (T4–T7).

`{ "id": 1, "ownerId": 2, "ownerName": "Ivan", "type": "VACATION", "startDate": "2026-11-02", "endDate": "2026-11-06", "reason": "Trip", "status": "CANCELLED", "createdAt": "2026-10-30T09:00:00Z", "decisionComment": "ok", "decidedById": 1, "decidedByName": "Ana", "decidedAt": "2026-10-30T10:00:00Z", "cancelReason": "Reorg", "cancelledById": 8, "cancelledByName": "Eva", "cancelledAt": "2026-10-31T08:00:00Z" }`

`<CalendarEntry>`, dva oblika:

| Pristup | Polja | Primjer |
| --- | --- | --- |
| `FULL` | `name`, `startDate`, `endDate`, `type`, `status` | `{ "name": "Ivan", "startDate": "2026-11-02", "endDate": "2026-11-06", "type": "VACATION", "status": "PENDING" }` |
| `LIMITED` | samo `name`, `startDate`, `endDate`; ključevi `type` i `status` ne postoje u JSON-u | `{ "name": "Marko", "startDate": "2026-11-09", "endDate": "2026-11-10" }` |

- Unos nema `reason`, `id` zahtjeva ni id korisnika (brief: kolege vide samo ime i datume; Q42 za voditelja kaže samo "s tipom"). Detalje zahtjeva s pristupom `FULL` korisnik čita preko `GET /api/requests`.

`<Team>`: `{ "id": 1, "name": "Platform" }`.

### Vidljivost

Koliko prijavljeni korisnik (`viewer`) vidi o zahtjevima vlasnika (`owner`). Pravila se gledaju redom, prvo koje odgovara odlučuje:

| # | Uvjet | Pristup | Izvor |
| --- | --- | --- | --- |
| 1 | `viewer` je `owner` | `FULL` | brief, Q41, Q44–Q46 |
| 2 | `viewer` je admin | `FULL` | brief, Q17, Q43 |
| 3 | `viewer` vodi tim u kojem je `owner` član | `FULL` | brief, Q16, Q42 |
| 4 | `viewer` i `owner` su članovi istog tima | `LIMITED` | brief, Q47 |
| 5 | inače, npr. `owner` bez tima | `NONE` | specifikacija 7 |

Što pristup znači:

| Pristup | Zahtjevi s detaljima (`GET /api/requests`, `/api/requests/{id}`) | Saldo (`GET /api/users/{id}/balance`) | Kalendar |
| --- | --- | --- | --- |
| `FULL` | svi, u svim stanjima | da | `PENDING` i `APPROVED`, oblik `FULL` |
| `LIMITED` | ne | ne | samo `APPROVED`, oblik `LIMITED` |
| `NONE` | ne | ne | ništa |

- `REJECTED` i `CANCELLED` nikad nisu na kalendaru (Q48: odbijeni i otkazani se ne prikazuju nikome).
- Red 3 vrijedi i kad je `owner` voditelj koji je član tuđeg tima: voditelj tog tima vidi njegove zahtjeve, iako ih ne smije odobriti. D2 (zahtjev voditelja ide adminu) je pravilo odobravanja, ne vidljivosti; specifikacija 7 kaže "sve zahtjeve članova timova koje vodi".
- Kad `viewer` i `owner` dijele više timova, vrijedi prvi red koji odgovara, dakle najviši pristup.

`VisibilityPolicy` (Spring komponenta): `access(User viewer, User owner)` vraća `Access` (enum u `com.example.timeoff.service`: `FULL`, `LIMITED`, `NONE`) po ovoj tablici. Jedino mjesto za pravilo vidljivosti; koriste ga svi endpointi T7.

### Provjere

Redom; prva koja padne određuje odgovor. Greške su `application/problem+json`.

| # | Provjera | Endpoint | Status | `detail` |
| --- | --- | --- | --- | --- |
| 1 | `id` ili `teamId` nije broj; `month` nedostaje ili nije `yyyy-MM` | svi s parametrom | 400 | koje polje nije ispravno |
| 2 | Zahtjev s tim `id` ne postoji | `GET /api/requests/{id}` | 404 | `Request not found` |
| 2 | Korisnik s tim `id` ne postoji | `GET /api/users/{id}/balance` | 404 | `User not found` |
| 3 | Pristup nije `FULL` | `GET /api/requests/{id}` | 403 | `You cannot view this request` |
| 3 | Pristup nije `FULL` | `GET /api/users/{id}/balance` | 403 | `You cannot view this balance` |

- Provjera 1 je API (oblik ulaza); 2 i 3 servis. Postojanje se gleda prije ovlasti, kao u T5 i T6.
- Popisi (`/api/me/requests`, `/api/requests`, `/api/calendar`, `/api/teams`) nikad ne daju 403: tko ne vidi ništa, dobiva praznu listu, kao `GET /api/approvals` u T5.
- Bez tokena: 401, kao u T2.

### Servis

`VisibilityService` (Spring servis), svaka metoda u svojoj transakciji, samo čitanje:

| Metoda | Vraća | Radi |
| --- | --- | --- |
| `mine(Long userId)` | `List<TimeOffRequest>` | zahtjevi korisnika, poredani kao gore |
| `visible(Long viewerId)` | `List<TimeOffRequest>` | zahtjevi čiji je vlasnik `FULL` |
| `get(Long requestId, Long viewerId)` | `TimeOffRequest` | provjere 2–3 |
| `balance(Long userId, Long viewerId)` | `Balance` | provjere 2–3, pa `BalanceService.balance(userId, tekuća godina)` |

`CalendarService` (Spring servis), svaka metoda u svojoj transakciji, samo čitanje:

| Metoda | Vraća | Radi |
| --- | --- | --- |
| `month(Long viewerId, YearMonth month, Long teamId)` | `List<CalendarEntry>` | unosi po tablici Vidljivost; `teamId` smije biti `null` (bez filtra) |
| `teams(Long viewerId)` | `List<Team>` | timovi kao `GET /api/teams` |

`CalendarEntry` (record u `com.example.timeoff.service`): `name` (`String`), `startDate`, `endDate` (`LocalDate`), `type` (`TimeOffType`), `status` (`Status`), tim redom. Za `LIMITED` su `type` i `status` `null`, a API ih ne piše u JSON.

- Filtriranje je u Javi, iz `findAll`, kao `GET /api/approvals` (L24, točka 2). Novi upiti u repozitorijima ne trebaju.
- Iznimke su postojeće: `NotFoundException` (404), `ForbiddenException` (403).

### Što testovi mogu provjeriti

Seed (T1): Ana vodi Platform (Ivan, Marko, Petra), Luka vodi Mobile (Sara, Tom), Eva je admin bez tima. Postojeće zahtjeve test sprema sam (repozitorij ili SQL), kao u T3.

- `GET /api/me/requests`: Ivan dobiva samo svoje, u svim stanjima, s `decisionComment`, `decidedById`, `cancelReason`, `cancelledById`; najnoviji `startDate` prvi; bez zahtjeva prazna lista.
- `GET /api/requests`: Ivan vidi samo svoje; Ana svoje te Ivanove, Markove i Petrine u svim stanjima, s `type` i `reason`, ne Sarine; Luka svoje, Sarine i Tomove, ne Anine; Eva sve.
- `GET /api/requests/{id}`: Ana na Ivanov odbijeni: 200; Ivan na Markov: 403; Luka na Ivanov: 403; Eva na bilo koji: 200; nepostojeći: 404; `id` nije broj: 400.
- `GET /api/users/{id}/balance`: Ivan za sebe: 200; Ana za Ivana: 200; Ana za Saru: 403; Ivan za Marka: 403; Luka za Anu: 403; Eva za Toma: 200, `entitlement` 12 (fiksni sat u 2026.); nepostojeći korisnik: 404.
- `GET /api/calendar?month=2026-11`:
  - Ivan: svoje `PENDING` i `APPROVED` s `type` i `status`; Markov `APPROVED` bez ključeva `type` i `status`; Markov `PENDING` ne; Anin `APPROVED` (Ana je članica Platforma) bez `type`; Sarin i Evin ne; `REJECTED` i `CANCELLED` ničiji.
  - Ana: Ivanov `PENDING` i `APPROVED` s `type`; Lukin ne.
  - Luka: Sarin i Tomov `PENDING` i `APPROVED` s `type`.
  - Eva: svi u tvrtki, i njezin (korisnica bez tima); s `teamId` Platforma samo Ana, Ivan, Marko, Petra.
  - Ivan s `teamId` Mobilea: prazna lista.
- Raspon: zahtjev 2026-10-29 do 2026-11-03 je i u `month=2026-10` i u `month=2026-11`, s cijelim rasponom; zahtjev u prosincu nije u studenom.
- `month` nedostaje ili je `2026-13`: 400. Bez tokena: 401.
- Više timova (test SQL-om doda Saru u Platform): Ivan vidi Sarin odobreni zahtjev jednom, `LIMITED`; Ana ga vidi `FULL`.
- Voditelj u tuđem timu (test SQL-om doda Anu u Mobile): Luka vidi Anine zahtjeve `FULL` u `GET /api/requests` i na kalendaru, a odobriti ih i dalje ne smije (T5, D2).
- `GET /api/teams`: Eva: Mobile, Platform; Ivan: Platform; Sara: Mobile.
- `decidedByName`, `cancelledByName` (L32): Ana odobri Ivanov zahtjev, Eva ga otkaže: `GET /api/me/requests` za Ivana daje `decidedByName` `Ana` i `cancelledByName` `Eva`; zahtjev na čekanju ima oba `null`; Ivan otkaže svoj: `cancelledByName` `Ivan`.

## T8 Frontend

Sloj: 1 Frontend (arhitektura: 1.1 UI, 1.2 Stanje, 1.3 API klijent). Next.js (App Router) i TypeScript u `frontend/`. Frontend ništa ne odlučuje: ne provjerava datume, salde ni ovlasti, nego šalje upit i prikaže odgovor ili `detail` greške (specifikacija 12). Sav tekst u UI-ju je na engleskom (Q54: sve na engleskom).

### Rute

| Ruta | Tko vidi | Ekran |
| --- | --- | --- |
| `/login` | svi, bez prijave | Login (D10: forma s korisničkim imenom i lozinkom) |
| `/` | svi | samo preusmjerava: s tokenom na `/my-time-off`, bez tokena na `/login` |
| `/my-time-off` | prijavljeni | My time off |
| `/approvals` | prijavljeni; poveznica u navigaciji samo za voditelje i admine (`teamLead` ili `admin` iz `<Me>`) | Approvals |
| `/calendar` | prijavljeni | Team calendar |

- Prijavljen znači: token postoji u `localStorage` (Stanje, ispod). Ruta za prijavljene bez tokena odmah preusmjeri na `/login`, bez poziva API-ja.
- Tko nije voditelj ni admin, a ručno otvori `/approvals`, vidi ekran s onim što vrati `GET /api/approvals`: praznu listu (T5). Frontend ne skriva ništa što API ne skriva (specifikacija 7).
- API se zove relativnim putem (`/api/...`); Next.js ga prosljeđuje backendu (`rewrites`, adresa backenda iz varijable `API_URL`, default `http://localhost:8080`). Zato backend ne treba CORS.

### Komponente

Svaki ekran je komponenta koja se može renderirati sama, bez stranice oko nje. Stranice u `src/app/` samo slažu `AppShell` i ekran. Uvoz preko aliasa `@/` (= `frontend/src/`).

| Komponenta | Datoteka | Radi |
| --- | --- | --- |
| `LoginScreen` | `src/components/LoginScreen.tsx` | forma za prijavu |
| `HomeRedirect` | `src/components/HomeRedirect.tsx` | ruta `/`: s tokenom `router.replace("/my-time-off")`, bez tokena `router.replace("/login")`; ne zove API, ne prikazuje ništa (L37) |
| `AppShell` | `src/components/AppShell.tsx` | omotač za rute prijavljenih: provjeri token, učita `<Me>` (`getMe`), prikaže navigaciju i `children` |
| `MyTimeOffScreen` | `src/components/MyTimeOffScreen.tsx` | My time off |
| `ApprovalsScreen` | `src/components/ApprovalsScreen.tsx` | Approvals |
| `CalendarScreen` | `src/components/CalendarScreen.tsx` | Team calendar |

Sve su imenovani izvozi (named export), bez propsa osim `children` kod `AppShell`. Navigacija koristi `useRouter()` iz `next/navigation` (`replace`), a poveznice `Link` iz `next/link`.

Prikaz vrijednosti, isti na svim ekranima:

| API | UI |
| --- | --- |
| `type` `VACATION`, `SICK_LEAVE`, `DAY_OFF` | `Vacation`, `Sick leave`, `Day off` |
| `status` `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED` | `Pending`, `Approved`, `Rejected`, `Cancelled` |
| datumi | kako ih API vrati, `yyyy-MM-dd` |

Greška: kad API vrati grešku, ekran prikaže njezin `detail` u elementu s `role="alert"`. Ostaje dok sljedeća akcija ne uspije. Na 401 ekran ne prikazuje grešku nego preusmjeri na `/login` (Stanje, ispod).

### Login (`LoginScreen`)

| Element | Oznaka za pronalaženje |
| --- | --- |
| naslov | `heading` `Sign in` |
| korisničko ime | `textbox` s labelom `Username` |
| lozinka | polje s labelom `Password` (`type="password"`) |
| gumb | `button` `Sign in` |
| greška | `alert`, npr. `Invalid username or password` |

- Klik na `Sign in` zove `login(username, password)` s upisanim vrijednostima, bez provjere u frontendu (prazno polje: backend vraća 400 i `detail` se prikaže).
- Uspjeh: token se spremi, `router.replace("/my-time-off")`.
- Greška (i 401 kod logina): `alert` s `detail`; nema preusmjeravanja.

### AppShell i navigacija

- Bez tokena: `router.replace("/login")`, ne zove API, ne prikazuje `children`.
- S tokenom: zove `getMe()`, pa prikaže `navigation` s imenom `Main` i `children`.

| Element | Oznaka |
| --- | --- |
| poveznica | `link` `My time off`, `href` `/my-time-off` |
| poveznica | `link` `Approvals`, `href` `/approvals`; samo ako je `teamLead` ili `admin` `true` |
| poveznica | `link` `Team calendar`, `href` `/calendar` |
| tko je prijavljen | tekst `Signed in as <name>`, npr. `Signed in as Ana` |
| odjava | `button` `Log out` |

### My time off (`MyTimeOffScreen`)

Pri otvaranju zove `getMyBalance()` i `getMyRequests()`. Naslov: `heading` razine 1 `My time off`.

**Saldo** (R12): `region` s imenom `Vacation balance <year>`, npr. `Vacation balance 2026` (to je i njegov naslov razine 2). Unutra parovi `term` / `definition`:

| `term` | `definition` |
| --- | --- |
| `Entitlement` | `entitlement` |
| `Used` | `used` |
| `Pending` | `pending` |
| `Remaining` | `remaining` |

**Novi zahtjev:** `form` s imenom `New request`.

| Polje | Oznaka | Vrijednosti |
| --- | --- | --- |
| tip | `combobox` s labelom `Type` | opcije `Vacation`, `Sick leave`, `Day off`; zadano `Vacation` |
| početak | polje s labelom `Start date` (`type="date"`) | `yyyy-MM-dd`, zadano prazno |
| kraj | polje s labelom `End date` (`type="date"`) | isto |
| razlog | `textbox` s labelom `Reason` | nije obavezan |
| gumb | `button` `Submit request` | |

- Klik zove `createRequest({ type, startDate, endDate, reason })` s točno upisanim vrijednostima (prazan razlog kao `""`).
- Uspjeh: forma se isprazni (tip natrag na `Vacation`), pa se ponovo zovu `getMyRequests()` i `getMyBalance()`.
- Greška (npr. `Request overlaps an existing pending or approved request`, `Not enough vacation days: requested 13, remaining 12`): `alert` s `detail`; upisane vrijednosti ostaju.

**Moji zahtjevi:** `table` s imenom `My requests`, redoslijed kako vrati API (najnoviji prvi).

| Stupac (`columnheader`) | Sadržaj |
| --- | --- |
| `Type` | tip |
| `Start date` | `startDate` |
| `End date` | `endDate` |
| `Reason` | `reason`, prazno ako `null` |
| `Status` | stanje, uz ime iz `decidedByName` ili `cancelledByName` (L32), vidi ispod |
| `Comment` | `decisionComment`, inače `cancelReason`, inače prazno |
| `Actions` | gumb `Cancel` samo za `Pending` i `Approved` |

- Tekst ćelije `Status` (L32; specifikacija 7: zaposlenik vidi tko je odlučio i tko je otkazao):

  | `status` | Tekst | Primjer |
  | --- | --- | --- |
  | `PENDING` | `Pending` | `Pending` |
  | `APPROVED` | `Approved by <decidedByName>` | `Approved by Ana` |
  | `REJECTED` | `Rejected by <decidedByName>` | `Rejected by Ana` |
  | `CANCELLED` | `Cancelled by <cancelledByName>` | `Cancelled by Eva`; i kad je otkazao sam vlasnik, npr. `Cancelled by Ivan` |

  Ako je ime `null` (npr. izmjena izravno u bazi, D6), piše samo stanje, npr. `Approved`.
- Bez zahtjeva: umjesto tablice tekst `No requests yet.`
- Klik na `Cancel` u retku otvori u tom retku `textbox` s labelom `Cancel reason` i gumbe `Confirm cancel` i `Keep request`. `Keep request` zatvori bez poziva.
- `Confirm cancel` zove `cancelRequest(id, reason)` (prazan razlog kao `""`). Uspjeh: ponovo `getMyRequests()` i `getMyBalance()`. Greška (npr. `Request has already started`, R7): `alert` s `detail`.
- Je li odobreni zahtjev počeo, frontend ne provjerava; gumb `Cancel` je i tada prikazan, a backend vrati 409 (L27, točka 12).

### Approvals (`ApprovalsScreen`)

Pri otvaranju zove `getApprovals()`. Naslov: `heading` razine 1 `Approvals`. Saldo podnositelja se ne prikazuje (L28).

`table` s imenom `Pending requests`, redoslijed kako vrati API:

| Stupac | Sadržaj |
| --- | --- |
| `Employee` | `ownerName` |
| `Type` | tip |
| `Start date` | `startDate` |
| `End date` | `endDate` |
| `Reason` | `reason`, prazno ako `null` |
| `Decision` | `textbox` s labelom `Comment`, gumbi `Approve` i `Reject` |

- Svaki red ima svoje polje i gumbe; test ih traži unutar retka (`within(row)`).
- `Approve` zove `approveRequest(id, comment)`, `Reject` zove `rejectRequest(id, comment)`, s upisanim komentarom (prazan kao `""`). Frontend ne provjerava je li komentar upisan (R5 provjerava backend: 400 `comment must not be blank`).
- Uspjeh: ponovo `getApprovals()`, pa odlučeni zahtjev nestane s popisa. Greška: `alert` s `detail`.
- Bez zahtjeva: umjesto tablice tekst `No requests waiting for approval.`

### Team calendar (`CalendarScreen`)

Pri otvaranju zove `getTeams()` i `getCalendar(<tekući mjesec>)`. Tekući mjesec je `yyyy-MM` iz `new Date()` u lokalnom vremenu preglednika. Naslov: `heading` razine 1 `Team calendar`.

| Element | Oznaka | Radi |
| --- | --- | --- |
| prikazani mjesec | `heading` razine 2, tekst `yyyy-MM`, npr. `2026-11` | |
| prethodni mjesec | `button` `Previous month` | `getCalendar` za mjesec prije, s istim timom |
| sljedeći mjesec | `button` `Next month` | `getCalendar` za mjesec poslije, s istim timom |
| tim | `combobox` s labelom `Team` | prva opcija `All teams` (bez `teamId`), pa timovi iz `getTeams()` redom kako ih vrati API; promjena zove `getCalendar(month, teamId)` |

Popis po danima (specifikacija 12):

- Za svaki dan od ponedjeljka do petka u prikazanom mjesecu na koji pada barem jedan unos postoji `region` s imenom datuma `yyyy-MM-dd` (i naslovom razine 3 s tim tekstom). Dani idu uzlazno. Subota i nedjelja se ne prikazuju (R10).
- Unos pada na dan ako je dan između `startDate` i `endDate`, oba uključiva. Unos koji počinje prije ili završava poslije mjeseca prikazuje se samo na danima tog mjeseca.
- U regiji je lista (`listitem` po unosu), redoslijed kako vrati API:
  - unos s `type` i `status` (`FULL`): `<name> (<Type>, <Status>)`, npr. `Ivan (Vacation, Pending)`;
  - unos bez njih (`LIMITED`): samo `<name>`, npr. `Marko`.
- Bez ijednog unosa u mjesecu: tekst `No absences this month.`

### Stanje (`src/lib/session.ts`)

| Funkcija | Radi |
| --- | --- |
| `getToken()` | vraća token ili `null` |
| `setToken(token)` | sprema token |
| `clearToken()` | briše token |

- Token je u `localStorage` pod ključem `timeoff.token`. Test se može "prijaviti" tako da sam upiše taj ključ.
- `<Me>` se ne sprema; `AppShell` ga čita s `GET /api/me` kod svakog otvaranja rute, pa izmjena uloge u bazi (D6) vrijedi bez nove prijave, kao u backendu (T2).
- Odjava: `Log out` pozove `clearToken()` i `router.replace("/login")`. Backend se ne zove (T2: odjava ne postoji na serveru).
- 401 iz bilo kojeg poziva osim `login`: API klijent obriše token (`clearToken()`) i baci `ApiError` sa `status` 401; ekran tada napravi `router.replace("/login")` i ne prikazuje `alert`.

### API klijent (`src/lib/api.ts`)

Tipovi su u `src/lib/types.ts`, s istim imenima polja kao JSON iz T2–T7: `Me`, `Balance`, `TimeOffRequest` (= `<Request>` iz T7, s `decidedByName` i `cancelledByName`), `CalendarEntry` (`type` i `status` opcionalni), `Team`, `NewRequest` (`type`, `startDate`, `endDate`, `reason`), `TimeOffType`, `RequestStatus`.

| Funkcija | Upit | Tijelo | Vraća |
| --- | --- | --- | --- |
| `login(username, password)` | `POST /api/auth/login` | `{ "username", "password" }` | `{ token, user: Me }`; token sprema sam (`setToken`) |
| `getMe()` | `GET /api/me` | | `Me` |
| `getMyBalance()` | `GET /api/me/balance` | | `Balance` |
| `getMyRequests()` | `GET /api/me/requests` | | `TimeOffRequest[]` |
| `createRequest(request: NewRequest)` | `POST /api/requests` | `{ "type", "startDate", "endDate", "reason" }` | `TimeOffRequest` |
| `cancelRequest(id, reason)` | `POST /api/requests/{id}/cancel` | `{ "reason" }` | `TimeOffRequest` |
| `getApprovals()` | `GET /api/approvals` | | `TimeOffRequest[]` |
| `approveRequest(id, comment)` | `POST /api/requests/{id}/approve` | `{ "comment" }` | `TimeOffRequest` |
| `rejectRequest(id, comment)` | `POST /api/requests/{id}/reject` | `{ "comment" }` | `TimeOffRequest` |
| `getCalendar(month, teamId?)` | `GET /api/calendar?month=<yyyy-MM>`, uz `&teamId=<id>` samo kad je zadan | | `CalendarEntry[]` |
| `getTeams()` | `GET /api/teams` | | `Team[]` |

Kako zove `fetch` (za test s lažnim `fetch`, npr. `vi.stubGlobal("fetch", vi.fn())`):

- Jedan poziv `fetch(url, init)` po funkciji. `url` je relativan put iz tablice, npr. `/api/me/balance`, `/api/requests/7/approve`, `/api/calendar?month=2026-11&teamId=1`.
- `init.method` je `GET` ili `POST`. `init.headers` je običan objekt: `Authorization: Bearer <token>` kad token postoji (`login` ga nikad ne šalje); uz tijelo i `Content-Type: application/json`. `init.body` je `JSON.stringify` tijela iz tablice; vrijednosti se šalju kako su dane, bez obrade.
- 2xx: vraća pročitani JSON.
- Ne-2xx: baca `ApiError` (klasa izvezena iz `src/lib/api.ts`, nasljeđuje `Error`) s poljem `status` (HTTP status) i `message` = `detail` iz ProblemDetail tijela. Ako tijelo nije JSON ili nema `detail`: `message` je `Request failed with status <status>`.
- 401 osim kod `login`: prije bacanja obriše token (Stanje).
- `fetch` sam baci grešku (mreža): `ApiError` sa `status` 0 i `message` `Cannot reach the server`.
- Lažni odgovor test može napraviti s `new Response(JSON.stringify(body), { status, headers: { "Content-Type": "application/problem+json" } })`.

### Testovi

| Što | Vrijednost |
| --- | --- |
| Alati | Vitest, React Testing Library, `@testing-library/user-event`, `@testing-library/jest-dom`, jsdom |
| Gdje | `frontend/tests/`, datoteke `*.test.ts` i `*.test.tsx` |
| Pokretanje | u `frontend/`: `npm test` (`vitest run`); bez testova prolazi |
| Konfiguracija | `frontend/vitest.config.mts`; alias `@/` radi kao u aplikaciji |
| Setup | `frontend/tests/setup.ts`: jest-dom matcheri (`toBeInTheDocument` i sl.); nakon svakog testa `cleanup()`, `localStorage.clear()`, `vi.restoreAllMocks()`, `vi.unstubAllGlobals()` |

- `next/navigation` test mocka sam: `vi.mock("next/navigation", () => ({ useRouter: () => ({ replace, push }) }))`, pa provjeri `replace("/login")` i sl.
- Tekući mjesec: `vi.useFakeTimers({ toFake: ["Date"] })` i `vi.setSystemTime(...)`; lažira se samo `Date`, inače `findBy...` čeka zauvijek.
- Testovi ne trebaju backend: sve ide kroz lažni `fetch`.

### Što testovi mogu provjeriti

Seed (T1): Ana vodi Platform, Eva je admin, Ivan je zaposlenik. Odgovore backenda daje lažni `fetch`.

- API klijent: za svaku funkciju put, metoda, zaglavlja i tijelo; `login` bez `Authorization`; ostale s tokenom iz `localStorage`; `getCalendar` bez i s `teamId`; ProblemDetail 409 daje `ApiError` sa `status` 409 i `detail` kao `message`; tijelo bez `detail`; mrežna greška: `status` 0; 401 briše token, 401 kod `login` ne.
- Login: uspjeh sprema token pod `timeoff.token` i ide na `/my-time-off`; 401 prikaže `Invalid username or password` u `alert`.
- AppShell: bez tokena `replace("/login")` i nema poziva `fetch`; Ivan ne vidi poveznicu `Approvals`, Ana i Eva je vide; `Signed in as Ana`; `Log out` briše token i ide na `/login`; 401 s `/api/me` ide na `/login`.
- My time off: saldo iz `getMyBalance`; redovi tablice s prikazom tipa i stanja; `Approved by Ana`, `Rejected by Ana`, `Cancelled by Eva`, `Pending` bez imena (L32); `Cancel` samo za `Pending` i `Approved`; slanje forme šalje upisane vrijednosti i ponovo učita popis i saldo; 409 iz `createRequest` prikaže `detail`, vrijednosti ostaju; otkazivanje s razlogom; 409 `Request has already started` se prikaže.
- Approvals: red s `ownerName`; `Approve` i `Reject` šalju komentar iz svog retka; 400 `comment must not be blank` se prikaže; nakon uspjeha red nestane; prazan popis daje `No requests waiting for approval.`
- Team calendar: tekući mjesec iz sata; `Next month` i `Previous month` zovu `getCalendar` s pravim `month`; izbor tima šalje `teamId`, `All teams` ga ne šalje; unos od petka do utorka pokazuje se pod petkom, ponedjeljkom i utorkom, ne pod vikendom; unos koji počinje u prethodnom mjesecu pokazuje se od prvog dana mjeseca; `FULL` unos s tipom i stanjem, `LIMITED` samo ime; prazan mjesec daje `No absences this month.`
