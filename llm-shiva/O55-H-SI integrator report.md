# Integratorski pregled: špageti i duplicirana pravila

Datum: 2026-10-30. Integrator je samo čitao: kod nije mijenjan, ništa nije commitano.

Izvori: [specifikacija](docs/zajednicko/specifikacija.md) (pravila R, Q, D), [arhitektura](docs/arhitektura.md), [pravila kodiranja](docs/pravila-kodiranja.md), kod u `backend/src/main` i `frontend/src`.

Putanje u dokazima su skraćene: `service/...`, `api/...`, `domain/...` i `security/...` su u `backend/src/main/java/com/example/timeoff/`, a `components/...` i `lib/...` u `frontend/src/`.

## A. Nalazi

### VAŽNO

**A1. Što se smije otkazati ili odobriti napisano je dvaput.**
Isto se pitanje postavlja na dva mjesta: jednom kao filter za popis, jednom kao provjera prije radnje. Ako se pravilo promijeni samo na jednom mjestu, popis i radnja se raziđu: ekran ponudi nešto što backend odbije, ili obrnuto.
- Dokaz za otkazivanje: `service/CancelService.java:65-69` (`cancellable`: stanje APPROVED, nije vlasnik, `canCancel`, nije počeo) i `service/CancelService.java:49-51` (`cancel`: `checkMayCancel`, `checkNotStarted`, prijelaz u domeni).
- Dokaz za odobravanje: `service/ApprovalService.java:69-71` (`pending`) i `service/ApprovalService.java:86` (`loadDecidable`) plus prijelaz u domeni.
- Pravila: R4 (odobrava se samo zahtjev na čekanju), R7 (otkazuje se odobreni koji nije počeo), D14 (tko smije odobriti, smije i otkazati).
- Refaktor: jedan predikat po servisu, `CancelService.mayCancelNow(actor, request)` i `ApprovalService.mayDecideNow(actor, request)`. Popis ga koristi kao filter, a radnja da odluči koju grešku baciti.

**A2. Iznimka "osim vlasnika" stoji kao filter izvan tablice ovlasti.**
CancelPolicy u prvom retku kaže da vlasnik smije otkazati. Popis "Approved requests" vlasnika onda ručno izbacuje posebnim filterom.
- Dokaz: `service/CancelService.java:67` `!request.isSubmittedBy(actor)`, uz red 1 u `service/CancelPolicy.java:22`.
- Pravila: R6 i R7 (vlasnik otkazuje), D14, sučelje T8.1 uvjet 2.
- Refaktor: `Rule` u CancelPolicy dobiva ulogu `OWNER` ili `APPROVER`, a politika izlaže `cancelsAsApprover(actor, request)`. `reasonRequired` i popis čitaju istu ulogu. Danas `reasonRequired` već vraća točno taj odgovor, samo pod drugim imenom.

**A3. Iznimka "admin vidi sve timove" je if izvan VisibilityPolicy.**
Koje timove korisnik vidi u filtru kalendara pravilo je vidljivosti, a odlučuje se ternarnim operatorom u servisu.
- Dokaz: `service/CalendarService.java:68` `viewer.isAdmin() ? teamRepository.findAll() : ...`.
- Pravila: Q43 (admin vidi sve timove, s filtrom), L27 točka 10.
- Refaktor: `VisibilityPolicy.seesTeam(viewer, team)`, s retkom tablice za admina. CalendarService filtrira `findAll()` kroz nju.

**A4. Što pojedini pristup znači na kalendaru odlučuje lanac if-ova, a ne tablica.**
VisibilityPolicy kaže koliko netko vidi (FULL, LIMITED, NONE). Što to znači (FULL vidi zahtjeve na čekanju i odobrene, LIMITED samo odobrene) riješeno je if-ovima u CalendarService. README tvrdi suprotno.
- Dokaz: `service/CalendarService.java:82-95` (`toEntry`), tablica "Što pristup znači" u sučelju T7, pravilo kodiranja 8, `README.md:77`.
- Pravila: Q42, Q47, Q48.
- Refaktor: enum `Access` dobiva tablicu `shownStatuses()` (FULL: PENDING i APPROVED; LIMITED: APPROVED; NONE: ništa). `toEntry` samo pita enum.

**A5. Skup "aktivnih stanja" (na čekanju ili odobren) postoji na tri mjesta, pod tri imena.**
Domena ima tablicu stanja i metodu `isPending()` koju nitko ne zove. Servisi umjesto toga sami uspoređuju stanja.
- Dokaz:
  - `service/RequestService.java:102`: preklapanje.
  - `service/CalendarService.java:75-79`: `isShown`.
  - `components/MyTimeOffScreen.tsx:36-38`: `isCancellable`.
  - Uz to `service/BalanceService.java:57-58`.
- Pravila: R9 (nema preklapanja zahtjeva na čekanju ili odobrenih), Q48 (odbijeni i otkazani se ne prikazuju), R13.
- Refaktor: `Status.isActive()` u domeni, uz tablicu prijelaza. Servisi zovu nju.

**A6. "Tekuća godina" za saldo određuje se dvaput, jednom u API sloju.**
Controller sam računa koja je godina, a VisibilityService isto to radi drugi put.
- Dokaz: `api/BalanceController.java:34-36` i `service/VisibilityService.java:90-92`. L19 točka 3 je controller svjesno izabrala.
- Pravila: R12 (saldo tekuće godine), D8 (vremenska zona).
- Refaktor: `BalanceService.currentBalance(userId)` sam čita sat. Oba pozivatelja ga zovu, a `Clock` izlazi iz controllera.

**A7. Isto pravilo, "komentar ili razlog je obavezan", živi u dva različita sloja.**
Kod odbijanja to provjerava API, kod otkazivanja servis. Servis za odbijanje komentar ne provjerava i ne čisti: odobrenje prazan komentar pretvara u `null`, odbijanje ne.
- Dokaz za R5 (odbijanje traži komentar) u API-ju: `api/ApprovalController.java:43-45` i `:68` `@NotBlank`; `service/ApprovalService.java:52-59` bez provjere, a `service/ApprovalService.java:44` čisti samo kod odobrenja.
- Dokaz za D3 (razlog kod tuđeg otkazivanja) u servisu: `service/CancelService.java:82-88`.
- Refaktor: `ApprovalService.reject` radi `Texts.blankToNull` i provjeru, kao `checkReason` u CancelService. U controlleru ostaje samo `@Size`.

**A8. Što troši saldo i kojoj godini zahtjev pripada računa se na više mjesta.**
Kad dođe proširenje "zahtjev preko granice godine" (Q28), mijenjalo bi se na tri mjesta, a ne na jednom.
- Godina zahtjeva: `service/BalanceService.java:75` i `service/RequestService.java:116`.
- Samo godišnji troši saldo (R14): `service/RequestService.java:112` i `service/BalanceService.java:73`.
- Radni dani istog zahtjeva broje se dvaput u istom pozivu: `service/RequestService.java:84` i `service/RequestService.java:118`. Treći put u `service/BalanceService.java:76`.
- Refaktor: `BalanceService.chargedDays(type, start, end)` vraća 0 za sve osim godišnjeg, a `yearOf(start)` je jedna funkcija. RequestService broj radnih dana računa jednom.

**A9. Frontend sam odlučuje o ovlastima.**
Arhitektura kaže da sloj 1 "ne odlučuje ništa", a dvije su odluke ipak u frontendu:
- Gumb Cancel: `components/MyTimeOffScreen.tsx:36-38` kopira tablicu stanja.
- Poveznica Approvals: `components/AppShell.tsx:41` `teamLead || admin` grubo kopira ApprovalPolicy. Primjer razilaženja: voditelj čiji su članovi samo drugi voditelji (D2: zahtjev voditelja ide adminu) vidi poveznicu, a nema što odobriti.

Prihvaćeno je u L27 točki 12 i sučelju T8, ali to su i dalje kopije pravila.
- Refaktor: backend šalje `<Request>.cancellable` i `<Me>.canApprove`, a frontend ih samo čita.

**A10. "Je li to ista osoba" provjerava se na dva načina.**
Domena ima `isSameAs`: isti objekt ili isti spremljeni id, a dva nespremljena korisnika nikad nisu ista osoba (L13). VisibilityPolicy uspoređuje samo id-jeve, pa bi dva nespremljena korisnika (`null == null`) bila ista osoba. To je točno ono što L13 zabranjuje.
- Dokaz: `domain/User.java:71-80` i `service/VisibilityPolicy.java:33-35`. Uzrok je to što je `isSameAs` package-private (L29 točka 3).
- Refaktor: `User.isSameAs` postaje `public`, a `isSelf` ga zove.

### SITNO

**S1. Mrtav kod.**
- `TimeOffRequest.isPending()` (`domain/TimeOffRequest.java:81`) koristi samo test.
- `disabled:opacity-50` u `components/styles.ts`: nijedan gumb nije `disabled`.
- `LoginResponse.user` frontend ne čita.
- Nisu mrtvi: `GET /api/requests`, `/api/requests/{id}` i `/api/users/{id}/balance` namjerno postoje samo u API-ju (L27 točka 2). `WorkingDays.isWorkingDay` je točka proširenja za blagdane (D1).
- Refaktor: `isPending` iskoristiti kroz A5 ili ga obrisati.

**S2. Isti komparator je copy-paste u dvije klase.**
- Dokaz: `BY_START_THEN_ID` u `service/ApprovalService.java:19-21` i `service/CancelService.java:20-22`. `NEWEST_FIRST` u `service/VisibilityService.java:18-21` je isti, samo obrnut.
- Refaktor: jedna konstanta, npr. `RequestOrder.BY_START_THEN_ID`.

**S3. "Učitaj korisnika ili zahtjev" ponovljeno je deset puta.**
- `userRepository.findById(..).orElseThrow()` je u sedam klasa: ApprovalService:77, BalanceService:53, CalendarService:132, CancelService:43 i :62, RequestService:42, VisibilityService:100, AuthService:47.
- `NotFoundException("Request not found")` je u ApprovalService:84, CancelService:46 i VisibilityService:70.
- Refaktor: package-private pomoćnik u `service`, npr. `Loader.user(id)` i `Loader.request(id)`.

**S4. Ista pretvorba popisa u JSON je copy-paste u controllerima.**
- Dokaz: `.stream().map(RequestView::from).toList()` u `api/ApprovalController.java:59` i `api/CancelController.java:45`. Samo `api/VisibilityController.java:55` ima pomoćnu funkciju za to.
- Refaktor: `RequestView.fromAll(list)`.

**S5. Copy-paste u frontendu.**
- Izvlačenje poruke greške postoji dvaput: `components/LoginScreen.tsx:21` i `components/useApiError.ts:20`.
- `renderPending` (`components/ApprovalsScreen.tsx:82`) i `renderApproved` (`:145`) imaju istu strukturu retka: polje za tekst i gumb u svakom retku. Isto ima i otkazivanje na My time off.
- Refaktor: `errorMessage(caught)` u `lib/api.ts` i mala komponenta `RowAction`.

**S6. Flaster: pristup na kalendaru prenosi se kroz `null`.**
Servis za LIMITED unos stavi `null` u tip i stanje. API zatim skrije ključeve s `null` vrijednošću, a frontend iz ključeva koji nedostaju zaključuje kakav je pristup. Kad bi FULL unos ikad imao prazan tip, izgledao bi kao LIMITED.
- Dokaz: `service/CalendarService.java:109-119`, `api/CalendarController.java:57` `NON_NULL`, `components/CalendarScreen.tsx:32`.
- Refaktor: `CalendarEntry` izričito nosi `Access`, ili postoje dva zapisa, `FullEntry` i `LimitedEntry`.

**S7. Flaster: API baca servisnu iznimku za zahtjev bez tijela.**
- Dokaz: `api/ApprovalController.java:43-45` (L24 točka 1).
- Refaktor: nestaje s A7.

**S8. Definicija radnog dana postoji i u frontendu.**
Prikaz kalendara ima vlastitu provjeru vikenda. Kad bi se dodali blagdani (D1), trebalo bi ih dodati na dva mjesta, a Načelo 5 kaže da se proširenje dodaje na jedno.
- Dokaz: `isWeekend` u `lib/month.ts` i `service/WorkingDays.java:15`.
- Refaktor: kalendar prikazuje dane koje vrati backend, ili backend šalje popis radnih dana u mjesecu.

**S9. Slojevi koje alat ne može razlikovati.**
- Domena (sloj 5) i repozitorij (sloj 6) su u istom paketu `domain`, pa nijedan alat ne može provjeriti da domena ne zove repozitorij.
- Security ide izravno u repozitorij (`security/AuthService.java:46`, `security/UserExistsJwtConverter.java:27`). L9 to implicitno dopušta.
- U redu je: API ne zove repozitorij, servis ne zna za HTTP, domena ne importa ništa iz projekta.
- Refaktor: repozitorije premjestiti u paket `repository`.

**S10. Token ne koristi zajednički sat.**
- Dokaz: `security/TokenService.java:27` zove `Instant.now()` mimo `Clock` beana. `service/ClockConfig.java` kaže da "danas" dolazi iz tog sata.
- Refaktor: ubaciti `Clock` u TokenService.

**S11. Komentar `NotFoundException` je netočan.**
- Dokaz: komentar kaže "The request does not exist", a iznimka se baca i za "User not found" (`service/VisibilityService.java:84`).

## B. Adresar pravila

| Pravilo | Gdje živi | Gdje se još pojavljuje |
| --- | --- | --- |
| R1 voditelj odobrava zahtjeve svog tima, Q5.1 dovoljan je jedan tim | `ApprovalPolicy` red 4 | `AppShell.tsx:41`, približno (A9) |
| R2 admin odobrava sve, Q1.1 i D15 admin i svoj zahtjev | `ApprovalPolicy` red 1 | — |
| R3 nitko ne odobrava svoj zahtjev, D2 zahtjev voditelja ide adminu | `ApprovalPolicy` redovi 2 i 3 | — |
| Q4 zahtjev zaposlenika bez tima ide adminu | `ApprovalPolicy` (zadano: ne) | — |
| R4 odobrava se samo zahtjev na čekanju | `Status` (tablica prijelaza) | `ApprovalService.pending` `findByStatus(PENDING)` (A1) |
| R5 odbijanje traži komentar | `ApprovalController:43,68` (API!) | nigdje u servisu (A7) |
| R6 i R7 vlasnik otkazuje | `CancelPolicy` red 1, `CancelService.checkNotStarted` | `CancelService:67-69`, `MyTimeOffScreen:36` (A1, A2, A9) |
| Q23 zahtjev koji počinje danas je počeo | `CancelService.hasStarted:100` | — |
| Q30, Q31, Q32, D14 tko smije odobriti, smije i otkazati | `CancelPolicy` red 2, zove `ApprovalPolicy` | — |
| D3 razlog kad otkazuje netko drugi | `CancelPolicy.reasonRequired`, `CancelService.checkReason` | — |
| R8 završni datum nije prije početnog | `RequestService.checkDateOrder:69` | CHECK u bazi `V1__schema.sql` (namjerno, D6), `WorkingDays.count:23` |
| R9 nema preklapanja | `RequestService.checkNoOverlap:99`, `TimeOffRequest.overlaps` | skup stanja i u `CalendarService:75` (A5) |
| R10 broje se samo radni dani | `WorkingDays`, `RequestService.checkHasWorkingDay` | `month.ts` `isWeekend` (S8) |
| D1 blagdani su proširenje | `WorkingDays.isWorkingDay` | `month.ts` (S8) |
| Spec 5 zahtjev je unutar jedne godine | `RequestService.checkSingleYear:76` | `BalanceService:70-75` se oslanja na to |
| D4 zahtjev ne počinje prije datuma zaposlenja | `RequestService.checkNotBeforeHireDate:92` | — |
| R11, Q15, D5 20 dana godišnje, razmjerno u godini zaposlenja | `BalanceService.entitlement:35` | — |
| R12 saldo tekuće godine | `BalanceController:34` (API!) | `VisibilityService:90` (A6) |
| R13 godišnji ne smije premašiti saldo | `RequestService.checkBalance:111` | — |
| R14 bolovanje i slobodni dan ne troše saldo | `RequestService:112` | `BalanceService:73` (A8) |
| D5 zahtjev troši saldo godine u koju pada | `RequestService:116` | `BalanceService:75` (A8) |
| Q48 odbijeni i otkazani se ne prikazuju na kalendaru | `CalendarService.isShown:75` | isti skup u `RequestService:102` (A5) |
| Vidljivost (brief, Q16, Q17, Q41–Q47) | `VisibilityPolicy` | `CalendarService.toEntry` (A4), `CalendarService.teams` (A3), `VisibilityService.seesAll` |
| Q43, D12 filter po timu | `CalendarService.isInTeam:121` | — |
| Q29 zahtjev podnosi samo vlasnik | `RequestController` (nema polja za vlasnika), `RequestService.create(userId)` | — |
| Q46, D13 tko je odlučio ili otkazao i kada | `TimeOffRequest.recordDecision` i `cancel` | `RequestView` (imena, L32) |
| D8 vremenska zona iz `TZ` | `ClockConfig` | mimo njega `TokenService:27` (S10) |
| L13 identitet korisnika | `User.isSameAs` | `VisibilityPolicy.isSelf` (A10) |
| D11 greške kao `application/problem+json` | `ApiExceptionHandler` | `ProblemAuthenticationEntryPoint` (za 401, opravdano) |
| Q49 JWT | `SecurityConfig`, `TokenService`, `UserExistsJwtConverter` | — |
| D16 seed | `V2__seed.sql` | — |

## C. Mapa koda

**Backend `com.example.timeoff`**

| Modul | Čemu služi | Pravila | Javne funkcije |
| --- | --- | --- | --- |
| `domain` | entiteti, stanje zahtjeva, repozitoriji | R4 (prijelazi), dio R9 (`overlaps`), L13 | `Status.canTransitionTo`; `User.isAdmin`, `leads`, `isMemberOf`, `isTeamLead`; `TimeOffRequest.isSubmittedBy`, `isPending`, `overlaps`, `approve`, `reject`, `cancel`; `UserRepository.findByUsername`, `TimeOffRequestRepository.findByOwner`, `findByStatus` |
| `security` | prijava i identitet | Q49, Q49.1, D16, L9 | `AuthService.login`, `me`; `CurrentUser.id`; `UserExistsJwtConverter.convert` |
| `api` | HTTP oblik, mapiranje grešaka | R5 (krivo, A7), D11, dio R12 (krivo, A6) | 14 endpointa u 7 controllera; `RequestView.from` |
| `service`, politike | tablice odluka | R1–R3, D2, D15, Q4, Q5.1; R6, R7, D3, D14; vidljivost | `ApprovalPolicy.canDecide`, `CancelPolicy.canCancel`, `reasonRequired`, `VisibilityPolicy.access` |
| `service`, servisi | tokovi i validacija | R8–R10, D4, R13, R14; Q23, Q30.1; R11–R14, Q15, D5; Q16, Q17; Q41–Q48, D12 | `RequestService.create`; `ApprovalService.approve`, `reject`, `pending`; `CancelService.cancel`, `cancellable`; `BalanceService.entitlement`, `balance`; `VisibilityService.mine`, `visible`, `get`, `balance`; `CalendarService.month`, `teams` |
| `service`, pomoćno | radni dani, sat, tekst, zapisi, iznimke | R10, D1, D8 | `WorkingDays.isWorkingDay`, `count`; `ClockConfig.clock`; `Texts.blankToNull`; `Balance`, `CalendarEntry`, `NewRequest`, `Access` |
| `resources/db` | shema i seed | R8 (CHECK), D16 | `V1__schema.sql`, `V2__seed.sql` |

**Frontend `src`**

| Modul | Čemu služi | Pravila (ne bi smjela biti tu) | Javne funkcije |
| --- | --- | --- | --- |
| `app/` | rute; slažu `AppShell` i ekran | — | stranice |
| `components/` | ekrani i omotač | `canApprove` (A9), `isCancellable` (A9), `isWeekend` preko `month.ts` (S8) | `AppShell`, `LoginScreen`, `HomeRedirect`, `MyTimeOffScreen`, `ApprovalsScreen`, `CalendarScreen`, `ErrorAlert`, `useApiError` |
| `lib/` | API klijent, token, tipovi, prikaz, mjeseci | — | `api.ts` (`call`, `ApiError`, 12 funkcija), `session.ts` (`getToken`, `setToken`, `clearToken`), `labels.ts`, `month.ts` (`currentMonth`, `shiftMonth`, `workingDaysOfMonth`) |

## D. Pravila za čuvare arhitekture

**ArchUnit (backend):**
1. Repozitorije premjestiti u paket `repository`. Zatim: `domain` ne ovisi ni o `repository`, `service`, `api` ni `security`. Pokriva S9.
2. `service` ne ovisi o `org.springframework.web..`, `org.springframework.http..` ni `jakarta.servlet..`.
3. `api` ne ovisi o klasama iz `repository`.
4. `User.isAdmin()`, `User.isTeamLead()` i `TimeOffRequest.isSubmittedBy()` smiju se zvati samo iz klasa `*Policy` i iz domene. Pokriva A2 i A3.
5. Konstante `Status.*` i `TimeOffRequest.getStatus()` smiju se koristiti samo u domeni. Pokriva A1 i A5.
6. `java.time.Clock` ne smije biti u `api`. Statički `Instant.now()` i `LocalDate.now()` bez argumenta zabranjeni su svugdje. Pokriva A6 i S10.
7. Klase u `api` ne smiju zvati konstruktore iznimki iz `service`. Pokriva A7 i S7.
8. `WorkingDays.count` smije zvati samo `BalanceService`. Pokriva A8.
9. CPD (PMD copy-paste detektor), prag oko 50 tokena. Pokriva S2–S4.
10. Inspekcija "unused declaration" (IntelliJ ili Qodana) u CI-ju. Pokriva S1.

**ESLint i alati (frontend):**
1. `no-restricted-syntax` u `src/components`: zabranjene usporedbe `.status === "..."` i čitanje `.admin` i `.teamLead`. Pokriva A9.
2. `no-restricted-globals`: `fetch` je dopušten samo u `lib/api.ts`.
3. `no-restricted-syntax`: `instanceof Error` je dopušten samo u jednom pomoćniku. Pokriva S5.
4. `no-restricted-syntax`: `getDay()` je dopušten samo u `lib/month.ts`. Pokriva S8, dok se ne preseli u backend.
5. `jscpd` za duplikate (S5) i `knip` za mrtve exporte (S1).

## E. Što nisam stigao provjeriti

- **Testove nisam čitao ni pokretao**, pa ne znam koji bi od predloženih refaktora slomio testove.
- **Alate nisam pokretao** (PMD, CPD, ArchUnit, knip). Duplikate sam našao grepom i čitanjem, pa ih može biti još.
- **Konfiguraciju nisam pregledao**: `pom.xml`, `eslint.config.mjs`, `vitest.config.mts`.
- **SQL migracije** sam pogledao samo letimično. CHECK za R8 u bazi namjerno je druga obrana (D6).
- **Performanse** nisam provjeravao. Filtriranje iz `findAll` svjesno je ostavljeno (L24 točka 2).
