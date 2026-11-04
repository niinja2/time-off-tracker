# Log i proces: neovisni review (zadaće 3 i 8)

Commit: eb785ba (HEAD). Datum: 2026-11-01.
Necommitane promjene (git status --short): M README.md, M docs/za-procjenu.md, M docs/zajednicko/specifikacija.md, M tester/e2e-izvjestaj.md; nepraćeno: .claude/, docs/diagrams.md, integrator_report.md, llm-shiva/, shiva_report.md.
Nisam čitao llm-shiva/ (osim ovog fajla), shiva_report.md, integrator_report.md ni docs/za-procjenu.md. Ništa nisam pokretao osim čitajućih git naredbi i brojanja.

## BLOKIRA

**B1. Predaja nakon strogog roka.**
Što: odgovor naručitelja kaže da je rok strog (29. 10. 2026.), a svi taskovi od T2 do T9 commitani su 30. 10. (00:30 do 17:21), dakle poslije roka.
Dokaz: specifikacija.md:144 "Rok je strog: 29. listopada 2026. (Q53.6)"; atomarna-pitanja.md:196 "Je li rok od 7 dana (29. listopada) strog? | Da | Sure"; atomarna-pitanja.md:163 "Poznato: od 2026-10-22". `git log`: 91581c9 (T2) 2026-10-30 00:29:58 +0100 ... eb785ba (T9) 2026-10-30 17:21:30 +0100. Na 29. 10. postoje samo 0badbf5 i 28abb35 (21:16, razvojni dokumenti i T1).
Napomena: reflog pokazuje da je povijest prepisana (vidi V1), pa su datumi datumi rekonstrukcije, ne nužno stvarnog rada. Ako je rad stvarno gotov do roka, to se iz repozitorija ne vidi.
Kome: Rajko/orkestrator, README (treba iskreno objasniti).

## VAŽNO

**V1. Povijest je prepisana i commitana naknadno u hrpama, protivno briefu i vlastitom procesu.**
Što: brief traži normalnu povijest, way-of-working traži commit po tasku nakon reviewa, a stvarna povijest ima parove commitova unutar sekundi i reset starih commitova.
Dokaz: brief.md:9 "We read the commit history, so commit as you normally would."; way-of-working.md:18 "7. **Commit.** Kad reviewer kaže "nema nalaza", task se označi gotovim u taskovima i commita."; razmaci: T2 91581c9 i T3 d9d624c 4 s; T5 1467c99 i T6 8e61eb9 11 s; T8.1 0626ebb, way-of-working 78ae305 i T9 eb785ba 20 s; skeleton 8d46b3f i brief 8ed66b3 2 s. `git reflog`: "HEAD@{14}: commit: Add schema, seed and domain model (T1)", "HEAD@{13}: reset: moving to 8ed66b3", ranije "eeeae09 ... commit: Seed users and teams, add JWT login", "0ecaaaf ... commit: Reset backend to the generated skeleton".
Kome: Rajko/orkestrator, README (How this was built to ne spominje).

**V2. "Review nakon svakog taska" nije potkrijepljen logom.**
Što: README tvrdi review nakon svakog taska, a log nema nijedan reviewerov unos za T5, T6, T8.1 i T9, niti zapis "nema nalaza" koji proces traži za commit.
Dokaz: README.md:47 "a review after every task"; way-of-working.md:18 (gore). Reviewerovi unosi u logu: L6-L8 (T1), L15-L16 (T2), L20 (T3), L22 (T4), L30 (T7), L35-L37 (T8). Za T5 (L23, L24), T6 (L25, L26), T8.1 (L40-L42) i T9 (L38) samo developer/tester. Možda je review bio bez nalaza i nije zapisan, ali to je NEPROVJERLJIVO iz repozitorija.
Kome: README/log (ili zapisati "nema nalaza").

**V3. Odluka orkestratora L28 mijenja što korisnik vidi, nema D, a obrazloženje je u suprotnosti s L39.**
Što: L28 odlučuje da nijedan ekran ne pokazuje saldo tuđih korisnika, iako naručitelj kaže da voditelj i admin vide salde (Q16, Q17); L39 je isti argument ("naručitelj je odgovorio da, pa korisnik to mora moći u aplikaciji") upotrijebio da obrne L33. Specifikacija nema D za to; README to navodi kao "L28:" proširenje s oznakom iz loga.
Dokaz: coding-log.md:393 "**Odluka:** Ekran Approvals ne pokazuje saldo podnositelja. Endpoint `GET /api/users/{id}/balance` ostaje u API-ju."; coding-log.md:11 "Odluka koja mijenja ponašanje ulazi i u specifikaciju kao nova oznaka D."; coding-log.md:547 (L39) "Naručitelj je na Q30 (voditelj otkazuje tuđi zahtjev), Q31 (admin otkazuje) i Q32 (povlačenje danog odobrenja) odgovorio "da", pa korisnik to mora moći napraviti u aplikaciji."; atomarna-pitanja.md:67 "Vidi li voditelj saldo članova svog tima? ... | Da"; README.md:225 "L28: show the requester's balance on the Approvals screen".
Kome: specifikacija (dodati D ili izričito prihvatiti kao 80/20), README (ukloniti oznaku loga, objasniti).

**V4. Otkazivanje tuđeg zahtjeva NA ČEKANJU još nije u UI-ju, a README to ne spominje.**
Što: D17 priznaje da to ne postoji u UI-ju; tražio je naručitelj (Q30, Q31); README u "Extensions (not built)" i "Deliberately out" to ne navodi.
Dokaz: specifikacija.md:179 (D17, radna kopija) "Otkazivanje tuđeg zahtjeva na čekanju kroz UI još nije napravljeno (samo API)"; atomarna-pitanja.md:91 "Smije li voditelj otkazati tuđi zahtjev? ... | Moze"; README.md:217-225 (popis proširenja nema te stavke).
Status odluke: D17 sam je PRIHVAĆENO (svjesno izostavljeno, dokumentirano u spec). Nalaz je samo da README to ne kaže.
Kome: README.

**V5. L14 je još "odlučeno", ali je odluka slabo provedena: `.env.example` daje valjanu javnu šifru.**
Što: L14 je zabranio zadanu šifru jer je javna u repozitoriju; `.env.example` sadrži vrijednost od 49 znakova koja prolazi provjeru od 32 bajta (L15), pa `cp .env.example .env` bez izmjene pokreće aplikaciju s javno poznatom šifrom. Uz to je stanje loga "odlučeno" iako je T9 gotov.
Dokaz: coding-log.md:163 "## L14 · odlučeno · T2 · pretpostavke"; coding-log.md:177 "Zadana šifra je javna u repozitoriju, pa bi svatko mogao napraviti valjani token"; `.env.example`: "JWT_SECRET=change-me-to-a-random-string-of-at-least-32-bytes"; docker-compose.yml: "JWT_SECRET: ${JWT_SECRET:?JWT_SECRET must be set in .env (see .env.example)}"; README.md:102 "cp .env.example .env    # then set JWT_SECRET to a random string of at least 32 bytes".
Kome: developer (kratka vrijednost u primjeru ili prazna), log (stanje).

**V6. taskovi.md opisuje drugi proces nego way-of-working.**
Što: taskovi.md kaže da svaka uloga ima svoj chat i da Rajko daje naredbe, tester vidi samo backend/src/test, više reviewera; way-of-working kaže da od T4 orkestrator pokreće agente, tester čita i frontend/tests, jedan reviewer.
Dokaz: taskovi.md:3 "Svaka uloga radi u svom chatu, a Rajko im daje kratke naredbe. Tester vidi samo `docs/zajednicko/` i `backend/src/test/`"; taskovi.md:11 "Revieweri pregledaju (linija, funkcija, cjelina)."; way-of-working.md:7 "od T4 orkestrator (glavni chat s najviše konteksta) pokreće uloge kao agente."; way-of-working.md:85-86 "(backend/src/test/, frontend/tests/)"; coding-log.md:437 (L31.10) "da, tester čita i piše `frontend/tests/`".
Kome: taskovi.md (README je usklađen s way-of-working).

**V7. Commitani HEAD je nekonzistentan; popravci su samo necommitani.**
Što: log (L39) i taskovi u HEAD-u govore o D17/T8.1, a specifikacija u HEAD-u nema D17 ni tablicu Approved requests; README u HEAD-u tvrdi 345/345 prošlo i D1-D16, iako izvješće kaže 344/1. Ispravke su u radnoj kopiji, ne u commitu.
Dokaz: `git diff` specifikacija.md dodaje redak D17 i §12 točku 2; `git diff` README.md "-Result: 345 of 345 passed, after one wrong check in a scenario was corrected (the app was right)." / "+Result: 344 of 345 passed."; coding-log.md:11 (pravilo o D).
Kome: Rajko (commit), specifikacija.

## SITNO

**S1. Stanja u logu ne slijede vlastita pravila.** L28 i L32 su "odlučeno (orkestrator)", ali provedeni (L32: sucelje.md:473 i `RequestView.java`; L28: ApprovalsScreen bez salda). L33 ima stanje "zamijenjeno s L39", koje nije među dopuštenima (coding-log.md:15 "Stanja: `otvoreno` (čeka odluku), `odlučeno` (čeka popravak), `riješeno` ..., `odbačeno` ..."). Pravilo coding-log.md:13 "Riješeni i odbačeni unosi se na kraju taska sele u `coding-log-arhiva.md`." nije provedeno: svih 42 unosa je u logu, arhive nema u docs/zajednicko/. Kome: log/orkestrator.

**S2. Specifikacija nosi stare statuse i reference na log.** specifikacija.md:4 "Status: nacrt za potvrdu. Šalje se naručitelju na potvrdu." ne kaže je li potvrđena (D1-D17 donosi sam autor, potvrda naručitelja nigdje ne postoji); D10 upućuje na "(L10)", D17 na "(L39, T8.1)", iako je spec "jedini izvor istine". Kome: specifikacija.

**S3. README "~530 tests" NEPROVJERLJIVO.** Brojao sam `@Test`/`@ParameterizedTest` u backend/src/test: 259; `it(`/`test(` u frontend/tests: 68; parametrizirani retci mogu to podići, ali točan broj nisam mogao potvrditi bez pokretanja. Kome: README.

**S4. L32 (imena odlučitelja/otkazivača u API-ju i UI-ju) nema zasebnog D.** Pokriveno tekstom specifikacija.md:107 "uključujući komentar odluke, tko je odlučio i tko je otkazao", pa samo bilješka.

## PRIHVAĆENO (svjesno izostavljeno ili zapisano kao odluka)

- Zahtjev preko granice godine daje 400 (L21.1), naručitelj Q28 kaže "Dijeli; i dalje je jedan zahtjev": proširenje, specifikacija §5 i §9, README Extensions.
- Prijenos dana, blagdani, bolovanje preko godišnjeg: D1, D5, D7, README "Deliberately out" i Extensions.
- Istodobni zahtjevi izostavljeni: README.md:94-95 "Deliberately out: concurrent requests (about 20 users), public holidays, carry-over" (Q53.1 20-ak korisnika; u specifikaciji nema D, samo u README-u).
- Korekcije izravno u bazi, bez ekrana: D6.
- Popis korisnika na prijavi izbačen (pretpostavka uz Q49.1 ga je sadržavala, odgovor "Da" na lozinku): D10, L10, brief dopušta formu.
- Greška 500 kad korisnik nestane usred poziva, naslućivanje postojanja korisničkog imena vremenom (L14.3, L14.4): prihvaćeno u L14.
- Docker obavezan umjesto opcionalan, voditelj više timova, admin odobrava vlastiti zahtjev: specifikacija §11.
- Pad jedne E2E provjere (344/345) iskreno naveden u README i izvješću.
- D17: otkazivanje tuđeg zahtjeva na čekanju samo kroz API (vidi V4 za README).

## Provjereno i u redu

- Svi unosi "odlučeno" iz loga pregledani: L14, L28, L32, L33 (zamijenjen), L39, te orkestratorovi umetci L10, L31.10, L38 (README Data corrections postoji, README.md:191-215). L10 i L39 imaju pripadajući D u spec/README, L32 je pokriven §7.
- L1, L2, L3, L9, L10, L11, L13: odluke ne proturječe odgovorima naručitelja (Q49, Q49.1, Q15, D6).
- L23 točka 3 (403 za postojeći, nedopušteni zahtjev) i L27 točka 11 u skladu sa spec §8.
- L25, L26, L40-L42: ne proturječe D3, D14, D17, Q23, Q30.1.
- Compose zahtijeva `JWT_SECRET` bez zadane vrijednosti (osim V5), `.env` je u .gitignore.
- README brojke: 42 unosa u logu, D1-D17, T1-T9 plus T8.1, ~75 pitanja (Q1-Q57 plus iteracija 2) odgovaraju dokumentima; 344/345 odgovara tester/e2e-izvjestaj.md.
- README sadrži sve što brief traži (pokretanje, korisnici, pretpostavke, odluke, što dalje).

## Nisam stigao provjeriti

- Odgovara li stvarni kod odlukama (čitao sam dokumente; kod samo grep-om za L32/L28).
- Sadržaj sucelje.md osim navedenih redaka, arhitektura.md, pravila-kodiranja.md, diagrams.md, e2e-scenariji, .claude/agents.
- Je li stvarno tester vidio samo dopuštene fajlove.
- Je li review T5, T6, T8.1, T9 održan izvan loga.
