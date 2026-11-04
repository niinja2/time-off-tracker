# UI i uloge: tko što može kroz frontend

Commit: eb785ba. Datum: 2026-11-01.
Necommitane promjene (git status --short): M README.md, M docs/za-procjenu.md, M docs/zajednicko/specifikacija.md, M tester/e2e-izvjestaj.md, ?? .claude/, ?? docs/diagrams.md, ?? integrator_report.md, ?? llm-shiva/, ?? shiva_report.md.
Čitano: brief.md, atomarna-pitanja.md, specifikacija.md, sucelje.md (retci 290-878), frontend/src (sve komponente i lib), README (odabrani dijelovi), coding-log (L28, L39-L41). Backend kod nisam čitao, pa što API vraća uzimam iz sucelja.md.

## Tablica uloga i radnji

Oznake: DA = moguće kroz UI i rezultat se vidi; NE = samo API; n/a = pravilo to ne dopušta.

### Zaposlenik

| Radnja (pravilo) | UI | Gdje / dokaz |
| --- | --- | --- |
| Podnijeti zahtjev, sva tri tipa (brief, Q9) | DA | MyTimeOffScreen.tsx:204-254, forma "New request" |
| Vidjeti greške backenda (preklapanje, saldo) (brief) | DA | MyTimeOffScreen.tsx:201 ErrorAlert, :93 handleError |
| Otkazati svoj zahtjev na čekanju (R6) | DA | MyTimeOffScreen.tsx:36-38, :125-157 |
| Otkazati svoj odobreni zahtjev koji nije počeo (R7) | DA | isto; gumb se prikazuje i za započeti, backend vraća 409 (L27, PRIHVAĆENO) |
| Vidjeti svoje zahtjeve sa svim detaljima (brief, Q44-Q46) | DJELOMIČNO | tip, datumi, razlog, stanje, tko je odlučio/otkazao, komentar su prikazani; razlog otkazivanja nije uvijek (V1); vremena nisu (S1) |
| Vidjeti svoj saldo (R12) | DA | MyTimeOffScreen.tsx:40-62 (Entitlement, Used, Pending, Remaining) |
| Kalendar: svoje na čekanju i odobrene, tuđe samo odobrene bez tipa (Q41, Q47) | DA | CalendarScreen.tsx:31-39 (bez type/status samo ime); filtriranje je u API-ju |
| Odobravati, odbijati | n/a | link Approvals skriven (AppShell.tsx:175,184) |

### Voditelj tima

| Radnja (pravilo) | UI | Gdje / dokaz |
| --- | --- | --- |
| Sve što zaposlenik (Q3: voditelj je i zaposlenik) | DA | iste rute, AppShell.tsx:181-191 |
| Odobriti/odbiti zahtjev člana tima (R1) | DA | ApprovalsScreen.tsx:380-393 |
| Odbijanje traži komentar (R5) | DA | UI ne provjerava, backend 400 se prikaže (ApprovalsScreen.tsx:327) |
| Vlastiti zahtjev ide adminu, ne odlučuje ga (R3, D2) | DA | popis `GET /api/approvals` ga ne sadrži (sucelje.md:373) |
| Otkazati tuđi odobreni zahtjev koji nije počeo, s razlogom (Q30, Q32, D3, D17) | DA | ApprovalsScreen.tsx:404-457, tablica "Approved requests" |
| Otkazati tuđi zahtjev NA ČEKANJU (Q30, D14, spec odjeljak 4) | NE | PRIHVAĆENO, D17: "Otkazivanje tuđeg zahtjeva na čekanju kroz UI još nije napravljeno (samo API)" |
| Vidjeti sve zahtjeve članova, sva stanja, s tipom i razlogom (brief, spec odjeljak 7) | NE / DJELOMIČNO | vidi V2 |
| Vidjeti saldo članova (Q16) | NE | PRIHVAĆENO: L28 i README "Extensions" |
| Kalendar svog tima s tipom i stanjem, filtar po timu (Q42, D12) | DA | CalendarScreen.tsx:114-128 (filtar), :36-38 (tip i stanje); koga vidi odlučuje API |
| Vlastiti zahtjev: podnijeti, otkazati, saldo | DA | isto kao zaposlenik |

### Admin

| Radnja (pravilo) | UI | Gdje / dokaz |
| --- | --- | --- |
| Sve što zaposlenik (Q1: admin je zaposlenik) | DA | iste rute |
| Odobriti/odbiti bilo koji zahtjev (R2) | DA | ApprovalsScreen.tsx, popis iz `GET /api/approvals` |
| Odobriti vlastiti zahtjev (Q1.1, D15) | DA | sucelje.md:373 "Eva vidi sve na čekanju, i svoje"; UI ništa ne filtrira |
| Odbiti vlastiti zahtjev | NEPROVJERLJIV | sucelje.md:316 red 1 kaže da admin smije "bilo koji zahtjev, i vlastiti"; spec redak 58 dopušta iznimku samo za odobravanje (Q1.1), R3 inače zabranjuje odbijanje vlastitog. Backend nisam čitao, a UI gumb Reject postoji u svakom retku |
| Otkazati tuđi odobreni, nije počeo, s razlogom (Q31, Q32) | DA | tablica "Approved requests" |
| Otkazati tuđi na čekanju (Q31) | NE | PRIHVAĆENO, D17 |
| Vidjeti sve zahtjeve u tvrtki (brief, Q17) | NE / DJELOMIČNO | vidi V2 |
| Vidjeti saldo svih (Q17) | NE | PRIHVAĆENO: L28, README |
| Kalendar svih timova s filtrom, uključujući korisnike bez tima (Q43) | DA | CalendarScreen.tsx:121 "All teams" kao zadano, :122-126 timovi iz `GET /api/teams` |
| Ispravak datuma zaposlenja, već započetog zahtjeva | NE | PRIHVAĆENO, D6 (SQL, README) |
| Zahtjev zaposlenika bez tima odobrava admin (Q4) | DA | popis admina sadrži sve na čekanju (sucelje.md:373) |

Usporedba sa specifikacijom odjeljak 12 i sucelje.md: tri ekrana i tablica "Approved requests" (D17) odgovaraju kodu. Rute, nazivi stupaca, tekstovi praznih stanja i oznake iz sucelje.md (retci 580-736 i 843-864) slažu se s kodom koji sam pročitao.

## BLOKIRA

Nema nalaza.

## VAŽNO

**V1. Vlasnik ne vidi razlog otkazivanja ako zahtjev ima komentar odluke.**
Što: stupac Comment prikazuje `decisionComment`, a `cancelReason` samo kad komentara odluke nema. Zahtjev koji je Ana odobrila s "ok", a Eva otkazala s razlogom (razlog je obavezan, D3), vlasniku pokazuje samo "ok". Specifikacija odjeljak 7 i brief traže da zaposlenik vidi svoje zahtjeve "sa svim detaljima".
Dokaz: frontend/src/components/MyTimeOffScreen.tsx:33: `return request.decisionComment ?? request.cancelReason ?? "";`. Sucelje.md:680: "`decisionComment`, inače `cancelReason`, inače prazno". Spec redak 107: "Svoje zahtjeve sa svim detaljima, uključujući komentar odluke, tko je odlučio i tko je otkazao".
Kome: specifikacija/sučelje (orkestrator odlučuje je li namjerno), zatim developer.

**V2. Voditelj i admin kroz UI ne mogu vidjeti zahtjeve koje spec odjeljak 7 kaže da vide (sva stanja, razlog).**
Što: pregled "svi zahtjevi, sva stanja, tip i razlog" postoji samo kao `GET /api/requests`. Approvals pokazuje samo zahtjeve na čekanju i odobrene koji nisu počeli (bez razloga). Odbijeni, otkazani i već započeti odobreni zahtjevi članova nigdje u UI-ju nisu vidljivi; kalendar pokazuje samo PENDING i APPROVED, bez razloga. Specifikacija odjeljak 12 ne navodi ekran za to, a README to ne navodi kao ograničenje ni proširenje (grep README: ne nalazi; `GET /api/requests` je samo u tablici API-ja, README.md:162).
Dokaz: sucelje.md:459: "nema; API za pravila vidljivosti". Spec redak 108: "Sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom". Brief retci 100-103 (Team lead: "Every request from their team, including type, reason and pending requests").
Kome: specifikacija (ili izričita odluka D) i README.

## SITNO

**S1. Vremena (stvaranje, odluka, otkazivanje) se ne prikazuju nigdje.**
Dokaz: spec redak 44 "Vrijeme stvaranja, vrijeme odluke ili otkazivanja | da" i D13; frontend/src/lib/types.ts ima `createdAt`, `decidedAt`, `cancelledAt`, ali MyTimeOffScreen.tsx:169-190 ih ne prikazuje. Kome: specifikacija (reći je li prikaz potreban) ili developer.

**S2. Tekući mjesec kalendara računa se u vremenu preglednika, ne servera (Europe/Zagreb).**
Dokaz: frontend/src/lib/month.ts:63-67 `new Date()`, `getFullYear()`, `getMonth()`; spec D8 i Q24 govore o vremenskoj zoni servera. Sucelje.md:720 to izričito propisuje ("u lokalnom vremenu preglednika"). Kome: specifikacija/sučelje.

**S3. Ruta /approvals nije zatvorena za zaposlenike, skriven je samo link.**
Spec redak 199 "samo za voditelje i admine"; sucelje.md:589 opisuje da ručno otvorena ruta pokazuje praznu listu. Podaci nisu otkriveni (API vraća prazno), pa je u skladu sa spec 7. PRIHVAĆENO u sučelju, navedeno radi potpunosti. Kome: nikome.

**S4. Sučelje T8 (Approvals) je zastarjelo u odnosu na T8.1.**
Dokaz: sucelje.md:700 "Pri otvaranju zove `getApprovals()`", :715 "Uspjeh: ponovo `getApprovals()`"; kod ApprovalsScreen.tsx:287 zove oba popisa. Sucelje.md:845 i :863 ispravljaju to, ali T8 odjeljak nije izmijenjen. Kome: log/orkestrator (sučelje).

**S5. README ne spominje da otkazivanje tuđeg zahtjeva na čekanju nije u UI-ju (D17), a "Extensions" navodi L28 s internom oznakom.**
Dokaz: README.md:225 "L28: show the requester's balance on the Approvals screen ..."; D17 (spec redak 179) jedini je zapis o rupi; grep README nije našao to ograničenje. Kome: README.

## PRIHVAĆENO (svjesno izostavljeno ili odlučeno)

- P1. Saldo drugih (Q16, Q17) nije u UI-ju: L28 (coding-log:389-396) i README.md:225. API postoji (`GET /api/users/{id}/balance`). Q16 i Q17 su odgovori naručitelja, ali ne spominju ekran.
- P2. Otkazivanje tuđeg zahtjeva na čekanju samo kroz API: spec D17 (redak 179). Odbijanje s komentarom pokriva isti cilj, ali daje stanje Rejected, ne Cancelled.
- P3. Gumb Cancel je prikazan i za započete odobrene zahtjeve, backend vraća 409: sucelje.md:696 (L27).
- P4. Login je forma bez popisa korisnika: D10; pretpostavka uz Q49.1 o popisu je nadjačana.
- P5. Prijenos dana, blagdani, zahtjev preko granice godine: README.md:94-95 i spec odjeljak 9. U UI-ju nema traga, što je dosljedno.

## Provjereno i u redu

- Navigacija: Approvals link samo za `teamLead || admin` (AppShell.tsx:175), nazivi odgovaraju sucelju.md:636-642.
- Forma, saldo i tablica My requests slijede sucelje.md:644-696 (stupci, tekst statusa "Approved by X", "Cancelled by X").
- Approvals: stupci i gumbi odgovaraju sucelju.md:702-716 i 847-864; oba popisa se ponovo učitavaju nakon approve/reject/cancel (ApprovalsScreen.tsx:336-338).
- Frontend ne provjerava komentar niti razlog (R5, D3), prikazuje `detail` backenda (api.ts:49-58, ErrorAlert.tsx).
- 401 vodi na /login bez poruke (useApiError.ts:14-17).
- Kalendar: grupiranje po danima, vikendi se ne prikazuju (month.ts:76-84), FULL/LIMITED unosi (CalendarScreen.tsx:31-39), filtar "All teams" kao zadano (D12, Q43).
- Svi tekstovi u UI-ju na engleskom (Q54).

## Nisam stigao / nisam mogao provjeriti

- Backend kod i testovi: ne znam stvarno ponašanje API-ja (vidljivost kalendara za voditelja u tuđem timu, može li admin odbiti vlastiti zahtjev); oslonio sam se na sucelje.md.
- Radi li UI stvarno (nisam pokretao aplikaciju, po zadatku).
- Frontend testovi (frontend/tests) nisu čitani.
- Dijelovi sucelja.md retci 1-289 (T1-T4) i README izvan navedenih redaka.
