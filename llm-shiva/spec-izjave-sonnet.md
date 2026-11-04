# Izjave o ponašanju aplikacije (brief, atomarna pitanja, specifikacija)

Pročitano samo: `docs/zajednicko/brief.md`, `docs/zajednicko/atomarna-pitanja.md`, `docs/zajednicko/specifikacija.md`.

Konvencije (da se ne mijenja značenje):
- Oznake redaka: `B` = brief, `P` = atomarna pitanja, `S` = specifikacija.
- "approve or reject" / "odobrava i odbija" brojim kao jednu izjavu kad pravilo vrijedi jednako za oboje.
- Ako dokument istu stvar kaže dvaput (u drugom odjeljku), svaki put je svoj redak.
- Citat je doslovan; izostavio sam samo oznake izvora na kraju rečenice, npr. `([🟩 Q2](...))`. Tri točkice `…` znače izostavljen dio unutar citata.
- Kod `atomarna-pitanja.md` citat je par pitanje (Q) i odgovor (O). Stupac "Pretpostavka" nisam uzimao kao izjavu jer odgovor ima prednost; vidi "Izvan formata".
- "Tko" je `nije rečeno` kad citat ne imenuje izvršitelja.

| # | Tko | Radi ili vidi | Nad čim | U kojem stanju / uvjetu | Gdje | Izvor | Točan citat | Napomena |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| B01 | employees | request | time off | – | nije rečeno | brief, "The app", `employees request` | «Build an app where employees request time off» | |
| B02 | team leads | approve or reject | requests | – | nije rečeno | brief, "The app", `team leads approve` | «team leads approve or reject those requests» | |
| B03 | everyone | see | who is away | – | nije rečeno | brief, "The app", `who is away` | «everyone can see who is away» | |
| B04 | Employee | requests | time off | – | nije rečeno | brief, "Users and roles", `Requests time off` | «**Employee.** Requests time off and sees their own requests.» | Jedna rečenica, dvije stvari; vidi B05 |
| B05 | Employee | sees | svoje requests | – | nije rečeno | brief, "Users and roles", `sees their own requests` | «Requests time off and sees their own requests.» | |
| B06 | Team lead | leads | jedan tim | – | nije rečeno | brief, "Users and roles", `Leads one team` | «**Team lead.** Leads one team.» | Specifikacija to mijenja, vidi S‑redak "Voditelj može voditi više timova" |
| B07 | Team lead | approves or rejects | requests tog tima | – | nije rečeno | brief, "Users and roles", `Approves or rejects` | «Approves or rejects requests from that team.» | |
| B08 | Team lead | is | employee | – | nije rečeno | brief, "Users and roles", `also an employee` | «A team lead is also an employee» | |
| B09 | Team lead | can request | time off | – | nije rečeno | brief, "Users and roles", `can request time off` | «and can request time off.» | |
| B10 | Admin | can see | sve u tvrtki | – | nije rečeno | brief, "Users and roles", `Can see and act` | «**Admin.** Can see and act on everything in the company.» | Jedna rečenica, dvije stvari; vidi B11 |
| B11 | Admin | can act on | sve u tvrtki | – | nije rečeno | brief, "Users and roles", `act on everything` | «Can see and act on everything in the company.» | |
| B12 | nije rečeno | (nema prave autentikacije) | authentication | – | nije rečeno | brief, "Users and roles", `real authentication` | «You don't need real authentication or a user management screen.» | |
| B13 | nije rečeno | seed | baza korisnika | – | nije rečeno | brief, "Users and roles", `Seed the database` | «Seed the database with these users» | |
| B14 | nije rečeno | djeluje kao svaki seedani korisnik | korisnici | – | nije rečeno | brief, "Users and roles", `act as each one` | «give us a simple way to act as each one» | |
| B15 | Ana | je | Team lead, tim Platform | – | nije rečeno | brief, "Users and roles", tablica, `Ana` | «Ana» · «Team lead» · «Platform» | |
| B16 | Ivan | je | Employee, tim Platform | – | nije rečeno | brief, tablica, `Ivan` | «Ivan» · «Employee» · «Platform» | |
| B17 | Marko | je | Employee, tim Platform | – | nije rečeno | brief, tablica, `Marko` | «Marko» · «Employee» · «Platform» | |
| B18 | Petra | je | Employee, tim Platform | – | nije rečeno | brief, tablica, `Petra` | «Petra» · «Employee» · «Platform» | |
| B19 | Luka | je | Team lead, tim Mobile | – | nije rečeno | brief, tablica, `Luka` | «Luka» · «Team lead» · «Mobile» | |
| B20 | Sara | je | Employee, tim Mobile | – | nije rečeno | brief, tablica, `Sara` | «Sara» · «Employee» · «Mobile» | |
| B21 | Tom | je | Employee, tim Mobile | – | nije rečeno | brief, tablica, `Tom` | «Tom» · «Employee» · «Mobile» | |
| B22 | Eva | je | Admin, bez tima | – | nije rečeno | brief, tablica, `Eva` | «Eva» · «Admin» · «none» | |
| B23 | request | ima | tip | – | nije rečeno | brief, "Time-off requests", `has a type` | «A request has a type» | |
| B24 | request | ima | početni datum | – | nije rečeno | brief, "Time-off requests", `start date` | «a start date» | |
| B25 | request | ima | završni datum | – | nije rečeno | brief, "Time-off requests", `end date` | «an end date» | |
| B26 | request | – | početni i završni datum | oba uključiva | nije rečeno | brief, "Time-off requests", `both inclusive` | «(both inclusive)» | |
| B27 | request | ima | razlog | opcionalan | nije rečeno | brief, "Time-off requests", `optional reason` | «an optional reason» | |
| B28 | request | tip je | Vacation | – | nije rečeno | brief, "Time-off requests", `Vacation` | «Vacation» | |
| B29 | request | tip je | Sick leave | – | nije rečeno | brief, `Sick leave` | «Sick leave» | |
| B30 | request | tip je | Day off | – | nije rečeno | brief, `Day off` | «Day off» | |
| B31 | request | je u jednom od stanja | stanje | – | nije rečeno | brief, "Time-off requests", `one of these states` | «A request is in one of these states» | |
| B32 | request | stanje Pending | novi request | svaki novi | nije rečeno | brief, `Pending` | «**Pending.** Every new request starts here.» | |
| B33 | request | stanje Approved | – | – | nije rečeno | brief, `Approved` | «**Approved.**» | |
| B34 | request | stanje Rejected | – | – | nije rečeno | brief, `Rejected` | «**Rejected.**» | |
| B35 | request | stanje Cancelled | – | – | nije rečeno | brief, `Cancelled` | «**Cancelled.**» | |
| B36 | team lead | approve or reject | requests članova svog tima | samo svog tima | nije rečeno | brief, Rules, Approvals 1 | «A team lead can approve or reject requests from members of their own team only.» | |
| B37 | admin | approve or reject | bilo koji request | – | nije rečeno | brief, Rules, Approvals 2 | «An admin can approve or reject any request.» | |
| B38 | nitko | ne može approve or reject | vlastiti request | – | nije rečeno | brief, Rules, Approvals 3 | «Nobody can approve or reject their own request.» | |
| B39 | team lead | request ide | adminu | request voditelja | nije rečeno | brief, Rules, Approvals 3 | «A team lead's request goes to an admin.» | |
| B40 | nije rečeno | approve or reject | request | samo Pending | nije rečeno | brief, Rules, Approvals 4 | «Only pending requests can be approved or rejected.» | |
| B41 | nije rečeno | odbijanje traži | komentar | rejection | nije rečeno | brief, Rules, Approvals 5 | «A rejection needs a comment.» | |
| B42 | nije rečeno | komentar uz odobrenje | approval | – | nije rečeno | brief, Rules, Approvals 5 | «An approval comment is optional.» | |
| B43 | employee | cancel | vlastiti request | Pending | nije rečeno | brief, Rules, Cancelling 6 | «An employee can cancel their own request while it is pending.» | |
| B44 | employee | cancel | vlastiti Approved request | još nije počeo | nije rečeno | brief, Rules, Cancelling 7 | «An employee can cancel their own approved request if it hasn't started yet.» | |
| B45 | nije rečeno | završni datum | request | ne smije biti prije početnog | nije rečeno | brief, Rules, Validation 8 | «The end date can't be before the start date.» | |
| B46 | user | ne može imati | dva Pending/Approved requesta | datumi se preklapaju | nije rečeno | brief, Rules, Validation 9 | «A user can't have two pending or approved requests whose dates overlap.» | |
| B47 | app | broji | samo radne dane | ponedjeljak do petak | nije rečeno | brief, Rules, Validation 10 | «The app counts working days only, Monday to Friday.» | |
| B48 | nije rečeno | request je invalid | request | pokriva samo vikend dane | nije rečeno | brief, Rules, Validation 10 | «A request that covers only weekend days is invalid.» | |
| B49 | every user | dobiva | 20 vacation days | po kalendarskoj godini | nije rečeno | brief, Rules, Vacation balance 11 | «Every user gets 20 vacation days per calendar year.» | |
| B50 | nije rečeno | prikaz svakom korisniku | iskorišteni (approved), pending i preostali dani | tekuća godina | nije rečeno | brief, Rules, Vacation balance 12 | «For the current year, show each user how many days they have used (approved), pending, and remaining.» | |
| B51 | nije rečeno | vacation request ne smije premašiti | preostali saldo | – | nije rečeno | brief, Rules, Vacation balance 13 | «A vacation request can't exceed the remaining balance.» | |
| B52 | nije rečeno | Pending requests se računaju | saldo | – | nije rečeno | brief, Rules, Vacation balance 13 | «Pending requests count against the balance.» | |
| B53 | nije rečeno | ne troše saldo | sick leave i day off | – | nije rečeno | brief, Rules, Vacation balance 14 | «Sick leave and days off don't count against the vacation balance.» | |
| B54 | nije rečeno | pretpostavka | request | unutar jedne kalendarske godine | nije rečeno | brief, Rules, kraj, `one calendar year` | «You can assume a request falls within one calendar year.» | |
| B55 | Employee | vidi | svoje requests, svi detalji | – | nije rečeno | brief, Visibility, tablica | «Their own requests with every detail.» | |
| B56 | Employee | vidi | approved izostanke kolega na team calendaru, samo ime i datume | – | nije rečeno | brief, Visibility, tablica | «Teammates' approved absences on the team calendar, showing only the name and dates.» | |
| B57 | Employee | ne vidi | tip izostanka kolega | – | nije rečeno | brief, Visibility, tablica | «No type» | |
| B58 | Employee | ne vidi | razlog izostanka kolega | – | nije rečeno | brief, Visibility, tablica | «no reason» | |
| B59 | Employee | ne vidi | pending requeste kolega | – | nije rečeno | brief, Visibility, tablica | «nothing pending» | |
| B60 | Team lead | vidi | svaki request svog tima, uključujući tip, razlog i pending | – | nije rečeno | brief, Visibility, tablica | «Every request from their team, including type, reason and pending requests.» | |
| B61 | Admin | vidi | svaki request u tvrtki | – | nije rečeno | brief, Visibility, tablica | «Every request in the company.» | |
| B62 | API | provodi | pravila vidljivosti | – | API | brief, Visibility, `API must enforce` | «The API must enforce these rules.» | |
| B63 | nije rečeno | skrivanje u frontendu | podaci | – | ekran | brief, Visibility, `Hiding data` | «Hiding data in the frontend doesn't count.» | |
| B64 | app | ima | najmanje tri ekrana | – | ekran | brief, Screens, `at least these three` | «We need at least these three screens.» | |
| B65 | My time off | prikazuje | popis mojih requesta sa statusom | – | ekran | brief, Screens 1 | «A list of my requests with their status» | |
| B66 | My time off | ima | formu za novi request | – | ekran | brief, Screens 1 | «a form to create a new one» | |
| B67 | My time off | ima | način otkazivanja | – | ekran | brief, Screens 1 | «a way to cancel» | |
| B68 | My time off | prikazuje | moj vacation balance | – | ekran | brief, Screens 1 | «and my vacation balance.» | |
| B69 | Approvals | dostupan | team leadovima i adminima | samo njima | ekran | brief, Screens 2 | «For team leads and admins only» | |
| B70 | Approvals | prikazuje | popis pending requesta koje smiju obraditi | – | ekran | brief, Screens 2 | «a list of pending requests they are allowed to act on» | |
| B71 | Approvals | ima | approve i reject akcije | – | ekran | brief, Screens 2 | «with approve and reject actions.» | |
| B72 | Team calendar | prikazuje | tko je odsutan u odabranom mjesecu, po pravilima vidljivosti | – | ekran | brief, Screens 3 | «Who is away during a selected month, following the visibility rules above.» | |
| B73 | Team calendar | popis | po danima | – | ekran | brief, Screens 3 | «A list grouped by day is fine.» | |
| B74 | nije rečeno | prikazuje poruke greške | kad backend odbije | npr. preklapanje ili premašen saldo | ekran | brief, Screens, kraj, `useful error messages` | «Show useful error messages when the backend rejects something, for example an overlap or an exceeded balance.» | |
| B75 | nije rečeno | isključiti blagdane iz brojanja dana | Hrvatska ili Belgija | opcionalni dodatak | nije rečeno | brief, Optional extras | «Exclude public holidays from day counts. Pick Croatia or Belgium.» | |
| B76 | nije rečeno | half-day requests | – | opcionalni dodatak | nije rečeno | brief, Optional extras | «Half-day requests.» | |
| B77 | nije rečeno | audit history | svaki request, tko i kada | opcionalni dodatak | nije rečeno | brief, Optional extras | «An audit history for each request showing who changed it and when.» | |
| B78 | team lead | notifikacija | novi request | opcionalni dodatak | nije rečeno | brief, Optional extras | «A notification to the team lead when a new request arrives.» | |
| B79 | nije rečeno | notifikacija se može | ispisati u konzolu | opcionalni dodatak | nije rečeno | brief, Optional extras | «Logging it to the console is fine.» | |
| B80 | nije rečeno | Docker Compose | baza, backend, frontend zajedno | opcionalni dodatak | nije rečeno | brief, Optional extras | «Docker Compose to start the database, backend and frontend together.» | |
| B81 | nije rečeno | izvan opsega | prava autentikacija ili SSO | – | nije rečeno | brief, Out of scope | «Real authentication or SSO» | |
| B82 | nije rečeno | izvan opsega | deployment | – | nije rečeno | brief, Out of scope | «Deployment» | |
| B83 | nije rečeno | izvan opsega | ekrani za upravljanje korisnicima i timovima | – | ekran | brief, Out of scope | «User and team management screens» | |
| B84 | nije rečeno | izvan opsega | prijevodi | – | nije rečeno | brief, Out of scope | «Translations» | |
| B85 | nije rečeno | izvan opsega | savršen dizajn | – | ekran | brief, Out of scope | «Pixel-perfect design» | |
| P01 | admin | je | zaposlenik | – | nije rečeno | pitanja, It.1 Uloge i timovi, Q1 | Q «Je li admin zaposlenik?» O «Da (npr. direktor koji prati i svoj godišnji)» | |
| P02 | admin | može biti | voditelj tima | – | nije rečeno | pitanja, It.1, Q2 | Q «Može li admin biti voditelj tima?» O «Da» | |
| P03 | voditelj | je član | tim koji vodi | – | nije rečeno | pitanja, It.1, Q3 | Q «Je li voditelj član tima koji vodi?» O «Da, i može voditi više timova» | Odgovor ima i drugi dio, vidi P04 |
| P04 | voditelj | može voditi | više timova | – | nije rečeno | pitanja, It.1, Q3 | O «Da, i može voditi više timova» | |
| P05 | zaposlenik | može biti | bez tima | – | nije rečeno | pitanja, It.1, Q4 | Q «Može li zaposlenik biti bez tima?» O «Da (npr. radi sam); njegovi zahtjevi idu adminu» | Odgovor ima i drugi dio, vidi P06 |
| P06 | admin | dobiva requeste | zaposlenika bez tima | – | nije rečeno | pitanja, It.1, Q4 | O «njegovi zahtjevi idu adminu» | |
| P07 | zaposlenik | može biti | u više timova | – | nije rečeno | pitanja, It.1, Q5 | Q «Može li zaposlenik biti u više timova?» O «Da» | |
| P08 | tim | ne može imati | više voditelja | – | nije rečeno | pitanja, It.1, Q6 | Q «Može li tim imati više voditelja?» O «Ne» | |
| P09 | tim | ne može biti | bez voditelja | – | nije rečeno | pitanja, It.1, Q7 | Q «Može li tim biti bez voditelja?» O «Ne» | |
| P10 | admin | može ih biti | više | – | nije rečeno | pitanja, It.1, Q8 | Q «Može li biti više admina?» O «Da» | |
| P11 | Day off | je | slobodan dan | – | nije rečeno | pitanja, It.1 Tipovi odsustva, Q9 | Q «Što je "Day off"?» O «Slobodan dan» | |
| P12 | bolovanje | nema | limit dana | – | nije rečeno | pitanja, It.1, Q10 | Q «Ima li bolovanje limit dana?» O «Ne» | |
| P13 | slobodan dan | nema | limit dana | – | nije rečeno | pitanja, It.1, Q11 | Q «Ima li slobodan dan limit dana?» O «Ne» | |
| P14 | slobodan dan | smije trajati | više dana zaredom | – | nije rečeno | pitanja, It.1, Q12 | Q «Smije li slobodan dan trajati više dana zaredom?» O «Da» | |
| P15 | bolovanje | ne traži | liječničku potvrdu | – | nije rečeno | pitanja, It.1, Q13 | Q «Treba li uz bolovanje priložiti liječničku potvrdu?» O «Ne» | |
| P16 | neiskorišteni dani | prenose se | u sljedeću godinu | – | nije rečeno | pitanja, It.1 Saldo, Q14 | Q «Prenose li se neiskorišteni dani u sljedeću godinu?» O «Da, kao u Hrvatskoj» | Spec to vodi kao proširenje, vidi S‑redak o prijenosu |
| P17 | tko počne usred godine | dobiva manje dana | razmjerno | – | nije rečeno | pitanja, It.1, Q15 | Q «Dobiva li manje dana tko počne raditi usred godine?» O «Da, razmjerno» | |
| P18 | voditelj | vidi | saldo članova svog tima | – | nije rečeno | pitanja, It.1, Q16 | Q «Vidi li voditelj saldo članova svog tima?» O «Da» | |
| P19 | admin | vidi | saldo svih korisnika | – | nije rečeno | pitanja, It.1, Q17 | Q «Vidi li admin saldo svih korisnika?» O «Da» | |
| P20 | nije rečeno | saldo se provjerava | za tekuću godinu | – | nije rečeno | pitanja, It.1, Q18 | Q «Provjerava li se saldo za godinu u kojoj zahtjev pada, a ne za tekuću godinu?» O «Za tekucu, ako ima od prijasnje godine, prijasnji se prvi trosi» | Odgovor ima i drugi dio, vidi P21 |
| P21 | nije rečeno | prvo se troši | saldo prošle godine | ako ga ima | nije rečeno | pitanja, It.1, Q18 | O «ako ima od prijasnje godine, prijasnji se prvi trosi» | |
| P22 | godišnji | smije počinjati | u prošlosti | ako se zaboravi unijeti | nije rečeno | pitanja, It.1 Datumi, Q19 | Q «Smije li godišnji počinjati u prošlosti?» O «Da, ako se zaboravi unijeti» | |
| P23 | bolovanje | smije počinjati | u prošlosti | ako se zaboravi unijeti | nije rečeno | pitanja, It.1, Q20 | Q «Smije li bolovanje počinjati u prošlosti?» O «Da, ako se zaboravi unijeti» | |
| P24 | slobodan dan | smije počinjati | u prošlosti | ako se zaboravi unijeti | nije rečeno | pitanja, It.1, Q21 | Q «Smije li slobodan dan počinjati u prošlosti?» O «Da, ako se zaboravi unijeti» | |
| P25 | bolovanje | smije počinjati | u budućnosti | – | nije rečeno | pitanja, It.1, Q22 | Q «Smije li bolovanje počinjati u budućnosti?» O «Da» | |
| P26 | zahtjev koji počinje danas | je već započeo | pravilo 7 | – | nije rečeno | pitanja, It.1, Q23 | Q «Je li zahtjev koji počinje danas već "započeo" (pravilo 7)?» O «Da» | |
| P27 | nije rečeno | "danas" se računa | po vremenskoj zoni servera | – | nije rečeno | pitanja, It.1, Q24 | Q «U kojoj se vremenskoj zoni računa "danas"?» O «Vremenska zona servera na koji se deploya» | |
| P28 | nije rečeno | koliko se unaprijed smije tražiti | – | – | nije rečeno | pitanja, It.1, Q25 | Q «Koliko se unaprijed smije tražiti?» O «Proizvoljno» | |
| P29 | nije rečeno | minimalni rok najave | – | – | nije rečeno | pitanja, It.1, Q26 | Q «Postoji li minimalan rok najave, npr. godišnji najmanje X dana unaprijed?» O «Nema» | |
| P30 | nije rečeno | najdulje trajanje jednog zahtjeva | – | – | nije rečeno | pitanja, It.1, Q27 | Q «Postoji li najdulje trajanje jednog zahtjeva?» O «Nema» | |
| P31 | API | dijeli | zahtjev preko granice godine | – | API | pitanja, It.1, Q28 | Q «Što API radi sa zahtjevom preko granice godine: odbija ili dijeli?» O «Dijeli; i dalje je jedan zahtjev» | Odgovor ima i drugi dio, vidi P32 |
| P32 | nije rečeno | podijeljen zahtjev je | i dalje jedan zahtjev | preko granice godine | nije rečeno | pitanja, It.1, Q28 | O «i dalje je jedan zahtjev» | |
| P33 | nitko | ne podnosi zahtjev | u ime drugog korisnika | – | nije rečeno | pitanja, It.1 Stanja i otkazivanje, Q29 | Q «Smije li netko podnijeti zahtjev u ime drugog korisnika?» O «Ne» | |
| P34 | voditelj | može otkazati | tuđi zahtjev | – | nije rečeno | pitanja, It.1, Q30 | Q «Smije li voditelj otkazati tuđi zahtjev?» O «Moze» | Odgovor ne kaže čiji zahtjev ni u kojem stanju |
| P35 | admin | može otkazati | tuđi zahtjev | – | nije rečeno | pitanja, It.1, Q31 | Q «Smije li admin otkazati tuđi zahtjev?» O «Da» | |
| P36 | voditelj ili admin | može povući | već dano odobrenje | isto kao otkazivanje | nije rečeno | pitanja, It.1, Q32 | Q «Smije li voditelj ili admin povući već dano odobrenje?» O «Da (isto kao otkazivanje, vidi 30 i 31)» | |
| P37 | zahtjev na čekanju | ne uređuje se | – | – | nije rečeno | pitanja, It.1, Q33 | Q «Smije li se uređivati zahtjev na čekanju?» O «Ne» | |
| P38 | odobreni zahtjev | ne uređuje se | – | – | nije rečeno | pitanja, It.1, Q34 | Q «Smije li se uređivati odobreni zahtjev?» O «Ne, otkaži i podnesi novi» | Odgovor ima i drugi dio, vidi P39 |
| P39 | nije rečeno | otkaže se i podnese novi | odobreni zahtjev | umjesto uređivanja | nije rečeno | pitanja, It.1, Q34 | O «otkaži i podnesi novi» | |
| P40 | nije rečeno | smije se odobriti | zahtjev čiji su datumi prošli | – | nije rečeno | pitanja, It.1, Q35 | Q «Smije li se odobriti zahtjev čiji su datumi već prošli?» O «Da» | |
| P41 | zahtjev na čekanju | ne ističe | kad prođu datumi | – | nije rečeno | pitanja, It.1, Q36 | Q «Ističe li zahtjev na čekanju kad prođu njegovi datumi?» O «Ne» | |
| P42 | nije rečeno | razlog otkazivanja | zahtjev | – | nije rečeno | pitanja, It.1, Q37 | Q «Treba li razlog za otkazivanje zahtjeva?» O «Moze, nema bude field» | Odgovor doslovno kako je napisan |
| P43 | nije rečeno | najveći broj zahtjeva na čekanju po korisniku | – | – | nije rečeno | pitanja, It.1, Q38 | Q «Postoji li najveći broj zahtjeva na čekanju po korisniku?» O «Ne» | |
| P44 | nije rečeno | preklapanje se računa | samo po radnim danima | – | nije rečeno | pitanja, It.1 Preklapanja, Q39 | O «Nije razumio pitanje. Naša odluka: preklapanje se računa samo po radnim danima» | Odgovor naručitelja je "Nije razumio pitanje"; izjava je naša odluka |
| P45 | nije rečeno | minimalan broj ljudi u timu | prisutnost | – | nije rečeno | pitanja, It.1, Q40 | Q «Postoji li minimalan broj ljudi koji moraju biti prisutni u timu?» O «Ne» | |
| P46 | zaposlenik | vidi | svoje zahtjeve na čekanju na kalendaru tima | – | nije rečeno | pitanja, It.1 Vidljivost, Q41 | Q «Vidi li zaposlenik svoje zahtjeve na čekanju na kalendaru tima?» O «Da» | |
| P47 | voditelj | vidi | zahtjeve na čekanju na kalendaru tima | – | nije rečeno | pitanja, It.1, Q42 | Q «Vidi li voditelj zahtjeve na čekanju na kalendaru tima?» O «Da» | |
| P48 | admin | vidi | kalendar svih timova odjednom | – | nije rečeno | pitanja, It.1, Q43 | Q «Vidi li admin kalendar svih timova odjednom ili po timu?» O «Oboje, s filtrom; da se vidi stanje ljudi u firmi» | Odgovor ima više dijelova, vidi P49 i P50 |
| P49 | admin | vidi kalendar | po timu | s filtrom | nije rečeno | pitanja, It.1, Q43 | O «Oboje, s filtrom» | |
| P50 | admin | vidi | stanje ljudi u firmi | – | nije rečeno | pitanja, It.1, Q43 | O «da se vidi stanje ljudi u firmi» | |
| P51 | nije rečeno | vidi se komentar odbijanja | vlasnik i oni koji smiju odobravati | – | nije rečeno | pitanja, It.1, Q44 | Q «Tko vidi komentar odbijanja?» O «Da (slaže se)» | Pretpostavka uz pitanje u tablici je "Vlasnik i oni koji smiju odobravati"; odgovor samo "Da (slaže se)" |
| P52 | nije rečeno | vidi se komentar odobrenja | vlasnik i oni koji smiju odobravati | – | nije rečeno | pitanja, It.1, Q45 | Q «Tko vidi komentar odobrenja?» O «Da (slaže se)» | Isto kao P51 |
| P53 | zaposlenik | vidi | tko je odobrio ili odbio njegov zahtjev | – | nije rečeno | pitanja, It.1, Q46 | Q «Vidi li zaposlenik tko je odobrio ili odbio njegov zahtjev?» O «Da» | |
| P54 | zaposlenik | vidi | bolovanje kolege na kalendaru kao "odsutan" bez tipa | – | nije rečeno | pitanja, It.1, Q47 | Q «Vidi li zaposlenik bolovanje kolege na kalendaru, kao "odsutan" bez tipa?» O «Da» | |
| P55 | kalendar | ne prikazuje voditelju i adminu | odbijene i otkazane zahtjeve | – | nije rečeno | pitanja, It.1, Q48 | Q «Prikazuje li kalendar voditelju i adminu odbijene i otkazane zahtjeve?» O «Ne, inače je pretrpano» | |
| P56 | nije rečeno | API se zove | kao određeni korisnik, mehanizam JWT | – | API | pitanja, It.1 API, Q49 | Q «Kojim mehanizmom ćete zvati API kao određeni korisnik?» O «JWT» | |
| P57 | nije rečeno | HTTP statusi i format grešaka | – | – | API | pitanja, It.1, Q50 | Q «Imate li preferencije za HTTP statuse i format grešaka?» O «Standardni statusi, obrada grešaka po volji» | |
| P58 | nije rečeno | Swagger dokumentacija | API | – | API | pitanja, It.1, Q51 | Q «Treba li dokumentacija API-ja (npr. Swagger)?» O «Ne Swagger; dijagrami tokova i sekvencijski dijagrami» | |
| P59 | nije rečeno | ne prijavljuje se | stvarno utrošeno vrijeme | – | nije rečeno | pitanja, It.1 Isporuka, Q52 | Q «Treba li prijaviti stvarno utrošeno vrijeme?» O «Ne» | |
| P60 | aplikacija | radi | u kontejneru | – | nije rečeno | pitanja, It.1, Q53 | Q «Imate li Docker na računalu?» O «Neka bude u kontejneru» | |
| P61 | UI, README i kod | jezik | engleski | preferencija | ekran | pitanja, It.1, Q54 | Q «Na kojem jeziku su UI, README i kod?» O «Svejedno, preferira engleski» | |
| P62 | seed | ne sadrži | primjere zahtjeva | – | nije rečeno | pitanja, It.1, Q55 | Q «Trebaju li u seedu biti i primjeri zahtjeva?» O «Ne trebaju; upute za pokretanje u README» | |
| P63 | bolovanje | smije preklopiti | odobreni godišnji | – | nije rečeno | pitanja, It.1 Prijedlozi izmjene pravila, Q57 | Q «Smije li bolovanje preklopiti odobreni godišnji?» O «Brate mili mozes otic na bolovanje kad si na GO.» | Odgovor ima više dijelova, vidi P64 i P65 |
| P64 | godišnji (GO) | otkazuje se / refundira | pri bolovanju preko GO | – | nije rečeno | pitanja, It.1, Q57 | O «onda ti se GO otkazuje / refundira.» | |
| P65 | nije rečeno | slučaj je | rubni, nije toliko bitan | – | nije rečeno | pitanja, It.1, Q57 | O «Ali to je edge case, nike toliko bitno.» | |
| P66 | admin | ima saldo | – | – | nije rečeno | pitanja, Odgođeno iz it.1, `Ima li admin saldo?` | «Ima li admin saldo?» · «Riješeno: da, admin je zaposlenik» | |
| P67 | admin | je član | tim koji vodi | kad je voditelj | nije rečeno | pitanja, Odgođeno iz it.1, `Je li admin član tima?` | «Riješeno: kad je voditelj, član je tima koji vodi» | |
| P68 | odsutnost admina | vidi se | na kalendaru timova kojima pripada | – | nije rečeno | pitanja, Odgođeno iz it.1, `Na čijem se kalendaru` | «Riješeno: na kalendaru timova kojima pripada; bez tima ga vide samo admini» | Odgovor ima i drugi dio, vidi P69 |
| P69 | admini | jedini vide odsutnost admina | admin bez tima | – | nije rečeno | pitanja, Odgođeno iz it.1, `Na čijem se kalendaru` | «bez tima ga vide samo admini» | |
| P70 | nije rečeno | broje se | samo radni dani | 20 dana godišnjeg | nije rečeno | pitanja, Maknuto s popisa, `Jesu li 20 dana` | Q «Jesu li 20 dana godišnjeg radni ili kalendarski dani?» O «Pravilo 10: broje se samo radni dani» | Ponavlja B47 |
| P71 | nije rečeno | odbijeni zahtjev ponovno otvoriti | – | – | nije rečeno | pitanja, Maknuto s popisa, `ponovno otvoriti` | Q «Može li se odbijeni zahtjev ponovno otvoriti?» O «Isto kao novi zahtjev» | Citat je "Zašto" stupac |
| P72 | nije rečeno | djelomično odobrenje | zahtjev | – | nije rečeno | pitanja, Maknuto s popisa, `djelomično` | Q «Može li se zahtjev djelomično odobriti?» O «Isto kao odbijanje pa novi zahtjev» | Citat je "Zašto" stupac |
| P73 | kalendar | prikazuje li vikend dane unutar zahtjeva | – | – | ekran | pitanja, Maknuto s popisa, `vikend dane` | Q «Prikazuje li kalendar vikend dane unutar zahtjeva?» O «Ne mijenja ponašanje» | Citat je "Zašto" stupac; ne kaže prikazuje li ili ne |
| P74 | API | 404 umjesto 403 za tuđi zahtjev | – | – | API | pitanja, Maknuto s popisa, `404 umjesto 403` | Q «Vraća li API 404 umjesto 403 za tuđi zahtjev?» O «Pokriva ga pretpostavka uz pitanje 50» | Citat je "Zašto" stupac; pretpostavka uz Q50 u tablici je "400, 403, 404, 409; `application/problem+json`" |
| P75 | admin | smije odobriti | vlastiti zahtjev za godišnji | – | nije rečeno | pitanja, It.2, Q1.1 | Q «Smije li admin odobriti vlastiti zahtjev za godišnji?» O «Da» | |
| P76 | voditelj bilo kojeg od timova | smije odobriti | zahtjev zaposlenika u više timova | – | nije rečeno | pitanja, It.2, Q5.1 | Q «Smije li zahtjev zaposlenika u više timova odobriti voditelj bilo kojeg od tih timova?» O «Da» | |
| P77 | aplikacija | prati stjecanje slobodnih dana | – | – | nije rečeno | pitanja, It.2, Q9.1 | Q «Treba li aplikacija pratiti kako zaposlenik stječe slobodne dane (npr. za prekovremene)?» O «Not for now» | |
| P78 | preneseni dani | vrijede do 30. lipnja sljedeće godine | – | – | nije rečeno | pitanja, It.2, Q14.1 | Q «Do kojeg datuma vrijede preneseni dani?» O «Yes» | Odgovor "Yes" ne imenuje datum; datum je u pretpostavci |
| P79 | prijenos dana | nema gornje granice | – | – | nije rečeno | pitanja, It.2, Q14.2 | Q «Postoji li najveći broj dana koji se smije prenijeti?» O «Nope» | |
| P80 | razmjerni saldo | računa se po mjesecima | – | – | nije rečeno | pitanja, It.2, Q15.1 | Q «Računa li se razmjerni saldo po mjesecima ili po danima?» O «Mjeseci» | |
| P81 | razmjerni saldo | zaokružuje se | prema gore | – | nije rečeno | pitanja, It.2, Q15.2 | Q «Kako se zaokružuje razmjerni saldo?» O «Ceil» | |
| P82 | zahtjev | nema granice | koliko unatrag | – | nije rečeno | pitanja, It.2, Q19.1 | Q «Postoji li granica koliko se unatrag smije podnijeti zahtjev?» O «Nope» | |
| P83 | voditelj | ne otkazuje | odobreni zahtjev člana koji je počeo | – | nije rečeno | pitanja, It.2, Q30.1 | Q «Smije li voditelj otkazati odobreni zahtjev člana tima koji je već počeo?» O «Nope, nema dizanja sa godisnjeg.» | |
| P84 | nije rečeno | izmjene nakon činjenice | već započeti zahtjev | po potrebi | nije rečeno | pitanja, It.2, Q30.1 | O «Po potrebi ako se i dogodi, neka after the fact izmjene» | |
| P85 | admin | ne otkazuje | odobreni zahtjev koji je počeo | – | nije rečeno | pitanja, It.2, Q31.1 | Q «Smije li admin otkazati odobreni zahtjev koji je već počeo?» O «Vidi gore» | Odgovor upućuje na redak Q30.1 |
| P86 | admin | nema pregleda tko je danas prisutan | u tvrtki | – | nije rečeno | pitanja, It.2, Q43.1 | Q «Treba li adminu pregled tko je danas prisutan u tvrtki?» O «Nope» | |
| P87 | prijava | traži lozinku | – | – | nije rečeno | pitanja, It.2, Q49.1 | Q «Traži li prijava lozinku?» O «Da» | |
| P88 | aplikacija | podržava oko 20 korisnika | PoC | – | nije rečeno | pitanja, It.2, Q53.1 | Q «Koliko otprilike korisnika aplikacija treba podržati?» O «Za PoC racunaj i manje 20-ak» | |
| P89 | PoC | ne deploya se | – | dovoljno u Dockeru | nije rečeno | pitanja, It.2, Q53.2 | Q «Treba li POC biti deployan negdje, ili je dovoljno lokalno u Dockeru?» O «Dovoljno u dockeru» | |
| P90 | aplikacija | nema AI funkcionalnost | – | – | nije rečeno | pitanja, It.2, Q53.4 | Q «Očekuje li se AI funkcionalnost unutar same aplikacije?» O «Ne» | |
| S01 | nije rečeno | uloge su | Employee, Team lead, Admin | – | nije rečeno | spec, 1 Uloge i timovi, `Uloge su` | «Uloge su Employee, Team lead i Admin.» | |
| S02 | korisnik | može imati | više uloga | npr. admin koji je i voditelj | nije rečeno | spec, 1, `više uloga` | «Korisnik može imati više uloga, npr. admin koji je i voditelj» | |
| S03 | svaki korisnik | je | zaposlenik, pa i admin | – | nije rečeno | spec, 1, `Svaki korisnik` | «Svaki korisnik je zaposlenik, pa i admin» | |
| S04 | tim | ima | točno jednog voditelja | – | nije rečeno | spec, 1, `točno jednog` | «Tim ima točno jednog voditelja» | |
| S05 | voditelj | je član | svakog tima koji vodi | – | nije rečeno | spec, 1, `član svakog tima` | «Voditelj je član svakog tima koji vodi» | |
| S06 | voditelj | može voditi | više timova | – | nije rečeno | spec, 1, `više timova` | «i može voditi više timova» | |
| S07 | zaposlenik | može biti | u nijednom, jednom ili više timova | – | nije rečeno | spec, 1, `nijednom` | «Zaposlenik može biti u nijednom, jednom ili više timova» | |
| S08 | nije rečeno | može biti | više admina | – | nije rečeno | spec, 1, `više admina` | «Može biti više admina» | |
| S09 | zahtjev | ima polje Podnositelj | – | obavezno: da | nije rečeno | spec, 2 Zahtjev, tablica `Podnositelj` | «Podnositelj» · «da» | |
| S10 | zahtjev | ima polje Tip (Vacation, Sick leave, Day off) | – | obavezno: da | nije rečeno | spec, 2, tablica `Tip: Vacation` | «Tip: Vacation, Sick leave, Day off» · «da» | |
| S11 | zahtjev | ima polja početni i završni datum, oba uključiva | – | obavezno: da | nije rečeno | spec, 2, tablica `Početni i završni` | «Početni i završni datum, oba uključiva» · «da» | |
| S12 | zahtjev | ima polje Razlog | – | obavezno: ne | nije rečeno | spec, 2, tablica `Razlog` | «Razlog» · «ne» | |
| S13 | zahtjev | ima polje Stanje (Pending, Approved, Rejected, Cancelled) | – | obavezno: da | nije rečeno | spec, 2, tablica `Stanje: Pending` | «Stanje: Pending, Approved, Rejected, Cancelled» · «da» | |
| S14 | zahtjev | ima polje Komentar odluke | – | obavezno samo kod odbijanja | nije rečeno | spec, 2, tablica `Komentar odluke` | «Komentar odluke» · «samo kod odbijanja» | |
| S15 | zahtjev | ima polje Tko je odlučio | – | obavezno uz odluku | nije rečeno | spec, 2, tablica `Tko je odlučio` | «Tko je odlučio» · «uz odluku» | |
| S16 | zahtjev | ima polje Razlog otkazivanja | – | obavezan kad ne otkazuje vlasnik | nije rečeno | spec, 2, tablica `Razlog otkazivanja` | «Razlog otkazivanja» · «obavezan kad ne otkazuje vlasnik» | |
| S17 | zahtjev | ima polje Tko je otkazao | – | obavezno uz otkazivanje | nije rečeno | spec, 2, tablica `Tko je otkazao` | «Tko je otkazao» · «uz otkazivanje» | |
| S18 | zahtjev | ima polje Vrijeme stvaranja, vrijeme odluke ili otkazivanja | – | obavezno: da | nije rečeno | spec, 2, tablica `Vrijeme stvaranja` | «Vrijeme stvaranja, vrijeme odluke ili otkazivanja» · «da» | |
| S19 | Day off i Sick leave | nemaju limit | – | – | nije rečeno | spec, 2, `nemaju limit` | «Day off i Sick leave nemaju limit ni vlastiti saldo» | Jedna rečenica, dvije stvari; vidi S20 |
| S20 | Day off i Sick leave | nemaju vlastiti saldo | – | – | nije rečeno | spec, 2, `vlastiti saldo` | «Day off i Sick leave nemaju limit ni vlastiti saldo» | |
| S21 | Day off i Sick leave | mogu trajati | više dana | – | nije rečeno | spec, 2, `više dana` | «Mogu trajati više dana» | |
| S22 | Sick leave | potvrda se ne prilaže | – | – | nije rečeno | spec, 2, `Potvrda` | «Potvrda se ne prilaže» | |
| S23 | aplikacija | ne prati | stjecanje slobodnih dana | – | nije rečeno | spec, 2, `ne prati` | «Aplikacija ne prati kako se stječu slobodni dani» | |
| S24 | zahtjev | ne uređuje se | – | – | nije rečeno | spec, 2, `ne uređuje` | «Zahtjev se ne uređuje» | |
| S25 | zahtjev | otkaže se i podnese novi | – | umjesto uređivanja | nije rečeno | spec, 2, `podnese novi` | «otkaže se i podnese novi» | |
| S26 | vlasnik | jedini podnosi | zahtjev | nitko u ime drugoga | nije rečeno | spec, 2, `samo vlasnik` | «Zahtjev podnosi samo vlasnik, nitko u ime drugoga» | |
| S27 | voditelj | odobrava i odbija | zahtjeve članova svih timova koje vodi | – | nije rečeno | spec, 3 Odobravanje, `svih timova koje vodi` | «Voditelj odobrava i odbija zahtjeve članova svih timova koje vodi» | |
| S28 | voditelj bilo kojeg od timova | smije odobriti | zahtjev člana više timova | – | nije rečeno | spec, 3, `više timova` | «Zahtjev člana više timova smije odobriti voditelj bilo kojeg od tih timova» | |
| S29 | nije rečeno | dovoljno je | jedno odobrenje | član više timova | nije rečeno | spec, 3, `jedno odobrenje` | «dovoljno je jedno odobrenje» | |
| S30 | admin | odobrava | zahtjev voditelja | – | nije rečeno | spec, 3, `Zahtjev voditelja ide` | «Zahtjev voditelja ide adminu» | |
| S31 | admin | odobrava | zahtjev voditelja | i kad je voditelj član tuđeg tima | nije rečeno | spec, 3, `tuđeg tima` | «i kad je voditelj član tuđeg tima» | |
| S32 | admin | odobrava | zahtjev zaposlenika bez tima | – | nije rečeno | spec, 3, `bez tima` | «Zahtjev zaposlenika bez tima odobrava admin» | |
| S33 | admin | odobrava i odbija | bilo koji zahtjev | – | nije rečeno | spec, 3, `bilo koji zahtjev` | «Admin odobrava i odbija bilo koji zahtjev» | |
| S34 | nitko | ne odobrava ni ne odbija | vlastiti zahtjev | – | nije rečeno | spec, 3, `vlastiti zahtjev` | «Nitko ne odobrava ni ne odbija vlastiti zahtjev» | |
| S35 | admin | smije odobriti | vlastiti zahtjev | iznimka od prethodnog | nije rečeno | spec, 3, `Iznimka` | «Iznimka: admin smije odobriti vlastiti zahtjev» | |
| S36 | admin | smije odobriti vlastiti zahtjev | – | bilo kojeg tipa | nije rečeno | spec, 3, `bilo kojeg tipa` | «bilo kojeg tipa» | |
| S37 | nije rečeno | odobrava se i odbija | samo zahtjev na čekanju | – | nije rečeno | spec, 3, `samo zahtjev na čekanju` | «Odobrava se i odbija samo zahtjev na čekanju» | |
| S38 | nije rečeno | odobrava se i odbija | zahtjev na čekanju | i kad su mu datumi prošli | nije rečeno | spec, 3, `datumi prošli` | «i kad su mu datumi prošli» | |
| S39 | nije rečeno | odbijanje traži | komentar | – | nije rečeno | spec, 3, `Odbijanje traži` | «Odbijanje traži komentar» | |
| S40 | nije rečeno | odobrenje ne traži | komentar | – | nije rečeno | spec, 3, `odobrenje ne` | «odobrenje ne» | |
| S41 | zahtjev na čekanju | ne istječe | – | – | nije rečeno | spec, 3, `ne istječe` | «Zahtjev na čekanju ne istječe» | |
| S42 | tko smije odobriti | smije i otkazati | zahtjev | – | nije rečeno | spec, 4 Otkazivanje, `smije ga i otkazati` | «Tko smije odobriti zahtjev, smije ga i otkazati» | |
| S43 | vlasnik | otkazuje, razlog opcionalan | zahtjev | Na čekanju | nije rečeno | spec, 4, tablica, `Vlasnik` | «Otkazuje, razlog opcionalan» | |
| S44 | vlasnik | otkazuje, razlog opcionalan | zahtjev | Odobren, nije počeo | nije rečeno | spec, 4, tablica, `Vlasnik` | «Otkazuje, razlog opcionalan» | |
| S45 | vlasnik | ne otkazuje | zahtjev | Odobren, počeo | nije rečeno | spec, 4, tablica, `Vlasnik` | «Ne» | |
| S46 | tko smije odobriti | otkazuje, razlog obavezan | zahtjev | Na čekanju | nije rečeno | spec, 4, tablica, `Tko smije odobriti (odjeljak 3)` | «Otkazuje, razlog obavezan» | |
| S47 | tko smije odobriti | otkazuje, razlog obavezan | zahtjev | Odobren, nije počeo | nije rečeno | spec, 4, tablica | «Otkazuje, razlog obavezan» | |
| S48 | tko smije odobriti | ne otkazuje | zahtjev | Odobren, počeo | nije rečeno | spec, 4, tablica | «Ne» | |
| S49 | nije rečeno | zahtjev je počeo | zahtjev koji počinje danas | – | nije rečeno | spec, 4, `počinje danas` | «Zahtjev koji počinje danas je počeo» | |
| S50 | nije rečeno | prelazi u Cancelled | otkazani zahtjev | – | nije rečeno | spec, 4, `prelazi u Cancelled` | «Otkazani zahtjev prelazi u Cancelled.» | |
| S51 | nije rečeno | izmjene rade se naknadno izravno u bazi | već započeti zahtjev | – | nije rečeno | spec, 4, `izravno u bazi` | «Izmjene već započetog zahtjeva rade se naknadno, izravno u bazi» | |
| S52 | aplikacija | broji samo radne dane | – | – | nije rečeno | spec, 5 Validacija, `samo radne dane` | «Aplikacija broji samo radne dane.» | |
| S53 | aplikacija | preskače vikende | – | – | nije rečeno | spec, 5, `Preskaču se vikendi` | «Preskaču se vikendi» | |
| S54 | blagdani | su proširenje | – | – | nije rečeno | spec, 5, `Blagdani` | «Blagdani su proširenje» | |
| S55 | nije rečeno | zahtjev je nevaljan | zahtjev | bez ijednog radnog dana | nije rečeno | spec, 5, `nevaljan` | «Zahtjev bez ijednog radnog dana je nevaljan» | |
| S56 | nije rečeno | završni datum nije prije početnog | zahtjev | – | nije rečeno | spec, 5, `nije prije početnog` | «Završni datum nije prije početnog» | |
| S57 | korisnik | nema dva zahtjeva | na čekanju ili odobrena | datumi se preklapaju, bez obzira na tip | nije rečeno | spec, 5, `preklapaju` | «Korisnik nema dva zahtjeva na čekanju ili odobrena čiji se datumi preklapaju, bez obzira na tip» | |
| S58 | nije rečeno | zahtjev ne počinje | prije datuma zaposlenja | – | nije rečeno | spec, 5, `datuma zaposlenja` | «Zahtjev ne počinje prije datuma zaposlenja» | |
| S59 | nije rečeno | zahtjev je | unutar jedne kalendarske godine | – | nije rečeno | spec, 5, `jedne kalendarske godine` | «Zahtjev je unutar jedne kalendarske godine» | |
| S60 | nije rečeno | zahtjev preko granice godine | je proširenje | odjeljak 9 | nije rečeno | spec, 5, `preko granice godine` | «Zahtjev preko granice godine je proširenje (odjeljak 9).» | |
| S61 | nije rečeno | datumi u prošlosti dopušteni | za sve tipove | – | nije rečeno | spec, 5, `u prošlosti` | «Datumi u prošlosti su dopušteni za sve tipove» | |
| S62 | nije rečeno | nema granice unatrag | datumi u prošlosti | – | nije rečeno | spec, 5, `bez granice unatrag` | «bez granice unatrag» | |
| S63 | nije rečeno | datumi u budućnosti dopušteni bez granice | – | – | nije rečeno | spec, 5, `u budućnosti` | «Datumi u budućnosti su dopušteni bez granice» | |
| S64 | nije rečeno | nema minimalnog roka najave | – | – | nije rečeno | spec, 5, `roka najave` | «Nema minimalnog roka najave» | |
| S65 | nije rečeno | nema najduljeg trajanja | – | – | nije rečeno | spec, 5, `najduljeg trajanja` | «ni najduljeg trajanja» | |
| S66 | nije rečeno | nema najvećeg broja | zahtjeva na čekanju | – | nije rečeno | spec, 5, `najvećeg broja` | «Nema najvećeg broja zahtjeva na čekanju» | |
| S67 | nije rečeno | nema minimalne prisutnosti | u timu | – | nije rečeno | spec, 5, `minimalne prisutnosti` | «ni minimalne prisutnosti u timu» | |
| S68 | svaki korisnik | dobiva | 20 radnih dana godišnjeg | za svaku kalendarsku godinu | nije rečeno | spec, 6 Saldo, `20 radnih dana` | «Svaki korisnik dobiva 20 radnih dana godišnjeg za svaku kalendarsku godinu» | |
| S69 | korisnik | dobiva razmjerni dio 20 × broj mjeseci / 12 | saldo | u godini zaposlenja | nije rečeno | spec, 6, `razmjerni dio` | «U godini zaposlenja dobiva razmjerni dio: 20 × broj mjeseci / 12» | |
| S70 | nije rečeno | zaokružuje se prema gore | razmjerni dio | – | nije rečeno | spec, 6, `prema gore` | «zaokruženo prema gore.» | |
| S71 | nije rečeno | broje se mjeseci | od mjeseca zaposlenja do prosinca, uključivo | – | nije rečeno | spec, 6, `uključivo` | «Broje se mjeseci od mjeseca zaposlenja do prosinca, uključivo» | |
| S72 | nije rečeno | primjer | zaposlen u lipnju | 7 mjeseci | nije rečeno | spec, 6, `Primjer` | «Primjer: zaposlen u lipnju, 7 mjeseci, 20 × 7 / 12 = 11,67, dakle 12 dana.» | |
| S73 | korisnik | vidi | iskorištene (odobrene), dane na čekanju i preostale dane | tekuća godina | nije rečeno | spec, 6, `tekuću godinu` | «Za tekuću godinu korisnik vidi iskorištene (odobrene), dane na čekanju i preostale dane» | |
| S74 | nije rečeno | ne smije premašiti | preostali saldo godine u koju pada | zahtjev za godišnji | nije rečeno | spec, 6, `ne smije premašiti` | «Zahtjev za godišnji ne smije premašiti preostali saldo godine u koju pada.» | |
| S75 | zahtjevi na čekanju | troše | saldo | – | nije rečeno | spec, 6, `troše saldo` | «Zahtjevi na čekanju troše saldo.» | |
| S76 | bolovanje i slobodni dani | ne troše | saldo | – | nije rečeno | spec, 6, `ne troše saldo` | «Bolovanje i slobodni dani ne troše saldo» | |
| S77 | nije rečeno | ne prenose se | neiskorišteni dani u sljedeću godinu | – | nije rečeno | spec, 6, `ne prenose` | «Neiskorišteni dani se ne prenose u sljedeću godinu» | |
| S78 | nije rečeno | prijenos je proširenje | – | – | nije rečeno | spec, 6, `prijenos je proširenje` | «prijenos je proširenje» | |
| S79 | nije rečeno | korekcije rade se izravno u bazi | izmjenom zahtjeva ili datuma zaposlenja | – | nije rečeno | spec, 6, `Korekcije` | «Korekcije se rade izravno u bazi, izmjenom zahtjeva ili datuma zaposlenja» | |
| S80 | API | provodi | sva pravila vidljivosti | – | API | spec, 7 Vidljivost, `API provodi` | «API provodi sva pravila vidljivosti.» | |
| S81 | nije rečeno | skrivanje u frontendu | – | se ne računa | ekran | spec, 7, `Skrivanje` | «Skrivanje u frontendu se ne računa» | |
| S82 | zaposlenik | vidi | svoje zahtjeve sa svim detaljima | – | nije rečeno | spec, 7, tablica Zahtjevi, `Zaposlenik` | «Svoje zahtjeve sa svim detaljima» | |
| S83 | zaposlenik | vidi | komentar odluke | na svoje zahtjeve | nije rečeno | spec, 7, tablica | «uključujući komentar odluke» | |
| S84 | zaposlenik | vidi | tko je odlučio | na svoje zahtjeve | nije rečeno | spec, 7, tablica | «tko je odlučio» | |
| S85 | zaposlenik | vidi | tko je otkazao | na svoje zahtjeve | nije rečeno | spec, 7, tablica | «i tko je otkazao» | |
| S86 | voditelj | vidi | sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom | – | nije rečeno | spec, 7, tablica, `Voditelj` | «Sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom» | |
| S87 | voditelj | vidi | saldo članova timova koje vodi | – | nije rečeno | spec, 7, tablica | «Saldo članova timova koje vodi» | |
| S88 | admin | vidi | sve zahtjeve u tvrtki | – | nije rečeno | spec, 7, tablica, `Admin` | «Sve zahtjeve i sve salde u tvrtki» | Jedan citat, dvije stvari; vidi S89 |
| S89 | admin | vidi | sve salde u tvrtki | – | nije rečeno | spec, 7, tablica | «Sve zahtjeve i sve salde u tvrtki» | |
| S90 | zaposlenik (kalendar) | vidi | svoje zahtjeve, na čekanju i odobrene | – | nije rečeno | spec, 7, tablica Kalendar | «Na čekanju i odobrene» | |
| S91 | zaposlenik (kalendar) | vidi | samo odobrene zahtjeve drugih | – | nije rečeno | spec, 7, tablica Kalendar | «Samo odobrene» | |
| S92 | zaposlenik (kalendar) | vidi | kolege iz timova u kojima je član | – | nije rečeno | spec, 7, tablica Kalendar | «kolega iz timova u kojima je član» | |
| S93 | zaposlenik (kalendar) | vidi | samo ime i datume | – | nije rečeno | spec, 7, tablica Kalendar | «samo ime i datumi» | |
| S94 | zaposlenik (kalendar) | ne vidi | tip | – | nije rečeno | spec, 7, tablica Kalendar | «bez tipa» | |
| S95 | zaposlenik (kalendar) | ne vidi | razlog | – | nije rečeno | spec, 7, tablica Kalendar | «razloga» | |
| S96 | zaposlenik (kalendar) | ne vidi | zahtjeve na čekanju | – | nije rečeno | spec, 7, tablica Kalendar | «zahtjeva na čekanju» | Tri redka (tip, razlog, na čekanju) dolaze iz jednog citata «bez tipa, razloga i zahtjeva na čekanju» |
| S97 | voditelj (kalendar) | vidi | svoje zahtjeve, na čekanju i odobrene | – | nije rečeno | spec, 7, tablica Kalendar, `Voditelj` | «Na čekanju i odobrene» | |
| S98 | voditelj (kalendar) | vidi | zahtjeve na čekanju i odobrene drugih | – | nije rečeno | spec, 7, tablica Kalendar | «Na čekanju i odobrene, članova timova koje vodi, s tipom» | |
| S99 | voditelj (kalendar) | vidi | članove timova koje vodi | – | nije rečeno | spec, 7, tablica Kalendar | «članova timova koje vodi» | |
| S100 | voditelj (kalendar) | vidi | tip | zahtjevi drugih | nije rečeno | spec, 7, tablica Kalendar | «s tipom» | |
| S101 | voditelj (kalendar) | vidi isto što i zaposlenik | ostali timovi u kojima je član | – | nije rečeno | spec, 7, tablica Kalendar | «Za ostale timove u kojima je član vidi isto što i zaposlenik» | |
| S102 | admin (kalendar) | vidi | svoje zahtjeve, na čekanju i odobrene | – | nije rečeno | spec, 7, tablica Kalendar, `Admin` | «Na čekanju i odobrene» | |
| S103 | admin (kalendar) | vidi | zahtjeve na čekanju i odobrene drugih | – | nije rečeno | spec, 7, tablica Kalendar | «Na čekanju i odobrene, svih u tvrtki» | |
| S104 | admin (kalendar) | vidi | sve u tvrtki | – | nije rečeno | spec, 7, tablica Kalendar | «svih u tvrtki» | |
| S105 | kalendar | ne prikazuje nikome | odbijene i otkazane zahtjeve | – | nije rečeno | spec, 7, `Odbijeni i otkazani` | «Odbijeni i otkazani zahtjevi ne prikazuju se nikome na kalendaru» | |
| S106 | admin | po defaultu vidi | kalendar sa svim timovima | – | nije rečeno | spec, 7, `po defaultu` | «Adminov kalendar po defaultu prikazuje sve timove» | |
| S107 | admin | vidi na kalendaru | korisnike bez tima | – | nije rečeno | spec, 7, `uključujući korisnike bez tima` | «uključujući korisnike bez tima» | |
| S108 | admin | ima filtar | po timu | kalendar | nije rečeno | spec, 7, `s filtrom po timu` | «s filtrom po timu» | |
| S109 | zaposlenik u više timova | vidi se | na kalendaru svakog svog tima | – | nije rečeno | spec, 7, `svakog svog tima` | «Zaposlenik u više timova vidi se na kalendaru svakog svog tima.» | |
| S110 | samo admini | vide | odsutnost korisnika bez tima | – | nije rečeno | spec, 7, `samo admini` | «Odsutnost korisnika bez tima vide samo admini.» | |
| S111 | nije rečeno | prijava lozinkom vraća | JWT | seedane lozinke | API | spec, 8 API i isporuka, `vraća JWT` | «Prijava lozinkom sa seedanim lozinkama vraća JWT» | |
| S112 | svaki poziv | šalje | `Authorization: Bearer` | – | API | spec, 8, `Authorization` | «svaki poziv šalje `Authorization: Bearer`» | |
| S113 | API | koristi standardne HTTP statuse | – | – | API | spec, 8, `Standardni HTTP statusi` | «Standardni HTTP statusi» | |
| S114 | API | koristi format grešaka | `application/problem+json` | – | API | spec, 8, `problem+json` | «format `application/problem+json`» | |
| S115 | API | 400 | neispravan unos | npr. nedostaje polje, završni datum prije početnog | API | spec, 8, `400:` | «400: neispravan unos (npr. nedostaje polje, završni datum prije početnog);» | |
| S116 | API | 401 | korisnik nije prijavljen | – | API | spec, 8, `401:` | «401: korisnik nije prijavljen;» | |
| S117 | API | 403 | korisnik nema pravo na radnju | – | API | spec, 8, `403:` | «403: korisnik nema pravo na radnju;» | |
| S118 | API | 404 | zahtjev ne postoji | – | API | spec, 8, `404:` | «404: zahtjev ne postoji;» | |
| S119 | API | 409 | kršenje pravila | preklapanje datuma, premašen saldo, krivo stanje zahtjeva | API | spec, 8, `409:` | «409: kršenje pravila (preklapanje datuma, premašen saldo, krivo stanje zahtjeva).» | |
| S120 | nije rečeno | nema Swaggera | – | – | API | spec, 8, `Bez Swaggera` | «Bez Swaggera; dijagrami tokova i sekvencijski dijagrami» | Jedan citat, dvije stvari; vidi "Izvan formata" |
| S121 | aplikacija | radi cijela | u Docker kontejnerima | – | nije rečeno | spec, 8, `Docker kontejnerima` | «Cijela aplikacija u Docker kontejnerima» | |
| S122 | aplikacija | pokreće se | jednom naredbom | – | nije rečeno | spec, 8, `jednom naredbom` | «pokretanje jednom naredbom» | |
| S123 | aplikacija | ne deploya se | na server | – | nije rečeno | spec, 8, `deploy na server` | «deploy na server ne treba» | |
| S124 | nije rečeno | sve na engleskom | UI, README, kod | – | ekran | spec, 8, `Sve na engleskom` | «Sve na engleskom: UI, README, kod» | |
| S125 | seed | sadrži | korisnike, timove, lozinke i datume zaposlenja | – | nije rečeno | spec, 8, `Seed:` | «Seed: korisnici, timovi, lozinke i datumi zaposlenja» | |
| S126 | seed | ne sadrži | zahtjeve | – | nije rečeno | spec, 8, `bez zahtjeva` | «bez zahtjeva» | |
| S127 | svi korisnici | imaju lozinku `password` | – | – | nije rečeno | spec, 8, `lozinku` | «Svi imaju lozinku `password`» | |
| S128 | svi korisnici | zaposleni su 2020-01-01, osim Toma | – | Tom 2026-06-01 | nije rečeno | spec, 8, `2020-01-01` | «svi su zaposleni 2020-01-01, osim Toma, zaposlenog 2026-06-01» | |
| S129 | aplikacija | za oko 20 korisnika | POC | – | nije rečeno | spec, 8 Opseg, `oko 20` | «POC za oko 20 korisnika» | |
| S130 | aplikacija | nema AI funkcionalnosti | – | – | nije rečeno | spec, 8 Opseg, `AI funkcionalnosti` | «U aplikaciji nema AI funkcionalnosti» | |
| S131 | nije rečeno | proširenja se ne rade u POC-u | – | – | nije rečeno | spec, 9 Proširenja, uvod, `ne radimo` | «Kolega ih je opisao u odgovorima, ali ih ne radimo u POC-u.» | |
| S132 | nije rečeno | prijenos neiskorištenih dana | u sljedeću godinu | proširenje | nije rečeno | spec, 9, tablica, `Prijenos` | «Prijenos neiskorištenih dana u sljedeću godinu» | Stupac "Gdje bi se dodalo": «Izračun salda» |
| S133 | nije rečeno | preneseni dani vrijede | do 30. lipnja | proširenje | nije rečeno | spec, 9, tablica | «vrijede do 30. lipnja» | Isti stupac "Gdje": «Izračun salda» |
| S134 | nije rečeno | prijenos nema | gornju granicu | proširenje | nije rečeno | spec, 9, tablica | «bez gornje granice» | Isti stupac "Gdje": «Izračun salda» |
| S135 | nije rečeno | prvo se troše | najstariji dani | proširenje | nije rečeno | spec, 9, tablica | «prvo se troše najstariji dani» | Isti stupac "Gdje": «Izračun salda» |
| S136 | nije rečeno | hrvatski blagdani se ne broje | kao radni dani | proširenje | nije rečeno | spec, 9, tablica | «Hrvatski blagdani se ne broje kao radni dani» | Stupac "Gdje bi se dodalo": «Funkcija koja broji radne dane» |
| S137 | nije rečeno | zahtjev preko granice godine ostaje | jedan zahtjev | proširenje | nije rečeno | spec, 9, tablica | «Zahtjev preko granice godine ostaje jedan zahtjev» | Stupac "Gdje bi se dodalo": «Validacija i izračun salda» |
| S138 | nije rečeno | dani se broje | po godinama | zahtjev preko granice godine, proširenje | nije rečeno | spec, 9, tablica | «a dani se broje po godinama» | Isti stupac "Gdje": «Validacija i izračun salda» |
| S139 | nije rečeno | bolovanje smije preklopiti | odobreni godišnji | proširenje | nije rečeno | spec, 9, tablica | «Bolovanje smije preklopiti odobreni godišnji» | Stupac "Gdje bi se dodalo": «Provjera preklapanja i izračun salda» |
| S140 | nije rečeno | godišnji se otkazuje / refundira | – | bolovanje preko odobrenog godišnjeg | nije rečeno | spec, 9, tablica | «"onda ti se GO otkazuje / refundira"» | Isti stupac "Gdje": «Provjera preklapanja i izračun salda» |
| S141 | nije rečeno | preskaču se samo vikendi | – | D1 | nije rečeno | spec, 10 Naše odluke, D1 | «Preskaču se samo vikendi» | |
| S142 | nije rečeno | hrvatski blagdani su proširenje | – | D1 | nije rečeno | spec, 10, D1 | «hrvatski blagdani su proširenje» | |
| S143 | admin | zahtjev voditelja uvijek ide adminu | zahtjev voditelja | i kad je voditelj član tuđeg tima (D2) | nije rečeno | spec, 10, D2 | «Zahtjev voditelja uvijek ide adminu, i kad je voditelj član tuđeg tima» | |
| S144 | voditelj, admin | moraju upisati razlog | otkazivanje tuđeg zahtjeva (D3) | – | nije rečeno | spec, 10, D3 | «Tko otkazuje tuđi zahtjev (voditelj, admin) mora upisati razlog» | |
| S145 | nije rečeno | zahtjev ne počinje | prije datuma zaposlenja (D4) | – | nije rečeno | spec, 10, D4 | «Zahtjev ne počinje prije datuma zaposlenja» | Ponavlja S58 |
| S146 | nije rečeno | svaka godina ima | svojih 20 dana (D5) | – | nije rečeno | spec, 10, D5 | «Svaka godina ima svojih 20 dana» | |
| S147 | nije rečeno | nema prijenosa | (D5) | – | nije rečeno | spec, 10, D5 | «bez prijenosa» | |
| S148 | nije rečeno | zahtjev troši | saldo godine u koju pada (D5) | – | nije rečeno | spec, 10, D5 | «zahtjev troši saldo godine u koju pada» | |
| S149 | nije rečeno | broje se mjeseci | od mjeseca zaposlenja do prosinca (D5) | godina zaposlenja | nije rečeno | spec, 10, D5 | «U godini zaposlenja broje se mjeseci od mjeseca zaposlenja do prosinca» | |
| S150 | nije rečeno | korekcije rade se izravno u bazi (SQL) | datum zaposlenja, već započet zahtjev (D6) | – | nije rečeno | spec, 10, D6 | «Korekcije (datum zaposlenja, već započet zahtjev) rade se izravno u bazi (SQL)» | |
| S151 | nije rečeno | postupak korekcije | – | opisan u README-u (D6) | nije rečeno | spec, 10, D6 | «postupak u README-u» | |
| S152 | nije rečeno | nema posebne tablice | korekcija salda (D6) | – | nije rečeno | spec, 10, D6 | «Nema posebne tablice korekcija salda» | |
| S153 | nije rečeno | proširenja iz odgovora kolege se ne rade | – | D7 | nije rečeno | spec, 10, D7 | «Proširenja iz odgovora kolege ne radimo, nego ih opisujemo u README-u» | |
| S154 | nije rečeno | vremenska zona se postavlja varijablom `TZ` | – | D8 | nije rečeno | spec, 10, D8 | «Vremenska zona se postavlja varijablom `TZ`» | |
| S155 | nije rečeno | `TZ` je u `docker-compose` | Europe/Zagreb | D8 | nije rečeno | spec, 10, D8 | «u `docker-compose` Europe/Zagreb» | |
| S156 | seed | bez demo zahtjeva | – | D9 | nije rečeno | spec, 10, D9 | «Seed bez demo zahtjeva» | Ponavlja S126 |
| S157 | nije rečeno | ekran prijave je forma | – | D10 | ekran | spec, 10, D10 | «Ekran prijave je forma» | |
| S158 | korisnik | upisuje | korisničko ime i lozinku | D10 | ekran | spec, 10, D10 | «korisnik upisuje korisničko ime i lozinku» | |
| S159 | nije rečeno | ekran prijave bez popisa korisnika | – | D10 | ekran | spec, 10, D10 | «bez popisa korisnika» | |
| S160 | nije rečeno | format grešaka | `application/problem+json` | D11 | API | spec, 10, D11 | «Format grešaka `application/problem+json`» | Ponavlja S114 |
| S161 | voditelj s više timova | ima | jedan kalendar | D12 | nije rečeno | spec, 10, D12 | «Voditelj s više timova ima jedan kalendar» | |
| S162 | voditelj s više timova | kalendar ima filtar | po timu | D12 | nije rečeno | spec, 10, D12 | «s filtrom po timu» | |
| S163 | zahtjev | bilježi vrijeme stvaranja | – | D13 | nije rečeno | spec, 10, D13 | «Zahtjev bilježi vrijeme stvaranja, odluke i otkazivanja» | Jedan citat, tri vremena; vidi S164 i S165 |
| S164 | zahtjev | bilježi vrijeme odluke | – | D13 | nije rečeno | spec, 10, D13 | «Zahtjev bilježi vrijeme stvaranja, odluke i otkazivanja» | |
| S165 | zahtjev | bilježi vrijeme otkazivanja | – | D13 | nije rečeno | spec, 10, D13 | «Zahtjev bilježi vrijeme stvaranja, odluke i otkazivanja» | |
| S166 | tko smije odobriti | smije i otkazati | zahtjev | D14 | nije rečeno | spec, 10, D14 | «Tko smije odobriti zahtjev, smije ga i otkazati» | Ponavlja S42 |
| S167 | admin | smije odobriti | vlastiti zahtjev bilo kojeg tipa | D15 | nije rečeno | spec, 10, D15 | «Admin smije odobriti vlastiti zahtjev bilo kojeg tipa» | Ponavlja S35 i S36 |
| S168 | nije rečeno | lozinka `password` | za sve | D16 | nije rečeno | spec, 10, D16 | «Seed: lozinka `password` za sve» | Ponavlja S127 |
| S169 | nije rečeno | datum zaposlenja 2020-01-01 | za sve osim Toma (2026-06-01) | D16 | nije rečeno | spec, 10, D16 | «datum zaposlenja 2020-01-01 za sve osim Toma (2026-06-01)» | Ponavlja S128 |
| S170 | Tom | ima razmjerni saldo | 7 mjeseci, 12 dana | godina zaposlenja | nije rečeno | spec, 10, D16, `Tom pokazuje` | «Tom pokazuje razmjerni saldo u godini zaposlenja ([🟩 Q15]…): 7 mjeseci, 12 dana» | U citatu je izostavljena oznaka izvora u zagradi |
| S171 | voditelj i admin | otkazuju | tuđi odobreni zahtjev koji nije počeo | D17 | ekran | spec, 10, D17 | «Voditelj i admin otkazuju tuđi odobreni zahtjev koji nije počeo kroz ekran Approvals» | |
| S172 | voditelj i admin | otkazuju kroz tablicu "Approved requests" | – | D17 | ekran | spec, 10, D17 | «(tablica "Approved requests", razlog obavezan)» | Jedan citat, dvije stvari; vidi S173 |
| S173 | voditelj i admin | razlog je obavezan | otkazivanje tuđeg odobrenog zahtjeva | D17 | ekran | spec, 10, D17 | «razlog obavezan» | |
| S174 | nije rečeno | otkazivanje tuđeg zahtjeva na čekanju kroz UI | – | još nije napravljeno | ekran | spec, 10, D17, `još nije napravljeno` | «Otkazivanje tuđeg zahtjeva na čekanju kroz UI još nije napravljeno» | |
| S175 | nije rečeno | otkazivanje tuđeg zahtjeva na čekanju | – | samo API | API | spec, 10, D17, `samo API` | «(samo API)» | |
| S176 | nije rečeno | voditelj može voditi više timova | – | odstupanje od briefa | nije rečeno | spec, 11 Odstupanja, `Voditelj može voditi više timova` | «Voditelj može voditi više timova» | Ponavlja S06 |
| S177 | admin | je zaposlenik | – | odstupanje od briefa | nije rečeno | spec, 11, `Admin je zaposlenik` | «Admin je zaposlenik i ima saldo» | Jedan citat, dvije stvari; vidi S178 |
| S178 | admin | ima saldo | – | odstupanje od briefa | nije rečeno | spec, 11, `ima saldo` | «Admin je zaposlenik i ima saldo» | |
| S179 | admin | smije odobriti | vlastiti zahtjev | odstupanje od briefa | nije rečeno | spec, 11, `Admin smije odobriti vlastiti` | «Admin smije odobriti vlastiti zahtjev» | Ponavlja S35 |
| S180 | tko smije odobriti | otkazuje | tuđi zahtjev | odstupanje od briefa | nije rečeno | spec, 11, `Otkazuje i tko smije odobriti` | «Otkazuje i tko smije odobriti» | |
| S181 | tko smije odobriti | otkazuje uz obavezan razlog | tuđi zahtjev | odstupanje od briefa | nije rečeno | spec, 11 | «uz obavezan razlog» | |
| S182 | nije rečeno | razmjerni saldo | u godini zaposlenja | odstupanje od briefa | nije rečeno | spec, 11, `Razmjerni saldo` | «Razmjerni saldo u godini zaposlenja» | |
| S183 | nije rečeno | Docker je obavezan | – | odstupanje od briefa | nije rečeno | spec, 11, `Docker je obavezan` | «Docker je obavezan» | |
| S184 | app | ima tri ekrana | My time off, Approvals, Team calendar | – | ekran | spec, 12 Ekrani, uvod | «Tri ekrana iz briefa» | |
| S185 | My time off | prikazuje | moje zahtjeve sa stanjem | – | ekran | spec, 12, `moji zahtjevi sa stanjem` | «moji zahtjevi sa stanjem» | |
| S186 | My time off | ima | formu za novi zahtjev | – | ekran | spec, 12, `forma za novi zahtjev` | «forma za novi zahtjev» | |
| S187 | My time off | ima | otkazivanje | – | ekran | spec, 12, `otkazivanje` | «otkazivanje» | |
| S188 | My time off | prikazuje | saldo godišnjeg | – | ekran | spec, 12, `saldo godišnjeg` | «saldo godišnjeg» | |
| S189 | Approvals | dostupan | voditeljima i adminima | samo njima | ekran | spec, 12, `samo za voditelje i admine` | «samo za voditelje i admine» | |
| S190 | Approvals | prikazuje | zahtjeve na čekanju koje smiju odobriti | – | ekran | spec, 12, `na čekanju koje smiju` | «zahtjevi na čekanju koje smiju odobriti» | |
| S191 | Approvals | ima | odobravanje i odbijanje | – | ekran | spec, 12, `s odobravanjem i odbijanjem` | «s odobravanjem i odbijanjem» | |
| S192 | Approvals | prikazuje ispod | odobrene tuđe zahtjeve koje smiju otkazati, a nisu počeli | – | ekran | spec, 12, `ispod njih` | «ispod njih odobreni tuđi zahtjevi koje smiju otkazati, a nisu počeli» | |
| S193 | Team calendar | prikazuje | tko je odsutan u odabranom mjesecu, po pravilima vidljivosti | – | ekran | spec, 12, `odabranom mjesecu` | «tko je odsutan u odabranom mjesecu, po pravilima vidljivosti» | |
| S194 | Team calendar | popis | po danima | – | ekran | spec, 12, `popis po danima` | «popis po danima» | |
| S195 | ekran | prikazuje poruku greške | kad backend odbije zahtjev | npr. preklapanje ili premašen saldo | ekran | spec, 12, `poruku greške` | «Kad backend odbije zahtjev, ekran prikazuje poruku greške, npr. preklapanje ili premašen saldo» | |

## Ukupan broj redaka po dokumentu

| Dokument | Redaka |
| --- | --- |
| brief.md (B01–B85) | 85 |
| atomarna-pitanja.md (P01–P90) | 90 |
| specifikacija.md (S01–S195) | 195 |
| Ukupno | 370 |

## Izvan formata

1. **Pretpostavke u atomarna-pitanja.md.** Svako pitanje ima stupac "Pretpostavka" (npr. Q14 "Ne", Q44 "Vlasnik i oni koji smiju odobravati") koji je različit od odgovora. Nisam ih uzeo kao redove jer dokument kaže da odgovor vrijedi, ali ponegdje je odgovor ("Da (slaže se)", "Yes", "Vidi gore") bez pretpostavke nejasan. Tablica nema stupac za dva različita izvora istog pitanja.
2. **Procesni i isporučni redovi nisu u tablici.** Brief: rok od 7 dana, 6 do 8 sati, README, testovi, način predaje, intervju. Pitanja: Q53.3, Q53.5, Q53.6, Q53.7, Q53.8, Q56. Specifikacija: "jedini izvor istine", Načelo, D7 (80/20), repozitorij GitHub, rok 29. listopada 2026., očekivano razumijevanje koda, dijagrami (kad je dio jedne rečenice sa Swaggerom, ostavljen u S120), odjeljak 13 Otvoreno. Opisuju kako se radi, a ne što aplikacija radi.
3. **Oznake izvora u specifikaciji.** Svako pravilo ima uz sebe šifre (R/Q/D) koje nisu dio rečenice. U citatima sam ih izostavio; tablica nema mjesta za njih, a bez njih ne vidi se koji je brief ili odgovor stoji iza pravila.
4. **Rečenice koje kažu dvije stvari, a ne mogu se pošteno razdvojiti.** "approve or reject" i "odobrava i odbija" ostavio sam u jednom retku. "Citat" kod S96, S88, S120, S163 i sličnih se ponavlja jer je isti iz jedne rečenice.
5. **Odgovori koji nisu izjava o ponašanju.** Q39 ("Nije razumio pitanje. Naša odluka: …") i Q53.1 ("Za PoC racunaj i manje 20-ak") uzeti su doslovno; P44 je izjava zadana kao naša odluka, a ne naručiteljev odgovor.
