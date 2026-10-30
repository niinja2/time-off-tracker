# Ručni E2E scenariji

Izvori: [specifikacija](../docs/zajednicko/specifikacija.md), [sučelje T8 i T8.1](../docs/zajednicko/sucelje.md#t8-frontend).
Aplikacija: http://localhost:3000.

Ovo su korisnički scenariji (happy path), ne pokrivenost po metodologiji: glavni tokovi i par najčešćih grešaka (80/20). Koraci idu kaskadno, svaki ovisi o prethodnima. **Svi koraci moraju proći.**

## Preduvjeti

1. **Čista baza: samo seed, bez ijednog zahtjeva.** Npr. `docker compose down -v`, pa `docker compose up`.
2. **Izvesti do 8. 12. 2026.** Aplikacija radi sa stvarnim satom. Odobreni zahtjev smije se otkazati samo dok nije počeo (R7: odobreni zahtjev koji nije počeo), a prvi takav korak (A9) otkazuje zahtjev od 9. 12. Saldo se računa za tekuću godinu, pa scenariji vrijede samo u 2026.
3. **Lanci A–D izvode se redom, na istoj bazi.** D gleda podatke iz A–C.
4. **Lanac E izvodi se zasebno, na novoj čistoj bazi** (ponovi preduvjet 1).

## Upute izvršitelju

1. Ne preskači nijedan korak. Ako korak ne možeš izvesti (nema gumba, polja i sl.), zapiši PALO i nastavi.
2. Ništa ne popravljaj: ne mijenjaj kod, bazu ni postavke, ne ponavljaj korak s drugim vrijednostima osim ako to piše u koraku.
3. Ispod svakog "Očekujem" popuni `Rezultat:` s **PROŠLO** ili **PALO** i **doslovno prepiši tekst koji vidiš**. Kod PALO napiši i što si očekivao.
4. Svi korisnici imaju lozinku `password`. Korisnička imena: `ana`, `ivan`, `marko`, `petra`, `luka`, `sara`, `tom`, `eva`.
5. **Prijava:** otvori http://localhost:3000/login, `Username` = korisničko ime, `Password` = `password`, klikni `Sign in`. Aplikacija otvara `/my-time-off`, a u navigaciji piše `Signed in as <Ime>`.
6. **Odjava:** klikni `Log out`. Prije svake nove prijave odjavi prethodnog korisnika.
7. **Novi zahtjev:** na `My time off`, u formi `New request` postavi `Type`, `Start date`, `End date`, `Reason` (prazno ako nije navedeno) i klikni `Submit request`.
8. **Datumi** su ovdje `yyyy-MM-dd`. Ako tipkaš u datumsko polje, upiši dan, mjesec i godinu redom kojim ih polje prikazuje. Tablice prikazuju datume kao `yyyy-MM-dd`.
9. **Tablice:** "redak s ..." znači redak u kojem su sve navedene vrijednosti. Na ekranu `Approvals` su dvije tablice: `Pending requests` (polje `Comment`, gumbi `Approve`, `Reject`) i `Approved requests` (polje `Cancel reason`, gumb `Cancel`). Polja i gumbi pripadaju svom retku: koristi ih samo u retku koji korak navodi. Redak biraj po `Employee` i `Start date`.
10. **Otkazivanje svog zahtjeva** (`My time off`): u retku klikni `Cancel`, pa `Confirm cancel`.
11. **Kalendar:** otvori `Team calendar`; klikći `Next month` ili `Previous month` dok naslov mjeseca nije traženi (npr. `2026-11`). Svaki radni dan s unosom ima svoj odjeljak s naslovom datuma (npr. `2026-11-02`). Subote i nedjelje se ne prikazuju (R10).
12. Saldo je odjeljak `Vacation balance 2026` na `My time off`: `Entitlement`, `Used`, `Pending`, `Remaining`.

### Očekivanja: MORA i NEMA

Ispod svakog "Očekujem" svaka stavka je u svom retku:

- `MORA: \`tekst\`` znači da se taj tekst **mora** vidjeti na ekranu.
- `NEMA: \`tekst\`` znači da se taj tekst **ne smije** vidjeti na ekranu.

Tekst u backtickovima je doslovno onaj s ekrana. Iza njega, u zagradi, piše gdje se gleda:

| Oznaka | Gdje se gleda |
| --- | --- |
| `(poruka)` | poruka greške (element `role="alert"`) |
| `(navigacija)` | navigacija na vrhu |
| `(stranica)` | naslov ili tekst stranice |
| `(adresa)` | adresa u pregledniku |
| `(saldo, X)` | vrijednost uz `X` u `Vacation balance 2026` |
| `(forma, X)` | vrijednost polja `X` u formi `New request` |
| `(My requests, D, X)` | tablica `My requests`, redak sa `Start date` = `D`, stupac `X` (`Type`, `Start date`, `End date`, `Reason`, `Status`, `Comment`, `Actions`) |
| `(My requests)` | bilo gdje u tablici `My requests` |
| `(Pending requests)`, `(Approved requests)` | bilo gdje u toj tablici na ekranu `Approvals`; ako tablice nema, NEMA je zadovoljen |
| `(Pending requests, I D, X)`, `(Approved requests, I D, X)` | ta tablica, redak s `Employee` = `I` i `Start date` = `D`, stupac `X` |
| `(Pending requests, redak N, Employee)` | ta tablica, N-ti redak (bez zaglavlja), stupac `Employee` |
| `(dan D)` | odjeljak kalendara s naslovom `D`; kod NEMA za sam datum: odjeljak s tim naslovom ne postoji |
| `(kalendar)` | bilo gdje u popisu po danima |
| `(Team)` | odabrana vrijednost polja `Team` |

Ostale riječi uz "Očekujem" (oznake pravila, objašnjenja) samo objašnjavaju i ne provjeravaju se.

Oznake pravila: R = pravilo iz briefa, Q = odgovor naručitelja, D = naša odluka, L = odluka iz coding-loga.

---

## Lanac A: Platform (Ivan, Ana, Marko, Petra)

**A1. Prijava i saldo**

1. Na `/login` upiši `ivan` / `wrong`, klikni `Sign in`.

Očekujem (D10: prijava je forma s korisničkim imenom i lozinkom):
- MORA: `Invalid username or password` (poruka)
- MORA: `/login` (adresa)
- NEMA: `Signed in as Ivan` (navigacija)

Rezultat:

2. Prijavi se kao `ivan`.

Očekujem (R11: 20 dana godišnje):
- MORA: `Signed in as Ivan` (navigacija)
- MORA: `20` (saldo, Entitlement)
- MORA: `0` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `20` (saldo, Remaining)
- MORA: `No requests yet.` (stranica)

Rezultat:

**A2. Ivan traži godišnji**

1. Novi zahtjev: `Vacation`, `2026-11-02` do `2026-11-06`, `Reason` `Trip`.

Očekujem (R13: zahtjevi na čekanju troše saldo; R10: pon–pet = 5):
- NEMA: `Request overlaps an existing pending or approved request` (poruka)
- MORA: `Vacation` (My requests, 2026-11-02, Type)
- MORA: `2026-11-06` (My requests, 2026-11-02, End date)
- MORA: `Trip` (My requests, 2026-11-02, Reason)
- MORA: `Pending` (My requests, 2026-11-02, Status)
- MORA: `Cancel` (My requests, 2026-11-02, Actions)
- MORA: `5` (saldo, Pending)
- MORA: `15` (saldo, Remaining)

Rezultat:

**A3. Ana odobrava s komentarom**

1. Odjavi se, prijavi kao `ana`, otvori `Approvals`.

Očekujem (R1: voditelj odobrava zahtjeve svog tima):
- MORA: `Vacation` (Pending requests, Ivan 2026-11-02, Type)
- MORA: `2026-11-06` (Pending requests, Ivan 2026-11-02, End date)
- MORA: `Trip` (Pending requests, Ivan 2026-11-02, Reason)

Rezultat:

2. U retku Ivana `Comment` = `Enjoy`, klikni `Approve`.

Očekujem:
- NEMA: `Ivan` (Pending requests)
- MORA: `No requests waiting for approval.` (stranica)

Rezultat:

3. Odjavi se, prijavi kao `ivan`.

Očekujem (Q46, L32: zaposlenik vidi tko je odlučio):
- MORA: `Approved by Ana` (My requests, 2026-11-02, Status)
- MORA: `Enjoy` (My requests, 2026-11-02, Comment)
- MORA: `5` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `15` (saldo, Remaining)

Rezultat:

**A4. Bolovanje ne troši saldo**

1. Kao Ivan, novi zahtjev: `Sick leave`, `2026-11-12` do `2026-11-12`.

Očekujem (R14: bolovanje i slobodni dani ne troše saldo):
- MORA: `Sick leave` (My requests, 2026-11-12, Type)
- MORA: `Pending` (My requests, 2026-11-12, Status)
- MORA: `5` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `15` (saldo, Remaining)

Rezultat:

2. Odjavi se, prijavi kao `ana`, otvori `Approvals`. U retku `Ivan` | `Sick leave` | `2026-11-12` klikni `Approve` (komentar prazan).

Očekujem (R5: odobrenje ne traži komentar):
- NEMA: `Ivan` (Pending requests)
- NEMA: `comment must not be blank` (poruka)

Rezultat:

3. Odjavi se, prijavi kao `ivan`.

Očekujem:
- MORA: `Approved by Ana` (My requests, 2026-11-12, Status)
- MORA: `5` (saldo, Used)
- MORA: `15` (saldo, Remaining)

Rezultat:

**A5. Preklapanje**

1. Kao Ivan, novi zahtjev: `Vacation`, `2026-11-02` do `2026-11-06`.

Očekujem (R9: nema preklapanja sa zahtjevom na čekanju ili odobrenim):
- MORA: `Request overlaps an existing pending or approved request` (poruka)
- MORA: `2026-11-02` (forma, Start date)
- MORA: `2026-11-06` (forma, End date)
- MORA: `Approved by Ana` (My requests, 2026-11-02, Status)
- NEMA: `Pending` (My requests, 2026-11-02, Status)

Rezultat:

2. Odjavi se.

**A6. Odbijanje bez komentara, pa s komentarom**

1. Prijavi se kao `marko`. Novi zahtjev: `Vacation`, `2026-11-16` do `2026-11-17`.

Očekujem:
- MORA: `Pending` (My requests, 2026-11-16, Status)
- MORA: `2` (saldo, Pending)
- MORA: `18` (saldo, Remaining)

Rezultat:

2. Odjavi se, prijavi kao `ana`, otvori `Approvals`. U retku `Marko` ostavi `Comment` prazan, klikni `Reject`.

Očekujem (R5: odbijanje traži komentar):
- MORA: `comment must not be blank` (poruka)
- MORA: `Marko` (Pending requests, Marko 2026-11-16, Employee)

Rezultat:

3. U retku `Marko` `Comment` = `Team offsite`, klikni `Reject`.

Očekujem:
- NEMA: `Marko` (Pending requests)

Rezultat:

4. Odjavi se, prijavi kao `marko`.

Očekujem:
- MORA: `Rejected by Ana` (My requests, 2026-11-16, Status)
- MORA: `Team offsite` (My requests, 2026-11-16, Comment)
- NEMA: `Cancel` (My requests, 2026-11-16, Actions)
- MORA: `0` (saldo, Pending)
- MORA: `20` (saldo, Remaining)

Rezultat:

**A7. Isti datumi nakon odbijanja**

1. Kao Marko, novi zahtjev: `Vacation`, `2026-11-16` do `2026-11-17`.

Očekujem (R9: odbijeni zahtjev ne blokira datume). **Ovaj zahtjev ostaje na čekanju do kraja lanca D.**
- NEMA: `Request overlaps an existing pending or approved request` (poruka)
- MORA: `Pending` (My requests, 2026-11-16, Status)
- MORA: `Rejected by Ana` (My requests, 2026-11-16, Status)
- MORA: `2` (saldo, Pending)
- MORA: `18` (saldo, Remaining)

Napomena: dva retka imaju `Start date` `2026-11-16`; jedan je `Pending`, drugi `Rejected by Ana`.

Rezultat:

2. Odjavi se.

**A8. Petra otkazuje zahtjev na čekanju**

1. Prijavi se kao `petra`. Novi zahtjev: `Vacation`, `2026-11-23` do `2026-11-24`.

Očekujem:
- MORA: `Pending` (My requests, 2026-11-23, Status)
- MORA: `2` (saldo, Pending)
- MORA: `18` (saldo, Remaining)

Rezultat:

2. U tom retku klikni `Cancel`.

Očekujem:
- MORA: `Cancel reason` (My requests, 2026-11-23, Actions)
- MORA: `Confirm cancel` (My requests, 2026-11-23, Actions)
- MORA: `Keep request` (My requests, 2026-11-23, Actions)

Rezultat:

3. `Cancel reason` ostavi prazan, klikni `Confirm cancel`.

Očekujem (R6: vlasnik otkazuje zahtjev na čekanju, razlog nije obavezan):
- NEMA: `reason must not be blank` (poruka)
- MORA: `Cancelled by Petra` (My requests, 2026-11-23, Status)
- NEMA: `Cancel` (My requests, 2026-11-23, Actions)
- MORA: `0` (saldo, Pending)
- MORA: `20` (saldo, Remaining)

Rezultat:

4. Odjavi se.

**A9. Ivan otkazuje odobreni zahtjev prije početka**

1. Prijavi se kao `ivan`. Novi zahtjev: `Vacation`, `2026-12-09` do `2026-12-10`.

Očekujem:
- MORA: `Pending` (My requests, 2026-12-09, Status)
- MORA: `5` (saldo, Used)
- MORA: `2` (saldo, Pending)
- MORA: `13` (saldo, Remaining)

Rezultat:

2. Odjavi se, prijavi kao `ana`, otvori `Approvals`. U retku `Ivan` | `2026-12-09` klikni `Approve` (komentar prazan).

Očekujem:
- NEMA: `Ivan` (Pending requests)

Rezultat:

3. Odjavi se, prijavi kao `ivan`.

Očekujem:
- MORA: `Approved by Ana` (My requests, 2026-12-09, Status)
- MORA: `7` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `13` (saldo, Remaining)

Rezultat:

4. U retku `2026-12-09` klikni `Cancel`, pa `Confirm cancel` (razlog prazan).

Očekujem (R7: vlasnik otkazuje odobreni zahtjev koji nije počeo):
- NEMA: `Request has already started` (poruka)
- MORA: `Cancelled by Ivan` (My requests, 2026-12-09, Status)
- MORA: `5` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `15` (saldo, Remaining)

Rezultat:

5. Odjavi se.

---

## Lanac B: Mobile (Tom, Sara, Luka)

**B1. Tom ima 12 dana**

1. Prijavi se kao `tom`.

Očekujem (Q15, D5: u godini zaposlenja razmjerni dio, zaposlen 2026-06-01):
- MORA: `12` (saldo, Entitlement)
- MORA: `12` (saldo, Remaining)

Rezultat:

2. Novi zahtjev: `Vacation`, `2026-12-01` do `2026-12-17`, `Reason` `Holiday`.

Očekujem (R13: godišnji ne smije premašiti preostali saldo):
- MORA: `Not enough vacation days: requested 13, remaining 12` (poruka)
- MORA: `2026-12-17` (forma, End date)
- MORA: `No requests yet.` (stranica)

Rezultat:

3. Promijeni samo `End date` u `2026-12-16`, klikni `Submit request`.

Očekujem:
- NEMA: `Not enough vacation days: requested 13, remaining 12` (poruka)
- MORA: `2026-12-16` (My requests, 2026-12-01, End date)
- MORA: `Holiday` (My requests, 2026-12-01, Reason)
- MORA: `Pending` (My requests, 2026-12-01, Status)
- MORA: `12` (saldo, Pending)
- MORA: `0` (saldo, Remaining)

Rezultat:

4. Odjavi se.

**B2. Luka odobrava Toma**

1. Prijavi se kao `luka`, otvori `Approvals`.

Očekujem (R1: samo članovi Lukinog tima):
- MORA: `Vacation` (Pending requests, Tom 2026-12-01, Type)
- MORA: `2026-12-16` (Pending requests, Tom 2026-12-01, End date)
- NEMA: `Ivan` (Pending requests)
- NEMA: `Marko` (Pending requests)

Rezultat:

2. U retku `Tom` klikni `Approve`.

Očekujem:
- NEMA: `Tom` (Pending requests)

Rezultat:

3. Odjavi se, prijavi kao `tom`.

Očekujem:
- MORA: `Approved by Luka` (My requests, 2026-12-01, Status)
- MORA: `12` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `0` (saldo, Remaining)

Rezultat:

4. Odjavi se.

**B3. Sara**

1. Prijavi se kao `sara`. Novi zahtjev: `Vacation`, `2026-11-18` do `2026-11-19`.

Očekujem:
- MORA: `Pending` (My requests, 2026-11-18, Status)
- MORA: `2` (saldo, Pending)
- MORA: `18` (saldo, Remaining)

Rezultat:

2. Odjavi se, prijavi kao `luka`, otvori `Approvals`, u retku `Sara` klikni `Approve`.

Očekujem:
- NEMA: `Sara` (Pending requests)
- MORA: `No requests waiting for approval.` (stranica)

Rezultat:

3. Odjavi se, prijavi kao `sara`.

Očekujem:
- MORA: `Approved by Luka` (My requests, 2026-11-18, Status)
- MORA: `2` (saldo, Used)
- MORA: `18` (saldo, Remaining)

Rezultat:

4. Odjavi se.

---

## Lanac C: Voditelji i admin (Ana, Luka, Eva)

**C1. Zahtjev voditeljice ide adminu**

1. Prijavi se kao `ana`. Novi zahtjev: `Vacation`, `2026-11-25` do `2026-11-27`, `Reason` `Wedding`.

Očekujem:
- MORA: `Pending` (My requests, 2026-11-25, Status)
- MORA: `3` (saldo, Pending)
- MORA: `17` (saldo, Remaining)

Rezultat:

2. Otvori `Approvals`.

Očekujem (R3: nitko ne odobrava vlastiti zahtjev):
- MORA: `Marko` (Pending requests, Marko 2026-11-16, Employee)
- NEMA: `Ana` (Pending requests)

Rezultat:

3. Odjavi se, prijavi kao `luka`, otvori `Approvals`.

Očekujem (D2: zahtjev voditelja ide adminu):
- MORA: `No requests waiting for approval.` (stranica)
- NEMA: `Ana` (Pending requests)

Rezultat:

4. Odjavi se, prijavi kao `eva`, otvori `Approvals`.

Očekujem (R2: admin odobrava bilo čiji zahtjev):
- MORA: `Marko` (Pending requests, redak 1, Employee)
- MORA: `Ana` (Pending requests, redak 2, Employee)
- MORA: `2026-11-25` (Pending requests, Ana 2026-11-25, Start date)

Rezultat:

5. U retku `Ana` klikni `Approve` (komentar prazan). **Markov redak ne diraj.**

Očekujem:
- NEMA: `Ana` (Pending requests)
- MORA: `Marko` (Pending requests, Marko 2026-11-16, Employee)

Rezultat:

**C2. Admin odobrava svoj zahtjev**

1. Kao Eva otvori `My time off`. Novi zahtjev: `Day off`, `2026-11-20` do `2026-11-20`, `Reason` `Errand`.

Očekujem (R14):
- MORA: `Day off` (My requests, 2026-11-20, Type)
- MORA: `Pending` (My requests, 2026-11-20, Status)
- MORA: `20` (saldo, Remaining)

Rezultat:

2. Otvori `Approvals`.

Očekujem:
- MORA: `Marko` (Pending requests, Marko 2026-11-16, Employee)
- MORA: `Eva` (Pending requests, Eva 2026-11-20, Employee)

Rezultat:

3. U retku `Eva` klikni `Approve`.

Očekujem (D15: admin smije odobriti svoj zahtjev, bilo kojeg tipa):
- NEMA: `Eva` (Pending requests)
- NEMA: `You cannot approve or reject this request` (poruka)

Rezultat:

4. Otvori `My time off`.

Očekujem:
- MORA: `Approved by Eva` (My requests, 2026-11-20, Status)

Rezultat:

5. Odjavi se, prijavi kao `ana`.

Očekujem:
- MORA: `Approved by Eva` (My requests, 2026-11-25, Status)
- MORA: `3` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `17` (saldo, Remaining)

Rezultat:

6. Odjavi se.

**C3. Voditeljica otkazuje Ivanov odobreni zahtjev (T8.1)**

1. Prijavi se kao `ivan`. Novi zahtjev: `Vacation`, `2026-12-14` do `2026-12-15`.

Očekujem:
- MORA: `Pending` (My requests, 2026-12-14, Status)
- MORA: `5` (saldo, Used)
- MORA: `2` (saldo, Pending)
- MORA: `13` (saldo, Remaining)

Rezultat:

2. Odjavi se, prijavi kao `ana`, otvori `Approvals`. U `Pending requests`, u retku `Ivan` | `2026-12-14`, ostavi `Comment` **prazan** i klikni `Approve`.

Očekujem (L39: odobreni zahtjevi koje smiješ otkazati, a nisu počeli; L40: svoje otkazuješ na My time off):
- NEMA: `Ivan` (Pending requests)
- MORA: `Vacation` (Approved requests, Ivan 2026-12-14, Type)
- MORA: `2026-12-15` (Approved requests, Ivan 2026-12-14, End date)
- MORA: `Ana` (Approved requests, Ivan 2026-12-14, Approved by)
- NEMA: `2026-11-25` (Approved requests)

Napomena: odobrenje je bez komentara jer stupac `Comment` kod Ivana pokazuje komentar odluke, a razlog otkazivanja tek kad komentara nema.

Rezultat:

3. U `Approved requests`, u retku `Ivan` | `2026-12-14`, ostavi `Cancel reason` prazan i klikni `Cancel`.

Očekujem (D3: tko otkazuje tuđi zahtjev mora upisati razlog):
- MORA: `reason must not be blank` (poruka)
- MORA: `Ivan` (Approved requests, Ivan 2026-12-14, Employee)

Rezultat:

4. U istom retku `Cancel reason` = `Project deadline`, klikni `Cancel`.

Očekujem (Q30, Q32: voditelj povlači dano odobrenje):
- NEMA: `2026-12-14` (Approved requests)

Rezultat:

5. Odjavi se, prijavi kao `ivan`.

Očekujem:
- MORA: `Cancelled by Ana` (My requests, 2026-12-14, Status)
- MORA: `Project deadline` (My requests, 2026-12-14, Comment)
- NEMA: `Cancel` (My requests, 2026-12-14, Actions)
- MORA: `5` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `15` (saldo, Remaining)

Rezultat:

6. Odjavi se.

Stanje nakon A–C (za kalendar):

| Tko | Tip | Datumi | Stanje |
| --- | --- | --- | --- |
| Ivan | Vacation | 2026-11-02 do 06 | Approved |
| Ivan | Sick leave | 2026-11-12 | Approved |
| Ivan | Vacation | 2026-12-09 do 10 | Cancelled |
| Ivan | Vacation | 2026-12-14 do 15 | Cancelled |
| Marko | Vacation | 2026-11-16 do 17 | Rejected (prvi), Pending (drugi) |
| Petra | Vacation | 2026-11-23 do 24 | Cancelled |
| Ana | Vacation | 2026-11-25 do 27 | Approved |
| Eva | Day off | 2026-11-20 | Approved |
| Tom | Vacation | 2026-12-01 do 16 | Approved |
| Sara | Vacation | 2026-11-18 do 19 | Approved |

---

## Lanac D: Kalendar (podaci iz A–C)

**D1. Marko (član Platforma)**

1. Prijavi se kao `marko`, otvori `Team calendar`, idi na `2026-11`.

Očekujem (brief, Q47: kolega vidi samo odobrene zahtjeve kolega, samo ime i datume; Q41: svoje vidi na čekanju i odobrene; Q48: odbijeni i otkazani se ne prikazuju):
- MORA: `Ivan` (dan 2026-11-02)
- MORA: `Ivan` (dan 2026-11-03)
- MORA: `Ivan` (dan 2026-11-04)
- MORA: `Ivan` (dan 2026-11-05)
- MORA: `Ivan` (dan 2026-11-06)
- MORA: `Ivan` (dan 2026-11-12)
- MORA: `Marko (Vacation, Pending)` (dan 2026-11-16)
- MORA: `Marko (Vacation, Pending)` (dan 2026-11-17)
- MORA: `Ana` (dan 2026-11-25)
- MORA: `Ana` (dan 2026-11-26)
- MORA: `Ana` (dan 2026-11-27)
- NEMA: `Ivan (Vacation, Approved)` (kalendar)
- NEMA: `Ivan (Sick leave, Approved)` (kalendar)
- NEMA: `Ana (Vacation, Approved)` (kalendar)
- NEMA: `2026-11-18` (dan 2026-11-18)
- NEMA: `2026-11-19` (dan 2026-11-19)
- NEMA: `2026-11-20` (dan 2026-11-20)
- NEMA: `2026-11-23` (dan 2026-11-23)
- NEMA: `2026-11-24` (dan 2026-11-24)
- NEMA: `Sara` (kalendar)
- NEMA: `Eva` (kalendar)
- NEMA: `Petra` (kalendar)

Rezultat:

2. Odjavi se.

**D2. Ana (voditeljica Platforma)**

1. Prijavi se kao `ana`, otvori `Team calendar`, idi na `2026-11`.

Očekujem (Q42: voditelj vidi zahtjeve svog tima na čekanju i odobrene, s tipom):
- MORA: `Ivan (Vacation, Approved)` (dan 2026-11-02)
- MORA: `Ivan (Vacation, Approved)` (dan 2026-11-06)
- MORA: `Ivan (Sick leave, Approved)` (dan 2026-11-12)
- MORA: `Marko (Vacation, Pending)` (dan 2026-11-16)
- MORA: `Marko (Vacation, Pending)` (dan 2026-11-17)
- MORA: `Ana (Vacation, Approved)` (dan 2026-11-25)
- MORA: `Ana (Vacation, Approved)` (dan 2026-11-27)
- NEMA: `Sara` (kalendar)
- NEMA: `Eva` (kalendar)
- NEMA: `Petra` (kalendar)

Rezultat:

2. Odjavi se.

**D3. Luka (voditelj Mobilea)**

1. Prijavi se kao `luka`, otvori `Team calendar`, idi na `2026-11`.

Očekujem:
- MORA: `Sara (Vacation, Approved)` (dan 2026-11-18)
- MORA: `Sara (Vacation, Approved)` (dan 2026-11-19)
- NEMA: `Ivan` (kalendar)
- NEMA: `Marko` (kalendar)
- NEMA: `Ana` (kalendar)
- NEMA: `Eva` (kalendar)

Rezultat:

2. Klikni `Next month` (`2026-12`).

Očekujem (R10: vikendi se ne prikazuju):
- MORA: `2026-12` (stranica)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-01)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-04)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-07)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-11)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-14)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-16)
- NEMA: `2026-12-05` (dan 2026-12-05)
- NEMA: `2026-12-06` (dan 2026-12-06)
- NEMA: `2026-12-17` (dan 2026-12-17)

Rezultat:

3. Odjavi se.

**D4. Eva (admin) i filtar po timu**

1. Prijavi se kao `eva`, otvori `Team calendar`, idi na `2026-11`.

Očekujem (Q43: admin vidi sve, i korisnike bez tima):
- MORA: `All teams` (Team)
- MORA: `Ivan (Vacation, Approved)` (dan 2026-11-02)
- MORA: `Ivan (Sick leave, Approved)` (dan 2026-11-12)
- MORA: `Marko (Vacation, Pending)` (dan 2026-11-16)
- MORA: `Sara (Vacation, Approved)` (dan 2026-11-18)
- MORA: `Eva (Day off, Approved)` (dan 2026-11-20)
- MORA: `Ana (Vacation, Approved)` (dan 2026-11-25)
- NEMA: `Petra` (kalendar)

Rezultat:

2. U polju `Team` odaberi `Platform`.

Očekujem (D12: filtar po timu). Eva nema tim, pa je vidljiva samo u `All teams`; to nije greška.
- MORA: `Ivan (Vacation, Approved)` (dan 2026-11-02)
- MORA: `Marko (Vacation, Pending)` (dan 2026-11-16)
- MORA: `Ana (Vacation, Approved)` (dan 2026-11-25)
- NEMA: `Sara` (kalendar)
- NEMA: `Eva` (kalendar)

Rezultat:

3. U polju `Team` odaberi `Mobile`.

Očekujem:
- MORA: `Sara (Vacation, Approved)` (dan 2026-11-18)
- MORA: `Sara (Vacation, Approved)` (dan 2026-11-19)
- NEMA: `Ivan` (kalendar)
- NEMA: `Marko` (kalendar)
- NEMA: `Ana` (kalendar)
- NEMA: `Eva` (kalendar)

Rezultat:

4. Klikni `Next month` (`Team` ostaje `Mobile`).

Očekujem:
- MORA: `2026-12` (stranica)
- MORA: `Mobile` (Team)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-01)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-16)
- NEMA: `Ivan` (kalendar)

Rezultat:

5. U polju `Team` odaberi `All teams`.

Očekujem:
- MORA: `2026-12` (stranica)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-01)
- NEMA: `Ivan` (kalendar)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-09)
- NEMA: `2026-12-17` (dan 2026-12-17)

Rezultat:

6. Odjavi se.

---

## Lanac E: Prosinac u firmi (zasebno, nova čista baza)

Prije E ponovi preduvjet 1 (čista baza). Blagdani se broje kao radni dani, npr. `2026-12-25` (D1: blagdani su proširenje; R10: preskaču se samo vikendi).

**E1.** Prijavi se kao `ivan`.

Očekujem:
- MORA: `20` (saldo, Entitlement)
- MORA: `20` (saldo, Remaining)
- MORA: `No requests yet.` (stranica)

Rezultat:

**E2.** Novi zahtjev: `Vacation`, `2026-12-21` do `2026-12-24`.

Očekujem:
- MORA: `Pending` (My requests, 2026-12-21, Status)
- MORA: `4` (saldo, Pending)
- MORA: `16` (saldo, Remaining)

Odjavi se.

Rezultat:

**E3.** Prijavi se kao `marko`. Novi zahtjev: `Vacation`, `2026-12-21` do `2026-12-23`.

Očekujem:
- MORA: `Pending` (My requests, 2026-12-21, Status)
- MORA: `3` (saldo, Pending)
- MORA: `17` (saldo, Remaining)

Odjavi se.

Rezultat:

**E4.** Prijavi se kao `petra`. Novi zahtjev: `Day off`, `2026-12-24` do `2026-12-24`.

Očekujem (R14):
- MORA: `Day off` (My requests, 2026-12-24, Type)
- MORA: `Pending` (My requests, 2026-12-24, Status)
- MORA: `20` (saldo, Remaining)

Odjavi se.

Rezultat:

**E5.** Prijavi se kao `ana`, otvori `Approvals`.

Očekujem: tri retka ovim redom. Redoslijed je po `Start date`, pa po redoslijedu stvaranja; Ivan i Marko imaju isti početak, a Ivanov je stvoren prvi.
- MORA: `Ivan` (Pending requests, redak 1, Employee)
- MORA: `Marko` (Pending requests, redak 2, Employee)
- MORA: `Petra` (Pending requests, redak 3, Employee)

Rezultat:

**E6.** U retku `Ivan` `Comment` = `Enjoy`, klikni `Approve`.

Očekujem:
- NEMA: `Ivan` (Pending requests)

Rezultat:

**E7.** U retku `Marko` ostavi `Comment` prazan, klikni `Reject`.

Očekujem (R5):
- MORA: `comment must not be blank` (poruka)
- MORA: `Marko` (Pending requests, Marko 2026-12-21, Employee)

Rezultat:

Zatim u retku `Marko` `Comment` = `Too many people out`, klikni `Reject`.

Očekujem:
- NEMA: `Marko` (Pending requests)

Odjavi se.

Rezultat:

**E8.** Prijavi se kao `marko`.

Očekujem:
- MORA: `Rejected by Ana` (My requests, 2026-12-21, Status)
- MORA: `Too many people out` (My requests, 2026-12-21, Comment)
- MORA: `0` (saldo, Pending)
- MORA: `20` (saldo, Remaining)

Rezultat:

**E9.** Novi zahtjev: `Vacation`, `2026-12-28` do `2026-12-30`.

Očekujem:
- MORA: `Pending` (My requests, 2026-12-28, Status)
- MORA: `3` (saldo, Pending)
- MORA: `17` (saldo, Remaining)

Odjavi se.

Rezultat:

**E10.** Prijavi se kao `ana`, otvori `Approvals`. U retku `Marko` | `2026-12-28` ostavi `Comment` **prazan** i klikni `Approve`.

Očekujem (bez komentara, jer E18 provjerava da Marko vidi razlog otkazivanja):
- NEMA: `Marko` (Pending requests)

Rezultat:

**E11.** U retku `Petra` klikni `Approve`.

Očekujem:
- NEMA: `Petra` (Pending requests)
- MORA: `No requests waiting for approval.` (stranica)

Rezultat:

**E12.** Otvori `My time off`. Novi zahtjev: `Vacation`, `2026-12-28` do `2026-12-31`.

Očekujem:
- MORA: `Pending` (My requests, 2026-12-28, Status)
- MORA: `4` (saldo, Pending)
- MORA: `16` (saldo, Remaining)

Rezultat:

Otvori `Approvals`.

Očekujem (R3: svog zahtjeva Ana ne vidi):
- MORA: `No requests waiting for approval.` (stranica)
- NEMA: `Ana` (Pending requests)

Odjavi se.

Rezultat:

**E13.** Prijavi se kao `luka`, otvori `Approvals`.

Očekujem (D2):
- MORA: `No requests waiting for approval.` (stranica)
- NEMA: `Ana` (Pending requests)

Odjavi se.

Rezultat:

Prijavi se kao `eva`, otvori `Approvals`. U retku `Ana` | `2026-12-28` klikni `Approve`.

Očekujem:
- NEMA: `Ana` (Pending requests)

Odjavi se.

Rezultat:

**E14.** Prijavi se kao `tom`. Novi zahtjev: `Vacation`, `2026-12-14` do `2026-12-31`.

Očekujem (R13):
- MORA: `Not enough vacation days: requested 14, remaining 12` (poruka)
- MORA: `No requests yet.` (stranica)

Rezultat:

Promijeni samo `Start date` u `2026-12-17`, klikni `Submit request`.

Očekujem:
- NEMA: `Not enough vacation days: requested 14, remaining 12` (poruka)
- MORA: `2026-12-31` (My requests, 2026-12-17, End date)
- MORA: `Pending` (My requests, 2026-12-17, Status)
- MORA: `11` (saldo, Pending)
- MORA: `1` (saldo, Remaining)

Odjavi se.

Rezultat:

**E15.** Prijavi se kao `luka`, otvori `Approvals`, u retku `Tom` klikni `Approve`. Odjavi se, prijavi kao `tom`.

Očekujem:
- MORA: `Approved by Luka` (My requests, 2026-12-17, Status)
- MORA: `11` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `1` (saldo, Remaining)

Odjavi se.

Rezultat:

**E16.** Prijavi se kao `ivan`. U retku `2026-12-21` (`Approved by Ana`) klikni `Cancel`, pa `Confirm cancel` (razlog prazan).

Očekujem (R7). `Comment` i dalje pokazuje komentar odluke `Enjoy`.
- MORA: `Cancelled by Ivan` (My requests, 2026-12-21, Status)
- MORA: `Enjoy` (My requests, 2026-12-21, Comment)
- MORA: `0` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `20` (saldo, Remaining)

Rezultat:

**E17.** Novi zahtjev: `Vacation`, `2026-12-22` do `2026-12-24`.

Očekujem (R9: otkazani zahtjev ne blokira datume):
- NEMA: `Request overlaps an existing pending or approved request` (poruka)
- MORA: `Pending` (My requests, 2026-12-22, Status)
- MORA: `3` (saldo, Pending)
- MORA: `17` (saldo, Remaining)

Odjavi se.

Rezultat:

Prijavi se kao `ana`, otvori `Approvals`, u retku `Ivan` | `2026-12-22` klikni `Approve`. Odjavi se, prijavi kao `ivan`.

Očekujem:
- MORA: `Approved by Ana` (My requests, 2026-12-22, Status)
- MORA: `3` (saldo, Used)
- MORA: `17` (saldo, Remaining)

Odjavi se.

Rezultat:

**E18.** Prijavi se kao `eva`, otvori `Approvals`.

Očekujem (L39, Q31: admin otkazuje tuđe odobrene zahtjeve koji nisu počeli):
- MORA: `No requests waiting for approval.` (stranica)
- MORA: `Tom` (Approved requests, redak 1, Employee)
- MORA: `Ivan` (Approved requests, redak 2, Employee)
- MORA: `Petra` (Approved requests, redak 3, Employee)
- MORA: `Marko` (Approved requests, redak 4, Employee)
- MORA: `Ana` (Approved requests, redak 5, Employee)
- NEMA: `2026-12-21` (Approved requests)

Rezultat:

U retku `Marko` | `2026-12-28` ostavi `Cancel reason` prazan, klikni `Cancel`.

Očekujem (D3):
- MORA: `reason must not be blank` (poruka)
- MORA: `Marko` (Approved requests, Marko 2026-12-28, Employee)

Rezultat:

U istom retku `Cancel reason` = `Year-end freeze`, klikni `Cancel`.

Očekujem:
- NEMA: `Marko` (Approved requests)

Odjavi se.

Rezultat:

Prijavi se kao `marko`.

Očekujem:
- MORA: `Cancelled by Eva` (My requests, 2026-12-28, Status)
- MORA: `Year-end freeze` (My requests, 2026-12-28, Comment)
- MORA: `0` (saldo, Used)
- MORA: `0` (saldo, Pending)
- MORA: `20` (saldo, Remaining)

Odjavi se.

Rezultat:

**E19.** Prijavi se kao `marko`, otvori `Team calendar`, idi na `2026-12`.

Očekujem (Q47, Q48):
- MORA: `Ivan` (dan 2026-12-22)
- MORA: `Ivan` (dan 2026-12-23)
- MORA: `Ivan` (dan 2026-12-24)
- MORA: `Petra` (dan 2026-12-24)
- MORA: `Ana` (dan 2026-12-28)
- MORA: `Ana` (dan 2026-12-31)
- NEMA: `2026-12-21` (dan 2026-12-21)
- NEMA: `Tom` (kalendar)
- NEMA: `Marko` (kalendar)
- NEMA: `Ivan (Vacation, Approved)` (kalendar)
- NEMA: `Petra (Day off, Approved)` (kalendar)

Rezultat:

Odjavi se.

**E20.** Prijavi se kao `ana`, `Team calendar`, `2026-12`.

Očekujem:
- MORA: `Ivan (Vacation, Approved)` (dan 2026-12-22)
- MORA: `Ivan (Vacation, Approved)` (dan 2026-12-24)
- MORA: `Petra (Day off, Approved)` (dan 2026-12-24)
- MORA: `Ana (Vacation, Approved)` (dan 2026-12-28)
- MORA: `Ana (Vacation, Approved)` (dan 2026-12-31)
- NEMA: `Tom` (kalendar)
- NEMA: `Marko` (kalendar)

Rezultat:

Odjavi se.

**E21.** Prijavi se kao `luka`, `Team calendar`, `2026-12`.

Očekujem:
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-17)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-18)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-21)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-25)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-28)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-31)
- NEMA: `2026-12-19` (dan 2026-12-19)
- NEMA: `Ivan` (kalendar)
- NEMA: `Petra` (kalendar)
- NEMA: `Ana` (kalendar)

Rezultat:

Odjavi se.

**E22.** Prijavi se kao `eva`, `Team calendar`, `2026-12`.

Očekujem (Q43, D12), `Team` = `All teams`:
- MORA: `All teams` (Team)
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-17)
- MORA: `Ivan (Vacation, Approved)` (dan 2026-12-22)
- MORA: `Petra (Day off, Approved)` (dan 2026-12-24)
- MORA: `Ana (Vacation, Approved)` (dan 2026-12-28)
- NEMA: `Marko` (kalendar)

Odaberi `Team` = `Mobile`:
- MORA: `Tom (Vacation, Approved)` (dan 2026-12-17)
- NEMA: `Ivan` (kalendar)
- NEMA: `Petra` (kalendar)
- NEMA: `Ana` (kalendar)

Odaberi `Team` = `Platform`:
- MORA: `Ivan (Vacation, Approved)` (dan 2026-12-22)
- MORA: `Petra (Day off, Approved)` (dan 2026-12-24)
- MORA: `Ana (Vacation, Approved)` (dan 2026-12-28)
- NEMA: `Tom` (kalendar)

Rezultat:

Odjavi se.
