# E2E izvjestaj

Izvor scenarija: `tester/e2e-scenariji.md`. Dokazi (doslovni `get_page_text` ispisi): `tester/e2e-dokazi/`.

## Sazetak

| Lanac | PROSLO | PALO | Trajanje (procjena iz vremena dokaza) |
| --- | --- | --- | --- |
| A (Platform) | 83 | 0 | 14:45:37 do 14:50:20, oko 5 min |
| B (Mobile) | 28 | 0 | 14:50:34 do 14:52:22, oko 2 min |
| C (Voditelji i admin) | 42 | 0 | 14:52:33 do 14:55:07, oko 2,5 min |
| D (Kalendar) | 76 | 1 | 14:55:29 do 14:57:17, oko 2 min |
| E (Prosinac, nova baza) | 115 | 0 | 14:58:00 do 15:05:28, oko 7,5 min |
| **Ukupno** | **344** | **1** | **oko 20 min** (bez cekanja na pripremu baze) |

Svi koraci su izvedeni. Nijedan korak nije preskocen. Jedina PALO stavka: D4, korak 5, `NEMA: 2026-12-09 (dan 2026-12-09)`.

**Naknadno (orkestrator):** PALO je greška u scenariju, ne u aplikaciji. Tomov odobreni godišnji 2026-12-01 do 16 pokriva i 2026-12-09, pa dan postoji; Ivana na kalendaru nema (`NEMA: Ivan` PROSLO). Stavka je u scenariju zamijenjena s `MORA: Tom (Vacation, Approved) (dan 2026-12-09)`, što dokaz `D4-5.txt` potvrđuje. Rezultat prema ispravljenom scenariju: 345 PROSLO, 0 PALO.

Napomene o mjerenju:
- Trajanje je procjena iz vremena zapisa dokaza, ne mjerenje sata.
- Brojanje stavki MORA/NEMA je rucno; moguce je manje odstupanje od brojanja tvoje strane.

## PALO stavke

1. **D4, korak 5 (Eva, All teams, 2026-12)**: `NEMA: 2026-12-09` (dan 2026-12-09). Na ekranu PIŠE: odjeljak `2026-12-09` postoji, s unosom `Tom (Vacation, Approved)`. Razlog: Tomov odobreni godisnji 2026-12-01 do 2026-12-16 pokriva i 2026-12-09. Ivanov zahtjev za 12-09 je otkazan i na kalendaru se ne prikazuje, ali naslov dana postoji zbog Toma. Stavka je presudena literalno prema pravilu iz oznake `(dan D)`.

## Izvjestaj po koraku

Svaka stavka: MORA ili NEMA, tekst, mjesto, presuda. Dokaz je u `tester/e2e-dokazi/`.

### Lanac A

**A1.1** (poruka, /login, navigacija): MORA `Invalid username or password` PROSLO; MORA `/login` PROSLO; NEMA `Signed in as Ivan` PROSLO. Dokaz: `A1-1.txt`.

**A1.2** (navigacija, saldo, stranica): MORA `Signed in as Ivan` PROSLO (provjereno `find`, u get_page_text nav nije); MORA `20` Entitlement PROSLO; MORA `0` Used PROSLO; MORA `0` Pending PROSLO; MORA `20` Remaining PROSLO; MORA `No requests yet.` PROSLO. Dokaz: `A1-2.txt`.

**A2** (poruka, My requests 2026-11-02, saldo): NEMA `Request overlaps...` PROSLO; MORA `Vacation` PROSLO; MORA `2026-11-06` PROSLO; MORA `Trip` PROSLO; MORA `Pending` PROSLO; MORA `Cancel` PROSLO; MORA `5` Pending PROSLO; MORA `15` Remaining PROSLO. Dokaz: `A2-1.txt`.

**A3.1** (Pending requests, Ivan 2026-11-02): MORA `Vacation` PROSLO; MORA `2026-11-06` PROSLO; MORA `Trip` PROSLO. Dokaz: `A3-1.txt`.

**A3.2** (Pending, Approved): NEMA `Ivan` (Pending) PROSLO; MORA `No requests waiting for approval.` PROSLO. Dokaz: `A3-2.txt`.

**A3.3** (My requests 11-02, saldo): MORA `Approved by Ana` PROSLO; MORA `Enjoy` PROSLO; MORA `5` Used PROSLO; MORA `0` Pending PROSLO; MORA `15` Remaining PROSLO. Dokaz: `A3-3.txt`.

**A4.1** (My requests 11-12): MORA `Sick leave` PROSLO; MORA `Pending` PROSLO; MORA `5` Used PROSLO; MORA `0` Pending PROSLO; MORA `15` Remaining PROSLO. Dokaz: `A4-1.txt`.

**A4.2** (Pending): NEMA `Ivan` (Pending) PROSLO; NEMA `comment must not be blank` PROSLO. Dokaz: `A4-2.txt`.

**A4.3** (My requests 11-12, saldo): MORA `Approved by Ana` PROSLO; MORA `5` Used PROSLO; MORA `15` Remaining PROSLO. Dokaz: `A4-3.txt`.

**A5** (poruka, forma, My requests): MORA `Request overlaps...` PROSLO; MORA `2026-11-02` (forma Start) PROSLO; MORA `2026-11-06` (forma End) PROSLO; MORA `Approved by Ana` PROSLO; NEMA `Pending` (My requests 11-02) PROSLO. Dokaz: `A5-1.txt`.

**A6.1** (My requests 11-16, saldo): MORA `Pending` PROSLO; MORA `2` Pending PROSLO; MORA `18` Remaining PROSLO. Dokaz: `A6-1.txt`.

**A6.2** (poruka, Pending): MORA `comment must not be blank` PROSLO; MORA `Marko` (Pending) PROSLO. Dokaz: `A6-2.txt`.

**A6.3** (Pending): NEMA `Marko` (Pending) PROSLO. Dokaz: `A6-3.txt`.

**A6.4** (My requests 11-16, saldo): MORA `Rejected by Ana` PROSLO; MORA `Team offsite` PROSLO; NEMA `Cancel` (11-16) PROSLO; MORA `0` Pending PROSLO; MORA `20` Remaining PROSLO. Dokaz: `A6-4.txt`.

**A7** (poruka, My requests 11-16, saldo): NEMA `Request overlaps...` PROSLO; MORA `Pending` PROSLO; MORA `Rejected by Ana` PROSLO; MORA `2` Pending PROSLO; MORA `18` Remaining PROSLO. Dokaz: `A7-1.txt`.

**A8.1** (My requests 11-23, saldo): MORA `Pending` PROSLO; MORA `2` Pending PROSLO; MORA `18` Remaining PROSLO. Dokaz: `A8-1.txt`.

**A8.2** (Actions 11-23): MORA `Cancel reason` PROSLO; MORA `Confirm cancel` PROSLO; MORA `Keep request` PROSLO. Dokaz: `A8-2.txt`.

**A8.3** (poruka, My requests 11-23, saldo): NEMA `reason must not be blank` PROSLO; MORA `Cancelled by Petra` PROSLO; NEMA `Cancel` (11-23) PROSLO; MORA `0` Pending PROSLO; MORA `20` Remaining PROSLO. Dokaz: `A8-3.txt`.

**A9.1** (My requests 12-09, saldo): MORA `Pending` PROSLO; MORA `5` Used PROSLO; MORA `2` Pending PROSLO; MORA `13` Remaining PROSLO. Dokaz: `A9-1.txt`.

**A9.2** (Pending): NEMA `Ivan` (Pending) PROSLO. Dokaz: `A9-2.txt`.

**A9.3** (My requests 12-09, saldo): MORA `Approved by Ana` PROSLO; MORA `7` Used PROSLO; MORA `0` Pending PROSLO; MORA `13` Remaining PROSLO. Dokaz: `A9-3.txt`.

**A9.4** (poruka, My requests 12-09, saldo): NEMA `Request has already started` PROSLO; MORA `Cancelled by Ivan` PROSLO; MORA `5` Used PROSLO; MORA `0` Pending PROSLO; MORA `15` Remaining PROSLO. Dokaz: `A9-4.txt`.

### Lanac B

**B1.1** (saldo): MORA `12` Entitlement PROSLO; MORA `12` Remaining PROSLO. Dokaz: `B1-1.txt`.

**B1.2** (poruka, forma, stranica): MORA `Not enough vacation days: requested 13, remaining 12` PROSLO; MORA `2026-12-17` (forma End) PROSLO; MORA `No requests yet.` PROSLO. Dokaz: `B1-2.txt`.

**B1.3** (poruka, My requests 12-01, saldo): NEMA `Not enough vacation days...` PROSLO; MORA `2026-12-16` PROSLO; MORA `Holiday` PROSLO; MORA `Pending` PROSLO; MORA `12` Pending PROSLO; MORA `0` Remaining PROSLO. Dokaz: `B1-3.txt`.

**B2.1** (Pending requests): MORA `Vacation` (Tom 12-01) PROSLO; MORA `2026-12-16` (Tom) PROSLO; NEMA `Ivan` PROSLO; NEMA `Marko` PROSLO. Dokaz: `B2-1.txt`.

**B2.2** (Pending): NEMA `Tom` (Pending) PROSLO. Dokaz: `B2-2.txt`.

**B2.3** (My requests 12-01, saldo): MORA `Approved by Luka` PROSLO; MORA `12` Used PROSLO; MORA `0` Pending PROSLO; MORA `0` Remaining PROSLO. Dokaz: `B2-3.txt`.

**B3.1** (My requests 11-18, saldo): MORA `Pending` PROSLO; MORA `2` Pending PROSLO; MORA `18` Remaining PROSLO. Dokaz: `B3-1.txt`.

**B3.2** (Pending): NEMA `Sara` (Pending) PROSLO; MORA `No requests waiting for approval.` PROSLO. Dokaz: `B3-2.txt`.

**B3.3** (My requests 11-18, saldo): MORA `Approved by Luka` PROSLO; MORA `2` Used PROSLO; MORA `18` Remaining PROSLO. Dokaz: `B3-3.txt`.

### Lanac C

**C1.1** (My requests 11-25, saldo): MORA `Pending` PROSLO; MORA `3` Pending PROSLO; MORA `17` Remaining PROSLO. Dokaz: `C1-1.txt`.

**C1.2** (Pending, Ana): MORA `Marko` (Pending, redak Marko 11-16) PROSLO; NEMA `Ana` (Pending) PROSLO. Dokaz: `C1-2.txt`.

**C1.3** (Luka, Pending): MORA `No requests waiting for approval.` PROSLO; NEMA `Ana` (Pending) PROSLO. Dokaz: `C1-3.txt`.

**C1.4** (Eva, Pending): MORA `Marko` (redak 1) PROSLO; MORA `Ana` (redak 2) PROSLO; MORA `2026-11-25` (Ana) PROSLO. Dokaz: `C1-4.txt`.

**C1.5** (Pending): NEMA `Ana` (Pending) PROSLO; MORA `Marko` (Pending) PROSLO. Dokaz: `C1-5.txt`.

**C2.1** (My requests 11-20, saldo): MORA `Day off` PROSLO; MORA `Pending` PROSLO; MORA `20` Remaining PROSLO. Dokaz: `C2-1.txt`.

**C2.2** (Pending, Eva): MORA `Marko` (Pending) PROSLO; MORA `Eva` (redak Eva 11-20) PROSLO. Dokaz: `C2-2.txt`.

**C2.3** (Pending, Eva, nakon klika Approve na retku Eva): NEMA `Eva` (Pending) PROSLO; NEMA `You cannot approve or reject this request` (poruka) PROSLO. Dokaz (ispis nakon klika): `C2-2b.txt`.

**C2.4** (My requests 11-20): MORA `Approved by Eva` PROSLO. Dokaz: `C2-3.txt`.

**C2.5** (My requests 11-25, saldo, Ana): MORA `Approved by Eva` PROSLO; MORA `3` Used PROSLO; MORA `0` Pending PROSLO; MORA `17` Remaining PROSLO. Dokaz: `C2-4.txt`.

**C3.1** (My requests 12-14, saldo): MORA `Pending` PROSLO; MORA `5` Used PROSLO; MORA `2` Pending PROSLO; MORA `13` Remaining PROSLO. Dokaz: `C3-1.txt`.

**C3.2** (Approved requests, Ivan 12-14): NEMA `Ivan` (Pending) PROSLO; MORA `Vacation` (Approved, Ivan 12-14, Type) PROSLO; MORA `2026-12-15` (Approved, Ivan 12-14, End date) PROSLO; MORA `Ana` (Approved by, Ivan 12-14) PROSLO; NEMA `2026-11-25` (Approved) PROSLO. Dokaz: `C3-2.txt`.

**C3.3** (poruka, Approved, Ivan 12-14): MORA `reason must not be blank` PROSLO; MORA `Ivan` (Approved, Ivan 12-14, Employee) PROSLO. Dokaz: `C3-3.txt`.

**C3.4** (Approved): NEMA `2026-12-14` (Approved) PROSLO. Dokaz: `C3-4.txt`.

**C3.5** (My requests 12-14, saldo, Ivan): MORA `Cancelled by Ana` PROSLO; MORA `Project deadline` PROSLO; NEMA `Cancel` (12-14) PROSLO; MORA `5` Used PROSLO; MORA `0` Pending PROSLO; MORA `15` Remaining PROSLO. Dokaz: `C3-5.txt`.

### Lanac D

**D1** (kalendar, 2026-11, Marko): MORA `Ivan` na 11-02, 11-03, 11-04, 11-05, 11-06, 11-12 (svi PROSLO); MORA `Marko (Vacation, Pending)` na 11-16 i 11-17 PROSLO; MORA `Ana` na 11-25, 11-26, 11-27 PROSLO; NEMA `Ivan (Vacation, Approved)` PROSLO; NEMA `Ivan (Sick leave, Approved)` PROSLO; NEMA `Ana (Vacation, Approved)` PROSLO; NEMA `2026-11-18`, `2026-11-19`, `2026-11-20`, `2026-11-23`, `2026-11-24` PROSLO; NEMA `Sara`, `Eva`, `Petra` PROSLO. Dokaz: `D1-1.txt`.

**D2** (kalendar, 2026-11, Ana): MORA `Ivan (Vacation, Approved)` na 11-02 i 11-06 PROSLO; MORA `Ivan (Sick leave, Approved)` na 11-12 PROSLO; MORA `Marko (Vacation, Pending)` na 11-16 i 11-17 PROSLO; MORA `Ana (Vacation, Approved)` na 11-25 i 11-27 PROSLO; NEMA `Sara`, `Eva`, `Petra` PROSLO. Dokaz: `D2-1.txt`.

**D3.1** (kalendar, 2026-11, Luka): MORA `Sara (Vacation, Approved)` na 11-18 i 11-19 PROSLO; NEMA `Ivan`, `Marko`, `Ana`, `Eva` PROSLO. Dokaz: `D3-1.txt`.

**D3.2** (kalendar, 2026-12, Luka): MORA `2026-12` PROSLO; MORA `Tom (Vacation, Approved)` na 12-01, 12-04, 12-07, 12-11, 12-14, 12-16 PROSLO; NEMA `2026-12-05`, `2026-12-06`, `2026-12-17` PROSLO. Dokaz: `D3-2.txt`.

**D4.1** (kalendar, 2026-11, Eva, Team = All teams): MORA `All teams` (Team) PROSLO; MORA `Ivan (Vacation, Approved)` 11-02 PROSLO; MORA `Ivan (Sick leave, Approved)` 11-12 PROSLO; MORA `Marko (Vacation, Pending)` 11-16 PROSLO; MORA `Sara (Vacation, Approved)` 11-18 PROSLO; MORA `Eva (Day off, Approved)` 11-20 PROSLO; MORA `Ana (Vacation, Approved)` 11-25 PROSLO; NEMA `Petra` PROSLO. Dokaz: `D4-1.txt`.

**D4.2** (Team = Platform, potvrdeno JS-om): MORA `Ivan (Vacation, Approved)` 11-02 PROSLO; MORA `Marko (Vacation, Pending)` 11-16 PROSLO; MORA `Ana (Vacation, Approved)` 11-25 PROSLO; NEMA `Sara` PROSLO; NEMA `Eva` PROSLO. Dokaz: `D4-2.txt`.

**D4.3** (Team = Mobile): MORA `Sara (Vacation, Approved)` 11-18 PROSLO; MORA `Sara (Vacation, Approved)` 11-19 PROSLO; NEMA `Ivan`, `Marko`, `Ana`, `Eva` PROSLO. Dokaz: `D4-3.txt`.

**D4.4** (Next month, Team = Mobile): MORA `2026-12` PROSLO; MORA `Mobile` (Team) PROSLO; MORA `Tom (Vacation, Approved)` 12-01 PROSLO; MORA `Tom (Vacation, Approved)` 12-16 PROSLO; NEMA `Ivan` PROSLO. Dokaz: `D4-4.txt`.

**D4.5** (Team = All teams, 2026-12): MORA `2026-12` PROSLO; MORA `Tom (Vacation, Approved)` 12-01 PROSLO; NEMA `Ivan` PROSLO; **NEMA `2026-12-09` (dan 2026-12-09) PALO** (odjeljak postoji, Tom 12-09); NEMA `2026-12-17` PROSLO. Dokaz: `D4-5.txt`.

### Lanac E (nova baza)

**E1** (saldo, stranica): MORA `20` Entitlement PROSLO; MORA `20` Remaining PROSLO; MORA `No requests yet.` PROSLO. Dokaz: `E1.txt`.

**E2** (My requests 12-21, saldo): MORA `Pending` PROSLO; MORA `4` Pending PROSLO; MORA `16` Remaining PROSLO. Dokaz: `E2.txt`.

**E3** (My requests 12-21, saldo, Marko): MORA `Pending` PROSLO; MORA `3` Pending PROSLO; MORA `17` Remaining PROSLO. Dokaz: `E3.txt`.

**E4** (My requests 12-24, Petra): MORA `Day off` PROSLO; MORA `Pending` PROSLO; MORA `20` Remaining PROSLO. Dokaz: `E4.txt`.

**E5** (Pending, Ana): MORA `Ivan` (redak 1) PROSLO; MORA `Marko` (redak 2) PROSLO; MORA `Petra` (redak 3) PROSLO. Dokaz: `E5.txt`.

**E6** (Pending): NEMA `Ivan` PROSLO. Dokaz: `E6.txt`.

**E7.1** (poruka, Pending): MORA `comment must not be blank` PROSLO; MORA `Marko` (Marko 12-21) PROSLO. Dokaz: `E7-1.txt`.

**E7.2** (Pending): NEMA `Marko` PROSLO. Dokaz: `E7-2.txt`.

**E8** (My requests 12-21, saldo, Marko): MORA `Rejected by Ana` PROSLO; MORA `Too many people out` PROSLO; MORA `0` Pending PROSLO; MORA `20` Remaining PROSLO. Dokaz: `E8.txt`.

**E9** (My requests 12-28, saldo): MORA `Pending` PROSLO; MORA `3` Pending PROSLO; MORA `17` Remaining PROSLO. Dokaz: `E9.txt`.

**E10** (Pending): NEMA `Marko` PROSLO. Dokaz: `E10.txt`.

**E11** (Pending, Petra): NEMA `Petra` PROSLO; MORA `No requests waiting for approval.` PROSLO. Dokaz: `E11.txt`.

**E12.1** (My requests 12-28, saldo, Ana): MORA `Pending` PROSLO; MORA `4` Pending PROSLO; MORA `16` Remaining PROSLO. Dokaz: `E12-1.txt`.

**E12.2** (Pending, Ana): MORA `No requests waiting for approval.` PROSLO; NEMA `Ana` (Pending) PROSLO. Dokaz: `E12-2.txt`.

**E13.1** (Luka, Pending): MORA `No requests waiting for approval.` PROSLO; NEMA `Ana` PROSLO. Dokaz: `E13-1.txt`.

**E13.2** (Eva, Pending, Ana 12-28 odobren): NEMA `Ana` (Pending) PROSLO. Dokaz: `E13-2.txt`.

**E14.1** (poruka, forma): MORA `Not enough vacation days: requested 14, remaining 12` PROSLO; MORA `No requests yet.` PROSLO. Dokaz: `E14-1.txt`.

**E14.2** (poruka, My requests 12-17, saldo): NEMA `Not enough vacation days...` PROSLO; MORA `2026-12-31` (End) PROSLO; MORA `Pending` PROSLO; MORA `11` Pending PROSLO; MORA `1` Remaining PROSLO. Dokaz: `E14-2.txt`.

**E15** (My requests 12-17, Tom): MORA `Approved by Luka` PROSLO; MORA `11` Used PROSLO; MORA `0` Pending PROSLO; MORA `1` Remaining PROSLO. Dokaz: `E15.txt`.

**E16** (My requests 12-21, Ivan): MORA `Cancelled by Ivan` PROSLO; MORA `Enjoy` PROSLO; MORA `0` Used PROSLO; MORA `0` Pending PROSLO; MORA `20` Remaining PROSLO. Dokaz: `E16.txt`.

**E17.1** (poruka, My requests 12-22, saldo): NEMA `Request overlaps...` PROSLO; MORA `Pending` PROSLO; MORA `3` Pending PROSLO; MORA `17` Remaining PROSLO. Dokaz: `E17-1.txt`.

**E17.2** (My requests 12-22, saldo, Ivan): MORA `Approved by Ana` PROSLO; MORA `3` Used PROSLO; MORA `17` Remaining PROSLO. Dokaz: `E17-2.txt`.

**E18.1** (Approvals, Eva): MORA `No requests waiting for approval.` PROSLO; MORA `Tom` (Approved redak 1) PROSLO; MORA `Ivan` (redak 2) PROSLO; MORA `Petra` (redak 3) PROSLO; MORA `Marko` (redak 4) PROSLO; MORA `Ana` (redak 5) PROSLO; NEMA `2026-12-21` (Approved) PROSLO. Dokaz: `E18-1.txt`.

**E18.2** (poruka, Approved, Marko 12-28): MORA `reason must not be blank` PROSLO; MORA `Marko` (Approved, Marko 12-28) PROSLO. Dokaz: `E18-2.txt`.

**E18.4** (My requests 12-28, Marko, saldo): MORA `Cancelled by Eva` PROSLO; MORA `Year-end freeze` PROSLO; MORA `0` Used PROSLO; MORA `0` Pending PROSLO; MORA `20` Remaining PROSLO. Dokaz: `E18-4.txt`.

**E19** (kalendar, 2026-12, Marko): MORA `Ivan` (12-22, 12-23, 12-24) PROSLO; MORA `Petra` (12-24) PROSLO; MORA `Ana` (12-28, 12-31) PROSLO; NEMA `2026-12-21` PROSLO; NEMA `Tom` PROSLO; NEMA `Marko` PROSLO; NEMA `Ivan (Vacation, Approved)` PROSLO; NEMA `Petra (Day off, Approved)` PROSLO. Dokaz: `E19.txt`.

**E20** (kalendar, 2026-12, Ana): MORA `Ivan (Vacation, Approved)` 12-22 i 12-24 PROSLO; MORA `Petra (Day off, Approved)` 12-24 PROSLO; MORA `Ana (Vacation, Approved)` 12-28 i 12-31 PROSLO; NEMA `Tom` PROSLO; NEMA `Marko` PROSLO. Dokaz: `E20.txt`.

**E21** (kalendar, 2026-12, Luka): MORA `Tom (Vacation, Approved)` na 12-17, 12-18, 12-21, 12-25, 12-28, 12-31 PROSLO; NEMA `2026-12-19` PROSLO; NEMA `Ivan`, `Petra`, `Ana` PROSLO. Dokaz: `E21.txt`.

**E22** (kalendar, 2026-12, Eva):
- Team = All teams: MORA `All teams` PROSLO; MORA `Tom (Vacation, Approved)` 12-17 PROSLO; MORA `Ivan (Vacation, Approved)` 12-22 PROSLO; MORA `Petra (Day off, Approved)` 12-24 PROSLO; MORA `Ana (Vacation, Approved)` 12-28 PROSLO; NEMA `Marko` PROSLO. Dokaz: `E22-1.txt`.
- Team = Mobile: MORA `Tom (Vacation, Approved)` 12-17 PROSLO; NEMA `Ivan`, `Petra`, `Ana` PROSLO. Dokaz: `E22-2.txt`.
- Team = Platform: MORA `Ivan (Vacation, Approved)` 12-22 PROSLO; MORA `Petra (Day off, Approved)` 12-24 PROSLO; MORA `Ana (Vacation, Approved)` 12-28 PROSLO; NEMA `Tom` PROSLO. Dokaz: `E22-3.txt`.

## Napomene za naruciteljа

- **Dokazi su rucni prijepisi ispisa.** Svaki dokaz je prepisan iz doslovnog ispisa `get_page_text` (nije generiran automatski). U dvije datoteke (`B3-3.txt`, `E15.txt`) sam pri prvom zapisu dodao prazan red koji u ispisu nije bio, pa sam ga uklonio. Ostale datoteke nisam mijenjao nakon zapisa.
- **Navigacija nije uvijek citana iz get_page_text.** Tekst `Signed in as ...` je u navigaciji i get_page_text ga ne vraca, pa sam ga provjeravao s `find` ili zoom snimkom.
- **Odstupanje od uputa.** Na A3 sam otvorio `Approvals` upisom adrese (navigate), a ne klikom na navigacijsku poveznicu. Ostali koraci su koristili poveznicu.
- **Zapazanje (nije stavka scenarija).** Vrijednosti forme Start/End date ostaju i nakon odjave i prijave drugog korisnika (npr. Marko je pri ulasku vidio datume Ivana). Kalendar takoder pamti mjesec i filtar tima izmedu prijava. Scenariji su to uvijek postavljali eksplicitno, pa nije utjecalo na presude.
- **Zapazanje (D4.5).** Pravilo za NEMA `2026-12-09` je doslovno; ako je namjera bila da Ivanov otkazani zahtjev ne vidi, provjera bi trebala biti na kartici Ivana, a ne na naslovu dana.
