# Mapa koda: backend/src/main, frontend/src, migracije

Putanje su relativne u odnosu na korijen repozitorija. Ukupno fajlova u mapi: 73 (backend 51, frontend 22).

## Backend: `backend/src/main/java/com/example/timeoff/`

| Putanja | Sloj | Što radi | Javne metode / exporti |
|---|---|---|---|
| TimeoffApplication.java | konfiguracija | Ulazna točka Spring Boot aplikacije. | main |
| api/ApiExceptionHandler.java | api | Pretvara iznimke aplikacije u HTTP odgovore (ProblemDetail). | invalidLogin, invalidStatusTransition, notFound, forbidden, invalidRequest, ruleViolation |
| api/ApprovalController.java | api | REST krajnje točke za odobravanje, odbijanje i popis zahtjeva na čekanju. | approve, reject, approvals; record ApproveBody, RejectBody |
| api/AuthController.java | api | Prijava i dohvat trenutnog korisnika. | login, me; record LoginRequest |
| api/BalanceController.java | api | Vlastiti saldo dana godišnjeg za tekuću godinu. | myBalance |
| api/CalendarController.java | api | Kalendar zahtjeva za mjesec i popis timova. | calendar, teams; record CalendarEntryView, TeamView |
| api/CancelController.java | api | Otkazivanje zahtjeva i popis zahtjeva koje korisnik smije otkazati. | cancel, cancellable; record CancelBody |
| api/RequestController.java | api | Kreiranje novog zahtjeva. | create; record CreateRequestBody |
| api/RequestView.java | api | Odgovor (DTO) za zahtjev, s imenom vlasnika i odlučitelja. | record RequestView; static from(TimeOffRequest) |
| api/VisibilityController.java | api | Čitanje zahtjeva prema vidljivosti i saldo drugog korisnika. | myRequests, requests, request, balance |
| domain/InvalidStatusTransitionException.java | domena | Iznimka za nedopušteni prijelaz statusa zahtjeva. | konstruktor (Status, Status) |
| domain/Status.java | domena | Enum statusa zahtjeva i pravila dopuštenih prijelaza. | enum; canTransitionTo(Status) |
| domain/Team.java | domena | JPA entitet tima (naziv, voditelj, članovi). | getId, getName, getLead, getMembers |
| domain/TeamRepository.java | repozitorij | Spring Data repozitorij za timove. | nasljeđuje JpaRepository, bez vlastitih metoda |
| domain/TimeOffRequest.java | domena | JPA entitet zahtjeva za odsustvo; sadrži prijelaze statusa approve, reject i cancel. | isSubmittedBy, isPending, overlaps, approve, reject, cancel, getId, getOwner, getType, getStartDate, getEndDate, getReason, getStatus, getDecisionComment, getDecidedBy, getDecidedAt, getCancelReason, getCancelledBy, getCancelledAt, getCreatedAt |
| domain/TimeOffRequestRepository.java | repozitorij | Repozitorij zahtjeva s upitima po vlasniku i statusu. | findByOwner, findByStatus |
| domain/TimeOffType.java | domena | Enum vrste odsustva. | enum |
| domain/User.java | domena | JPA entitet korisnika (admin oznaka, veza na timove). | isAdmin, leads, isMemberOf, isTeamLead, getId, getUsername, getName, getPasswordHash, getHireDate, getTeams |
| domain/UserRepository.java | repozitorij | Repozitorij korisnika. | findByUsername |
| security/AuthService.java | servis | Provjera prijave korisnika i sastavljanje odgovora s tokenom i podacima korisnika. | login, me |
| security/CurrentUser.java | servis | Vraća ID prijavljenog korisnika iz sigurnosnog konteksta. | id |
| security/InvalidLoginException.java | servis | Iznimka za neuspjelu prijavu. | konstruktor () |
| security/LoginResult.java | servis | Rezultat prijave: token i korisnik. | record LoginResult(token, user) |
| security/ProblemAuthenticationEntryPoint.java | api | Odgovor 401 u formatu problem (JSON) kad autentikacija nedostaje. | commence; konstruktor (ObjectMapper) |
| security/SecurityConfig.java | konfiguracija | Sigurnosni lanac filtera, JWT ključ, dekoder i enkoder te enkoder lozinki. | securityFilterChain, jwtKey, jwtDecoder, jwtEncoder, passwordEncoder (beanovi) |
| security/TokenService.java | servis | Izdaje JWT token za korisnika s trajanjem iz konfiguracije. | issue(User) |
| security/UserExistsJwtConverter.java | servis | Pretvara JWT u autentikaciju i provjerava da korisnik postoji. | convert(Jwt) |
| security/UserView.java | servis | DTO korisnika (id, username, name, admin, teamLead). | record UserView; static of(User) |
| service/Access.java | policy | Enum razine vidljivosti zahtjeva drugog korisnika. | FULL, LIMITED, NONE |
| service/ApprovalPolicy.java | policy | Pravila tko smije odobriti ili odbiti zahtjev; pravila se čitaju redom. | canDecide |
| service/ApprovalService.java | servis | Odobravanje, odbijanje i popis zahtjeva na čekanju. | approve, reject, pending |
| service/Balance.java | servis | DTO salda: godina, pravo, iskorišteno, na čekanju, preostalo. | record Balance |
| service/BalanceService.java | servis | Izračun godišnjeg prava i salda dana. | entitlement, balance |
| service/CalendarEntry.java | servis | DTO stavke kalendara. | record CalendarEntry |
| service/CalendarService.java | servis | Sastavlja stavke kalendara za mjesec uz pravila vidljivosti, i popis timova. | month, teams |
| service/CancelPolicy.java | policy | Pravila tko smije otkazati zahtjev i je li razlog obavezan. | canCancel, reasonRequired |
| service/CancelService.java | servis | Otkazivanje zahtjeva uz provjeru prava i provjeru da zahtjev nije počeo; popis otkazivih zahtjeva. | cancel, cancellable |
| service/ClockConfig.java | konfiguracija | Definira Clock bean (sistemska zona) za pravila "danas". | clock() |
| service/ForbiddenException.java | servis | Iznimka za zabranjenu radnju. | konstruktor (String) |
| service/InvalidRequestException.java | servis | Iznimka za neispravan zahtjev. | konstruktor (String) |
| service/NewRequest.java | servis | DTO ulaza za novi zahtjev. | record NewRequest |
| service/NotFoundException.java | servis | Iznimka kad traženi entitet nije pronađen. | konstruktor (String) |
| service/RequestService.java | servis | Kreiranje zahtjeva uz provjere datuma, radnih dana, datuma zaposlenja, preklapanja i salda. | create |
| service/RuleViolationException.java | servis | Iznimka za kršenje poslovnog pravila. | konstruktor (String) |
| service/Texts.java | servis | Pomoćna funkcija: prazan tekst pretvara u null. | blankToNull (paketna static) |
| service/VisibilityPolicy.java | policy | Određuje razinu vidljivosti (Access) jednog korisnika za zahtjeve drugog. | access |
| service/VisibilityService.java | servis | Dohvat zahtjeva i salda prema pravilima vidljivosti. | mine, visible, get, balance |
| service/WorkingDays.java | servis | Provjera radnog dana i brojanje radnih dana (vikend se ne broji). | isWorkingDay, count |

## Backend: konfiguracija i migracije

| Putanja | Sloj | Što radi | Javne metode / exporti |
|---|---|---|---|
| backend/src/main/resources/application.properties | konfiguracija | Postavke aplikacije: baza, JPA, aktuator, JWT tajna i trajanje. | nema (svojstva) |
| backend/src/main/resources/db/migration/V1__schema.sql | migracija | Kreira tablice users, teams, team_members i time_off_requests. | nema (ograničenja u sekciji dolje) |
| backend/src/main/resources/db/migration/V2__seed.sql | migracija | Unosi početne korisnike, timove i članove timova. | nema |

## Frontend: `frontend/src/`

| Putanja | Sloj | Što radi | Javne metode / exporti |
|---|---|---|---|
| app/layout.tsx | frontend ekran | Korijenski layout aplikacije i metapodaci stranice. | metadata; default RootLayout |
| app/page.tsx | frontend ekran | Početna ruta koja preusmjerava (HomeRedirect). | default HomePage |
| app/login/page.tsx | frontend ekran | Ruta za prijavu. | default LoginPage |
| app/my-time-off/page.tsx | frontend ekran | Ruta "moja odsustva" unutar AppShell. | default MyTimeOffPage |
| app/approvals/page.tsx | frontend ekran | Ruta za odobravanja unutar AppShell. | default ApprovalsPage |
| app/calendar/page.tsx | frontend ekran | Ruta za kalendar unutar AppShell. | default CalendarPage |
| app/globals.css | frontend komponenta | Globalni stilovi (CSS, bez exporta). | nema |
| app/favicon.ico | frontend komponenta | Ikona stranice (binarni fajl, bez exporta). | nema |
| components/AppShell.tsx | frontend komponenta | Zajednički okvir stranica: navigacija i provjera prijave. | AppShell |
| components/LoginScreen.tsx | frontend ekran | Forma za prijavu. | LoginScreen |
| components/MyTimeOffScreen.tsx | frontend ekran | Saldo, popis vlastitih zahtjeva, stvaranje i otkazivanje zahtjeva. | MyTimeOffScreen |
| components/ApprovalsScreen.tsx | frontend ekran | Popis zahtjeva na čekanju i otkazivih zahtjeva, uz odobri i odbij. | ApprovalsScreen |
| components/CalendarScreen.tsx | frontend ekran | Mjesečni kalendar odsustva s filtrom po timu. | CalendarScreen |
| components/HomeRedirect.tsx | frontend komponenta | Preusmjerava na prijavu ili na početnu ovisno o tokenu. | HomeRedirect |
| components/ErrorAlert.tsx | frontend komponenta | Prikaz poruke o pogrešci. | ErrorAlert |
| components/useApiError.ts | frontend komponenta | Hook za pretvaranje API pogrešaka u poruku. | useApiError |
| components/styles.ts | frontend komponenta | Zajedničke CSS klase za polja, gumbe i tablice. | inputClass, buttonClass, secondaryButtonClass, tableClass, cellClass, headerCellClass |
| lib/api.ts | frontend api klijent | Poziva backend REST API i nosi token u zaglavlju. | ApiError (klasa), login, getMe, getMyBalance, getMyRequests, createRequest, cancelRequest, getApprovals, getCancellableRequests, approveRequest, rejectRequest, getCalendar, getTeams |
| lib/types.ts | frontend api klijent | Tipovi odgovora i zahtjeva prema API-ju. | TimeOffType, RequestStatus, Me, Balance, TimeOffRequest, CalendarEntry, Team, NewRequest, LoginResponse |
| lib/labels.ts | frontend komponenta | Prikazni tekstovi za vrste i statuse. | TYPE_LABELS, STATUS_LABELS, TYPE_OPTIONS |
| lib/session.ts | frontend komponenta | Čuva i čita token prijave u pregledniku. | getToken, setToken, clearToken |
| lib/month.ts | frontend komponenta | Rad s mjesecima (tekući, pomak, radni dani u mjesecu). | currentMonth, shiftMonth, workingDaysOfMonth |

## Endpointi

| HTTP | Putanja | Controller metoda | Servis metoda |
|---|---|---|---|
| POST | /api/auth/login | AuthController.login | AuthService.login |
| GET | /api/me | AuthController.me | AuthService.me |
| GET | /api/me/balance | BalanceController.myBalance | BalanceService.balance |
| GET | /api/me/requests | VisibilityController.myRequests | VisibilityService.mine |
| GET | /api/requests | VisibilityController.requests | VisibilityService.visible |
| GET | /api/requests/{id} | VisibilityController.request | VisibilityService.get |
| GET | /api/users/{id}/balance | VisibilityController.balance | VisibilityService.balance |
| POST | /api/requests | RequestController.create | RequestService.create |
| POST | /api/requests/{id}/approve | ApprovalController.approve | ApprovalService.approve |
| POST | /api/requests/{id}/reject | ApprovalController.reject | ApprovalService.reject |
| GET | /api/approvals | ApprovalController.approvals | ApprovalService.pending |
| POST | /api/requests/{id}/cancel | CancelController.cancel | CancelService.cancel |
| GET | /api/approvals/cancellable | CancelController.cancellable | CancelService.cancellable |
| GET | /api/calendar | CalendarController.calendar | CalendarService.month |
| GET | /api/teams | CalendarController.teams | CalendarService.teams |

## Enumi

| Enum | Vrijednosti | Gdje je definiran |
|---|---|---|
| Status | PENDING, APPROVED, REJECTED, CANCELLED | domain/Status.java; SQL CHECK u time_off_requests.status; frontend RequestStatus |
| TimeOffType | VACATION, SICK_LEAVE, DAY_OFF | domain/TimeOffType.java; SQL CHECK u time_off_requests.type; frontend TimeOffType |
| Access | FULL, LIMITED, NONE | service/Access.java |

## Gdje se koristi Clock

| Fajl | Mjesto |
|---|---|
| service/ClockConfig.java | Definira bean: Clock.systemDefaultZone() |
| api/BalanceController.java | myBalance: LocalDate.now(clock) |
| service/VisibilityService.java | balance: LocalDate.now(clock) |
| service/RequestService.java | create: clock.instant() |
| service/ApprovalService.java | approve i reject: clock.instant() |
| service/CancelService.java | cancel: clock.instant(); hasStarted: LocalDate.now(clock) |

## Ograničenja u tablicama (V1__schema.sql)

| Tablica | Primarni ključ | Unique | Check | Strani ključ |
|---|---|---|---|---|
| users | id | username | nema | nema |
| teams | id | name | nema | lead_id -> users(id) |
| team_members | (team_id, user_id) | nema | nema | team_id -> teams(id); user_id -> users(id) |
| time_off_requests | id | nema | type IN (VACATION, SICK_LEAVE, DAY_OFF); status IN (PENDING, APPROVED, REJECTED, CANCELLED); end_date >= start_date | owner_id -> users(id); decided_by_id -> users(id); cancelled_by_id -> users(id) |

V2__seed.sql nema ograničenja, samo INSERT naredbe.
