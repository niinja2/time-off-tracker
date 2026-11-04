# Adresar pravila, mapa koda i pravila za čuvare

Preuzeto doslovno iz [O55-H-SI integrator report.md](O55-H-SI%20integrator%20report.md) (odjeljci B, C i D), stanje 2026-10-30, Opus 5.5 high. Od tada nije ažurirano: nalazi u [nalazi.md](nalazi.md) mogu pokazati mjesta koja se ovdje ne slažu s kodom.

Neutralna mapa svih 73 fajla (Haiku, 2026-11-01, bez ocjena): [lece-integrator/mapa.md](lece-integrator/mapa.md).

Putanje: `service/`, `api/`, `domain/`, `security/` su u `backend/src/main/java/com/example/timeoff/`; `components/` i `lib/` u `frontend/src/`.

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

