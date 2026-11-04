# Nalazi o projektu

Popis svega što su reviewi našli na projektu, bez podataka o modelima. Tko je što našao i koliko je koji model točan: [usporedba.md](usporedba.md) i [zakljucci.md](zakljucci.md), koji se na ove nalaze pozivaju brojem (I1, S1, N1…). Karta koda za popravke: [adresar-mapa-cuvari.md](adresar-mapa-cuvari.md).

- **Dokazi vrijede za** commit `eb785ba` i radnu kopiju od 2026-11-01. Gdje piše HEAD, dokaz je u commitanoj verziji. Brojevi redaka se pomiču kad se kod mijenja.
- **Putanje:** `service/`, `api/`, `domain/`, `security/` su u `backend/src/main/java/com/example/timeoff/`; `components/` i `lib/` u `frontend/src/`.
- **Stanje:** ništa nije označeno kao riješeno.
- **Dodavanje:** prije upisa provjeri postoji li nalaz već. Ako postoji, ne dodaji ga ponovo. Novi nalaz dobiva sljedeću oznaku (I57, S44, …). Tvrdnju koja se ne potvrdi stavi u odjeljak "Vrlo vjerojatno krivi nalazi".

## Integrator: kod sa samim sobom (56)

Integrator gleda kod sa samim sobom i s pravilima arhitekture: isto pravilo na više mjesta, pravilo izvan svoje adrese, copy-paste, slojevi, mrtav kod. Pitanje je: je li ovo napisano čisto?

### Svjesno prihvaćeno

Stvarno, ali odlučeno da se ne radi: "Deliberately out: concurrent requests (about 20 users)" (`README.md:94`).

| # | Nalaz | Dokaz |
| --- | --- | --- |
| I34 | Dva istodobna zahtjeva mogu oba proći R9 (preklapanje) i R13 (saldo), jer nema zaključavanja. | `service/RequestService.java:99-126`; nema `@Lock` ni ograničenja u `V1__schema.sql` |
| I40 | Dvije istodobne odluke nad istim zahtjevom obje uspiju, a zadnja tiho prepiše prvu (nema `@Version`). | `service/ApprovalService.java:43-47`, `domain/TimeOffRequest.java:109-114` |
| I41 | Dva istodobna otkazivanja, ili otkazivanje uz odobravanje, prepišu jedno drugo. | `service/CancelService.java:45,54` |

Napomena: ako "concurrent requests" pokriva i istodobne odluke i otkazivanja, a ne samo kreiranje, vrijedi to u README-u reći izričito.

### VAŽNO

| # | Nalaz | Dokaz |
| --- | --- | --- |
| I1 | R14 (bolovanje i slobodni dani ne troše saldo) i "godina početka" napisani su dvaput. | `service/RequestService.java:112,116` i `service/BalanceService.java:73,75` |
| I2 | R5 (odbijanje traži komentar) provjerava samo API; servis vjeruje da je provjera već obavljena, a D3 (razlog otkazivanja) provjerava servis. Sučelje T5 to propisuje ("Provjere 1 i 2 radi API"), pa popravak mijenja sučelje i redoslijed grešaka: odluka orkestratora. | `api/ApprovalController.java:43-45,68`, `service/ApprovalService.java:52`, `service/CancelService.java:82`, `docs/zajednicko/sucelje.md:336` |
| I3 | "Tekuća godina" za saldo računa se u API sloju (kontroler drži `Clock`) i još jednom u servisu. | `api/BalanceController.java:18,34`, `service/VisibilityService.java:90` |
| I4 | Pravilo "odobren zahtjev koji je počeo ne otkazuje se" (R7, Q23) nije u `CancelPolicy`; if je u servisu, a `hasStarted` se zove na dva mjesta. | `service/CancelService.java:69,91-104`, `service/CancelPolicy.java:21-24` |
| I5 | Gumb Cancel na My time off prikazuje se i za odobreni zahtjev koji je već počeo; klik vraća 409. Korisnik to vidi. | `components/MyTimeOffScreen.tsx:36-38,126` prema `service/CancelService.java:92-95` |
| I6 | Uvjet "na čekanju ili odobren" napisan je na tri mjesta. | `service/RequestService.java:102`, `service/CalendarService.java:78`, `components/MyTimeOffScreen.tsx:37` |
| I7 | Dio pravila vidljivosti (LIMITED vidi samo odobrene; Q48: odbijeni i otkazani se ne prikazuju) živi u `CalendarService`, iako `VisibilityPolicy` i README tvrde da je to jedna tablica. | `service/VisibilityPolicy.java:10` prema `service/CalendarService.java:75-95`, `README.md:77` (HEAD) |
| I9 | "Voditelj vodi tim vlasnika" ima isto tijelo u dvije Policy klase. | `service/ApprovalPolicy.java:33-38`, `service/VisibilityPolicy.java:37-41` |
| I11 | R10 (broje se samo radni dani) je kopiran u frontend: kad se doda blagdan (D1), kalendar ga i dalje prikazuje. | `lib/month.ts:28-42`, `service/WorkingDays.java:9,13` |

### SITNO

| # | Nalaz | Dokaz |
| --- | --- | --- |
| I8 | Iznimka "osim admina" za popis timova napisana je kao ternarni operator izvan Policy klase. | `service/CalendarService.java:68` |
| I10 | Postoje dvije definicije "isti korisnik": `isSelf` za dva nespremljena korisnika vraća true, a `isSameAs` false (L13). | `service/VisibilityPolicy.java:33-35`, `domain/User.java:70-80` |
| I12 | Frontend sam zna koje uloge vide Approvals; može se razići s backendom (npr. voditelj čiji su članovi samo voditelji). Prihvatljivo, backend filtrira. | `components/AppShell.tsx:41` |
| I13 | Komparator `BY_START_THEN_ID` je kopiran, a `NEWEST_FIRST` je njegov obrnuti oblik. | `service/ApprovalService.java:19`, `service/CancelService.java:20`, `service/VisibilityService.java:18` |
| I14 | `userRepository.findById(..).orElseThrow()` ponavlja se na 8 mjesta, bez poruke: 500 ako korisnik nestane usred poziva. | ApprovalService:77, CalendarService:132, VisibilityService:100, CancelService:43,62, RequestService:42, BalanceService:53, AuthService:47 |
| I15 | `pending()` i `cancellable()` imaju isti oblik (po statusu → policy → sortiranje). | `service/ApprovalService.java:66-73`, `service/CancelService.java:61-71` |
| I16 | `TimeOffRequest.isPending()` koriste samo testovi. | `domain/TimeOffRequest.java:81`; jedini poziv je `TimeOffRequestTest.java:52` |
| I17 | `entitlement` i `isWorkingDay` su public, a izvan klase ih koriste samo testovi (`isWorkingDay` je možda namjerna točka proširenja). | `BalanceServiceTest.java:48`, `WorkingDaysTest.java:33` |
| I18 | 401 se slaže ručno kao `Map`, a ostale greške preko `ProblemDetail` (D11). Možda opravdano. | `security/ProblemAuthenticationEntryPoint.java:37-42` |
| I19 | Kalendar uzima "danas" u zoni preglednika, a backend u zoni iz D8 (tako kaže sučelje T8). | `lib/month.ts:16-20`, `components/CalendarScreen.tsx:42` |
| I20 | Magični broj 500 stoji u četiri DTO-a i u SQL-u. | ApprovalController:64,68, CancelController:50, RequestController:52, `V1__schema.sql:28,30,33` |
| I21 | "Vodi tim" ima dva izvora: `Team.lead` i `User.ledTeams` (usklađeno preko `mappedBy`). | `domain/User.java:36-37,82` |
| I22 | `Access` je u paketu service, a ne u domain (mišljenje). | `service/Access.java` |
| I23 | "Dobar" primjer u pravilima kodiranja uči upravo "osim admina" if. | `docs/pravila-kodiranja.md:63` prema načelu 8 (`:14`) |
| I24 | Repozitoriji su u paketu domain, a arhitektura ih vodi kao zaseban sloj 6; security ide izravno u repozitorij. | `domain/*Repository.java`, `security/AuthService.java:47`, `security/UserExistsJwtConverter.java:27` |
| I25 | `TokenService` zove `Instant.now()` mimo `Clock` (D8). | `security/TokenService.java:27` |
| I26 | "Ograničeni zapis" kalendara izražen je kao `null` na tri mjesta. | CalendarService:109-119, CalendarController:57, CalendarScreen:32 |
| I27 | `.map(RequestView::from).toList()` ponavlja se u kontrolerima. | Approval:59, Cancel:45, Visibility:55-58 |
| I28 | Copy-paste u frontendu: `reload` ponavlja početno učitavanje, Login ponavlja `useApiError`, a red s poljem i gumbom postoji više puta. | MyTimeOffScreen:72-86, LoginScreen:21 prema useApiError:20, ApprovalsScreen:82,145 |
| I29 | `WorkingDays.count` ponovo provjerava redoslijed datuma (R8) i tiho vraća 0. | `service/WorkingDays.java:23` |
| I30 | Kontroleri uvoze domenske entitete, a API baca servisnu iznimku. | ApprovalController:3,44 |
| I31 | Uvjet "osim vlasnika" u popisu za otkazivanje je if izvan `CancelPolicy`. | `service/CancelService.java:67` |
| I32 | Polja tipova u frontendu se ne koriste (`ownerId`, `decidedById`…, `LoginResponse.user`). | `lib/types.ts`, `lib/api.ts:90-97` |
| I33 | GET `/api/requests`, `/api/requests/{id}` i `/api/users/{id}/balance` nemaju potrošača u frontendu (namjerno: API provodi vidljivost). | `lib/api.ts` (nema poziva) |
| I35 | Radni dani istog zahtjeva broje se dvaput u istom pozivu (i treći put u saldu). | `service/RequestService.java:84,118`, `service/BalanceService.java:76` |
| I36 | Popis i radnja odvojeno pitaju isto: što se smije odobriti (`pending` prema `loadDecidable`) i otkazati (`cancellable` prema `cancel`). | `service/ApprovalService.java:69-71,86`, `service/CancelService.java:49-51,65-69` |
| I37 | Odbijanje ne čisti komentar (`blankToNull`), a odobrenje ga čisti. | `service/ApprovalService.java:44` prema `:59` |
| I38 | Stil `disabled:opacity-50` je mrtav: nijedan gumb nije `disabled`. | `components/styles.ts:6` |
| I39 | Komentar `NotFoundException` kaže "The request does not exist", a iznimka se baca i za korisnika. | `service/NotFoundException.java:3`, `service/VisibilityService.java:84` |
| I42 | Prijava otkriva postoji li korisničko ime: za nepostojećeg korisnika BCrypt se ne izvrši, pa je odgovor brži; komentar obećava suprotno. Korisnici su ionako navedeni u README-u. | `security/AuthService.java:27,31-33` |
| I43 | Mehanizam "prvo pravilo koje vrijedi odlučuje" ponavlja se u tri Policy klase. | `ApprovalPolicy.java:26-28`, `VisibilityPolicy.java:26-28`, `CancelPolicy.java:39-41` |
| I44 | `sharesTeam` i `leadsTeamOfOwner` imaju isti oblik tijela, razlikuje se samo predikat. | `service/VisibilityPolicy.java:37-47` |
| I45 | `approve` i `reject` imaju gotovo isto tijelo (može i ostati). | `service/ApprovalService.java:41-62` |
| I46 | Skup statusa zapisan je u Javi, SQL-u i frontendu (četiri mjesta). | `domain/Status.java:8-11`, `V1__schema.sql:29`, `lib/types.ts:5`, `lib/labels.ts:11-16` |
| I47 | Skup tipova zapisan je u Javi, SQL-u i frontendu, a `TYPE_OPTIONS` je ručna kopija ključeva `TYPE_LABELS`. | `domain/TimeOffType.java:4-6`, `V1__schema.sql:25`, `lib/labels.ts:5-9,18` |
| I48 | Frontend odlučuje "tko je odlučio" preko string literala statusa. | `components/MyTimeOffScreen.tsx:23,25` |
| I49 | Naslov "Approved requests" obećava sve odobrene, a prikazuje samo one koje korisnik smije otkazati. | `components/ApprovalsScreen.tsx:209` prema `service/CancelService.java:65-69` |
| I50 | Tekst se nigdje ne obrezuje: "  ok  " se sprema s razmacima. | `service/Texts.java:14` |
| I51 | Polja za komentar odbijanja i razlog otkazivanja nisu označena kao obavezna; korisnik to sazna tek iz 400 (namjerno prepušteno backendu, `ApprovalsScreen.tsx:68`). | `components/ApprovalsScreen.tsx:113-120,176` |
| I52 | Forma za novi zahtjev ima `noValidate` i nijednu provjeru unosa. | `components/MyTimeOffScreen.tsx:207` |
| I53 | Komentar `RuleViolationException` ne spominje slučaj "zahtjev je već počeo". | `service/RuleViolationException.java:3` prema `service/CancelService.java:95` |
| I54 | Komentar klase `ApprovalService` kaže da ona provjerava "is pending", a to radi entitet. | `service/ApprovalService.java:15` prema `domain/TimeOffRequest.java:110` |
| I55 | Komentar `UserView` kaže "lazy teams", a `isTeamLead()` čita `ledTeams`. | `security/UserView.java:5`, `domain/User.java:82-84` |
| I56 | Komentar u `CalendarService` pretpostavlja Q3 (voditelj je član svog tima), a pravilo postoji samo u seedu. | `service/CalendarService.java:64`, `V2__seed.sql:18` |

### Vrlo vjerojatno krivi nalazi

Reviewer ih je prijavio, a provjera ih ne potvrđuje. Ne rade se. Ovdje su da ih sljedeći review ne prijavi ponovo. Što s njima dalje, treba razraditi u procesu.

| # | Tvrdnja | Zašto je vjerojatno kriva |
| --- | --- | --- |
| N1 | `if (body == null)` i `@NotBlank` su "dvostruka provjera za isti slučaj". | `@NotBlank` ne radi kad tijela nema; `if` pokriva upravo taj slučaj (`api/ApprovalController.java:40-45`). |
| N2 | `arhitektura.md` 2.3 ("uloge i timovi iz baze") odstupa od koda. | `docs/arhitektura.md:57-58` kaže točno ono što kod radi. |
| N3 | "Prazno mrtvo polje" u `TimeoffApplication`. | Reviewer fajl nije otvorio ("vjerojatno standardno"); neprovjerljivo. |
| N5 | Admin smije odobriti vlastiti zahtjev, a to "treba provjeriti". | Namjerno: D15 (`specifikacija.md:177`), a komentar to kaže (`service/ApprovalPolicy.java:18`). |
| N6 | Komentar `ApprovalsScreen.tsx:68` krivo tvrdi da backend vraća 400 i za odobrenje bez komentara. | Sporno: komentar navodi R5 i D3, dakle odbijanje i razlog otkazivanja, za koje backend stvarno vraća 400. |
| — | "Sve CSS klase se koriste" (navedeno kao provjereno i u redu). | `disabled:opacity-50` je mrtav (I38). |

Napomena: blok u `frontend/AGENTS.md` dva su reviewera prijavila kao prompt injection. To je standardni tekst koji generira `next dev`, nije nalaz.

## Shiva: projekt prema briefu, odgovorima naručitelja, specifikaciji i README-u (43)

Shiva uspoređuje projekt s onim što je dogovoreno: brief, odgovori naručitelja (Q), specifikacija (R, D), coding log, sučelje, README i procesni dokumenti, pa svako od toga s kodom. Pitanje je: radi li ovo ono što je dogovoreno?

Preklapanje s integratorom: I2 (R5 u API-ju), I3 (tekuća godina u API sloju) i I7 (vidljivost u `CalendarService`) našla je i shiva. Vode se pod integratorom.

### Svjesno prihvaćeno

Odluke koje nisu nalazi same po sebi. Nalaz je samo ako nisu zapisane gdje treba (npr. S3, S6).

- "Deliberately out: concurrent requests (about 20 users), public holidays, carry-over" (`README.md:94`).
- D7: proširenja (prijenos dana, zahtjev preko granice godine, bolovanje preko godišnjeg) se ne rade, opisuju se u README-u.
- D15: admin smije odobriti vlastiti zahtjev.
- D6: izmjene već započetog zahtjeva rade se izravno u bazi.

### BLOKIRA

Krši odgovor naručitelja ili specifikaciju. Većinom pitanje za naručitelja ili vlasnika.

| # | Nalaz | Dokaz |
| --- | --- | --- |
| S1 | Q49.1 prihvaća popis korisnika na prijavi, a D10 ga ukida; promjena nije u odjeljku 11, a pitanja tvrde da prijava "nije pitanje". | `atomarna-pitanja.md:171,190`, `specifikacija.md:172`, `coding-log.md:110-118` |
| S2 | D3 čini razlog otkazivanja obaveznim, a odgovor na Q37 to ne kaže ("Moze, nema bude field"); D3 odgovor krivo citira ("Može, neka bude polje"). | `atomarna-pitanja.md:98`, `specifikacija.md:165`, `service/CancelService.java:83` |
| S3 | Prijenos dana (Q14, Q14.1, Q14.2), Q28 i Q57 odbačeni su preko D7, a D7 stoji na Q53.3, čiji je odgovor "?", označenom kao 🟩 odgovor umjesto 🟨 pretpostavka. | `atomarna-pitanja.md:65,182,183,193`, `specifikacija.md:17,141,154,169` |
| S4 | Q39 kaže da se preklapanje broji po radnim danima, a kod ga broji kalendarski; specifikacija Q39 ne spominje. | `atomarna-pitanja.md:105`, `domain/TimeOffRequest.java:85`, `specifikacija.md:81` |
| S19 | Q18 kaže da se saldo provjerava "za tekuću" godinu, a D5 i kod troše saldo godine u koju zahtjev pada; D5 iz Q18 čita samo dio o prijenosu. | `atomarna-pitanja.md:69`, `specifikacija.md:94,167`, `service/RequestService.java:116` |
| S20 | Rok 29. 10. bio je strog, a T2–T9 su commitani 30. 10.; T5 i T6 u razmaku od 11 s, a T8.1, way-of-working i T9 unutar 20 s. Reflog pokazuje resetove, pa su datumi commitova datumi slaganja povijesti, ne nužno rada. | `specifikacija.md:144`, `atomarna-pitanja.md:196`, `git log`, `git reflog` |

### VAŽNO

| # | Nalaz | Dokaz |
| --- | --- | --- |
| S5 | Tablica polja kaže "Komentar odluke: samo kod odbijanja", a R5 kaže da je komentar odobrenja opcionalan; kod i UI ga šalju. | `specifikacija.md:40` prema `brief.md:73` |
| S6 | Q28 i Q57 nisu navedeni u odjeljku 11 (odstupanja), a README čitatelja šalje upravo tamo. | `specifikacija.md:156-157` prema `:183-192`, `README.md:169` |
| S7 | Tuđi zahtjev na čekanju ne može se otkazati kroz UI (D17 to priznaje). | `components/ApprovalsScreen.tsx:154`, `service/CancelService.java:65`, `specifikacija.md:70,179` |
| S8 | Saldo članova i svih korisnika (Q16, Q17) postoji samo u API-ju; L28 to izbacuje bez oznake D, a obrazloženjem proturječi L39 ("naručitelj je odgovorio da, pa korisnik to mora moći u aplikaciji"). | `lib/api.ts:103`, `coding-log.md:389-395,547` |
| S9 | Specifikacija obećava "pokretanje jednom naredbom", a README traži `cp .env` i ručni ključ; placeholder iz `.env.example` (49 znakova) prolazi provjeru od 32 bajta. | `specifikacija.md:133`, `README.md:101-103`, `.env.example:3`, `security/SecurityConfig.java:24,53` |
| S10 | Javni zadani JWT ključ je u repozitoriju; vrijedi kad se image pokrene bez `JWT_SECRET`. Komentar "Docker sets JWT_SECRET" vrijedi samo za Compose. | `application.properties:12-13`, `README.md:109` |
| S11 | `docs/diagrams.md` (Q51) nije u gitu, a README i specifikacija ga linkaju. | `git ls-files docs/diagrams.md` prazan; `README.md:152`, `specifikacija.md:206` |
| S12 | Testovi nisu po metodologiji (README to priznaje: rađeni su po developerovom popisu). | `README.md:28,92`, `docs/way-of-working.md:14` |
| S13 | Taskovi i way-of-working su zastarjeli: tester vidi i `frontend/tests/`, agente pokreće orkestrator, "revieweri" su bili jedan. | `docs/taskovi.md:3,11`, `docs/way-of-working.md:7-8`, `coding-log.md:436-437` |
| S21 | L39 je promijenio ponašanje (novi endpoint i tablica), a u HEAD-u nije u specifikaciji kao D. | `coding-log.md:545`, `git diff docs/zajednicko/specifikacija.md` (D17 samo u radnoj kopiji) |
| S22 | Na My time off se ne vidi razlog otkazivanja kad postoji komentar odobrenja. | `components/MyTimeOffScreen.tsx:33`, `sucelje.md:680` |
| S23 | README pretpostavke i odluke šalje u dokumente na hrvatskom, a specifikacija kaže "sve na engleskom". | `specifikacija.md:134`, `README.md:5` |
| S24 | README (HEAD) ne objašnjava izbor biblioteka. | HEAD `README.md` |
| S25 | README je uglavnom o procesu ("The main deliverable is the process"), a Q53.5 kaže da ga README kratko spominje. | `README.md:9`, `specifikacija.md:143` |
| S31 | Voditelj i admin kroz UI ne vide zahtjeve svog tima ili tvrtke u svim stanjima, s tipom i razlogom; postoji samo API. Approvals pokazuje samo zahtjeve na čekanju i odobrene koji nisu počeli, a kalendar nema razlog. | `brief.md:101-102`, `specifikacija.md:108`, `lib/api.ts` (nema poziva `GET /api/requests`), `sucelje.md:459` |
| S32 | `backend/mvnw` je u gitu bez izvršnog bita, pa `./mvnw test` iz README-a ne radi na svježem checkoutu na Linuxu i macOS-u. | `git ls-files -s backend/mvnw` → `100644`, `README.md:116` |
| S33 | Commitani HEAD nije usklađen: log i taskovi govore o D17 i T8.1, a specifikacija s D17 i README s 344/345 postoje samo u radnoj kopiji. | `git status`, `git diff` |

### SITNO

| # | Nalaz | Dokaz |
| --- | --- | --- |
| S14 | Coding log ne prati svoja pravila: nema arhive, L14, L28 i L32 su "odlučeno" iako su napravljeni, a "zamijenjeno" nije dopušteno stanje. | `coding-log.md:13,15,163,389,456,464` |
| S15 | Arhitektura je zastarjela: odobravanje ide kroz `ApprovalController`, a ne `RequestController.approve`; servis ne zove `save`; User nema "ulogu" nego `admin` i `ledTeams`. | `docs/arhitektura.md:69,108,120`, `api/ApprovalController.java:31`, `domain/User.java:28-37` |
| S16 | `frontend/README.md` je predložak create-next-app. | `frontend/README.md:1` |
| S17 | L9 (token nosi samo identitet) i L12 točka 6 (60 s tolerancije) mijenjaju ponašanje bez oznake D. | `coding-log.md:106,139` |
| S18 | Zaglavlje specifikacije je zastarjelo ("nacrt za potvrdu", 2026-10-28), a nema zapisa da je naručitelj potvrdio. | `specifikacija.md:3-4,7` |
| S26 | Voditelj vidi komentar odluke na zahtjevu drugog voditelja kojeg ne smije odobriti, a Q45 kaže "vlasnik i oni koji smiju odobravati". | `atomarna-pitanja.md:116`, `sucelje.md:509` |
| S27 | D8 fiksira Europe/Zagreb, a Q24 kaže "zona servera"; nije zapisano kao odstupanje. | `atomarna-pitanja.md:80`, `docker-compose.yml:8,30,42` |
| S28 | Sučelje T2 kaže da je bez prijave dostupan "još samo" `/actuator/health`, a kod pušta i `/error`; T1 ne navodi `findByStatus`. | `sucelje.md:118`, `security/SecurityConfig.java:37` |
| S29 | Brojke se ne slažu: README (HEAD) "~50 pitanja" i "T1–T9"; `za-procjenu.md:7` u istom retku kaže 443 + 86 i "~460". | HEAD `README.md:12,15`, `za-procjenu.md:7` |
| S30 | Naredbe za testove (HEAD) u istom bloku ne rade jedna za drugom (`cd`). | HEAD `README.md:113-114` |
| S34 | D14 sužava Q30: voditelj ne otkazuje zahtjev drugog voditelja, a Q30 kaže samo "Moze"; odjeljak 11 suženje ne spominje. | `specifikacija.md:176,190`, `atomarna-pitanja.md:91` |
| S35 | `specifikacija.md:141` stavlja "po pravilu 80/20" uz "pravila iz briefa s testovima", pa se može čitati kao 80/20 i za testove; README kaže "80/20 is for features, never for tests". | `specifikacija.md:141`, `README.md:92` |
| S36 | D12 (jedan kalendar s filtrom po timu) navodi Q3 kao izvor, a Q3 kaže samo da voditelj može voditi više timova; filtar je naša odluka. | `specifikacija.md:174`, `atomarna-pitanja.md:44` |
| S37 | Sučelje T8 (Approvals) nije ažurirano nakon T8.1: i dalje kaže da ekran zove samo `getApprovals()`. | `sucelje.md:700,715` prema `:845,863` |
| S38 | Vremena stvaranja, odluke i otkazivanja (D13) se bilježe, ali UI ih nigdje ne prikazuje; specifikacija ne kaže treba li. | `specifikacija.md:44,107`, `lib/types.ts`, `components/MyTimeOffScreen.tsx` |
| S39 | `TZ` (D8) se postavlja samo u Composeu, a `ClockConfig` koristi `Clock.systemDefaultZone()`; samostalni backend image računa "danas" u UTC-u. | `docker-compose.yml:8,30,42`, `service/ClockConfig.java:13` |
| S40 | Konfiguracija: `DB_URL`, `DB_USER`, `DB_PASSWORD` nitko ne postavlja (Compose šalje `SPRING_DATASOURCE_*`); baza je izložena na `5432` s lozinkom `timeoff`; backend nema healthcheck, a frontend ga čeka bez uvjeta. | `application.properties:3-5`, `docker-compose.yml:7,10,25-27,39` |
| S41 | README kaže samo "Needs Docker", a naredbe za testove traže i JDK 21 i Node (verzija nije navedena). | `README.md:99,116`, `backend/pom.xml:30` |
| S42 | README u "sljedećim koracima" navodi E2E testove nad Compose stackom, a E2E putovi su već odrađeni; stvarni sljedeći korak je skripta. | `README.md:46-52,231` |
| S43 | README ne kaže da otkazivanje tuđeg zahtjeva na čekanju nije u UI-ju (D17), a u "Extensions" koristi internu oznaku "L28:". | `README.md:217-225`, `specifikacija.md:179` |

### Vrlo vjerojatno krivi nalazi

Reviewer ih je prijavio, a provjera ih ne potvrđuje. Ne rade se. Ovdje su da ih sljedeći review ne prijavi ponovo. Što s njima dalje, treba razraditi u procesu.

| # | Tvrdnja | Zašto je vjerojatno kriva |
| --- | --- | --- |
| N4 | "~530 testova" (`README.md:46`) nije točno. | Neprovjerljivo bez pokretanja: statičko brojanje daje oko 330 deklaracija, a parametrizirani testovi broj podižu. |
| N7 | U logu nema reviewerovog unosa za T5, T6, T8.1 i T9, pa "review nakon svakog taska" nije potkrijepljen. | Log je samo za probleme (`coding-log.md:3`); reviewer bez nalaza vraća "nema nalaza" orkestratoru (`.claude/agents/reviewer.md:29`), a to je dovoljno za commit (`way-of-working.md:18`). Po želji, radi sljedivosti: jedan redak "review: nema nalaza" po tasku. |
| N8 | D2 se krivo poziva na Q5.1, jer Q5.1 pita o zaposleniku, a ne o voditelju. | Voditelj koji je član tuđeg tima jest zaposlenik u više timova (Q3), pa Q5.1 za njega vrijedi i sukob s R3 postoji (`specifikacija.md:164`, `atomarna-pitanja.md:180`). |
| — | "Haiku" u README-u nigdje nije zapisan. | Piše u `za-procjenu.md:7`. |
| — | Može li admin odbiti vlastiti zahtjev (R3 prema D15). | Neprovjereno; sučelje kaže "bilo koji zahtjev, i vlastiti" (`sucelje.md:316`), a D15 govori samo o odobravanju. Vrijedi provjeriti. |
