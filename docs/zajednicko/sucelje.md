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
