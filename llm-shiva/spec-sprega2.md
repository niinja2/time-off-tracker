# Mjesta gdje bi dva developera napravila različitu aplikaciju

Izvori: `llm-shiva/spec-izjave.md`, `llm-shiva/spec-izjave-sonnet.md`. Citate sam provjerio u `docs/zajednicko/specifikacija.md`, `brief.md` i `atomarna-pitanja.md`.
Preskočeno: vikendi, praznici, istovremeni zahtjevi, prijenos dana, proširenja (specifikacija, odjeljak 9).
Oznake: **R** = pravilo iz briefa, **Q** = odgovor naručitelja, **D** = naša odluka. Uz svaku oznaku piše njezin tekst.

Poredak: od onoga što najviše mijenja aplikaciju prema manjem.

---

## 1. Voditelj i admin "vide saldo" članova, ali nijedan ekran to ne pokazuje

**Što:** Obećano je da voditelj vidi saldo svog tima, a admin svih. Nigdje ne piše na kojem ekranu ni kojim API pozivom.

**Izjave:**
- Q16: "Vidi li voditelj saldo članova svog tima?" → "Da".
- Q17: "Vidi li admin saldo svih korisnika?" → "Da".
- Spec 7: voditelj vidi "Saldo članova timova koje vodi"; admin "Sve zahtjeve i sve salde u tvrtki".
- Spec 12 (ekrani): My time off ima samo "saldo godišnjeg" (moj). Approvals i Team calendar ne spominju saldo.
- R12: "For the current year, show each user how many days they have used (approved), pending, and remaining." (svaki korisnik vidi svoj).

**Tumačenja:**
- A) Samo API: postoji endpoint saldo-za-korisnika s provjerom prava, ekrana nema.
- B) I ekran: npr. uz svaki zahtjev na Approvals prikazati saldo podnositelja, ili novi popis.

**Pitanje vlasniku:** Treba li voditelj/admin saldo tuđih vidjeti na ekranu? Ako da, gdje (uz zahtjev na Approvals, na kalendaru, nešto treće)?

---

## 2. Voditelj i admin "vide sve zahtjeve sa svim detaljima", ali ekrani to ne pokrivaju

**Što:** Voditelj smije vidjeti svaki zahtjev tima u svim stanjima, s tipom i razlogom. Ekrani pokazuju samo čekanje i odobreno otkazivo, a kalendar ne pokazuje razlog ni odbijene/otkazane.

**Izjave:**
- R (Visibility, brief): voditelj vidi "Every request from their team, including type, reason and pending requests."
- Spec 7: voditelj vidi "Sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom"; admin "Sve zahtjeve ... u tvrtki".
- Q48: "Prikazuje li kalendar voditelju i adminu odbijene i otkazane zahtjeve?" → "Ne, inače je pretrpano".
- Spec 12: Approvals = zahtjevi na čekanju + odobreni nepočeli. Kalendar = "tko je odsutan", bez razloga.

**Tumačenja:**
- A) Samo API (lista zahtjeva s filtrima); ekrana nema.
- B) Treba i ekran s popisom svih zahtjeva tima (sva stanja, tip, razlog).

**Pitanje vlasniku:** Gdje na ekranu voditelj vidi odbijeni zahtjev svog člana ili razlog odsustva? Ako nigdje, je li to namjerno (samo API)?

---

## 3. Može li odobrenje imati komentar?

**Što:** Brief kaže da je komentar uz odobrenje neobavezan, a spec ga kao polje navodi "samo kod odbijanja".

**Izjave:**
- R5: "A rejection needs a comment. An approval comment is optional."
- Spec 2 (polja): "Komentar odluke — samo kod odbijanja".
- Spec 3: "Odbijanje traži komentar, odobrenje ne."
- Q45: "Tko vidi komentar odobrenja?" → "Da (slaže se)" (pretpostavka uz pitanje: vlasnik i oni koji smiju odobravati).
- Spec 12: Approvals ima "odobravanje i odbijanje", komentar se ne spominje.

**Tumačenja:**
- A) Polje postoji uvijek; obavezno je samo kod odbijanja, kod odobrenja se može upisati.
- B) Polje postoji samo kod odbijanja; odobrenje komentar ne može imati.

**Pitanje vlasniku:** Smije li voditelj uz odobrenje upisati komentar (i vidi li ga vlasnik)? Ako da, ima li ga forma na Approvals?

---

## 4. Prijava: popis korisnika ili forma, i što je "korisničko ime"

**Što:** Dvije izjave kažu suprotno za ekran prijave. Uz to nigdje ne piše kako izgleda korisničko ime.

**Izjave:**
- Brief (Users and roles): "A login form with seeded passwords works, and so does a "log in as" picker."
- Pretpostavka uz Q49.1 ("Traži li prijava lozinku?"): "Da, seedane lozinke; na ekranu prijave popis korisnika za brzi odabir"; odgovor: "Da".
- D10: "Ekran prijave je forma: korisnik upisuje korisničko ime i lozinku, bez popisa korisnika"; "korisnička imena su u README-u".
- Spec 8, seed: "korisnici, timovi, lozinke i datumi zaposlenja", lozinka `password` (D16). Korisničko ime nije ni u seedu ni u briefu (tamo su samo imena Ana, Ivan...).

**Tumačenja:**
- A) Forma bez popisa (D10); ime je npr. `ana`, `ana@firma.hr` ili "Ana".
- B) Forma s popisom za brzi odabir (pretpostavka uz Q49.1).

**Pitanje vlasniku:** Jesu li Q49.1 "Da" i D10 pomireni, tj. bez popisa? I kako se točno upisuje korisničko ime (npr. `ana`, mala slova)?

---

## 5. Isti slučaj, više mogućih HTTP kodova (400 / 403 / 404 / 409)

**Što:** Spec daje kodove po vrsti greške, ali nekoliko slučajeva stane u dvije vrste. Testovi i frontend ovise o kodu.

**Izjave (spec 8):**
- 400: "neispravan unos (npr. nedostaje polje, završni datum prije početnog)".
- 403: "korisnik nema pravo na radnju".
- 404: "zahtjev ne postoji".
- 409: "kršenje pravila (preklapanje datuma, premašen saldo, krivo stanje zahtjeva)".
- Slučajevi koje to ne rješava:
  - Vlasnik otkazuje odobren zahtjev koji je počeo (spec 4: "Ne"): 403 (nema pravo) ili 409 (krivo stanje)?
  - Zahtjev prije datuma zaposlenja (D4: "Zahtjev ne počinje prije datuma zaposlenja"): 400 ili 409?
  - Odbijanje bez komentara (R5): 400 (nedostaje polje) ili 409 (kršenje pravila)?
  - Zaposlenik traži tuđi zahtjev kojeg ne smije vidjeti (R: "The API must enforce these rules"): 403 ili 404? (Q50 pretpostavka u dokumentu: "400, 403, 404, 409; `application/problem+json`")

**Tumačenja:** A) pravo/uloga → 403, stanje i pravila → 409, samo sintaksa → 400. B) sve što ovisi o stanju zahtjeva → 409, sve što ovisi o korisniku → 403, što god ne smije vidjeti → 404.

**Pitanje vlasniku:** Treba li fiksna tablica "situacija → kod" za ova četiri slučaja? (Naručitelj je rekao "obrada grešaka po volji", Q50.)

---

## 6. Što točno kalendar prikazuje (oblik, oznake, tip za admina)

**Što:** Kalendar miješa čekanje i odobreno, a tablica ne kaže kako se razlikuju, a za admina ne kaže prikazuje li tip.

**Izjave:**
- Spec 7 (kalendar): voditelj vidi tuđe "Na čekanju i odobrene, članova timova koje vodi, s tipom"; admin "Na čekanju i odobrene, svih u tvrtki" (bez "s tipom"); zaposlenik svoje "Na čekanju i odobrene".
- Brief (Screens): "Who is away during a selected month ... A list grouped by day is fine."
- Spec 12: "popis po danima".
- Brief (Visibility, admin): "Every request in the company."
- Q47: bolovanje kolege vidi se "kao 'odsutan' bez tipa".

**Tumačenja:**
- A) Svaki redak ima oznaku stanja (Pending/Approved); admin vidi i tip i razlog.
- B) Bez oznake stanja; admin vidi tip kao voditelj, razlog ne.

**Pitanje vlasniku:** Mora li redak na kalendaru pokazivati je li zahtjev na čekanju ili odobren? Vidi li admin na kalendaru tip (i razlog)?

---

## 7. Zaposlenik u više timova: što kalendar pokazuje kad se timovi preklapaju

**Što:** Ista osoba može biti u timu koji vodim i u timu gdje sam običan član. Nije rečeno koji pogled vrijedi.

**Izjave:**
- Spec 7: voditelj vidi "Za ostale timove u kojima je član ... isto što i zaposlenik"; "Zaposlenik u više timova vidi se na kalendaru svakog svog tima."
- D12: "Voditelj s više timova ima jedan kalendar s filtrom po timu."
- Spec 1: "Zaposlenik može biti u nijednom, jednom ili više timova."

**Tumačenja:**
- A) Jedan kalendar, jedan unos po osobi; najviše pravo pobjeđuje (osoba u mom timu → s tipom).
- B) Unos po timu: ista osoba je s tipom pod timom A, a bez tipa pod timom B; bez filtra se vidi dvaput.

Za običnog zaposlenika u više timova nije rečeno ni ima li filtar po timu (D12 kaže samo za voditelja).

**Pitanje vlasniku:** Je li jedan unos po osobi (viši pogled pobjeđuje)? Ima li i običan zaposlenik u više timova filtar po timu?

---

## 8. Je li "voditelj" spremljena uloga ili izvedena iz toga što vodi tim

**Što:** Tko vidi ekran Approvals i tko je "voditelj" ovisi o tome je li uloga polje u bazi ili posljedica vođenja tima.

**Izjave:**
- Spec 1: "Korisnik može imati više uloga, npr. admin koji je i voditelj"; "Tim ima točno jednog voditelja" (Q6: "Može li tim imati više voditelja?" → "Ne"; Q7: "Može li tim biti bez voditelja?" → "Ne").
- Brief (Screens): Approvals je "For team leads and admins only".
- Brief (Users): tablica daje "Role" i "Team" kao odvojene stupce (Ana: Team lead, Platform).

**Tumačenja:**
- A) Uloga je spremljena (lista uloga po korisniku); voditelj bez tima je moguć.
- B) Voditelj = netko tko je voditelj barem jednog tima; uloga se izvodi.

**Pitanje vlasniku:** Je li uloga zasebni podatak u seedu ili se izvodi iz vođenja timova?

---

## 9. Razlog otkazivanja: "može" (Q37) ili "obavezan" (D3)

**Što:** Naručitelj je rekao "može", mi smo to pretvorili u "obavezno" za tuđe otkazivanje. Odluka je naša i još nema vlasnikovog potvrđenog "da".

**Izjave:**
- Q37: "Treba li razlog za otkazivanje zahtjeva?" → "Moze, nema bude field".
- D3: "Tko otkazuje tuđi zahtjev (voditelj, admin) mora upisati razlog".
- Spec 4: vlasnik "razlog opcionalan"; tko smije odobriti "razlog obavezan".
- Spec 7: zaposlenik vidi "komentar odluke, tko je odlučio i tko je otkazao"; razlog otkazivanja nije nabrojen.

**Tumačenja:**
- A) Razlog obavezan za tuđe otkazivanje i vlasnik ga vidi uz zahtjev.
- B) Razlog je samo opcionalno polje svugdje (doslovno Q37); ili je obavezan, ali ga vlasnik ne vidi.

**Pitanje vlasniku:** Potvrđuješ li "obavezan razlog za tuđe otkazivanje"? I vidi li vlasnik taj razlog?

---

## 10. "Odsutnost korisnika bez tima vide samo admini" i vlastita odsutnost

**Što:** Mala proturječnost u formulaciji: korisnik bez tima ne bi smio vidjeti ni sebe.

**Izjave:**
- Spec 7: "Odsutnost korisnika bez tima vide samo admini."
- Spec 7 (tablica): zaposlenik vidi "Svoje zahtjeve ... Na čekanju i odobrene".

**Tumačenja:** A) Vlasnik vidi svoje (tablica "svoje") i admini sve. B) Doslovno samo admini; zaposlenik bez tima na kalendaru ne vidi ništa, ni sebe.

**Pitanje vlasniku:** Vidi li zaposlenik bez tima svoje zahtjeve na kalendaru?

---

## Kako smo riješili (4. 11., privremeni format)

Statusi: **RIJEŠENO U SPECIFIKACIJI** (promijenili smo specifikaciju), **RIJEŠENO NIŽE** (specifikacija šuti, ali sučelje ili kod su dobro odlučili; prihvaćeno), **ODLUKA POSTOJI** (odluka D već postoji, samo nije povezana s izjavom koju mijenja), **OTVORENO · za Rajka** (čeka odluku vlasnika), **NIJE PROBLEM**.

| # | Nalaz | Status | Gdje / zašto |
| --- | --- | --- | --- |
| 1 | Saldo članova bez ekrana | RIJEŠENO U SPECIFIKACIJI | D18 (novi ekran Team requests, s tablicom salda), L28. Ekran još nije napravljen. |
| 2 | Svi zahtjevi tima bez ekrana | RIJEŠENO U SPECIFIKACIJI | D18 (Team requests: sva stanja, razlog, komentar, tko je odlučio ili otkazao, filtar po timu), L28. Ne sudara se s Q48, jer je to popis, a ne kalendar. Ekran još nije napravljen. |
| 3 | Komentar uz odobrenje | RIJEŠENO U SPECIFIKACIJI | Red u tablici polja (odjeljak 2) bio je krivo izveden iz R5: sada "obavezan kod odbijanja, neobavezan kod odobrenja". Kod je već radio po R5. |
| 4 | Popis korisnika na prijavi | ODLUKA POSTOJI | D10: forma bez popisa. D10 ne kaže da mijenja našu pretpostavku uz Q49.1 ("popis korisnika"), na koju je naručitelj rekao "Da". |
| 4b | Kako izgleda korisničko ime | OTVORENO · za Rajka | `ana`, mala slova, razlikuje velika i mala; piše samo u README-u, nije odlučeno ni testirano. |
| 5 | HTTP statusi za granične slučajeve | RIJEŠENO NIŽE | Sučelje, tablice grešaka: otkazivanje započetog 409, prije datuma zaposlenja 409, odbijanje bez komentara 400, tuđi zahtjev 403. Pravilo: 400 unos sam po sebi kriv, 403 nema prava, 409 sudar s podacima ili stanjem. |
| 6 | Kalendar: oznaka stanja, tip za admina, razlog | RIJEŠENO NIŽE + D18 | Stanje i tip se vide (puni pogled), razlog na kalendaru ne vidi nitko, a vidi se na Team requests (D18). U specifikaciji 7 kod admina fali "s tipom". |
| 7 | Osoba u dva tima na kalendaru | OTVORENO · za Rajka | Sučelje i kod: jedan unos po osobi, jače pravo pobjeđuje (redoslijed pravila u `VisibilityPolicy`). Lokalna odluka developera; vlasnik je ostavlja otvorenom. |
| 8 | Voditelj: spremljena uloga ili izvedena | RIJEŠENO NIŽE | Sučelje T1: voditelj je tko vodi barem jedan tim (`lead_id` na timu), admin je polje `admin`. Voditelj bez tima ne postoji, u skladu s Q7. |
| 9 | Razlog otkazivanja: "može" ili "obavezan" | ODLUKA POSTOJI | R5 je glavni, Q37 ("Moze, nema bude field") je dvosmislen, D3 ih pomiruje: obavezan kad otkazuje voditelj ili admin, neobavezan za vlasnika. |
| 9b | Vlasnik ne vidi razlog otkazivanja kad postoji i komentar odobrenja | OTVORENO · za Rajka | My time off, stupac Comment: komentar odluke, inače razlog otkazivanja (sučelje T8). Prijedlog: prikazati oba. Specifikacija 7 ne nabraja razlog otkazivanja među onim što zaposlenik vidi. |
| 10 | Korisnik bez tima i vlastita odsutnost | RIJEŠENO NIŽE | Prvo pravilo vidljivosti je "ja sam", pa vidi sebe. Rečenica u specifikaciji 7 trebala bi biti "vide samo on i admini". |
