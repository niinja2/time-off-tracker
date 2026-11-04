# Q / D / R review

Commit: eb785ba. Datum: 2026-11-01. Necommitane promjene: README.md, docs/za-procjenu.md, docs/zajednicko/specifikacija.md, tester/e2e-izvjestaj.md (M); .claude/, docs/diagrams.md, integrator_report.md, llm-shiva/, shiva_report.md (??). Specifikacija koju sam čitao je necommitana verzija (diff: dodan D17, Approvals u odjeljku 12, Q51 prekrižen). Čitao sam brief.md, atomarna-pitanja.md, specifikacija.md i README.md (samo "Deliberately out" i dijelove koji se pozivaju na Q/D). Kod nisam čitao.

Zadaća 1: provjereno 77 od 77 Q-ova (57 iz iteracije 1, 20 iz iteracije 2). Ne slažu se oni navedeni dolje. Zadaća 2: provjereno 17 od 17 D-ova. Zadaća 4: provjereno 14 od 14 R-ova plus tablica vidljivosti i tablica polja.

## BLOKIRA

Nema. Nijedan odgovor naručitelja ni brief nisu prekršeni bez zapisane odluke; sve što odstupa od odgovora ima D ili stoji u odjeljku 9.

## VAŽNO

1. Q18 odgovor doslovno kaže suprotno od §6 i D5. (specifikacija)
   - Q18 (atomarna-pitanja.md:70), pitanje "Provjerava li se saldo za godinu u kojoj zahtjev pada, a ne za tekuću godinu?", pretpostavka "Da", odgovor: "Za tekucu, ako ima od prijasnje godine, prijasnji se prvi trosi".
   - specifikacija.md:94: "Zahtjev za godišnji ne smije premašiti preostali saldo godine u koju pada." i D5 (redak 167): "zahtjev troši saldo godine u koju pada".
   - Naručitelj je odbacio pretpostavku, a spec je vraća. D5 to pokriva samo tvrdnjom da Q18 "opisuje prijenos". Odstupanje nije u §11. README (redak 172 i dalje) to objašnjava, ali spec §11 ne. Pola tvrdnje je prihvaćeno (prijenos je D7), pola ("tekuća godina") nije raspravljena.

2. Q39 nije prenesen u specifikaciju. (specifikacija)
   - Q39 (atomarna-pitanja.md:105): "Naša odluka: preklapanje se računa samo po radnim danima".
   - specifikacija.md:81: "čiji se datumi preklapaju, bez obzira na tip". Pretraga spec fajla za "Q39" daje 0 pogodaka; odluka nije ni u §10 kao D.
   - Spec doslovno traži preklapanje datuma, odluka kaže radne dane. Dev i tester čitaju samo spec, pa ovisi o tome što su pretpostavili. (Napomena: znam da je tema vikenda zatvorena; ovo je izostavljena odluka, ne novi rubni slučaj.)

3. D10 ukida ono što je naručitelj potvrdio u Q49.1, a D10 se poziva na pogrešan izvor. (specifikacija)
   - Q49.1 (atomarna-pitanja.md:190): pretpostavka "Da, seedane lozinke; na ekranu prijave popis korisnika za brzi odabir", odgovor "Da".
   - D10 (specifikacija.md:172): "Ekran prijave je forma ... bez popisa korisnika"; izvor navodi samo brief, ne Q49.1. README redak 135: "no user picker".
   - Odgovor "Da" prihvaća i popis. Odstupanje nije u §11. Brief dopušta oboje, pa ne krši brief, ali krši odgovor naručitelja bez zapisa.

4. Carry-over (Q14, Q14.1, Q14.2) nije u §11, a README to obećava. (specifikacija, README)
   - Q14 (redak 66): "Da, kao u Hrvatskoj"; Q14.1 "Yes"; Q14.2 "Nope".
   - specifikacija.md:96: "prijenos je proširenje (D5)"; §9 redak 154. README:169: "Deviations from the brief, based on the client's answers: section 11." Odjeljak 11 (retci 185-192) ima 6 redaka i nema carry-over ni Q28 ni Q57.
   - PRIHVAĆENO kao odluka (D7, README "Deliberately out" redak 94), ali je na krivom mjestu: README šalje čitatelja u §11, a tamo toga nema. Isto vrijedi za Q28 (dijeljenje zahtjeva preko granice godine) i Q57 (bolovanje preko godišnjeg): PRIHVAĆENO (§9 redci 156-157, README 223-224), ali ne u §11.

5. Rok i povijest commitova. (log/orkestrator)
   - Q53.6 (redak 196): "Je li rok od 7 dana (29. listopada) strog? ... Sure". specifikacija.md:144: "Rok je strog: 29. listopada 2026."
   - `git log`: eb785ba, 78ae305 i 0626ebb imaju datum 2026-10-30; danas je 2026-11-01. Isporuka je po vlastitim dokumentima izvan strogog roka. Ne mogu provjeriti je li rok pomaknut dogovorom.

## SITNO

6. D14 sužava Q30 bez bilješke. Q30 (redak 91) "Smije li voditelj otkazati tuđi zahtjev? ... Moze"; D14 (redak 176): "voditelj ne otkazuje zahtjev drugog voditelja". Isto za Q31 u odnosu na D14: admin smije sve, što je u skladu. §11 redak 190 kaže "Otkazuje i tko smije odobriti", što je točno, ali ne spominje suženje. (specifikacija)

7. D2 se poziva na Q5.1 kao na sukob, a Q5.1 (redak 180) pita samo za zaposlenika u više timova, ne za voditelja. D2 je zapravo izravno R3. (specifikacija)

8. D6 / specifikacija.md:74 pozivaju se na Q30.1 za "izravno u bazi". Q30.1 (redak 187) kaže samo "neka after the fact izmjene". "U bazi" je naša odluka (D6 to i jest), ali citat uz redak 74 sugerira da je izvor naručitelj. D12 (redak 174) se poziva na Q3, a Q3 ništa ne kaže o kalendaru s filtrom; to je čista odluka. (specifikacija)

9. Q25 "Proizvoljno" (redak 81) spec čita kao "bez granice" (specifikacija.md:85) i nema D za to tumačenje. (specifikacija)

10. D4 (nema zahtjeva prije datuma zaposlenja) ne ukida Q19/Q19.1 ("Nope" granica unatrag), ali redak 84 "bez granice unatrag" i D4 se sudaraju bez križne napomene. (specifikacija)

11. Tablica polja, redak "Komentar odluke | samo kod odbijanja" (specifikacija.md:40). R5 i Q45 govore i o neobaveznom komentaru odobrenja; stupac "Obavezno" je točan, ali nema retka za opcionalni komentar odobrenja. (specifikacija)

12. specifikacija.md:141: "po pravilu 80/20" stoji uz "pravila iz briefa s testovima". README redak 91 kaže "80/20 is for features, never for tests". Formulacija u specu se može pročitati kao 80/20 i za testove. (specifikacija)

13. Zaglavlje spec fajla (redak 4): "Status: nacrt za potvrdu. Šalje se naručitelju na potvrdu." uz tvrdnju (redak 7) "jedini izvor istine za kod". Nema zapisa da je naručitelj potvrdio. (specifikacija)

14. Odgođena tema iz atomarna-pitanja.md:155 "Prikazuje li se saldo i za sljedeću godinu? ... Tema ekrana, ovisi o 14.1" nije riješena u spec §12 ni §6. R12 traži samo tekuću godinu. (specifikacija)

15. D17 (specifikacija.md:179) priznaje: "Otkazivanje tuđeg zahtjeva na čekanju kroz UI još nije napravljeno (samo API)". PRIHVAĆENO (svjesno), ali §12 i §11 to ne spominju, pa Q30/Q31 u UI-ju vrijede samo za odobrene zahtjeve. Voditelj na čekanju može odbiti (s komentarom), što je funkcionalno blizu. (specifikacija, README)

16. Q-ovi bez ikakve reference u specu: Q9, Q39 (vidi nalaz 2), Q43.1, Q52. Q9, Q43.1 i Q52 ne mijenjaju ponašanje; samo Q39 je stvarni propust.

## Q-ovi koji se ne slažu (sažetak zadaće 1)

Q14, Q14.1, Q14.2, Q18, Q28, Q39, Q49.1, Q57 (i uz ograničenja Q25, Q30). Provjereno 77 od 77.

## D-ovi (zadaća 2), citat vs Q

- D1: brief (opcionalni dodaci), točno. D2: R3 doslovno ("A team lead's request goes to an admin"), Q5.1 pogrešno navedeno (nalaz 7).
- D3: Q37 "Moze, nema bude field" je dvosmislen, D3 to sam kaže; u §11. U redu.
- D4: nema Q, čista odluka, u redu. D5: R11 točno, Q18 vidi nalaz 1, Q15.1 "Mjeseci" točno.
- D6: Q30.1 nalaz 8. D7: Q53.3 "?" doslovno točno. D8: Q24 "Vremenska zona servera na koji se deploya", u skladu.
- D9: Q55 doslovno ("Ne trebaju"). D10: nalaz 3. D11: Q50 "obrada grešaka po volji" točno.
- D12: nalaz 8. D13: Q46/Q30/Q31 su izvori za "tko", "kada" je dodatak, u redu.
- D14: nalaz 6. D15: Q1.1 pita samo godišnji, D15 to izričito navodi, u §11 (redak 189 bilježi Q1.1). D16: Q15 točno; 7 mjeseci i 12 dana provjereno računom (lipanj do prosinac, 20x7/12=11,67, ceil 12).
- D17: Q30, Q31, Q32 točno; PRIHVAĆENO ograničenje (nalaz 15).

## R-ovi (zadaća 4)

R1 (§3 retci 53-54), R2 (57), R3 (58, 55, uz Q1.1 u §11 redak 189), R4 (59), R5 (60), R6 (§4 tablica), R7 (§4 + Q23 redak 72), R8 (80), R9 (81), R10 (78-79), R11 (91), R12 (93), R13 (94), R14 (95), "within one calendar year" (83): sve odgovara briefu. Tablica vidljivosti iz briefa (zaposlenik / voditelj / admin) odgovara §7 retcima 105-117, uključujući "no type, no reason, nothing pending". Tri ekrana i poruke o greškama odgovaraju §12. "Leads one team" je u §11 redak 187. Odstupanja od briefa u §11 (6 redaka) sva imaju točan Q.

## PRIHVAĆENO (svjesno izostavljeno, navedeno radi cjelovitosti)

Carry-over Q14/Q14.1/Q14.2/Q18 (D5, D7, README redak 94); blagdani (D1, README 94, 222); Q28 dijeljenje preko godine (§9, README 223); Q57 bolovanje preko godišnjeg (§9, README 224); istovremeni zahtjevi (README 94, Q53.1); Q9.1 praćenje stjecanja slobodnih dana (Q9.1 "Not for now", spec redak 47); UI otkazivanje tuđeg zahtjeva na čekanju (D17).

## Provjereno i u redu

Svi Q-ovi iz iteracije 1 osim gore navedenih (Q1-Q13, Q15-Q17, Q19-Q24, Q26, Q27, Q29, Q31-Q38, Q40-Q48, Q50-Q56); iteracija 2 osim navedenih (Q1.1, Q5.1, Q9.1, Q15.1, Q15.2, Q19.1, Q30.1, Q31.1, Q43.1, Q49, Q53.1-Q53.8 u skladu). Sidra Q i brief.md#rules postoje. Seed (lozinka, datumi zaposlenja) u skladu s D16. README upućuje na D1-D17 i §11 ispravno osim nalaza 4.

## Nisam stigao / nisam smio

Nisam čitao kod, testove, sucelje.md, coding-log.md (osim potvrde da L10 postoji) ni README u cijelosti, pa ne znam ponaša li se implementacija prema nalazima 1-3 (npr. preklapanje po datumima ili radnim danima). Brojeve linija koristim samo iz fajlova koje sam otvorio. Nisam provjeravao je li naručitelj potvrdio specifikaciju (nalaz 13) niti je li rok pomaknut (nalaz 5): NEPROVJERLJIVO iz dokumenata.
