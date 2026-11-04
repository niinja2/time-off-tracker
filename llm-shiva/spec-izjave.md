# Izjave o ponašanju aplikacije

Izvori: `docs/zajednicko/brief.md`, `docs/zajednicko/atomarna-pitanja.md`, `docs/zajednicko/specifikacija.md`.

Pravila popisa:
- Jedna izjava po retku. Ponavljanja u više dokumenata dobivaju vlastiti redak.
- Bez tumačenja i ocjene. Citat je doslovan iz dokumenta (za `atomarna-pitanja.md` citat je odgovor kolege).
- Iz `atomarna-pitanja.md` uzete su samo izjave koje odgovor kolege daje. Pitanja, pretpostavke i napomene o procesu nisu uzete.
- Izostavljene su izjave o procesu rada, rokovima, ocjenjivanju, predaji i hostingu repozitorija. Nisu izjave o ponašanju aplikacije.
- Prefiksi: `B` = brief, `A` = atomarna-pitanja, `S` = specifikacija.

## brief.md

| # | Tko | Radi ili vidi | Nad čim | U kojem stanju / uvjetu | Gdje (API, ekran, oboje, nije rečeno) | Izvor (dokument, odjeljak, riječ za Ctrl+F) | Točan citat |
| --- | --- | --- | --- | --- | --- | --- | --- |
| B1 | Zaposlenik | podnosi zahtjev za odsustvo | zahtjev za odsustvo | — | nije rečeno | brief · The app · "Build an app where" | "employees request time off" |
| B2 | Voditelj tima | odobrava ili odbija | zahtjeve za odsustvo | — | nije rečeno | brief · The app · "Build an app where" | "team leads approve or reject those requests" |
| B3 | Svatko | vidi tko je odsutan | odsutnost zaposlenika | — | nije rečeno | brief · The app · "Build an app where" | "everyone can see who is away" |
| B4 | Zaposlenik | podnosi zahtjeve | zahtjev za odsustvo | — | nije rečeno | brief · Users and roles · "Employee." | "Requests time off and sees their own requests." |
| B5 | Zaposlenik | vidi | svoje zahtjeve | — | nije rečeno | brief · Users and roles · "Employee." | "sees their own requests" |
| B6 | Voditelj tima | vodi | jedan tim | — | nije rečeno | brief · Users and roles · "Team lead." | "Leads one team." |
| B7 | Voditelj tima | odobrava ili odbija | zahtjeve članova svog tima | — | nije rečeno | brief · Users and roles · "Team lead." | "Approves or rejects requests from that team." |
| B8 | Voditelj tima | je i zaposlenik i može podnijeti zahtjev | zahtjev za odsustvo | — | nije rečeno | brief · Users and roles · "Team lead." | "A team lead is also an employee and can request time off." |
| B9 | Admin | vidi | sve u tvrtki | — | nije rečeno | brief · Users and roles · "Admin." | "Can see and act on everything in the company." |
| B10 | Admin | djeluje na | sve u tvrtki | — | nije rečeno | brief · Users and roles · "Admin." | "Can see and act on everything in the company." |
| B11 | Aplikacija | nema | stvarnu autentikaciju ni ekran za upravljanje korisnicima | — | nije rečeno | brief · Users and roles · "You don't need real authentication" | "You don't need real authentication or a user management screen." |
| B12 | Seed baze | sadrži | korisnike iz tablice | — | nije rečeno | brief · Users and roles · "Seed the database" | "Seed the database with these users" |
| B13 | Aplikacija | omogućuje | djelovanje kao svaki korisnik, prijavom formom sa seedanim lozinkama | — | nije rečeno | brief · Users and roles · "A login form with seeded passwords works" | "A login form with seeded passwords works" |
| B14 | Aplikacija | omogućuje | djelovanje kao svaki korisnik, odabirom "log in as" | — | nije rečeno | brief · Users and roles · "log in as" picker | "a \"log in as\" picker" |
| B15 | Ana | je | voditelj tima Platform | seed | nije rečeno | brief · Users and roles · tablica, "Ana" | "\| Ana \| Team lead \| Platform \|" |
| B16 | Ivan | je | zaposlenik tima Platform | seed | nije rečeno | brief · Users and roles · tablica, "Ivan" | "\| Ivan \| Employee \| Platform \|" |
| B17 | Marko | je | zaposlenik tima Platform | seed | nije rečeno | brief · Users and roles · tablica, "Marko" | "\| Marko \| Employee \| Platform \|" |
| B18 | Petra | je | zaposlenica tima Platform | seed | nije rečeno | brief · Users and roles · tablica, "Petra" | "\| Petra \| Employee \| Platform \|" |
| B19 | Luka | je | voditelj tima Mobile | seed | nije rečeno | brief · Users and roles · tablica, "Luka" | "\| Luka \| Team lead \| Mobile \|" |
| B20 | Sara | je | zaposlenica tima Mobile | seed | nije rečeno | brief · Users and roles · tablica, "Sara" | "\| Sara \| Employee \| Mobile \|" |
| B21 | Tom | je | zaposlenik tima Mobile | seed | nije rečeno | brief · Users and roles · tablica, "Tom" | "\| Tom \| Employee \| Mobile \|" |
| B22 | Eva | je | admin bez tima | seed | nije rečeno | brief · Users and roles · tablica, "Eva" | "\| Eva \| Admin \| none \|" |
| B23 | Zahtjev | ima | tip | — | nije rečeno | brief · Time-off requests · "A request has a type" | "A request has a type," |
| B24 | Zahtjev | ima | početni datum i završni datum | — | nije rečeno | brief · Time-off requests · "a start date, an end date" | "a start date, an end date" |
| B25 | Zahtjev | ima datume koji su | uključivi (i početni i završni) | — | nije rečeno | brief · Time-off requests · "both inclusive" | "(both inclusive)" |
| B26 | Zahtjev | ima | razlog, opcionalan | — | nije rečeno | brief · Time-off requests · "optional reason" | "an optional reason" |
| B27 | Tip zahtjeva | može biti | Vacation | — | nije rečeno | brief · Time-off requests · "The types are:" | "Vacation" |
| B28 | Tip zahtjeva | može biti | Sick leave | — | nije rečeno | brief · Time-off requests · "The types are:" | "Sick leave" |
| B29 | Tip zahtjeva | može biti | Day off | — | nije rečeno | brief · Time-off requests · "The types are:" | "Day off" |
| B30 | Zahtjev | počinje u stanju | Pending | pri nastanku | nije rečeno | brief · Time-off requests · "Every new request starts here." | "Every new request starts here." |
| B31 | Zahtjev | može biti u stanju | Approved | — | nije rečeno | brief · Time-off requests · "States" (popis stanja) | "Approved." |
| B32 | Zahtjev | može biti u stanju | Rejected | — | nije rečeno | brief · Time-off requests · "States" (popis stanja) | "Rejected." |
| B33 | Zahtjev | može biti u stanju | Cancelled | — | nije rečeno | brief · Time-off requests · "States" (popis stanja) | "Cancelled." |
| B34 | Voditelj tima | odobrava ili odbija | zahtjeve članova svog tima | — | nije rečeno | brief · Rules · "Approvals" (1.) | "A team lead can approve or reject requests from members of their own team only." |
| B35 | Voditelj tima | ne smije odobriti ni odbiti | zahtjeve osoba izvan svog tima | — | nije rečeno | brief · Rules · "Approvals" (1.) | "from members of their own team only." |
| B36 | Admin | odobrava ili odbija | bilo koji zahtjev | — | nije rečeno | brief · Rules · "Approvals" (2.) | "An admin can approve or reject any request." |
| B37 | Nitko | ne smije odobriti ni odbiti | vlastiti zahtjev | — | nije rečeno | brief · Rules · "Approvals" (3.) | "Nobody can approve or reject their own request." |
| B38 | Zahtjev voditelja | ide | adminu | — | nije rečeno | brief · Rules · "Approvals" (3.) | "A team lead's request goes to an admin." |
| B39 | Aplikacija | dopušta odobravanje i odbijanje samo za | zahtjeve na čekanju | stanje: Pending | nije rečeno | brief · Rules · "Approvals" (4.) | "Only pending requests can be approved or rejected." |
| B40 | Aplikacija | traži pri odbijanju | komentar | pri odbijanju | nije rečeno | brief · Rules · "Approvals" (5.) | "A rejection needs a comment." |
| B41 | Aplikacija | ne traži pri odobrenju | komentar, opcionalan | pri odobrenju | nije rečeno | brief · Rules · "Approvals" (5.) | "An approval comment is optional." |
| B42 | Zaposlenik | može otkazati | svoj zahtjev | stanje: Pending | nije rečeno | brief · Rules · "Cancelling" (6.) | "An employee can cancel their own request while it is pending." |
| B43 | Zaposlenik | može otkazati | svoj odobreni zahtjev | stanje: Approved, nije počeo | nije rečeno | brief · Rules · "Cancelling" (7.) | "An employee can cancel their own approved request if it hasn't started yet." |
| B44 | Aplikacija | odbija | zahtjev čiji je završni datum prije početnog | — | nije rečeno | brief · Rules · "Validation" (8.) | "The end date can't be before the start date." |
| B45 | Aplikacija | ne dopušta | dva zahtjeva istog korisnika na čekanju ili odobrena s preklapajućim datumima | — | nije rečeno | brief · Rules · "Validation" (9.) | "A user can't have two pending or approved requests whose dates overlap." |
| B46 | Aplikacija | broji | samo radne dane, ponedjeljak do petak | — | nije rečeno | brief · Rules · "Validation" (10.) | "The app counts working days only, Monday to Friday." |
| B47 | Aplikacija | odbija | zahtjev koji pokriva samo vikend | — | nije rečeno | brief · Rules · "Validation" (10.) | "A request that covers only weekend days is invalid." |
| B48 | Svaki korisnik | dobiva | 20 dana godišnjeg | po kalendarskoj godini | nije rečeno | brief · Rules · "Vacation balance" (11.) | "Every user gets 20 vacation days per calendar year." |
| B49 | Aplikacija | prikazuje korisniku | iskorišteno (odobreno), na čekanju i preostalo | za tekuću godinu | nije rečeno | brief · Rules · "Vacation balance" (12.) | "show each user how many days they have used (approved), pending, and remaining." |
| B50 | Zahtjev za godišnji | ne smije premašiti | preostali saldo | — | nije rečeno | brief · Rules · "Vacation balance" (13.) | "A vacation request can't exceed the remaining balance." |
| B51 | Zahtjev na čekanju | troši | saldo godišnjeg | — | nije rečeno | brief · Rules · "Vacation balance" (13.) | "Pending requests count against the balance." |
| B52 | Bolovanje i slobodni dani | ne troše | saldo godišnjeg | — | nije rečeno | brief · Rules · "Vacation balance" (14.) | "Sick leave and days off don't count against the vacation balance." |
| B53 | Zahtjev | pada | u jednu kalendarsku godinu (pretpostavka) | — | nije rečeno | brief · Rules · "calendar year" | "You can assume a request falls within one calendar year." |
| B54 | Zaposlenik | vidi | svoje zahtjeve sa svim detaljima | — | oboje | brief · Visibility · tablica, "Employee" | "Their own requests with every detail." |
| B55 | Zaposlenik | vidi | odobrene odsutnosti suradnika u timu na kalendaru, samo ime i datume | — | oboje | brief · Visibility · tablica, "Employee" | "Teammates' approved absences on the team calendar, showing only the name and dates." |
| B56 | Zaposlenik | ne vidi | tip, razlog ni zahtjeve na čekanju suradnika | — | oboje | brief · Visibility · tablica, "Employee" | "No type, no reason, nothing pending." |
| B57 | Voditelj tima | vidi | svaki zahtjev svog tima, s tipom, razlogom i zahtjevima na čekanju | — | oboje | brief · Visibility · tablica, "Team lead" | "Every request from their team, including type, reason and pending requests." |
| B58 | Admin | vidi | svaki zahtjev u tvrtki | — | oboje | brief · Visibility · tablica, "Admin" | "Every request in the company." |
| B59 | API | provodi | pravila vidljivosti | — | API | brief · Visibility · "The API must enforce these rules." | "The API must enforce these rules." |
| B60 | Skrivanje u frontendu | ne zamjenjuje | provedbu pravila vidljivosti u API-ju | — | API | brief · Visibility · "Hiding data in the frontend doesn't count." | "Hiding data in the frontend doesn't count." |
| B61 | Ekran My time off | prikazuje | listu mojih zahtjeva sa statusom | — | ekran | brief · Screens · "1. My time off" | "A list of my requests with their status" |
| B62 | Ekran My time off | ima | formu za novi zahtjev | — | ekran | brief · Screens · "1. My time off" | "a form to create a new one" |
| B63 | Ekran My time off | omogućuje | otkazivanje zahtjeva | — | ekran | brief · Screens · "1. My time off" | "a way to cancel" |
| B64 | Ekran My time off | prikazuje | moj saldo godišnjeg | — | ekran | brief · Screens · "1. My time off" | "and my vacation balance" |
| B65 | Ekran Approvals | je dostupan | samo voditeljima i adminima | — | ekran | brief · Screens · "2. Approvals" | "For team leads and admins only" |
| B66 | Ekran Approvals | prikazuje | zahtjeve na čekanju koje korisnik smije odobriti | — | ekran | brief · Screens · "2. Approvals" | "a list of pending requests they are allowed to act on" |
| B67 | Ekran Approvals | ima akcije | odobri i odbij | — | ekran | brief · Screens · "2. Approvals" | "with approve and reject actions" |
| B68 | Ekran Team calendar | prikazuje | tko je odsutan u odabranom mjesecu | — | ekran | brief · Screens · "3. Team calendar" | "Who is away during a selected month" |
| B69 | Ekran Team calendar | prikazuje odsutnosti | po pravilima vidljivosti | — | ekran | brief · Screens · "3. Team calendar" | "following the visibility rules above" |
| B70 | Ekran Team calendar | može biti | popis grupiran po danima | — | ekran | brief · Screens · "3. Team calendar" | "A list grouped by day is fine." |
| B71 | Ekran | prikazuje | poruku greške kad backend odbije zahtjev | npr. preklapanje ili premašen saldo | ekran | brief · Screens · "Show useful error messages" | "Show useful error messages when the backend rejects something, for example an overlap or an exceeded balance." |
| B72 | Aplikacija | ima najmanje tri ekrana | — | — | ekran | brief · Screens · "We need at least these three screens." | "We need at least these three screens." |
| B73 | Aplikacija (dodatak) | može isključiti | javne blagdane iz broja dana (Hrvatska ili Belgija) | opcionalno | nije rečeno | brief · Optional extras · prvi redak | "Exclude public holidays from day counts. Pick Croatia or Belgium." |
| B74 | Aplikacija (dodatak) | može podržati | polovicu dana u zahtjevu | opcionalno | nije rečeno | brief · Optional extras · "Half-day requests." | "Half-day requests." |
| B75 | Aplikacija (dodatak) | može prikazivati | povijest izmjena po zahtjevu, tko i kada | opcionalno | nije rečeno | brief · Optional extras · "An audit history" | "An audit history for each request showing who changed it and when." |
| B76 | Aplikacija (dodatak) | može slati | obavijest voditelju kad stigne novi zahtjev (konzola je dovoljna) | opcionalno | nije rečeno | brief · Optional extras · "A notification to the team lead" | "A notification to the team lead when a new request arrives. Logging it to the console is fine." |
| B77 | Aplikacija (dodatak) | može se pokretati | baza, backend i frontend zajedno, Docker Compose | opcionalno | nije rečeno | brief · Optional extras · "Docker Compose" | "Docker Compose to start the database, backend and frontend together." |
| B78 | Aplikacija | nema | pravu autentikaciju ni SSO | izvan opsega | nije rečeno | brief · Out of scope · "Real authentication" | "Real authentication or SSO" |
| B79 | Aplikacija | nema | deployment | izvan opsega | nije rečeno | brief · Out of scope · "Deployment" | "Deployment" |
| B80 | Aplikacija | nema | ekrane za upravljanje korisnicima i timovima | izvan opsega | nije rečeno | brief · Out of scope · "User and team management screens" | "User and team management screens" |
| B81 | Aplikacija | nema | prijevode | izvan opsega | nije rečeno | brief · Out of scope · "Translations" | "Translations" |
| B82 | Aplikacija | nema | dizajn piksel-savršen | izvan opsega | nije rečeno | brief · Out of scope · "Pixel-perfect design" | "Pixel-perfect design" |

Ukupno redaka u brief.md: **82**

## atomarna-pitanja.md

Samo odgovori kolege. Pitanja, pretpostavke i napomene o procesu nisu uključeni.

| # | Tko | Radi ili vidi | Nad čim | U kojem stanju / uvjetu | Gdje (API, ekran, oboje, nije rečeno) | Izvor (dokument, odjeljak, riječ za Ctrl+F) | Točan citat |
| --- | --- | --- | --- | --- | --- | --- | --- |
| A1 | Admin | je zaposlenik | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Je li admin zaposlenik?" (Q1) | "Da (npr. direktor koji prati i svoj godišnji)" |
| A2 | Admin | može biti voditelj tima | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li admin biti voditelj tima?" (Q2) | "Da" |
| A3 | Voditelj tima | je član tima koji vodi | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Je li voditelj član tima koji vodi?" (Q3) | "Da, i može voditi više timova" |
| A4 | Voditelj tima | može voditi više timova | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Je li voditelj član tima koji vodi?" (Q3) | "Da, i može voditi više timova" |
| A5 | Zaposlenik | može biti bez tima | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li zaposlenik biti bez tima?" (Q4) | "Da (npr. radi sam); njegovi zahtjevi idu adminu" |
| A6 | Zahtjevi zaposlenika bez tima | idu | adminu | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li zaposlenik biti bez tima?" (Q4) | "Da (npr. radi sam); njegovi zahtjevi idu adminu" |
| A7 | Zaposlenik | može biti u više timova | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li zaposlenik biti u više timova?" (Q5) | "Da" |
| A8 | Tim | ne može imati više voditelja | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li tim imati više voditelja?" (Q6) | "Ne" |
| A9 | Tim | ne može biti bez voditelja | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li tim biti bez voditelja?" (Q7) | "Ne" |
| A10 | Aplikacija | može imati više admina | — | — | nije rečeno | atomarna · Iteracija 1 / Uloge i timovi · "Može li biti više admina?" (Q8) | "Da" |
| A11 | Day off | je | slobodan dan | — | nije rečeno | atomarna · Iteracija 1 / Tipovi odsustva · "Što je \"Day off\"?" (Q9) | "Slobodan dan" |
| A12 | Bolovanje | nema | limit dana | — | nije rečeno | atomarna · Iteracija 1 / Tipovi odsustva · "Ima li bolovanje limit dana?" (Q10) | "Ne" |
| A13 | Slobodan dan | nema | limit dana | — | nije rečeno | atomarna · Iteracija 1 / Tipovi odsustva · "Ima li slobodan dan limit dana?" (Q11) | "Ne" |
| A14 | Slobodan dan | može trajati | više dana zaredom | — | nije rečeno | atomarna · Iteracija 1 / Tipovi odsustva · "Smije li slobodan dan trajati više dana zaredom?" (Q12) | "Da" |
| A15 | Bolovanje | ne traži | liječničku potvrdu | — | nije rečeno | atomarna · Iteracija 1 / Tipovi odsustva · "Treba li uz bolovanje priložiti liječničku potvrdu?" (Q13) | "Ne" |
| A16 | Neiskorišteni dani | se prenose | u sljedeću godinu | — | nije rečeno | atomarna · Iteracija 1 / Saldo · "Prenose li se neiskorišteni dani" (Q14) | "Da, kao u Hrvatskoj" |
| A17 | Zaposlenik koji počne usred godine | dobiva | razmjerni broj dana | — | nije rečeno | atomarna · Iteracija 1 / Saldo · "Dobiva li manje dana tko počne raditi usred godine?" (Q15) | "Da, razmjerno" |
| A18 | Voditelj | vidi | saldo članova svog tima | — | nije rečeno | atomarna · Iteracija 1 / Saldo · "Vidi li voditelj saldo članova svog tima?" (Q16) | "Da" |
| A19 | Admin | vidi | saldo svih korisnika | — | nije rečeno | atomarna · Iteracija 1 / Saldo · "Vidi li admin saldo svih korisnika?" (Q17) | "Da" |
| A20 | Saldo | se provjerava | za godinu u kojoj zahtjev pada | za tekuću godinu: prvo se troše dani iz prijašnje godine, ako ih ima | nije rečeno | atomarna · Iteracija 1 / Saldo · "Provjerava li se saldo za godinu" (Q18) | "Za tekucu, ako ima od prijasnje godine, prijasnji se prvi trosi" |
| A21 | Godišnji | smije počinjati | u prošlosti | "ako se zaboravi unijeti" | nije rečeno | atomarna · Iteracija 1 / Datumi · "Smije li godišnji počinjati u prošlosti?" (Q19) | "Da, ako se zaboravi unijeti" |
| A22 | Bolovanje | smije počinjati | u prošlosti | "ako se zaboravi unijeti" | nije rečeno | atomarna · Iteracija 1 / Datumi · "Smije li bolovanje počinjati u prošlosti?" (Q20) | "Da, ako se zaboravi unijeti" |
| A23 | Slobodan dan | smije počinjati | u prošlosti | "ako se zaboravi unijeti" | nije rečeno | atomarna · Iteracija 1 / Datumi · "Smije li slobodan dan počinjati u prošlosti?" (Q21) | "Da, ako se zaboravi unijeti" |
| A24 | Bolovanje | smije počinjati | u budućnosti | — | nije rečeno | atomarna · Iteracija 1 / Datumi · "Smije li bolovanje počinjati u budućnosti?" (Q22) | "Da" |
| A25 | Zahtjev koji počinje danas | je "započeo" | — | — | nije rečeno | atomarna · Iteracija 1 / Datumi · "Je li zahtjev koji počinje danas" (Q23) | "Da" |
| A26 | Aplikacija | računa "danas" | u vremenskoj zoni servera na koji se deploya | — | nije rečeno | atomarna · Iteracija 1 / Datumi · "U kojoj se vremenskoj zoni računa" (Q24) | "Vremenska zona servera na koji se deploya" |
| A27 | Aplikacija | ograničava unaprijed traženje | nije ograničeno (proizvoljno) | — | nije rečeno | atomarna · Iteracija 1 / Datumi · "Koliko se unaprijed smije tražiti?" (Q25) | "Proizvoljno" |
| A28 | Aplikacija | nema | minimalnog roka najave | — | nije rečeno | atomarna · Iteracija 1 / Datumi · "Postoji li minimalan rok najave" (Q26) | "Nema" |
| A29 | Aplikacija | nema | najdulјeg trajanja zahtjeva | — | nije rečeno | atomarna · Iteracija 1 / Datumi · "Postoji li najdulje trajanje jednog zahtjeva?" (Q27) | "Nema" |
| A30 | API | dijeli zahtjev | preko granice godine | — | API | atomarna · Iteracija 1 / Datumi · "Što API radi sa zahtjevom preko granice godine" (Q28) | "Dijeli; i dalje je jedan zahtjev" |
| A31 | Zahtjev | ostaje jedan | pri podjeli preko granice godine | — | API | atomarna · Iteracija 1 / Datumi · "Što API radi sa zahtjevom preko granice godine" (Q28) | "Dijeli; i dalje je jedan zahtjev" |
| A32 | Zaposlenik | ne može podnijeti zahtjev | u ime drugog korisnika | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li netko podnijeti zahtjev u ime drugog" (Q29) | "Ne" |
| A33 | Voditelj | može otkazati | tuđi zahtjev | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li voditelj otkazati tuđi zahtjev?" (Q30) | "Moze" |
| A34 | Admin | može otkazati | tuđi zahtjev | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li admin otkazati tuđi zahtjev?" (Q31) | "Da" |
| A35 | Voditelj ili admin | može povući | već dano odobrenje | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li voditelj ili admin povući" (Q32) | "Da (isto kao otkazivanje, vidi 30 i 31)" |
| A36 | Zahtjev na čekanju | se ne uređuje | — | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li se uređivati zahtjev na čekanju?" (Q33) | "Ne" |
| A37 | Odobreni zahtjev | se ne uređuje | — | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li se uređivati odobreni zahtjev?" (Q34) | "Ne, otkaži i podnesi novi" |
| A38 | Aplikacija | odobrava zahtjev | čiji su datumi već prošli | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Smije li se odobriti zahtjev čiji su datumi već prošli?" (Q35) | "Da" |
| A39 | Zahtjev na čekanju | ne ističe | kad prođu njegovi datumi | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Ističe li zahtjev na čekanju" (Q36) | "Ne" |
| A40 | Otkazivanje | može imati | razlog (polje) | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Treba li razlog za otkazivanje" (Q37) | "Moze, nema bude field" |
| A41 | Aplikacija | nema | najvećeg broja zahtjeva na čekanju po korisniku | — | nije rečeno | atomarna · Iteracija 1 / Stanja i otkazivanje · "Postoji li najveći broj zahtjeva na čekanju" (Q38) | "Ne" |
| A42 | Aplikacija | računa preklapanje | samo po radnim danima | — | nije rečeno | atomarna · Iteracija 1 / Preklapanja · "Je li preklapanje samo na vikendu" (Q39) | "Nije razumio pitanje. Naša odluka: preklapanje se računa samo po radnim danima" |
| A43 | Tim | nema | minimalnog broja prisutnih članova | — | nije rečeno | atomarna · Iteracija 1 / Preklapanja · "Postoji li minimalan broj ljudi" (Q40) | "Ne" |
| A44 | Zaposlenik | vidi | svoje zahtjeve na čekanju na kalendaru tima | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Vidi li zaposlenik svoje zahtjeve na čekanju" (Q41) | "Da" |
| A45 | Voditelj | vidi | zahtjeve na čekanju na kalendaru tima | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Vidi li voditelj zahtjeve na čekanju" (Q42) | "Da" |
| A46 | Admin | vidi kalendar | svih timova, odjednom i po timu, s filtrom | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Vidi li admin kalendar svih timova" (Q43) | "Oboje, s filtrom; da se vidi stanje ljudi u firmi" |
| A47 | Komentar odbijanja | vide | vlasnik i oni koji smiju odobravati | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Tko vidi komentar odbijanja?" (Q44) | "Da (slaže se)" |
| A48 | Komentar odobrenja | vide | vlasnik i oni koji smiju odobravati | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Tko vidi komentar odobrenja?" (Q45) | "Da (slaže se)" |
| A49 | Zaposlenik | vidi | tko je odobrio ili odbio njegov zahtjev | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Vidi li zaposlenik tko je odobrio" (Q46) | "Da" |
| A50 | Zaposlenik | vidi | bolovanje kolege na kalendaru kao "odsutan" bez tipa | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Vidi li zaposlenik bolovanje kolege" (Q47) | "Da" |
| A51 | Kalendar voditelja i admina | ne prikazuje | odbijene i otkazane zahtjeve | — | nije rečeno | atomarna · Iteracija 1 / Vidljivost · "Prikazuje li kalendar voditelju i adminu odbijene" (Q48) | "Ne, inače je pretrpano" |
| A52 | Pozivi API-ja kao korisnik | koriste | JWT | — | API | atomarna · Iteracija 1 / API · "Kojim mehanizmom ćete zvati API" (Q49) | "JWT" |
| A53 | API | koristi | standardne HTTP statuse; obrada grešaka po volji | — | API | atomarna · Iteracija 1 / API · "Imate li preferencije za HTTP statuse" (Q50) | "Standardni statusi, obrada grešaka po volji" |
| A54 | Dokumentacija API-ja | je | dijagrami tokova i sekvencijski dijagrami, bez Swaggera | — | nije rečeno | atomarna · Iteracija 1 / API · "Treba li dokumentacija API-ja" (Q51) | "Ne Swagger; dijagrami tokova i sekvencijski dijagrami" |
| A55 | Aplikacija | ne prijavljuje | stvarno utrošeno vrijeme | — | nije rečeno | atomarna · Iteracija 1 / Isporuka · "Treba li prijaviti stvarno utrošeno vrijeme?" (Q52) | "Ne" |
| A56 | Aplikacija | je u Docker kontejneru | — | — | nije rečeno | atomarna · Iteracija 1 / Isporuka · "Imate li Docker na računalu?" (Q53) | "Neka bude u kontejneru" |
| A57 | UI, README i kod | su na engleskom | — | — | nije rečeno | atomarna · Iteracija 1 / Isporuka · "Na kojem jeziku su UI, README i kod?" (Q54) | "Svejedno, preferira engleski" |
| A58 | Seed | nema primjere zahtjeva | — | — | nije rečeno | atomarna · Iteracija 1 / Isporuka · "Trebaju li u seedu biti i primjeri zahtjeva?" (Q55) | "Ne trebaju; upute za pokretanje u README" |
| A59 | Bolovanje | smije preklopiti | odobreni godišnji | — | nije rečeno | atomarna · Iteracija 1 / Prijedlozi izmjene pravila · "Smije li bolovanje preklopiti odobreni godišnji?" (Q57) | "Brate mili mozes otic na bolovanje kad si na GO." |
| A60 | Odobreni godišnji | se otkazuje ili refundira | kad se bolovanje preklopi s njim | — | nije rečeno | atomarna · Iteracija 1 / Prijedlozi izmjene pravila · "Smije li bolovanje preklopiti odobreni godišnji?" (Q57) | "onda ti se GO otkazuje / refundira" |
| A61 | Admin | ima | saldo | — | nije rečeno | atomarna · Iteracija 1 / Odgođeno iz iteracije 1 · "Ima li admin saldo?" | "Riješeno: da, admin je zaposlenik" |
| A62 | Admin | je član | tima koji vodi, kad je voditelj | — | nije rečeno | atomarna · Iteracija 1 / Odgođeno iz iteracije 1 · "Je li admin član tima?" | "Riješeno: kad je voditelj, član je tima koji vodi" |
| A63 | Odsustvo admina | se vidi | na kalendaru timova kojima pripada; bez tima samo adminima | — | nije rečeno | atomarna · Iteracija 1 / Odgođeno iz iteracije 1 · "Na čijem se kalendaru vidi odsustvo admina?" | "Riješeno: na kalendaru timova kojima pripada; bez tima ga vide samo admini" |
| A64 | Dani godišnjeg | su | radni dani | — | nije rečeno | atomarna · Iteracija 1 / Maknuto s popisa · "Jesu li 20 dana godišnjeg radni" | "Pravilo 10: broje se samo radni dani" |
| A65 | Odbijeni zahtjev | se može ponovno otvoriti | kao novi zahtjev | — | nije rečeno | atomarna · Iteracija 1 / Maknuto s popisa · "Može li se odbijeni zahtjev ponovno otvoriti?" | "Isto kao novi zahtjev" |
| A66 | Zahtjev | se ne može djelomično odobriti | — | — | nije rečeno | atomarna · Iteracija 1 / Maknuto s popisa · "Može li se zahtjev djelomično odobriti?" | "Isto kao odbijanje pa novi zahtjev" |
| A67 | Admin | smije odobriti | vlastiti zahtjev za godišnji | — | nije rečeno | atomarna · Iteracija 2 · "Smije li admin odobriti vlastiti zahtjev" (1.1) | "Da" |
| A68 | Voditelj bilo kojeg od timova | smije odobriti | zahtjev zaposlenika u više timova, dovoljno je jedno odobrenje | — | nije rečeno | atomarna · Iteracija 2 · "Smije li zahtjev zaposlenika u više timova" (5.1) | "Da" |
| A69 | Aplikacija | ne prati | kako zaposlenik stječe slobodne dane | — | nije rečeno | atomarna · Iteracija 2 · "Treba li aplikacija pratiti kako zaposlenik stječe" (9.1) | "Not for now" |
| A70 | Preneseni dani | vrijede | do 30. lipnja sljedeće godine | — | nije rečeno | atomarna · Iteracija 2 · "Do kojeg datuma vrijede preneseni dani?" (14.1) | "Yes" |
| A71 | Aplikacija | nema | gornje granice broja prenesenih dana | — | nije rečeno | atomarna · Iteracija 2 · "Postoji li najveći broj dana koji se smije prenijeti?" (14.2) | "Nope" |
| A72 | Razmjerni saldo | se računa | po mjesecima | — | nije rečeno | atomarna · Iteracija 2 · "Računa li se razmjerni saldo po mjesecima ili po danima?" (15.1) | "Mjeseci" |
| A73 | Razmjerni saldo | se zaokružuje | prema gore (Ceil) | — | nije rečeno | atomarna · Iteracija 2 · "Kako se zaokružuje razmjerni saldo?" (15.2) | "Ceil" |
| A74 | Aplikacija | nema | granice koliko unatrag se smije podnijeti zahtjev | — | nije rečeno | atomarna · Iteracija 2 · "Postoji li granica koliko se unatrag" (19.1) | "Nope" |
| A75 | Voditelj | ne može otkazati | odobreni zahtjev člana tima koji je već počeo | stanje: Approved, počeo | nije rečeno | atomarna · Iteracija 2 · "Smije li voditelj otkazati odobreni zahtjev" (30.1) | "Nope, nema dizanja sa godisnjeg." |
| A76 | Izmjene | se rade | naknadno (after the fact) | za već započete zahtjeve | nije rečeno | atomarna · Iteracija 2 · "Smije li voditelj otkazati odobreni zahtjev" (30.1) | "Po potrebi ako se i dogodi, neka after the fact izmjene" |
| A77 | Admin | ne može otkazati | odobreni zahtjev koji je već počeo | stanje: Approved, počeo | nije rečeno | atomarna · Iteracija 2 · "Smije li admin otkazati odobreni zahtjev koji je već počeo?" (31.1) | "Vidi gore" |
| A78 | Admin | nema pregled | tko je danas prisutan u tvrtki | — | nije rečeno | atomarna · Iteracija 2 · "Treba li adminu pregled tko je danas prisutan" (43.1) | "Nope" |
| A79 | Prijava | traži | lozinku | — | nije rečeno | atomarna · Iteracija 2 · "Traži li prijava lozinku?" (49.1) | "Da" |
| A80 | Ekran prijave | ima | popis korisnika za brzi odabir (pretpostavka uz pitanje 49.1, potvrđena odgovorom) | — | ekran | atomarna · Iteracija 2 · "Traži li prijava lozinku?" (49.1) | "Da" |
| A81 | PoC | računa na | oko 20 korisnika, i manje | — | nije rečeno | atomarna · Iteracija 2 · "Koliko otprilike korisnika aplikacija treba podržati?" (53.1) | "Za PoC racunaj i manje 20-ak" |
| A82 | PoC | radi | lokalno u Dockeru, bez deploya | — | nije rečeno | atomarna · Iteracija 2 · "Treba li POC biti deployan negdje" (53.2) | "Dovoljno u dockeru" |
| A83 | Aplikacija | nema | AI funkcionalnost | — | nije rečeno | atomarna · Iteracija 2 · "Očekuje li se AI funkcionalnost unutar same aplikacije?" (53.4) | "Ne" |

Ukupno redaka u atomarna-pitanja.md: **83**

## specifikacija.md

| # | Tko | Radi ili vidi | Nad čim | U kojem stanju / uvjetu | Gdje (API, ekran, oboje, nije rečeno) | Izvor (dokument, odjeljak, riječ za Ctrl+F) | Točan citat |
| --- | --- | --- | --- | --- | --- | --- | --- |
| S1 | Sustav | ima uloge | Employee, Team lead, Admin | — | nije rečeno | spec · 1. Uloge i timovi · "Uloge su" | "Uloge su Employee, Team lead i Admin." |
| S2 | Korisnik | može imati više uloga | — | — | nije rečeno | spec · 1. Uloge i timovi · "Korisnik može imati više uloga" | "Korisnik može imati više uloga, npr. admin koji je i voditelj" |
| S3 | Svaki korisnik | je zaposlenik | — | — | nije rečeno | spec · 1. Uloge i timovi · "Svaki korisnik je zaposlenik" | "Svaki korisnik je zaposlenik, pa i admin" |
| S4 | Tim | ima | točno jednog voditelja | — | nije rečeno | spec · 1. Uloge i timovi · "Tim ima točno jednog voditelja" | "Tim ima točno jednog voditelja" |
| S5 | Voditelj | je član | svakog tima koji vodi | — | nije rečeno | spec · 1. Uloge i timovi · "Voditelj je član svakog tima" | "Voditelj je član svakog tima koji vodi i može voditi više timova" |
| S6 | Voditelj | može voditi | više timova | — | nije rečeno | spec · 1. Uloge i timovi · "Voditelj je član svakog tima" | "može voditi više timova" |
| S7 | Zaposlenik | može biti | u nijednom, jednom ili više timova | — | nije rečeno | spec · 1. Uloge i timovi · "Zaposlenik može biti u nijednom" | "Zaposlenik može biti u nijednom, jednom ili više timova" |
| S8 | Aplikacija | može imati više admina | — | — | nije rečeno | spec · 1. Uloge i timovi · "Može biti više admina" | "Može biti više admina" |
| S9 | Zahtjev | ima podnositelja | — | obavezno | nije rečeno | spec · 2. Zahtjev · tablica "Podnositelj" | "Podnositelj \| da" |
| S10 | Zahtjev | ima tip: Vacation, Sick leave, Day off | — | obavezno | nije rečeno | spec · 2. Zahtjev · tablica "Tip" | "Tip: Vacation, Sick leave, Day off \| da" |
| S11 | Zahtjev | ima početni i završni datum, oba uključiva | — | obavezno | nije rečeno | spec · 2. Zahtjev · tablica "Početni i završni datum" | "Početni i završni datum, oba uključiva \| da" |
| S12 | Zahtjev | ima razlog | — | opcionalno | nije rečeno | spec · 2. Zahtjev · tablica "Razlog" | "Razlog \| ne" |
| S13 | Zahtjev | ima stanje: Pending, Approved, Rejected, Cancelled | — | obavezno | nije rečeno | spec · 2. Zahtjev · tablica "Stanje" | "Stanje: Pending, Approved, Rejected, Cancelled \| da" |
| S14 | Zahtjev | ima komentar odluke | — | samo kod odbijanja | nije rečeno | spec · 2. Zahtjev · tablica "Komentar odluke" | "Komentar odluke \| samo kod odbijanja" |
| S15 | Zahtjev | bilježi tko je odlučio | — | uz odluku | nije rečeno | spec · 2. Zahtjev · tablica "Tko je odlučio" | "Tko je odlučio \| uz odluku" |
| S16 | Zahtjev | ima razlog otkazivanja | — | obavezan kad ne otkazuje vlasnik | nije rečeno | spec · 2. Zahtjev · tablica "Razlog otkazivanja" | "obavezan kad ne otkazuje vlasnik" |
| S17 | Zahtjev | bilježi tko je otkazao | — | uz otkazivanje | nije rečeno | spec · 2. Zahtjev · tablica "Tko je otkazao" | "Tko je otkazao \| uz otkazivanje" |
| S18 | Zahtjev | bilježi vrijeme stvaranja, odluke ili otkazivanja | — | obavezno | nije rečeno | spec · 2. Zahtjev · tablica "Vrijeme stvaranja" | "Vrijeme stvaranja, vrijeme odluke ili otkazivanja \| da" |
| S19 | Day off | nema limit | — | — | nije rečeno | spec · 2. Zahtjev · "Day off i Sick leave" | "Day off i Sick leave nemaju limit ni vlastiti saldo" |
| S20 | Sick leave | nema limit | — | — | nije rečeno | spec · 2. Zahtjev · "Day off i Sick leave" | "Day off i Sick leave nemaju limit ni vlastiti saldo" |
| S21 | Day off i Sick leave | nemaju vlastiti saldo | — | — | nije rečeno | spec · 2. Zahtjev · "Day off i Sick leave" | "nemaju limit ni vlastiti saldo" |
| S22 | Day off i Sick leave | mogu trajati | više dana | — | nije rečeno | spec · 2. Zahtjev · "Mogu trajati više dana" | "Mogu trajati više dana" |
| S23 | Bolovanje | ne prilaže | potvrdu | — | nije rečeno | spec · 2. Zahtjev · "Potvrda se ne prilaže" | "Potvrda se ne prilaže" |
| S24 | Aplikacija | ne prati | kako se stječu slobodni dani | — | nije rečeno | spec · 2. Zahtjev · "Aplikacija ne prati" | "Aplikacija ne prati kako se stječu slobodni dani" |
| S25 | Zahtjev | se ne uređuje | otkaže se i podnese novi | — | nije rečeno | spec · 2. Zahtjev · "Zahtjev se ne uređuje" | "Zahtjev se ne uređuje: otkaže se i podnese novi" |
| S26 | Zahtjev | podnosi | samo vlasnik, nitko u ime drugoga | — | nije rečeno | spec · 2. Zahtjev · "Zahtjev podnosi samo vlasnik" | "Zahtjev podnosi samo vlasnik, nitko u ime drugoga" |
| S27 | Voditelj | odobrava i odbija | zahtjeve članova svih timova koje vodi | — | nije rečeno | spec · 3. Odobravanje · "Voditelj odobrava i odbija" | "Voditelj odobrava i odbija zahtjeve članova svih timova koje vodi" |
| S28 | Voditelj bilo kojeg tima člana više timova | smije odobriti | zahtjev tog člana | — | nije rečeno | spec · 3. Odobravanje · "Zahtjev člana više timova" | "Zahtjev člana više timova smije odobriti voditelj bilo kojeg od tih timova" |
| S29 | Aplikacija | dovoljno je | jedno odobrenje | za zahtjev člana više timova | nije rečeno | spec · 3. Odobravanje · "dovoljno je jedno odobrenje" | "dovoljno je jedno odobrenje" |
| S30 | Zahtjev voditelja | ide | adminu | — | nije rečeno | spec · 3. Odobravanje · "Zahtjev voditelja ide adminu" | "Zahtjev voditelja ide adminu, i kad je voditelj član tuđeg tima" |
| S31 | Zahtjev voditelja | ide | adminu i kad je voditelj član tuđeg tima | — | nije rečeno | spec · 3. Odobravanje · "Zahtjev voditelja ide adminu" | "i kad je voditelj član tuđeg tima" |
| S32 | Zahtjev zaposlenika bez tima | odobrava | admin | — | nije rečeno | spec · 3. Odobravanje · "Zahtjev zaposlenika bez tima" | "Zahtjev zaposlenika bez tima odobrava admin" |
| S33 | Admin | odobrava i odbija | bilo koji zahtjev | — | nije rečeno | spec · 3. Odobravanje · "Admin odobrava i odbija" | "Admin odobrava i odbija bilo koji zahtjev" |
| S34 | Nitko | ne odobrava ni ne odbija | vlastiti zahtjev | — | nije rečeno | spec · 3. Odobravanje · "Nitko ne odobrava ni ne odbija" | "Nitko ne odobrava ni ne odbija vlastiti zahtjev" |
| S35 | Admin | smije odobriti | vlastiti zahtjev | — | nije rečeno | spec · 3. Odobravanje · "Iznimka: admin smije odobriti" | "Iznimka: admin smije odobriti vlastiti zahtjev" |
| S36 | Admin | smije odobriti | vlastiti zahtjev bilo kojeg tipa | — | nije rečeno | spec · 3. Odobravanje · "bilo kojeg tipa" | "bilo kojeg tipa" |
| S37 | Aplikacija | odobrava i odbija | samo zahtjev na čekanju | stanje: Pending | nije rečeno | spec · 3. Odobravanje · "Odobrava se i odbija samo zahtjev na čekanju" | "Odobrava se i odbija samo zahtjev na čekanju" |
| S38 | Aplikacija | odobrava i odbija | zahtjev kojem su datumi prošli | — | nije rečeno | spec · 3. Odobravanje · "i kad su mu datumi prošli" | "i kad su mu datumi prošli" |
| S39 | Odbijanje | traži | komentar | — | nije rečeno | spec · 3. Odobravanje · "Odbijanje traži komentar" | "Odbijanje traži komentar, odobrenje ne" |
| S40 | Odobrenje | ne traži | komentar | — | nije rečeno | spec · 3. Odobravanje · "Odbijanje traži komentar, odobrenje ne" | "odobrenje ne" |
| S41 | Zahtjev na čekanju | ne istječe | — | — | nije rečeno | spec · 3. Odobravanje · "Zahtjev na čekanju ne istječe" | "Zahtjev na čekanju ne istječe" |
| S42 | Tko smije odobriti zahtjev | smije ga i otkazati | — | — | nije rečeno | spec · 4. Otkazivanje · "Tko smije odobriti zahtjev, smije ga i otkazati" | "Tko smije odobriti zahtjev, smije ga i otkazati" |
| S43 | Vlasnik | otkazuje | zahtjev na čekanju, razlog opcionalan | stanje: Pending | nije rečeno | spec · 4. Otkazivanje · tablica "Vlasnik" | "Otkazuje, razlog opcionalan" |
| S44 | Vlasnik | otkazuje | odobreni zahtjev koji nije počeo, razlog opcionalan | stanje: Approved, nije počeo | nije rečeno | spec · 4. Otkazivanje · tablica "Vlasnik" | "Otkazuje, razlog opcionalan" |
| S45 | Vlasnik | ne smije otkazati | odobreni zahtjev koji je počeo | stanje: Approved, počeo | nije rečeno | spec · 4. Otkazivanje · tablica "Vlasnik", stupac "Odobren, počeo" | "Ne" |
| S46 | Tko smije odobriti | otkazuje | zahtjev na čekanju, razlog obavezan | stanje: Pending | nije rečeno | spec · 4. Otkazivanje · tablica "Tko smije odobriti" | "Otkazuje, razlog obavezan" |
| S47 | Tko smije odobriti | otkazuje | odobreni zahtjev koji nije počeo, razlog obavezan | stanje: Approved, nije počeo | nije rečeno | spec · 4. Otkazivanje · tablica "Tko smije odobriti" | "Otkazuje, razlog obavezan" |
| S48 | Tko smije odobriti | ne smije otkazati | odobreni zahtjev koji je počeo | stanje: Approved, počeo | nije rečeno | spec · 4. Otkazivanje · tablica "Tko smije odobriti", stupac "Odobren, počeo" | "Ne" |
| S49 | Zahtjev koji počinje danas | je počeo | — | — | nije rečeno | spec · 4. Otkazivanje · "Zahtjev koji počinje danas je počeo" | "Zahtjev koji počinje danas je počeo" |
| S50 | Otkazani zahtjev | prelazi u | Cancelled | — | nije rečeno | spec · 4. Otkazivanje · "Otkazani zahtjev prelazi u Cancelled" | "Otkazani zahtjev prelazi u Cancelled." |
| S51 | Izmjene započetog zahtjeva | se rade | naknadno, izravno u bazi | za započeti zahtjev | nije rečeno | spec · 4. Otkazivanje · "Izmjene već započetog zahtjeva" | "Izmjene već započetog zahtjeva rade se naknadno, izravno u bazi" |
| S52 | Aplikacija | broji | samo radne dane | — | nije rečeno | spec · 5. Validacija · "Aplikacija broji samo radne dane" | "Aplikacija broji samo radne dane. Preskaču se vikendi" |
| S53 | Aplikacija | preskače | vikende | — | nije rečeno | spec · 5. Validacija · "Preskaču se vikendi" | "Preskaču se vikendi" |
| S54 | Blagdani | su | proširenje | — | nije rečeno | spec · 5. Validacija · "Blagdani su proširenje" | "Blagdani su proširenje" |
| S55 | Zahtjev bez ijednog radnog dana | je | nevaljan | — | nije rečeno | spec · 5. Validacija · "Zahtjev bez ijednog radnog dana" | "Zahtjev bez ijednog radnog dana je nevaljan" |
| S56 | Završni datum | nije | prije početnog | — | nije rečeno | spec · 5. Validacija · "Završni datum nije prije početnog" | "Završni datum nije prije početnog" |
| S57 | Korisnik | nema dva zahtjeva | na čekanju ili odobrena čiji se datumi preklapaju, bez obzira na tip | — | nije rečeno | spec · 5. Validacija · "Korisnik nema dva zahtjeva" | "Korisnik nema dva zahtjeva na čekanju ili odobrena čiji se datumi preklapaju, bez obzira na tip" |
| S58 | Zahtjev | ne počinje prije | datuma zaposlenja | — | nije rečeno | spec · 5. Validacija · "Zahtjev ne počinje prije datuma zaposlenja" | "Zahtjev ne počinje prije datuma zaposlenja" |
| S59 | Zahtjev | je unutar | jedne kalendarske godine | — | nije rečeno | spec · 5. Validacija · "Zahtjev je unutar jedne kalendarske godine" | "Zahtjev je unutar jedne kalendarske godine" |
| S60 | Zahtjev preko granice godine | je | proširenje | — | nije rečeno | spec · 5. Validacija · "Zahtjev preko granice godine je proširenje" | "Zahtjev preko granice godine je proširenje" |
| S61 | Datumi u prošlosti | su dopušteni | za sve tipove, bez granice unatrag | — | nije rečeno | spec · 5. Validacija · "Datumi u prošlosti su dopušteni" | "Datumi u prošlosti su dopušteni za sve tipove, bez granice unatrag" |
| S62 | Datumi u budućnosti | su dopušteni | bez granice | — | nije rečeno | spec · 5. Validacija · "Datumi u budućnosti su dopušteni bez granice" | "Datumi u budućnosti su dopušteni bez granice" |
| S63 | Aplikacija | nema | minimalnog roka najave | — | nije rečeno | spec · 5. Validacija · "Nema minimalnog roka najave" | "Nema minimalnog roka najave ni najduljeg trajanja" |
| S64 | Aplikacija | nema | najduljeg trajanja zahtjeva | — | nije rečeno | spec · 5. Validacija · "Nema minimalnog roka najave ni najduljeg trajanja" | "Nema minimalnog roka najave ni najduljeg trajanja" |
| S65 | Aplikacija | nema | najvećeg broja zahtjeva na čekanju | — | nije rečeno | spec · 5. Validacija · "Nema najvećeg broja zahtjeva na čekanju" | "Nema najvećeg broja zahtjeva na čekanju" |
| S66 | Aplikacija | nema | minimalne prisutnosti u timu | — | nije rečeno | spec · 5. Validacija · "ni minimalne prisutnosti u timu" | "ni minimalne prisutnosti u timu" |
| S67 | Svaki korisnik | dobiva | 20 radnih dana godišnjeg | za svaku kalendarsku godinu | nije rečeno | spec · 6. Saldo godišnjeg · "Svaki korisnik dobiva 20 radnih dana" | "Svaki korisnik dobiva 20 radnih dana godišnjeg za svaku kalendarsku godinu" |
| S68 | Korisnik u godini zaposlenja | dobiva | razmjerni dio: 20 × broj mjeseci / 12, zaokruženo prema gore | u godini zaposlenja | nije rečeno | spec · 6. Saldo godišnjeg · "U godini zaposlenja dobiva razmjerni dio" | "U godini zaposlenja dobiva razmjerni dio: 20 × broj mjeseci / 12, zaokruženo prema gore" |
| S69 | Broje se mjeseci | od mjeseca zaposlenja | do prosinca, uključivo | u godini zaposlenja | nije rečeno | spec · 6. Saldo godišnjeg · "Broje se mjeseci od mjeseca zaposlenja do prosinca" | "Broje se mjeseci od mjeseca zaposlenja do prosinca, uključivo" |
| S70 | Zaposlenik zaposlen u lipnju (7 mjeseci) | dobiva | 12 dana (primjer) | u godini zaposlenja | nije rečeno | nije rečeno | spec · 6. Saldo godišnjeg · "Primjer: zaposlen u lipnju" | "Primjer: zaposlen u lipnju, 7 mjeseci, 20 × 7 / 12 = 11,67, dakle 12 dana." |
| S71 | Korisnik | vidi za tekuću godinu | iskorištene (odobrene) dane, dane na čekanju i preostale dane | za tekuću godinu | nije rečeno | spec · 6. Saldo godišnjeg · "Za tekuću godinu korisnik vidi" | "Za tekuću godinu korisnik vidi iskorištene (odobrene), dane na čekanju i preostale dane" |
| S72 | Zahtjev za godišnji | ne smije premašiti | preostali saldo godine u koju pada | — | nije rečeno | spec · 6. Saldo godišnjeg · "Zahtjev za godišnji ne smije premašiti" | "Zahtjev za godišnji ne smije premašiti preostali saldo godine u koju pada" |
| S73 | Zahtjevi na čekanju | troše | saldo | — | nije rečeno | spec · 6. Saldo godišnjeg · "Zahtjevi na čekanju troše saldo" | "Zahtjevi na čekanju troše saldo" |
| S74 | Bolovanje | ne troši | saldo | — | nije rečeno | spec · 6. Saldo godišnjeg · "Bolovanje i slobodni dani ne troše saldo" | "Bolovanje i slobodni dani ne troše saldo" |
| S75 | Slobodni dani | ne troše | saldo | — | nije rečeno | spec · 6. Saldo godišnjeg · "Bolovanje i slobodni dani ne troše saldo" | "Bolovanje i slobodni dani ne troše saldo" |
| S76 | Neiskorišteni dani | se ne prenose | u sljedeću godinu | — | nije rečeno | spec · 6. Saldo godišnjeg · "Neiskorišteni dani se ne prenose" | "Neiskorišteni dani se ne prenose u sljedeću godinu" |
| S77 | Korekcije | se rade | izravno u bazi, izmjenom zahtjeva ili datuma zaposlenja | — | nije rečeno | spec · 6. Saldo godišnjeg · "Korekcije se rade izravno u bazi" | "Korekcije se rade izravno u bazi, izmjenom zahtjeva ili datuma zaposlenja" |
| S78 | API | provodi | sva pravila vidljivosti | — | API | spec · 7. Vidljivost · "API provodi sva pravila vidljivosti" | "API provodi sva pravila vidljivosti" |
| S79 | Skrivanje u frontendu | se ne računa | kao provedba pravila | — | API | spec · 7. Vidljivost · "Skrivanje u frontendu se ne računa" | "Skrivanje u frontendu se ne računa" |
| S80 | Zaposlenik | vidi | svoje zahtjeve sa svim detaljima, uključujući komentar odluke, tko je odlučio i tko je otkazao | — | oboje | spec · 7. Vidljivost · tablica "Zaposlenik" | "Svoje zahtjeve sa svim detaljima, uključujući komentar odluke, tko je odlučio i tko je otkazao" |
| S81 | Voditelj | vidi | sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom | — | oboje | spec · 7. Vidljivost · tablica "Voditelj" | "Sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom" |
| S82 | Voditelj | vidi | saldo članova timova koje vodi | — | oboje | spec · 7. Vidljivost · tablica "Voditelj" | "Saldo članova timova koje vodi" |
| S83 | Admin | vidi | sve zahtjeve u tvrtki | — | oboje | spec · 7. Vidljivost · tablica "Admin" | "Sve zahtjeve i sve salde u tvrtki" |
| S84 | Admin | vidi | sve salde u tvrtki | — | oboje | spec · 7. Vidljivost · tablica "Admin" | "Sve zahtjeve i sve salde u tvrtki" |
| S85 | Zaposlenik | vidi na kalendaru | svoje zahtjeve na čekanju i odobrene | — | oboje | spec · 7. Vidljivost · tablica kalendara "Zaposlenik" | "Na čekanju i odobrene" |
| S86 | Zaposlenik | vidi na kalendaru | zahtjeve drugih: samo odobrene, kolega iz timova u kojima je član | — | oboje | spec · 7. Vidljivost · tablica kalendara "Zaposlenik" | "Samo odobrene, kolega iz timova u kojima je član, samo ime i datumi" |
| S87 | Zaposlenik | ne vidi na kalendaru | tip, razlog i zahtjeve na čekanju drugih | — | oboje | spec · 7. Vidljivost · tablica kalendara "Zaposlenik" | "bez tipa, razloga i zahtjeva na čekanju" |
| S88 | Voditelj | vidi na kalendaru | svoje zahtjeve na čekanju i odobrene | — | oboje | spec · 7. Vidljivost · tablica kalendara "Voditelj" | "Na čekanju i odobrene" |
| S89 | Voditelj | vidi na kalendaru | zahtjeve na čekanju i odobrene članova timova koje vodi, s tipom | — | oboje | spec · 7. Vidljivost · tablica kalendara "Voditelj" | "Na čekanju i odobrene, članova timova koje vodi, s tipom" |
| S90 | Voditelj | vidi na kalendaru | za ostale timove u kojima je član isto što i zaposlenik | — | oboje | spec · 7. Vidljivost · tablica kalendara "Voditelj" | "Za ostale timove u kojima je član vidi isto što i zaposlenik" |
| S91 | Admin | vidi na kalendaru | svoje zahtjeve na čekanju i odobrene | — | oboje | spec · 7. Vidljivost · tablica kalendara "Admin" | "Na čekanju i odobrene" |
| S92 | Admin | vidi na kalendaru | zahtjeve na čekanju i odobrene svih u tvrtki | — | oboje | spec · 7. Vidljivost · tablica kalendara "Admin" | "Na čekanju i odobrene, svih u tvrtki" |
| S93 | Odbijeni i otkazani zahtjevi | ne prikazuju se | nikome na kalendaru | — | oboje | spec · 7. Vidljivost · "Odbijeni i otkazani zahtjevi ne prikazuju" | "Odbijeni i otkazani zahtjevi ne prikazuju se nikome na kalendaru" |
| S94 | Adminov kalendar | po defaultu prikazuje | sve timove, uključujući korisnike bez tima | — | ekran | spec · 7. Vidljivost · "Adminov kalendar po defaultu" | "Adminov kalendar po defaultu prikazuje sve timove, uključujući korisnike bez tima" |
| S95 | Adminov kalendar | ima filtar | po timu | — | ekran | spec · 7. Vidljivost · "s filtrom po timu" | "s filtrom po timu" |
| S96 | Zaposlenik u više timova | se vidi | na kalendaru svakog svog tima | — | oboje | spec · 7. Vidljivost · "Zaposlenik u više timova vidi se" | "Zaposlenik u više timova vidi se na kalendaru svakog svog tima" |
| S97 | Odsutnost korisnika bez tima | vide | samo admini | — | oboje | spec · 7. Vidljivost · "Odsutnost korisnika bez tima vide samo admini" | "Odsutnost korisnika bez tima vide samo admini" |
| S98 | Prijava lozinkom | vraća | JWT | sa seedanim lozinkama | API | spec · 8. API i isporuka · "Prijava lozinkom" | "Prijava lozinkom sa seedanim lozinkama vraća JWT" |
| S99 | Svaki poziv | šalje | Authorization: Bearer | — | API | spec · 8. API i isporuka · "svaki poziv šalje" | "svaki poziv šalje `Authorization: Bearer`" |
| S100 | API | koristi | standardne HTTP statuse | — | API | spec · 8. API i isporuka · "Standardni HTTP statusi" | "Standardni HTTP statusi, format `application/problem+json`" |
| S101 | API | formatira greške | kao application/problem+json | — | API | spec · 8. API i isporuka · "format `application/problem+json`" | "format `application/problem+json`" |
| S102 | API | vraća 400 | za neispravan unos (npr. nedostaje polje, završni datum prije početnog) | neispravan unos | API | spec · 8. API i isporuka · "400: neispravan unos" | "400: neispravan unos (npr. nedostaje polje, završni datum prije početnog);" |
| S103 | API | vraća 401 | kad korisnik nije prijavljen | nije prijavljen | API | spec · 8. API i isporuka · "401: korisnik nije prijavljen" | "401: korisnik nije prijavljen;" |
| S104 | API | vraća 403 | kad korisnik nema pravo na radnju | nema pravo | API | spec · 8. API i isporuka · "403: korisnik nema pravo na radnju" | "403: korisnik nema pravo na radnju;" |
| S105 | API | vraća 404 | kad zahtjev ne postoji | ne postoji | API | spec · 8. API i isporuka · "404: zahtjev ne postoji" | "404: zahtjev ne postoji;" |
| S106 | API | vraća 409 | za kršenje pravila (preklapanje datuma, premašen saldo, krivo stanje zahtjeva) | kršenje pravila | API | spec · 8. API i isporuka · "409: kršenje pravila" | "409: kršenje pravila (preklapanje datuma, premašen saldo, krivo stanje zahtjeva)." |
| S107 | Aplikacija | nema | Swagger | — | nije rečeno | spec · 8. API i isporuka · "Bez Swaggera" | "Bez Swaggera; dijagrami tokova i sekvencijski dijagrami" |
| S108 | Aplikacija | ima | dijagrame tokova i sekvencijske dijagrame | — | nije rečeno | spec · 8. API i isporuka · "dijagrami tokova i sekvencijski dijagrami" | "dijagrami tokova i sekvencijski dijagrami" |
| S109 | Cijela aplikacija | radi u | Docker kontejnerima, pokreće se jednom naredbom | — | nije rečeno | spec · 8. API i isporuka · "Cijela aplikacija u Docker kontejnerima" | "Cijela aplikacija u Docker kontejnerima, pokretanje jednom naredbom" |
| S110 | Aplikacija | ne treba | deploy na server | — | nije rečeno | spec · 8. API i isporuka · "deploy na server ne treba" | "deploy na server ne treba" |
| S111 | UI, README i kod | su na | engleskom | — | nije rečeno | spec · 8. API i isporuka · "Sve na engleskom" | "Sve na engleskom: UI, README, kod" |
| S112 | Seed | sadrži | korisnike, timove, lozinke i datume zaposlenja | — | nije rečeno | spec · 8. API i isporuka · "Seed: korisnici" | "Seed: korisnici, timovi, lozinke i datumi zaposlenja, bez zahtjeva" |
| S113 | Seed | nema | zahtjeve | — | nije rečeno | spec · 8. API i isporuka · "bez zahtjeva" | "bez zahtjeva" |
| S114 | Svi korisnici u seedu | imaju lozinku | password | — | nije rečeno | spec · 8. API i isporuka · "Svi imaju lozinku" | "Svi imaju lozinku `password`" |
| S115 | Svi korisnici u seedu osim Toma | su zaposleni | 2020-01-01 | — | nije rečeno | spec · 8. API i isporuka · "Svi su zaposleni 2020-01-01" | "Svi su zaposleni 2020-01-01, osim Toma" |
| S116 | Tom | je zaposlen | 2026-06-01 | — | nije rečeno | spec · 8. API i isporuka · "osim Toma, zaposlenog 2026-06-01" | "osim Toma, zaposlenog 2026-06-01" |
| S117 | Aplikacija | ima POC za | oko 20 korisnika | — | nije rečeno | spec · 8. API i isporuka · "Opseg i očekivanja" · "POC za oko 20 korisnika" | "POC za oko 20 korisnika" |
| S118 | Aplikacija | nema | AI funkcionalnost | — | nije rečeno | spec · 8. API i isporuka · "U aplikaciji nema AI funkcionalnosti" | "U aplikaciji nema AI funkcionalnosti" |
| S119 | Prijenos neiskorištenih dana | ide | u sljedeću godinu (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 1 | "Prijenos neiskorištenih dana u sljedeću godinu" |
| S120 | Preneseni dani | vrijede | do 30. lipnja (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 1 | "vrijede do 30. lipnja, bez gornje granice" |
| S121 | Preneseni dani | nemaju | gornju granicu (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 1 | "bez gornje granice" |
| S122 | Prvo se troše | najstariji dani | (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 1 | "prvo se troše najstariji dani" |
| S123 | Hrvatski blagdani | se ne broje | kao radni dani (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 2 | "Hrvatski blagdani se ne broje kao radni dani" |
| S124 | Zahtjev preko granice godine | ostaje | jedan zahtjev (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 3 | "Zahtjev preko granice godine ostaje jedan zahtjev" |
| S125 | Dani | se broje | po godinama (proširenje) | za zahtjev preko granice godine | nije rečeno | spec · 9. Proširenja · tablica, red 3 | "a dani se broje po godinama" |
| S126 | Bolovanje | smije preklopiti | odobreni godišnji (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 4 | "Bolovanje smije preklopiti odobreni godišnji" |
| S127 | Odobreni godišnji | se otkazuje ili refundira | kad ga bolovanje preklopi (proširenje) | — | nije rečeno | spec · 9. Proširenja · tablica, red 4 | "onda ti se GO otkazuje / refundira" |
| S128 | Preskaču se | samo vikendi | — | — | nije rečeno | spec · 10. Naše odluke · "D1" | "Preskaču se samo vikendi" |
| S129 | Hrvatski blagdani | su | proširenje | — | nije rečeno | spec · 10. Naše odluke · "D1" | "hrvatski blagdani su proširenje" |
| S130 | Zahtjev voditelja | uvijek ide | adminu, i kad je voditelj član tuđeg tima | — | nije rečeno | spec · 10. Naše odluke · "D2" | "Zahtjev voditelja uvijek ide adminu, i kad je voditelj član tuđeg tima" |
| S131 | Tko otkazuje tuđi zahtjev (voditelj, admin) | mora upisati | razlog | — | nije rečeno | spec · 10. Naše odluke · "D3" | "Tko otkazuje tuđi zahtjev (voditelj, admin) mora upisati razlog" |
| S132 | Zahtjev | ne počinje prije | datuma zaposlenja | — | nije rečeno | spec · 10. Naše odluke · "D4" | "Zahtjev ne počinje prije datuma zaposlenja" |
| S133 | Svaka godina | ima | 20 dana, bez prijenosa | — | nije rečeno | spec · 10. Naše odluke · "D5" | "Svaka godina ima svojih 20 dana, bez prijenosa" |
| S134 | Zahtjev | troši saldo | godine u koju pada | — | nije rečeno | spec · 10. Naše odluke · "D5" | "zahtjev troši saldo godine u koju pada" |
| S135 | U godini zaposlenja | broje se | mjeseci od mjeseca zaposlenja do prosinca | — | nije rečeno | spec · 10. Naše odluke · "D5" | "U godini zaposlenja broje se mjeseci od mjeseca zaposlenja do prosinca" |
| S136 | Korekcije (datum zaposlenja, već započet zahtjev) | rade se | izravno u bazi (SQL) | — | nije rečeno | spec · 10. Naše odluke · "D6" | "Korekcije (datum zaposlenja, već započet zahtjev) rade se izravno u bazi (SQL)" |
| S137 | Aplikacija | nema | posebnu tablicu korekcija salda | — | nije rečeno | spec · 10. Naše odluke · "D6" | "Nema posebne tablice korekcija salda" |
| S138 | Proširenja iz odgovora kolege | se ne rade | opisuju se u README-u | — | nije rečeno | spec · 10. Naše odluke · "D7" | "Proširenja iz odgovora kolege ne radimo, nego ih opisujemo u README-u" |
| S139 | Vremenska zona | se postavlja | varijablom TZ | — | nije rečeno | spec · 10. Naše odluke · "D8" | "Vremenska zona se postavlja varijablom `TZ`" |
| S140 | Vremenska zona u docker-compose | je | Europe/Zagreb | — | nije rečeno | spec · 10. Naše odluke · "D8" | "u `docker-compose` Europe/Zagreb" |
| S141 | Kontejner | je inače | u UTC-u | — | nije rečeno | spec · 10. Naše odluke · "D8" | "kontejner je inače u UTC-u" |
| S142 | Seed | nema | demo zahtjeve | — | nije rečeno | spec · 10. Naše odluke · "D9" | "Seed bez demo zahtjeva" |
| S143 | Ekran prijave | je | forma: korisničko ime i lozinka | — | ekran | spec · 10. Naše odluke · "D10" | "Ekran prijave je forma: korisnik upisuje korisničko ime i lozinku" |
| S144 | Ekran prijave | nema | popis korisnika | — | ekran | spec · 10. Naše odluke · "D10" | "bez popisa korisnika" |
| S145 | Korisnička imena | su | u README-u | — | nije rečeno | spec · 10. Naše odluke · "D10" | "korisnička imena su u README-u (L10)" |
| S146 | Format grešaka | je | application/problem+json | — | API | spec · 10. Naše odluke · "D11" | "Format grešaka `application/problem+json`" |
| S147 | Voditelj s više timova | ima | jedan kalendar s filtrom po timu | — | ekran | spec · 10. Naše odluke · "D12" | "Voditelj s više timova ima jedan kalendar s filtrom po timu" |
| S148 | Zahtjev | bilježi | vrijeme stvaranja, odluke i otkazivanja | — | nije rečeno | spec · 10. Naše odluke · "D13" | "Zahtjev bilježi vrijeme stvaranja, odluke i otkazivanja" |
| S149 | Tko smije odobriti zahtjev | smije ga i otkazati | — | — | nije rečeno | spec · 10. Naše odluke · "D14" | "Tko smije odobriti zahtjev, smije ga i otkazati" |
| S150 | Voditelj | ne otkazuje | zahtjev drugog voditelja | — | nije rečeno | spec · 10. Naše odluke · "D14" | "voditelj ne otkazuje zahtjev drugog voditelja" |
| S151 | Admin | smije odobriti | vlastiti zahtjev bilo kojeg tipa | — | nije rečeno | spec · 10. Naše odluke · "D15" | "Admin smije odobriti vlastiti zahtjev bilo kojeg tipa" |
| S152 | Seed | ima lozinku | password za sve korisnike | — | nije rečeno | spec · 10. Naše odluke · "D16" | "lozinka `password` za sve" |
| S153 | Seed | ima datum zaposlenja | 2020-01-01 za sve osim Toma | — | nije rečeno | spec · 10. Naše odluke · "D16" | "datum zaposlenja 2020-01-01 za sve osim Toma (2026-06-01)" |
| S154 | Voditelj i admin | otkazuju | tuđi odobreni zahtjev koji nije počeo | stanje: Approved, nije počeo | ekran | spec · 10. Naše odluke · "D17" | "Voditelj i admin otkazuju tuđi odobreni zahtjev koji nije počeo kroz ekran Approvals" |
| S155 | Otkazivanje | se radi | kroz ekran Approvals, tablica "Approved requests" | — | ekran | spec · 10. Naše odluke · "D17" | "tablica \"Approved requests\"" |
| S156 | Razlog otkazivanja | je | obavezan | pri otkazivanju tuđeg odobrenog zahtjeva | ekran | spec · 10. Naše odluke · "D17" | "razlog obavezan" |
| S157 | Otkazivanje tuđeg zahtjeva na čekanju | je | dostupno samo kroz API, ne kroz UI | — | API | spec · 10. Naše odluke · "D17" | "Otkazivanje tuđeg zahtjeva na čekanju kroz UI još nije napravljeno (samo API)" |
| S159 | Voditelj | može voditi | više timova (odstupanje od briefa) | — | nije rečeno | spec · 11. Odstupanja od briefa · "Team lead. Leads one team." | "Voditelj može voditi više timova" |
| S160 | Admin | je zaposlenik i ima | saldo (odstupanje od briefa) | — | nije rečeno | spec · 11. Odstupanja od briefa · "Admin je zaposlenik i ima saldo" | "Admin je zaposlenik i ima saldo" |
| S161 | Admin | smije odobriti | vlastiti zahtjev (odstupanje od briefa) | — | nije rečeno | spec · 11. Odstupanja od briefa · "Admin smije odobriti vlastiti zahtjev" | "Admin smije odobriti vlastiti zahtjev" |
| S162 | Otkazuje | i tko smije odobriti, uz obavezan razlog (odstupanje od briefa) | — | — | nije rečeno | spec · 11. Odstupanja od briefa · "Otkazuje i tko smije odobriti" | "Otkazuje i tko smije odobriti, uz obavezan razlog" |
| S163 | Aplikacija | ima | razmjerni saldo u godini zaposlenja (odstupanje od briefa) | — | nije rečeno | spec · 11. Odstupanja od briefa · "Razmjerni saldo u godini zaposlenja" | "Razmjerni saldo u godini zaposlenja" |
| S164 | Docker | je | obavezan | — | nije rečeno | spec · 11. Odstupanja od briefa · "Docker je obavezan" | "Docker je obavezan" |
| S165 | Aplikacija | ima tri ekrana | My time off, Approvals, Team calendar | — | ekran | spec · 12. Ekrani · "Tri ekrana iz briefa" | "Tri ekrana iz briefa" |
| S166 | Ekran My time off | prikazuje | moje zahtjeve sa stanjem | — | ekran | spec · 12. Ekrani · "1. **My time off:**" | "moji zahtjevi sa stanjem" |
| S167 | Ekran My time off | ima | formu za novi zahtjev | — | ekran | spec · 12. Ekrani · "1. **My time off:**" | "forma za novi zahtjev" |
| S168 | Ekran My time off | omogućuje | otkazivanje | — | ekran | spec · 12. Ekrani · "1. **My time off:**" | "otkazivanje" |
| S169 | Ekran My time off | prikazuje | saldo godišnjeg | — | ekran | spec · 12. Ekrani · "1. **My time off:**" | "saldo godišnjeg" |
| S170 | Ekran Approvals | je dostupan | samo voditeljima i adminima | — | ekran | spec · 12. Ekrani · "2. **Approvals:**" | "samo za voditelje i admine" |
| S171 | Ekran Approvals | prikazuje | zahtjeve na čekanju koje korisnik smije odobriti, s odobravanjem i odbijanjem | — | ekran | spec · 12. Ekrani · "2. **Approvals:**" | "zahtjevi na čekanju koje smiju odobriti, s odobravanjem i odbijanjem" |
| S172 | Ekran Approvals | prikazuje ispod | odobrene tuđe zahtjeve koje korisnik smije otkazati, a nisu počeli | — | ekran | spec · 12. Ekrani · "2. **Approvals:**" | "ispod njih odobreni tuđi zahtjevi koje smiju otkazati, a nisu počeli" |
| S173 | Ekran Team calendar | prikazuje | tko je odsutan u odabranom mjesecu, po pravilima vidljivosti | — | ekran | spec · 12. Ekrani · "3. **Team calendar:**" | "tko je odsutan u odabranom mjesecu, po pravilima vidljivosti" |
| S174 | Ekran Team calendar | prikazuje | popis po danima | — | ekran | spec · 12. Ekrani · "3. **Team calendar:**" | "popis po danima" |
| S175 | Ekran | prikazuje | poruku greške kad backend odbije zahtjev, npr. preklapanje ili premašen saldo | kad backend odbije zahtjev | ekran | spec · 12. Ekrani · "Kad backend odbije zahtjev" | "Kad backend odbije zahtjev, ekran prikazuje poruku greške, npr. preklapanje ili premašen saldo" |

Ukupno redaka u specifikacija.md: **174**

## Ukupno po dokumentu

| Dokument | Broj redaka |
| --- | --- |
| brief.md | 82 |
| atomarna-pitanja.md | 83 |
| specifikacija.md | 174 |
| **Ukupno** | **339** |

## Izvan formata

Tablica nema mjesto za sljedeće. Redci nisu mijenjani.

1. **Proturječje: prijava.** D10 (specifikacija) kaže da ekran prijave nema popis korisnika. A79–A80 (atomarna, 49.1) potvrđuju "Da" uz pretpostavku da popis postoji. Obje izjave su u tablici kao ravnopravne. Tablica nema stupac koji pokazuje da se dvije izjave isključuju.
2. **Odstupanja od briefa.** Odjeljak 11 specifikacije izričito zamjenjuje pravila iz briefa (npr. "Leads one team" i "A team lead's request goes to an admin" u odnosu na S-redke o više timova i voditeljevim zahtjevima). Tablica ne označava što koja izjava poništava.
3. **Izostavljeni odgovori.** Kolega nije odgovorio na 53.3 ("?"), a na 53.6 i 53.7 je odgovorio "Sure" i "Ne", no to nije izjava o ponašanju aplikacije. Pitanja, pretpostavke i odgovori o procesu nisu u popisu. Tablica nema način da pokaže "nije odgovoreno".
4. **Gdje je "nije rečeno" gotovo svuda.** Za većinu pravila dokument ne kaže hoće li vrijediti na API-ju ili na ekranu, nego to samo može zaključiti. Stupac Gdje ima samo vrijednosti iz popisa, pa ne može pokazati da je neka vrijednost zaključena, a ne navedena.
5. **Citati su pisani ručno.** Citati su prepisani iz pročitanih dokumenata, a nisu strojno provjereni protiv izvora. Tablica ih prikazuje kao doslovne, a to nisam provjerio alatom.
