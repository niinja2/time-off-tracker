# Usporedba neovisnih reviewa

Datum: 2026-11-01. Samo čitanje: kod i dokumenti nisu mijenjani, testovi nisu pokretani. Svaki nalaz provjeren je u kodu ili dokumentu (fajl:redak).

## Prije svega: ulaz

- **"Opus 5.5 high shiva integrator.txt"** je jedna sesija s oba prompta. Pokrenuta je **2026-10-30**, a svi ostali reviewi 2026-10-31. Njeni izvještaji su `O55-H-SI shiva report.md` i `O55-H-SI integrator report.md` (prije u korijenu kao `shiva_report.md` i `integrator_report.md`), pa sam njih provjeravao. Zbirni popis nalaza svih modela je `nalazi.md`. Prijašnja verzija fajla bila je kopija Opus 4.8 i zamijenjena je.
- Zbog datuma je dio nalaza te sesije bio stvaran 30. 10., a ispravljen je prije ostalih reviewa (`za-procjenu.md:38`; izmjene još nisu commitane). Takve nalaze označavam kao **STVARAN, sad riješeno**. Ostali modeli ih nisu ni mogli naći.
- Iz te sesije je napravljen i odjeljak "Neovisni review (Shiva, DeepSeek)" u `za-procjenu.md`, pa je velik dio njenih shiva nalaza već tamo.
- Fajlovi su `.txt`, ne `.md`.
- Oznake: model-jačina-prompt. **H** = high, **M** = medium; **S** = shiva, **I** = integrator, **SI** = ista sesija radila je oba prompta (prvo shiva, pa integrator), **L** = uske leće (svaka sesija gleda jednu vrstu problema). **Hk-M-L** Haiku 5.5 medium, 10 integrator leća u 5 sesija, 2026-11-01, uz neutralnu mapu koda (`haiku 5.5 medium 10prompt.txt`, `lece-integrator/`, `lece-integrator/mapa.md`). **So-M-L** Sonnet 5.5 medium, 4 shiva leće u 4 sesije (Q/D/R, log i proces, UI i uloge, README i konfiguracija), 2026-11-01, $2,70 (`sonnet 5.5 medium shiva 4prompt.txt`, `lece-shiva/`, `shiva-lece-tokeni.sh`). **Hk-M-S** Haiku 5.5 medium shiva · **Hk-M-I** Haiku 5.5 medium integrator · **O47-M-I** Opus 4.7 medium integrator · **O48-M-I** Opus 4.8 medium integrator · **So-M-I** Sonnet 5.5 medium integrator · **O55-H-I** Opus 5.5 high integrator (zaseban fajl) · **O55-M-I** Opus 5.5 medium integrator · **O55-H-SI** Opus 5.5 high, sesija s oba prompta (`O55-H-SI shiva report.md` i `O55-H-SI integrator report.md`). Gdje se O55-H-SI pojavljuje kao model, stupac "Prompt" ili tablica (shiva ili integrator) kaže o kojem je dijelu sesije riječ.
- Putanje: `service/`, `api/`, `domain/`, `security/` su u `backend/src/main/java/com/example/timeoff/`; `components/` i `lib/` u `frontend/src/`.

## 1–2. Svi nalazi s provjerom

Težina je moja procjena nakon provjere. **STVARAN** znači da dokaz stoji; **NETOČAN** da tvrdnja ne odgovara kodu ili dokumentu; **NEPROVJERLJIV** da se ne može potvrditi bez pokretanja ili bez sadržaja.

### Integrator prompt (kod)

| # | Nalaz | Težina | Modeli | Provjera | Dokaz |
| --- | --- | --- | --- | --- | --- |
| I1 | R14 (bolovanje i slobodni dani ne troše saldo) i "godina početka" napisani su dvaput. | VAŽNO | Hk-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `service/RequestService.java:112,116` i `service/BalanceService.java:73,75` |
| I2 | R5 (odbijanje traži komentar) provjerava samo API; servis vjeruje da je provjera već obavljena, a D3 (razlog otkazivanja) provjerava servis. | VAŽNO | Hk-M-I, So-M-I, O55-H-I, O55-H-SI (oba dijela), O55-M-I, Hk-M-L | STVARAN | `api/ApprovalController.java:43-45,68`, `service/ApprovalService.java:52`, `service/CancelService.java:82`. **Sučelje T5 to propisuje** ("Provjere 1 i 2 radi API", `docs/zajednicko/sucelje.md:336`), pa je odluka orkestratorova. Tako to vide O55-H-I, O55-M-I i O55-H-SI ("developer (sučelje)"); So-M-I i Hk-M-I krive developera. |
| I3 | "Tekuća godina" za saldo računa se u API sloju (kontroler drži `Clock`) i još jednom u servisu. | VAŽNO | Hk-M-S, Hk-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `api/BalanceController.java:18,34`, `service/VisibilityService.java:90` |
| I4 | Pravilo "odobren zahtjev koji je počeo ne otkazuje se" (R7, Q23) nije u `CancelPolicy`; if je u servisu, a `hasStarted` se zove na dva mjesta. | VAŽNO | Hk-M-I, O47-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I | STVARAN | `service/CancelService.java:69,91-104`, `service/CancelPolicy.java:21-24` |
| I5 | Gumb Cancel na My time off prikazuje se i za odobreni zahtjev koji je već počeo; klik vraća 409. | VAŽNO (korisnik to vidi) | Hk-M-I, O47-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I; O48-M-I ga je vidio i ocijenio prihvatljivim, Hk-M-L | STVARAN | `components/MyTimeOffScreen.tsx:36-38,126` prema `service/CancelService.java:92-95` |
| I6 | Uvjet "na čekanju ili odobren" napisan je na tri mjesta. | VAŽNO | Hk-M-I, O47-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `service/RequestService.java:102`, `service/CalendarService.java:78`, `components/MyTimeOffScreen.tsx:37` |
| I7 | Dio pravila vidljivosti (LIMITED vidi samo odobrene; Q48: odbijeni i otkazani se ne prikazuju) živi u `CalendarService`, iako `VisibilityPolicy` i README (u HEAD-u, `README.md:77`) tvrde da je to jedna tablica. | VAŽNO | Hk-M-S, Hk-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI (oba dijela), O55-M-I, Hk-M-L | STVARAN | `service/VisibilityPolicy.java:10` prema `service/CalendarService.java:75-95` |
| I8 | Iznimka "osim admina" za popis timova napisana je kao ternarni operator izvan Policy klase. | SITNO | Hk-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `service/CalendarService.java:68` |
| I9 | "Voditelj vodi tim vlasnika" ima isto tijelo u dvije Policy klase. | VAŽNO | So-M-I, O55-H-I, O55-M-I (Hk-M-I samo u adresaru), Hk-M-L | STVARAN | `service/ApprovalPolicy.java:33-38`, `service/VisibilityPolicy.java:37-41` |
| I10 | Postoje dvije definicije "isti korisnik": `isSelf` za dva nespremljena korisnika vraća true, a `isSameAs` false (L13). | SITNO | So-M-I, O55-H-I, O55-H-SI, O55-M-I | STVARAN | `service/VisibilityPolicy.java:33-35`, `domain/User.java:70-80` |
| I11 | R10 (broje se samo radni dani) je kopiran u frontend: kad se doda blagdan (D1), kalendar ga i dalje prikazuje. | VAŽNO | Hk-M-I, O47-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `lib/month.ts:28-42`, `service/WorkingDays.java:9,13` |
| I12 | Frontend sam zna koje uloge vide Approvals (O55-H-SI daje primjer razilaženja: voditelj čiji su članovi samo voditelji). | SITNO (prihvatljivo, backend filtrira) | Hk-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `components/AppShell.tsx:41` |
| I13 | Komparator `BY_START_THEN_ID` je kopiran, a `NEWEST_FIRST` je njegov obrnuti oblik. | SITNO | Hk-M-I, O47-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `service/ApprovalService.java:19`, `service/CancelService.java:20`, `service/VisibilityService.java:18` |
| I14 | `userRepository.findById(..).orElseThrow()` ponavlja se na 8 mjesta (bez poruke: 500 ako korisnik nestane usred poziva). | SITNO | Hk-M-I, O47-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | ApprovalService:77, CalendarService:132, VisibilityService:100, CancelService:43,62, RequestService:42, BalanceService:53, AuthService:47. O47-M-I kaže "pet", So-M-I "sedam"; točno je 8. |
| I15 | `pending()` i `cancellable()` imaju isti oblik (po statusu → policy → sortiranje). | SITNO | Hk-M-I, So-M-I, Hk-M-L | STVARAN | `service/ApprovalService.java:66-73`, `service/CancelService.java:61-71` |
| I16 | `TimeOffRequest.isPending()` koriste samo testovi. | SITNO | Hk-M-I, O48-M-I, So-M-I, O55-H-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `domain/TimeOffRequest.java:81`; jedini poziv je `TimeOffRequestTest.java:52` |
| I17 | `entitlement` i `isWorkingDay` su public, a izvan klase ih koriste samo testovi. | SITNO | Hk-M-I, O48-M-I, O55-M-I (O55-H-SI: `isWorkingDay` je namjerna točka proširenja), Hk-M-L | STVARAN | `BalanceServiceTest.java:48`, `WorkingDaysTest.java:33` |
| I18 | 401 se slaže ručno kao `Map`, a ostale greške preko `ProblemDetail` (D11). | SITNO | Hk-M-I, So-M-I (O55-H-SI: opravdano) | STVARAN | `security/ProblemAuthenticationEntryPoint.java:37-42` |
| I19 | Kalendar uzima "danas" u zoni preglednika, a backend u zoni iz D8. | SITNO | Hk-M-I, Hk-M-L | STVARAN (tako kaže sučelje T8) | `lib/month.ts:16-20`, `components/CalendarScreen.tsx:42` |
| I20 | Magični broj 500 stoji u četiri DTO-a i u SQL-u. | SITNO | Hk-M-I, So-M-I | STVARAN | ApprovalController:64,68, CancelController:50, RequestController:52, `V1__schema.sql:28,30,33` |
| I21 | "Vodi tim" ima dva izvora: `Team.lead` i `User.ledTeams`. | SITNO | Hk-M-I | STVARAN (usklađeno preko `mappedBy`) | `domain/User.java:36-37,82` |
| I22 | `Access` je u paketu service, a ne u domain. | SITNO (mišljenje) | O47-M-I | STVARAN | `service/Access.java` |
| I23 | "Dobar" primjer u pravilima kodiranja uči upravo "osim admina" if. | SITNO | O48-M-I | STVARAN | `docs/pravila-kodiranja.md:63` prema načelu 8 (`:14`) |
| I24 | Repozitoriji su u paketu domain, a arhitektura ih vodi kao zaseban sloj 6; security ide izravno u repozitorij. | SITNO | So-M-I, O55-H-I, O55-H-SI, O55-M-I | STVARAN | `domain/*Repository.java`, `security/AuthService.java:47`, `security/UserExistsJwtConverter.java:27` |
| I25 | `TokenService` zove `Instant.now()` mimo `Clock` (D8). | SITNO | So-M-I, O55-H-SI, O55-M-I, O55-H-I (samo u D), Hk-M-L | STVARAN | `security/TokenService.java:27` |
| I26 | "Ograničeni zapis" kalendara izražen je kao `null` na tri mjesta. | SITNO | So-M-I, O55-H-SI | STVARAN | CalendarService:109-119, CalendarController:57, CalendarScreen:32 |
| I27 | `.map(RequestView::from).toList()` ponavlja se u kontrolerima. | SITNO | So-M-I ("četiri", krivo), O55-H-SI (dva plus pomoćna funkcija, točno) | STVARAN | Approval:59, Cancel:45, Visibility:55-58 |
| I28 | Copy-paste u frontendu: `reload` ponavlja početno učitavanje, Login ponavlja `useApiError`, a red s poljem i gumbom postoji više puta. | SITNO | So-M-I, O55-H-I, O55-H-SI, O55-M-I | STVARAN | MyTimeOffScreen:72-86, LoginScreen:21 prema useApiError:20, ApprovalsScreen:82,145 |
| I29 | `WorkingDays.count` ponovo provjerava redoslijed datuma (R8) i tiho vraća 0. | SITNO | O55-H-I (O55-H-SI samo u adresaru) | STVARAN | `service/WorkingDays.java:23` |
| I30 | Kontroleri uvoze domenske entitete, a API baca servisnu iznimku. | SITNO | So-M-I, O55-H-SI | STVARAN | ApprovalController:3,44 |
| I31 | Uvjet "osim vlasnika" u popisu za otkazivanje je if izvan `CancelPolicy`. | SITNO | So-M-I, O55-H-SI, Hk-M-L | STVARAN | `service/CancelService.java:67` |
| I32 | Polja tipova u frontendu se ne koriste (`ownerId`, `decidedById`…, `LoginResponse.user`). | SITNO | Hk-M-I, So-M-I, O55-H-SI, O55-M-I, Hk-M-L | STVARAN | `lib/types.ts`, `lib/api.ts:90-97` |
| I33 | GET `/api/requests`, `/api/requests/{id}` i `/api/users/{id}/balance` nemaju potrošača u frontendu. | SITNO (namjerno) | So-M-I, O55-H-I, O55-H-SI, O55-M-I | STVARAN | `lib/api.ts` (nema poziva) |
| I34 | Dva istodobna zahtjeva mogu oba proći R9 (preklapanje) i R13 (saldo), jer nema zaključavanja. | VAŽNO, **svjesno prihvaćeno** | So-M-I, Hk-M-L | STVARAN, prihvaćeno: "Deliberately out: concurrent requests (about 20 users)" (`README.md:94`) | RequestService:99-126; nema `@Lock` ni ograničenja u `V1__schema.sql` |
| I35 | Radni dani istog zahtjeva broje se dvaput u istom pozivu (i treći put u saldu). | SITNO | O55-H-SI | STVARAN | `service/RequestService.java:84,118`, `service/BalanceService.java:76` |
| I36 | Popis i radnja odvojeno pitaju isto: što se smije odobriti (`pending` prema `loadDecidable`) i otkazati (`cancellable` prema `cancel`). | SITNO | O55-H-SI | STVARAN | `service/ApprovalService.java:69-71,86`, `service/CancelService.java:49-51,65-69` |
| I37 | Odbijanje ne čisti komentar (`blankToNull`), a odobrenje ga čisti. | SITNO | O55-H-SI, Hk-M-L | STVARAN | `service/ApprovalService.java:44` prema `:59` |
| I38 | Stil `disabled:opacity-50` je mrtav: nijedan gumb nije `disabled`. | SITNO | O55-H-SI | STVARAN | `components/styles.ts:6` |
| I39 | Komentar `NotFoundException` kaže "The request does not exist", a iznimka se baca i za korisnika. | SITNO | O55-H-SI, Hk-M-L | STVARAN | `service/NotFoundException.java:3`, `service/VisibilityService.java:84` |
| I40 | Dvije istodobne odluke nad istim zahtjevom obje uspiju, a zadnja tiho prepiše prvu (nema `@Version`). | VAŽNO, **svjesno prihvaćeno** | Hk-M-L (L5-3) | STVARAN, prihvaćeno kao I34 (`README.md:94`) | `service/ApprovalService.java:43-47`, `domain/TimeOffRequest.java:109-114`; nema `@Version` u `backend/src/main` |
| I41 | Dva istodobna otkazivanja, ili otkazivanje uz odobravanje, prepišu jedno drugo. | SITNO, prihvaćeno | Hk-M-L (L5-4) | STVARAN, prihvaćeno (`README.md:94`) | `service/CancelService.java:45,54` |
| I42 | Prijava otkriva postoji li korisničko ime: za nepostojećeg korisnika BCrypt se ne izvrši, pa je odgovor brži; komentar obećava suprotno. | SITNO (Hk-M-L: VAŽNO; korisnici su ionako navedeni u README-u) | Hk-M-L (L9-1) | STVARAN | `security/AuthService.java:27,31-33` |
| I43 | Mehanizam "prvo pravilo koje vrijedi odlučuje" ponavlja se u tri Policy klase. | SITNO | Hk-M-L (L1-3) | STVARAN | `ApprovalPolicy.java:26-28`, `VisibilityPolicy.java:26-28`, `CancelPolicy.java:39-41` |
| I44 | `sharesTeam` i `leadsTeamOfOwner` imaju isti oblik tijela, razlikuje se samo predikat. | SITNO | Hk-M-L (L1-2) | STVARAN | `service/VisibilityPolicy.java:37-47` |
| I45 | `approve` i `reject` imaju gotovo isto tijelo. | SITNO (Hk-M-L: ostaviti) | Hk-M-L (L1-8) | STVARAN | `service/ApprovalService.java:41-62` |
| I46 | Skup statusa zapisan je u Javi, SQL-u i frontendu (četiri mjesta). | SITNO (Hk-M-L: VAŽNO) | Hk-M-L (L2-1) | STVARAN | `domain/Status.java:8-11`, `V1__schema.sql:29`, `lib/types.ts:5`, `lib/labels.ts:11-16` |
| I47 | Skup tipova zapisan je u Javi, SQL-u i frontendu, a `TYPE_OPTIONS` je ručna kopija ključeva `TYPE_LABELS`. | SITNO (Hk-M-L: VAŽNO) | Hk-M-L (L2-2) | STVARAN | `domain/TimeOffType.java:4-6`, `V1__schema.sql:25`, `lib/labels.ts:5-9,18` |
| I48 | Frontend odlučuje "tko je odlučio" preko string literala statusa. | SITNO | Hk-M-L (L2-7) | STVARAN | `components/MyTimeOffScreen.tsx:23,25` |
| I49 | Naslov "Approved requests" obećava sve odobrene, a prikazuje samo one koje korisnik smije otkazati. | SITNO | Hk-M-L (L6-4) | STVARAN | `components/ApprovalsScreen.tsx:209` prema `service/CancelService.java:65-69` |
| I50 | Tekst se nigdje ne obrezuje: "  ok  " se sprema s razmacima. | SITNO | Hk-M-L (L8-3) | STVARAN | `service/Texts.java:14` |
| I51 | Polja za komentar odbijanja i razlog otkazivanja nisu označena kao obavezna; korisnik to sazna tek iz 400. | SITNO | Hk-M-L (L10-5) | STVARAN (namjerno prepušteno backendu, `ApprovalsScreen.tsx:68`) | `components/ApprovalsScreen.tsx:113-120,176` |
| I52 | Forma za novi zahtjev ima `noValidate` i nijednu provjeru unosa. | SITNO | Hk-M-L (L10-6) | STVARAN | `components/MyTimeOffScreen.tsx:207` |
| I53 | Komentar `RuleViolationException` ne spominje slučaj "zahtjev je već počeo". | SITNO | Hk-M-L (L9-4) | STVARAN | `service/RuleViolationException.java:3` prema `service/CancelService.java:95` |
| I54 | Komentar klase `ApprovalService` kaže da ona provjerava "is pending", a to radi entitet. | SITNO | Hk-M-L (L9-5) | STVARAN | `service/ApprovalService.java:15` prema `domain/TimeOffRequest.java:110` |
| I55 | Komentar `UserView` kaže "lazy teams", a `isTeamLead()` čita `ledTeams`. | SITNO | Hk-M-L (L9-6) | STVARAN | `security/UserView.java:5`, `domain/User.java:82-84` |
| I56 | Komentar u `CalendarService` pretpostavlja Q3 (voditelj je član svog tima), a pravilo postoji samo u seedu. | SITNO | Hk-M-L (L9-7) | STVARAN | `service/CalendarService.java:64`, `V2__seed.sql:18` |
| N1 | `if (body == null)` i `@NotBlank` su "dvostruka provjera za isti slučaj". | — | O47-M-I | **NETOČAN** | `@NotBlank` ne radi kad tijela nema; `if` pokriva upravo taj slučaj (`api/ApprovalController.java:40-45`). |
| N2 | `arhitektura.md` 2.3 ("uloge i timovi iz baze") odstupa od koda. | — | So-M-I | **NETOČAN** | `docs/arhitektura.md:57-58` kaže točno ono što kod radi. |
| N3 | "Prazno mrtvo polje": `TimeoffApplication` nije otvoren, "vjerojatno standardno". | — | O47-M-I | **NEPROVJERLJIV** (nije nalaz) | — |
| N5 | Admin smije odobriti vlastiti zahtjev, a to "treba provjeriti" (L6-2). | — | Hk-M-L | **NETOČAN** | Namjerno: D15 (`specifikacija.md:177`), a komentar na istom retku to kaže (`service/ApprovalPolicy.java:18`) |
| N6 | Komentar `ApprovalsScreen.tsx:68` krivo tvrdi da backend vraća 400 i za odobrenje bez komentara (L9-8). | — | Hk-M-L | **SPORNO**, ne broji se | Komentar navodi R5 i D3, dakle odbijanje i razlog otkazivanja, za koje backend stvarno vraća 400 |

Hk-M-L ima i jednu stavku koja nije nalaz (L10-4: gumbi na Approvals bez provjere prava; sam kaže da ne treba ništa mijenjati) i jednu krivu tvrdnju "provjereno, bez nalaza": da se sve CSS klase koriste, a `disabled:opacity-50` je mrtav (I38).

Napomena: O47-M-I i O48-M-I su blok u `frontend/AGENTS.md` prijavili kao pokušaj prompt injectiona. To je standardni tekst koji generira `next dev`, pa se ne broji.

### Shiva prompt (dokumenti ↔ kod)

| # | Nalaz | Težina | Modeli | Provjera | Dokaz |
| --- | --- | --- | --- | --- | --- |
| S1 | Q49.1 prihvaća popis korisnika na prijavi, a D10 ga ukida; promjena nije u odjeljku 11, a pitanja tvrde da prijava "nije pitanje". | BLOKIRA (pitanje za naručitelja) | Hk-M-S, O55-H-SI, So-M-L (kao VAŽNO) | STVARAN | `atomarna-pitanja.md:171,190`, `specifikacija.md:172`, `coding-log.md:110-118` |
| S2 | D3 čini razlog otkazivanja obaveznim, a odgovor na Q37 to ne kaže; D3 i odgovor krivo citira. | BLOKIRA (pitanje) | Hk-M-S (točan citat "Moze, nema bude field"), O55-H-SI (citira "Može, neka bude field", **krivi citat**); So-M-L citira točno, ali D3 ocjenjuje kao "u redu" (propust) | STVARAN | `atomarna-pitanja.md:98`, `specifikacija.md:165`, `service/CancelService.java:83` |
| S3 | Prijenos dana (Q14, Q14.1, Q14.2), Q28 i Q57 odbačeni su preko D7, a D7 stoji na Q53.3, čiji je odgovor "?", označenom kao 🟩 odgovor umjesto 🟨 pretpostavka. | BLOKIRA (pitanje) | Hk-M-S, O55-H-SI (+ legenda), So-M-L djelomično (vidi da prijenosa nema u odjeljku 11, a oslonac D7 na "?" ocjenjuje kao točan) | STVARAN | `atomarna-pitanja.md:65,182,183,193`, `specifikacija.md:17,141,154,169` |
| S4 | Q39 kaže da se preklapanje broji po radnim danima, a kod ga broji kalendarski. | BLOKIRA (Hk-M-S) / SITNO (O55-H-SI) | Hk-M-S (s točnim primjerom), O55-H-SI, So-M-L (samo da Q39 nije u specifikaciji; kod nije čitao) | STVARAN | `atomarna-pitanja.md:105`, `domain/TimeOffRequest.java:85`, `specifikacija.md:81` |
| S5 | Tablica polja kaže "Komentar odluke: samo kod odbijanja", a R5 kaže da je komentar odobrenja opcionalan; kod i UI ga šalju. | VAŽNO | Hk-M-S, So-M-L (kao SITNO) | STVARAN | `specifikacija.md:40` prema `brief.md:73` |
| S6 | Q28 i Q57 nisu navedeni u odjeljku 11 (odstupanja). | VAŽNO | Hk-M-S, So-M-L (+ README:169 šalje čitatelja u odjeljak 11) | STVARAN | `specifikacija.md:156-157` prema `:183-192` |
| S7 | Tuđi zahtjev na čekanju ne može se otkazati kroz UI. | VAŽNO | Hk-M-S, O55-H-SI, So-M-L (kao PRIHVAĆENO zbog D17) | STVARAN | `components/ApprovalsScreen.tsx:154`, `service/CancelService.java:65`, `specifikacija.md:70` |
| S8 | Saldo članova i svih korisnika (Q16, Q17) postoji samo u API-ju; L28 to izbacuje bez oznake D. | VAŽNO | Hk-M-S, O55-H-SI, So-M-L (+ L28 obrazloženjem proturječi L39: "naručitelj je rekao da, pa korisnik to mora moći u aplikaciji", `coding-log.md:547`) | STVARAN | `lib/api.ts:103`, `coding-log.md:389-395` |
| S9 | Specifikacija obećava "pokretanje jednom naredbom", a README traži `cp .env` i ručni ključ; placeholder iz `.env.example` (49 znakova) prolazi provjeru od 32 bajta. | VAŽNO | Hk-M-S, So-M-L (placeholder, bez "jednom naredbom") | STVARAN | `specifikacija.md:133`, `README.md:101-103`, `.env.example:3`, `security/SecurityConfig.java:24,53` |
| S10 | Javni zadani JWT ključ je u repozitoriju; vrijedi kad se image pokrene bez `JWT_SECRET`. | VAŽNO | Hk-M-S, So-M-L (+ komentar u `application.properties:12` je netočan) (O55-H-SI Docker JWT ocjenjuje kao u redu, jer gleda samo Compose) | STVARAN | `application.properties:13`, `README.md:109` |
| S11 | Dijagrami (Q51): 30. 10. nisu postojali (O55-H-SI); 31. 10. postoje, ali nisu u gitu, a linkovi na njih jesu (Hk-M-S). | BLOKIRA → VAŽNO | O55-H-SI, Hk-M-S, So-M-L | STVARAN; za O55-H-SI sad riješeno | HEAD `specifikacija.md` "Otvoreno"; `git ls-files docs/diagrams.md` prazan; `README.md:152` |
| S12 | Testovi nisu po metodologiji (README to priznaje). | VAŽNO | Hk-M-S | STVARAN; poznato i u tijeku: testovi su rađeni po developerovom popisu (`README.md:92`), usklađuju se s metodologijom | `README.md:28`, `docs/way-of-working.md:14` |
| S13 | Taskovi i way-of-working su zastarjeli: tester vidi i `frontend/tests/`, agente pokreće orkestrator, "revieweri" su bili jedan. | VAŽNO | Hk-M-S, O55-H-SI, So-M-L | STVARAN | `docs/taskovi.md:3`, `docs/way-of-working.md:8`, `coding-log.md:436-437` |
| S14 | Coding log ne prati svoja pravila: nema arhive, L14, L28 i L32 su "odlučeno" iako su napravljeni, a "zamijenjeno" nije dopušteno stanje. | SITNO | Hk-M-S (arhiva), O55-H-SI (sve), So-M-L (sve) | STVARAN | `coding-log.md:13,15,163,389,456,464` |
| S15 | Arhitektura je zastarjela: `RequestController.approve`, `save`, "User: uloga". | SITNO | Hk-M-S (`save`), O55-H-SI (sve) | STVARAN | `docs/arhitektura.md:69,108,120` |
| S16 | `frontend/README.md` je predložak create-next-app. | SITNO | Hk-M-S, O55-H-SI | STVARAN | `frontend/README.md:1` |
| S17 | L9 (token nosi samo identitet) i L12 točka 6 (60 s tolerancije) mijenjaju ponašanje bez oznake D. | SITNO | Hk-M-S | STVARAN | `coding-log.md:106,139` |
| S18 | Zaglavlje specifikacije je zastarjelo ("nacrt za potvrdu", 2026-10-28). | SITNO | Hk-M-S, O55-H-SI, So-M-L | STVARAN | `specifikacija.md:3-4` |
| S19 | Q18 kaže da se saldo provjerava "za tekuću" godinu, a D5 i kod troše saldo godine u koju zahtjev pada; D5 iz Q18 čita samo dio o prijenosu. | BLOKIRA (pitanje) | O55-H-SI, So-M-L (kao VAŽNO) | STVARAN | `atomarna-pitanja.md:69`, `specifikacija.md:167`, `service/RequestService.java:116` |
| S20 | Rok 29. 10. bio je strog, a T2–T9 su commitani 30. 10.; T5 i T6 u razmaku od 11 s, a T8.1, way-of-working i T9 unutar 20 s. So-M-L dodaje da reflog pokazuje resetove, pa su datumi commitova datumi slaganja povijesti, ne nužno rada. | BLOKIRA (za vlasnika) | O55-H-SI, So-M-L | STVARAN | `specifikacija.md:144`, `atomarna-pitanja.md:196`, `git log`, `git reflog` ("reset: moving to 8ed66b3") |
| S21 | L39 je promijenio ponašanje (novi endpoint i tablica), a nije ušao u specifikaciju kao D. | VAŽNO | O55-H-SI | STVARAN, sad riješeno (D17, necommitano) | `git diff docs/zajednicko/specifikacija.md` |
| S22 | Na My time off se ne vidi razlog otkazivanja kad postoji komentar odobrenja. | VAŽNO | O55-H-SI, Hk-M-L (L8-2, L10-2), So-M-L (s točnim retkom `:33`) | STVARAN; **redak `MyTimeOffScreen.tsx:613` je izmišljen** (fajl ima 259 redaka; stvarno je `:33`) | `components/MyTimeOffScreen.tsx:33` |
| S23 | README pretpostavke i odluke šalje u dokumente na hrvatskom, a specifikacija kaže "sve na engleskom". | VAŽNO | O55-H-SI | STVARAN | `specifikacija.md:134`, `README.md:5` |
| S24 | README ne objašnjava izbor biblioteka. | VAŽNO | O55-H-SI | STVARAN, sad riješeno | HEAD `README.md` prema sadašnjem `:125-128` |
| S25 | README je uglavnom o procesu ("The main deliverable is the process"), a Q53.5 kaže da ga README kratko spominje. | VAŽNO | O55-H-SI | STVARAN | `README.md:9`, `specifikacija.md:143` |
| S26 | Voditelj vidi komentar odluke na zahtjevu drugog voditelja kojeg ne smije odobriti, a Q45 kaže "vlasnik i oni koji smiju odobravati". | SITNO | O55-H-SI | STVARAN | `atomarna-pitanja.md:116`, `sucelje.md:509` |
| S27 | D8 fiksira Europe/Zagreb, a Q24 kaže "zona servera"; nije zapisano kao odstupanje. | SITNO | O55-H-SI (So-M-L ocjenjuje kao "u skladu") | STVARAN | `atomarna-pitanja.md:80`, `docker-compose.yml:8,30,42` |
| S28 | Sučelje T2 kaže da je bez prijave dostupan "još samo" `/actuator/health`, a kod pušta i `/error`; T1 ne navodi `findByStatus`. | SITNO | O55-H-SI | STVARAN | `sucelje.md:118`, `security/SecurityConfig.java:37` |
| S29 | Brojke se ne slažu: README "~50 pitanja" i "T1–T9"; `za-procjenu.md:7` u istom retku kaže 443 + 86 i "~460". | SITNO | O55-H-SI | STVARAN; README sad riješen, `za-procjenu.md:7` ostaje | HEAD `README.md:12,15`, `za-procjenu.md:7` |
| S30 | Naredbe za testove u istom bloku ne rade jedna za drugom (`cd`). | SITNO | O55-H-SI | STVARAN, sad riješeno | HEAD `README.md:113-114` prema sadašnjem `:116-117` |
| S31 | Voditelj i admin kroz UI ne vide zahtjeve svog tima ili tvrtke u svim stanjima, s tipom i razlogom; postoji samo API. Approvals pokazuje samo zahtjeve na čekanju i odobrene koji nisu počeli, a kalendar nema razlog. | VAŽNO | So-M-L, O55-H-SI (usput, uz krivu tvrdnju "brief to ne traži") | STVARAN | `brief.md:101-102`, `specifikacija.md:108`, `lib/api.ts` (nema poziva `GET /api/requests`), `sucelje.md:459` |
| S32 | `backend/mvnw` je u gitu bez izvršnog bita, pa `./mvnw test` iz README-a ne radi na svježem checkoutu na Linuxu i macOS-u. | VAŽNO | So-M-L | STVARAN (zaključak iz moda, nije pokretano) | `git ls-files -s backend/mvnw` → `100644`, `README.md:116` |
| S33 | Commitani HEAD nije usklađen: log i taskovi govore o D17 i T8.1, a specifikacija s D17 i README s 344/345 postoje samo u radnoj kopiji. | VAŽNO (za vlasnika, prije predaje) | So-M-L | STVARAN | `git status` (M `specifikacija.md`, `README.md`), `git diff` |
| S34 | D14 sužava Q30: voditelj ne otkazuje zahtjev drugog voditelja, a Q30 kaže samo "Moze"; odjeljak 11 suženje ne spominje. | SITNO | So-M-L | STVARAN | `specifikacija.md:176,190`, `atomarna-pitanja.md:91` |
| S35 | `specifikacija.md:141` stavlja "po pravilu 80/20" uz "pravila iz briefa s testovima", pa se može čitati kao 80/20 i za testove; README kaže "80/20 is for features, never for tests". | SITNO | So-M-L (navodi redak 91, stvarno 92) | STVARAN | `specifikacija.md:141`, `README.md:92` |
| S36 | D12 (jedan kalendar s filtrom po timu) navodi Q3 kao izvor, a Q3 kaže samo da voditelj može voditi više timova; filtar je naša odluka. | SITNO | So-M-L | STVARAN | `specifikacija.md:174`, `atomarna-pitanja.md:44` |
| S37 | Sučelje T8 (Approvals) nije ažurirano nakon T8.1: i dalje kaže da ekran zove samo `getApprovals()`. | SITNO | So-M-L | STVARAN | `sucelje.md:700,715` prema `:845,863` |
| S38 | Vremena stvaranja, odluke i otkazivanja (D13) se bilježe, ali UI ih nigdje ne prikazuje; specifikacija ne kaže treba li. | SITNO | So-M-L | STVARAN | `specifikacija.md:44,107`, `lib/types.ts`, `components/MyTimeOffScreen.tsx` (nema `createdAt`, `decidedAt`, `cancelledAt`) |
| S39 | `TZ` (D8) se postavlja samo u Composeu, a `ClockConfig` koristi `Clock.systemDefaultZone()`; samostalni backend image računa "danas" u UTC-u. | SITNO | So-M-L | STVARAN | `docker-compose.yml:8,30,42`, `service/ClockConfig.java:13` |
| S40 | Konfiguracija: `DB_URL`, `DB_USER`, `DB_PASSWORD` nitko ne postavlja (Compose šalje `SPRING_DATASOURCE_*`); baza je izložena na `5432` s lozinkom `timeoff`; backend nema healthcheck, a frontend ga čeka bez uvjeta. | SITNO | So-M-L | STVARAN | `application.properties:3-5`, `docker-compose.yml:7,10,25-27,39` |
| S41 | README kaže samo "Needs Docker", a naredbe za testove traže i JDK 21 i Node (verzija nije navedena). | SITNO | So-M-L | STVARAN | `README.md:99,116`, `backend/pom.xml:30` |
| S42 | README u "sljedećim koracima" navodi E2E testove nad Compose stackom, a E2E putovi su već odrađeni; stvarni sljedeći korak je skripta. | SITNO | So-M-L | STVARAN | `README.md:46-52,231` |
| S43 | README ne kaže da otkazivanje tuđeg zahtjeva na čekanju nije u UI-ju (D17), a u "Extensions" koristi internu oznaku "L28:". | SITNO | So-M-L | STVARAN | `README.md:217-225`, `specifikacija.md:179` |
| N4 | "~530 testova" ne može se potvrditi. | — | Hk-M-S, So-M-L (O55-H-SI isto stavlja pod "nisam provjerio") | **NEPROVJERLJIV** bez pokretanja | `README.md:46` |
| N7 | U logu nema reviewerovog unosa za T5, T6, T8.1 i T9, pa "review nakon svakog taska" nije potkrijepljen. | — | So-M-L | **NETOČAN zaključak** | Log je samo za probleme (`coding-log.md:3`); reviewer bez nalaza vraća "nema nalaza" orkestratoru (`.claude/agents/reviewer.md:29`), a to je dovoljno za commit (`way-of-working.md:18`). Ostaje samo da review bez nalaza ne ostavlja trag u repozitoriju. |
| N8 | D2 se krivo poziva na Q5.1, jer Q5.1 pita o zaposleniku, a ne o voditelju. | — | So-M-L | **NETOČAN** | Voditelj koji je član tuđeg tima jest zaposlenik u više timova (Q3), pa Q5.1 za njega vrijedi i sukob s R3 postoji (`specifikacija.md:164`, `atomarna-pitanja.md:180`) |

So-M-L ima i stavke koje nisu nalaz: Q25 "Proizvoljno" čitan kao "bez granice", D4 uz "bez granice unatrag", odgođena tema salda za sljedeću godinu (ovisi o prijenosu, koji je izvan opsega), Q-ovi bez reference koji ne mijenjaju ponašanje, "~75 pitanja" prema 77 sidara i otvorena ruta `/approvals` (sam kaže "nikome"). "Haiku" u README-u ocijenio je NEPROVJERLJIVIM, a piše u `za-procjenu.md:7`, koji nije smio čitati, pa je ocjena poštena.

**Izmišljeni redci u So-M-L** (sve u leći ui-uloge, sadržaj tvrdnji je točan): `ApprovalsScreen.tsx:287, 327, 336-338, 380-393, 404-457` (fajl ima 214 redaka), `AppShell.tsx:175, 181-191, 184` (69 redaka; provjera uloge je na `:41`), `month.ts:63-67, 76-84` (42 retka; stvarno `:18`). Pomaknuti za jedan: Q18 `atomarna-pitanja.md:70` (stvarno `:69`), Q14 `:66` (stvarno `:65`), `README.md:91` (stvarno `:92`). Putanja `lib/useApiError.ts` je kriva (fajl je u `components/`), redci su točni. Svi ostali redci u dokumentima su točni.

S31–S43 su nalazi koje je donio So-M-L. Shiva nalaza je sad 43, od kojih 40 još vrijedi (S21, S24 i S30 su riješeni).

Shiva modeli su našli i dva integrator nalaza: I3 (Hk-M-S), I2 (O55-H-SI) i I7 (oba).

## 3. Po modelu

"Ključni" su integrator nalazi označeni kao VAŽNO koji nisu svjesno prihvaćeni, ukupno 9: I1–I7, I9 i I11. Istodobnost (I34, I40) je VAŽNO, ali je prihvaćena u `README.md:94`, pa nije ključna. Ukupno 56 su svi redovi I1–I56 tablice integrator nalaza, bez N redova.

| Model | Prompt | Nalaza | Stvarnih | Netočnih | Neprovjerljivih ili spornih | Izmišljeni redak ili citat | Jedinstvenih stvarnih | Ključni (od 9) | Pokriva od 56 integrator nalaza |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Hk-M-S (Haiku 5.5 medium) | shiva | 21 | 20 | 0 | 1 | 0 | 2 (S12, S17); prije So-M-L 6 | — | — |
| O55-H-SI (Opus 5.5 high, shiva dio) | shiva | 26 | 26 (5 sad riješeno) | 0 | 0 | 2 (S22 redak, S2 citat) | 6 važećih (S23, S25–S29) + 3 riješena; prije So-M-L 12 | — | — |
| **So-M-L (Sonnet 5.5 medium, 4 leće)** | shiva | 48 | 37 (oko 6 ponavljanja između leća) | 2 (N7, N8) | 3 | **oko 10 redaka** u 3 fajla + 3 pomaknuta za jedan | **12 (S32–S43)** | — | — |
| Hk-M-I (Haiku 5.5 medium) | integrator | 16 | 16 | 0 | 0 | 0 | 1 (I21) | 8/9 (fali I9) | 20 |
| **Hk-M-L (Haiku 5.5 medium, 10 leća)** | integrator | 53 | 50 | 1 | 1 | 0 | **17 (I40–I56)** | 8/9 (fali I4) | **39** |
| O47-M-I (Opus 4.7 medium) | integrator | 9 | 7 | 1 | 1 | 0 | 1 (I22) | 4/9 | 7 |
| O48-M-I (Opus 4.8 medium) | integrator | 7 | 7 | 0 | 0 | 0 | 1 (I23) | 3/9 | 9 |
| So-M-I (Sonnet 5.5 medium) | integrator | 22 | 21 | 1 | 0 | 0 | 0 | 9/9 | 28 |
| O55-H-I (Opus 5.5 high) | integrator | 17 | 17 | 0 | 0 | 0 | 1 (I29) | 9/9 | 20 |
| O55-H-SI (Opus 5.5 high, integrator dio) | integrator | 21 | 21 | 0 | 0 | 0 | 3 (I35, I36, I38) | 8/9 (fali I9) | 28 |
| O55-M-I (Opus 5.5 medium) | integrator | 17 | 17 | 0 | 0 | 0 | 0 | 9/9 | 21 |

Hk-M-L: 53 stavke iz 5 sesija (po dvije leće), od kojih se oko 6 ponavlja između leća. Trošak svih 6 sesija (mapa i 5 parova leća) je **$0,47** (`lece-integrator/pokreni-tokene.sh`). Hk-M-L je radio dan kasnije i uz mapu koda, pa usporedba s ostalima nije potpuno poštena.

Shiva pokrivenost, od 40 nalaza koji još vrijede (S1–S43 bez S21, S24 i S30), i BLOKIRA od 6 (S1–S4, S19, S20):

| Model | Pokriva od 40 | BLOKIRA od 6 | Trošak |
| --- | --- | --- | --- |
| So-M-L | **28** (+ S3 djelomično) | 4 + S3 djelomično (fali S2) | $2,70 |
| O55-H-SI | 22 (S31 usput) | 6 | ? |
| Hk-M-S | 18 | 4 (fale S19, S20) | ? |

So-M-L je radio istog dana kad i Hk-M-S, a dan poslije O55-H-SI, pa O55-H-SI nije mogao naći S31–S43 koji su nastali ili ostali nakon ispravaka (npr. S33 i S43 govore o D17).

Nalaz se broji onako kako ga je model numerirao. Kad jedan nalaz modela pokriva više redova (npr. I13 i I14), broji se jednom. Brojevi redaka u O55-H-SI su točni (provjerio sam sve iz S3 i A1–A10).

### Što bi tko propustio

- **O47-M-I i O48-M-I propuštaju polovicu bitnog:** od ključnih obojica propuštaju I1, I3 i I9, a od sitnih I8 i I10. O48-M-I propušta i I2 i I6, a I5 proglašava prihvatljivim. O47-M-I krivo opisuje R5 (N1).
- **Hk-M-I** od ključnih propušta I9 (i sitni I10), ali nema nijednu netočnu tvrdnju.
- **Hk-M-L** (isti model, 10 uskih leća) pokriva gotovo dvostruko više nego Hk-M-I (39 prema 20 od 56) i dodaje 17 nalaza koje nitko nije našao. Leća za istodobnost našla je I34 i još jedan prihvaćeni problem iste vrste (I40). Od ključnih propušta I4 ("počeo" izvan `CancelPolicy`). Ima jednu netočnu tvrdnju (N5) i precjenjuje težinu (skupove statusa i tipova označava kao VAŽNO). Ne nalazi ono što nijedna leća nije tražila (I18, I20, I26–I28).
- **So-M-I** ima svih 9 ključnih i najširi pregled jednim promptom, ali i jednu izmišljenu nekonzistentnost (N2) i jedan krivi broj (I27), a R5 pripisuje developeru. Njegov jedini jedinstveni nalaz (I34) našao je i Hk-M-L, a ionako je svjesno prihvaćen.
- **Opus 5.5:** tri integrator izvođenja nemaju nijednu netočnu tvrdnju. Izvođenja se ipak razlikuju: O55-H-I i O55-M-I nalaze I9, a O55-H-SI ne; O55-H-SI zato nalazi tri stvari koje nitko drugi nije našao (pet prije Hk-M-L). Isti model pokrenut dvaput dodaje gotovo jednako kao drugi model.
- **Shiva, O55-H-SI prema Hk-M-S:** O55-H-SI nalazi više i dublje. Od ta dva samo on vidi da je Q18 ("za tekuću") u sukobu s D5 (S19) i rok sa skupnim commitima (S20); te je oboje kasnije našao i So-M-L. Jedini vidi README u kojem dominira proces (S25) i komentar koji vidi voditelj bez prava (S26). Hk-M-S je precizniji: nijedan krivi redak ni citat, i jedini točno citira Q37 i iz toga izvodi nalaz. Od ta dva samo on vidi sigurnost konfiguracije (S9, S10) i komentar odobrenja u tablici polja (S5), što je našao i So-M-L, a jedini vidi L9/L12 bez D (S17). Datumi nisu isti: O55-H-SI je radio dan ranije, pa je mogao naći stvari koje su ispravljene prije Hk-M-S-a, a Hk-M-S stvari nastale tim ispravkom (S11).
- **So-M-L (Sonnet, 4 shiva leće)** pokriva najviše shiva nalaza (28 od 40) i donosi 12 novih, od kojih su važni samo S32 (`mvnw`) i S33 (necommitani popravci), a S31 (pregled svih zahtjeva u UI-ju) je prvi put točno obrazložen. Ostalo je sitno, najviše u konfiguraciji i README-u. Prolaz "Q po Q" (provjereno 77 od 77) nije spriječio propust S2: točno citira Q37, a D3 ipak ocjenjuje kao "u redu". Propušta sve iz dokumenata koje nijedna leća nije pokrila: metodologiju (S12), arhitekturu (S15), `frontend/README.md` (S16), L9 i L12 (S17), README prema Q53.5 (S25). Ocjene težine su blaže (nijedan BLOKIRA). Na dokumentima su redci točni, a na kodu UI-ja izmišlja brojeve redaka (oko 10), više nego svi ostali modeli zajedno. Odsutnost zapisa čita kao odsutnost rada (N7).
- **Shiva i integrator se gotovo ne preklapaju:** shiva modeli dijele s integratorima samo I2, I3 i I7. Nijedan integrator nije vidio sukobe s naručiteljem, README, Docker i JWT ni procesne dokumente.

## 4. Zaključak

**Uske leće.** Najveći pomak donio je **Haiku 5.5 medium s 10 uskih leća** (Hk-M-L): 39 od 56 integrator nalaza, 17 novih i 8 od 9 ključnih, za $0,47. Uzak prompt po vrsti problema vrijedi više od jačeg modela sa širokim promptom. Leće nalaze samo ono što traže, pa ih treba dopuniti jednim širokim prolazom.

**Leće i na shivi.** Sonnet 5.5 medium s 4 shiva leće (So-M-L) pokriva 28 od 40 shiva nalaza, više od O55-H-SI (22) i Hk-M-S (18), i donosi 12 novih, za $2,70. Isto pravilo kao kod integratora: leće nalaze ono što traže, a propuštaju dokumente bez leće i propuste u prosudbi (S2). Brojeve redaka iz koda treba obavezno provjeriti.

**Model.** Jednim širokim promptom za integrator najviše vrijedi **Opus 5.5**: tri izvođenja, nula netočnih tvrdnji, 8 ili 9 od 9 ključnih (jedno nema I9) i najbolje pripisivanje odgovornosti. Medium je dovoljan, jer high ne nalazi sustavno više; razlika između dva high izvođenja veća je nego između high i medium. **Za shiva najviše vrijedi Opus 5.5 high**, jer jedini nalazi sukob s naručiteljem kod Q18. Treba mu provjeriti retke i citate (jedan izmišljeni redak, jedan krivi citat). **Haiku 5.5** je jeftin i precizan na oba prompta, s nula netočnih tvrdnji, i nalazi stvari koje Opus propušta. **Sonnet 5.5** ima svih 9 ključnih i najveću širinu jednim promptom, ali mu nalaze treba provjeriti (jedna izmišljena nekonzistentnost). **Opus 4.7 i 4.8** nisu dovoljni.

**Prompt.** Trebaju oba, jer nalaze različite stvari. Najviše daje dva različita modela po promptu (npr. Opus 5.5 i Haiku 5.5), uz provjeru redaka i citata. Reviewerima treba dati i popis svjesno izostavljenog (npr. `README.md:94`), s uputom da takve nalaze navedu i označe kao prihvaćene, a ne da ih preskoče. Treba im reći i što proces bilježi, a što ne (npr. "log sadrži samo probleme; review bez nalaza ne ostavlja trag"), inače odsutnost zapisa čitaju kao odsutnost rada (N7). Shiva lećama nedostaje peta leća "procesni i tehnički dokumenti prema kodu" (arhitektura, pravila kodiranja, metodologija, `frontend/README.md`), a uz "Q po Q" treba uputa da se citat iz D usporedi s izvorom slovo po slovo (S2).

**Stvarni nalazi kojih nema u `docs/za-procjenu.md`:**

Iz shiva prompta:
- **S19:** Q18 kaže "za tekuću" godinu, a D5 i kod troše saldo godine u koju zahtjev pada (za-procjenu Q18 vodi samo kao prijenos).
- **S20:** rok 29. 10. bio je strog, a T2–T9 su commitani 30. 10., djelomično u razmaku od nekoliko sekundi.
- **S5:** tablica polja kaže "komentar samo kod odbijanja", a brief dopušta i komentar odobrenja (`specifikacija.md:40`).
- **S9, S10:** "jednom naredbom" nije istina; placeholder iz `.env.example` prolazi provjeru od 32 bajta; u `application.properties:13` je javni zadani JWT ključ.
- **S11:** `docs/diagrams.md` nije u gitu.
- **S6:** Q28 i Q57 nisu u odjeljku 11.
- **S17:** L9 i L12 točka 6 mijenjaju ponašanje bez oznake D.
- **S25:** README je uglavnom o procesu, a Q53.5 kaže da ga README kratko spominje.
- **S26:** voditelj vidi komentar odluke na zahtjevu koji ne smije odobriti (Q45).
- **S29:** `za-procjenu.md:7` sam sebi proturječi (443 + 86 prema ~460).
- **S31:** voditelj i admin kroz UI ne vide zahtjeve tima ili tvrtke u svim stanjima, s tipom i razlogom, a brief to traži (`brief.md:101-102`). Za-procjenu ima samo otkazivanje na čekanju (`za-procjenu.md:40`).
- **S32:** `backend/mvnw` bez izvršnog bita; `./mvnw test` ne radi na Linuxu i macOS-u.
- **S33:** popravci (D17, README 344/345) su samo u radnoj kopiji; prije predaje commitati zajedno s `docs/diagrams.md` (S11).
- Sitno: S34 (D14 sužava Q30), S35 ("80/20" u `specifikacija.md:141`), S36 (D12 i Q3), S37 (sučelje T8 nakon T8.1), S38 (vremena se ne prikazuju), S39 (`TZ` samo u Composeu), S40 (`DB_*`, port 5432, healthcheck), S41 (JDK i Node), S42 (E2E u sljedećim koracima), S43 (README i D17, oznaka "L28:").
- Po želji, radi sljedivosti: jedan redak po tasku "review: nema nalaza" (npr. u taskovima), jer se "a review after every task" (`README.md:47`) za T5, T6, T8.1 i T9 iz repozitorija ne vidi.

Iz integrator prompta (za-procjenu ima samo R5 u API-ju i dio vidljivosti kalendara):
- **I5:** gumb Cancel za odobreni zahtjev koji je počeo vraća 409. Jedini nalaz koji korisnik vidi.
- **I1, I3, I4, I9:** isto pravilo na dva mjesta: R14 i godina zahtjeva, tekuća godina u API-ju, "počeo" izvan `CancelPolicy`, "vodi tim vlasnika".
- **I11:** R10 kopiran u frontend; blagdani (D1) ne bi stigli do kalendara.
- **I6:** "na čekanju ili odobren" je na tri mjesta, ne na dva kako piše `za-procjenu.md:43`.
- **I23:** primjer u `pravila-kodiranja.md:63` uči "osim admina" if.
- **I42:** prijava otkriva postoji li korisničko ime (vrijeme odgovora), a komentar u `AuthService.java:27` obećava suprotno.
- Sitno: I8, I10, I12–I22, I24–I33, I35–I39, I43–I56 (copy-paste, mrtav kod, `Instant.now` mimo `Clock`, problem+json na dva načina, repozitoriji u domain, dvostruko brojanje radnih dana, skupovi statusa i tipova na četiri mjesta, nema obrezivanja teksta, `noValidate`, pet krivih komentara).
- Istodobnost (I34, I40, I41) nije u za-procjenu, ali je svjesno izostavljena u `README.md:94`. Ako "concurrent requests" treba pokrivati i istodobne odluke i otkazivanja, a ne samo kreiranje, vrijedi to u README-u reći izričito.
- Uz I2 (već u za-procjenu): sučelje T5 propisuje da R5 provjerava API (`sucelje.md:336`). Popravak zato mijenja sučelje i redoslijed grešaka, pa odluka pripada orkestratoru.
