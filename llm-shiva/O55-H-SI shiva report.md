# Shiva: izvještaj neovisnog reviewa

Datum: 2026-10-30. Reviewer je samo čitao: ništa nije mijenjano, ništa nije upisano u log, ništa nije commitano.

Izvori istine, redom prednosti: [brief](docs/zajednicko/brief.md), [atomarna pitanja](docs/zajednicko/atomarna-pitanja.md) (oznake Q), [specifikacija](docs/zajednicko/specifikacija.md) (pravila R, odgovori Q, odluke D).

## BLOKIRA

**B1. Saldo se provjerava za pogrešnu godinu u odnosu na odgovor naručitelja.**
Pitali smo: "Provjerava li se saldo za godinu u kojoj zahtjev pada, a ne za tekuću?" Naručitelj je odgovorio: "Za tekucu, ako ima od prijasnje godine, prijasnji se prvi trosi". D5 (zahtjev troši saldo godine u koju pada) i kod rade upravo suprotno. Obrazloženje uz D5 bavi se samo drugom polovicom odgovora, prijenosom dana, a "za tekuću" prešućuje.
- Dokaz: `atomarna-pitanja.md:69` (Q18), `specifikacija.md:167` (D5), `backend/src/main/java/com/example/timeoff/service/RequestService.java:116`, L21 točka 5 (nitko nije spomenuo Q18).
- Kome: specifikacija, orkestrator.

**B2. Nisu isporučeni dijagrami koje je naručitelj tražio umjesto Swaggera.**
Specifikacija ih navodi kao zahtjev, a ne kao proširenje. Postoji samo jedan ASCII tok, za odobravanje, na hrvatskom.
- Dokaz: Q51 "Ne Swagger; dijagrami tokova i sekvencijski dijagrami", `specifikacija.md:132`, i dalje u "Otvoreno" (`specifikacija.md:205`), README ih stavlja u "What's next" (`README.md:210`).
- Kome: specifikacija, README.

**B3. Rok je prekoračen, ako nije produžen.**
Specifikacija kaže: "Rok je strog: 29. listopada 2026." (Q53.6), a T2–T9 su commitani 2026-10-30 (`git log`). Uz to povijest izgleda skupno složena: T5 i T6 su commitani u razmaku od 11 sekundi, a T8.1, way-of-working i T9 unutar 20 sekundi. Brief kaže: "We read the commit history, so commit as you normally would".
- Kome: vlasnik, orkestrator.

## VAŽNO

**V1. Voditelj i admin ne mogu kroz UI otkazati tuđi zahtjev na čekanju.**
Pravilo to dopušta: u specifikaciji 4, retku "Tko smije odobriti", za zahtjev "Na čekanju" piše "Otkazuje, razlog obavezan". Ekran Approvals za zahtjeve na čekanju ima samo Approve i Reject, a popis za otkazivanje vraća samo odobrene. L39 se poziva na Q30 i Q31 i kaže "korisnik to mora moći napraviti u aplikaciji", a napravljena je samo polovica.
- Dokaz: `backend/src/main/java/com/example/timeoff/service/CancelService.java:65`, sučelje T8.1, uvjet 1.
- Kome: log/orkestrator, developer.

**V2. L39 je promijenio ponašanje, a nije ušao u specifikaciju.**
Dodani su novi endpoint i nova tablica, a specifikacija 12 za Approvals i dalje kaže samo "zahtjevi na čekanju ... s odobravanjem i odbijanjem". Nema ni nove odluke D. To krši pravilo loga: "Odluka koja mijenja ponašanje ulazi i u specifikaciju kao nova oznaka D".
- Dokaz: `specifikacija.md:198`, `coding-log.md:11`.
- Kome: specifikacija, orkestrator.

**V3. Zaposlenik na My time off ne vidi razlog otkazivanja kad postoji komentar odobrenja.**
Primjer: Ana odobri s komentarom "ok", pa otkaže s obaveznim razlogom "Reorg". Ivan vidi "ok". D3 (tko otkazuje tuđi zahtjev mora upisati razlog) postoji upravo zato da vlasnik zna zašto. Problem je poznat u `za-procjenu.md:8`, ali nije u README-u.
- Dokaz: `frontend/src/components/MyTimeOffScreen.tsx:613` `decisionComment ?? cancelReason`, L31 točka 8.
- Kome: developer, README.

**V4. Ekran prijave bez popisa korisnika ide protiv potvrđene pretpostavke.**
Uz Q49.1 (prijava traži lozinku) pretpostavka je glasila "seedane lozinke; na ekranu prijave popis korisnika za brzi odabir", a odgovor je bio "Da". L10 (orkestrator) je popis izbacio i promijenio D10. Brief dopušta oboje, ali promjena nije navedena u specifikaciji 11 (odstupanja). Uz to `atomarna-pitanja.md:171` kaže da je "način prijave u UI-ju" naša odluka, a ne pitanje, iako je Q49.1 o tome pitao.
- Kome: specifikacija, orkestrator.

**V5. Na neodgovorenom pitanju stoji odluka koja odbacuje pet odgovora naručitelja.**
Naručitelj je na Q53.3 (pravila ili proširenja) odgovorio samo "?". Specifikacija ga ipak označava kao 🟩 odgovor i citira ga kao izvor za 80/20. Po legendi bi trebao biti 🟨 pretpostavka. Na tome stoji D7 (proširenja ne radimo), koja odbacuje izričite odgovore Q14, Q14.1, Q14.2 (prijenos dana), Q28 (zahtjev preko granice godine) i Q57 (bolovanje preko godišnjeg). Sve je dokumentirano, ali je temelj odluke ne-odgovor.
- Dokaz: `specifikacija.md:17`, `specifikacija.md:141`, `specifikacija.md:169`.
- Kome: specifikacija.

**V6. D3 pooštrava odgovor na Q37, a naručitelj to nije potvrdio.**
Na Q37 (treba li razlog za otkazivanje) odgovor je "Može, neka bude field", dakle opcionalno polje. D3 ga čini obaveznim za voditelja i admina, uz 400. Tumačenje je otvoreno navedeno, ali nije potvrđeno.
- Dokaz: `backend/src/main/java/com/example/timeoff/service/CancelService.java:82`.
- Kome: specifikacija.

**V7. R5 je u API sloju, a arhitektura kaže da API "nema pravila".**
R5 (odbijanje traži komentar) provjerava controller. Servis se na to oslanja i sam ga ne provjerava. Sličan D3 je u servisu, pa su dva gotovo ista pravila u dva različita sloja.
- Dokaz: `backend/src/main/java/com/example/timeoff/api/ApprovalController.java` (`RejectBody @NotBlank`, `body == null`), `backend/src/main/java/com/example/timeoff/service/ApprovalService.java:52` "the API has already rejected a blank one", sučelje T5, Provjere, red 2, `arhitektura.md:11`.
- Kome: developer (sučelje), arhitektura.

**V8. Tvrdnja "jedna tablica, ne razbacani if-ovi" za vidljivost nije točna.**
README i pravilo kodiranja 8 to tvrde. Tablica "Što pristup znači" iz sučelja T7 (FULL na kalendaru: na čekanju i odobreni; LIMITED: samo odobreni) u kodu je niz if-ova. Uvjet "PENDING ili APPROVED" ponovljen je na dva mjesta.
- Dokaz: `README.md:77`, `backend/src/main/java/com/example/timeoff/service/CalendarService.java:82` (`toEntry`), `CalendarService.java:75`, `RequestService.java:102`.
- Kome: developer.

**V9. README pretpostavke i odluke šalje u dokumente na hrvatskom.**
Brief traži da README sam pokrije pretpostavke i odluke. README ih šalje na D1–D16, specifikaciju 11 i unose "pretpostavke" u logu, a sve je to na hrvatskom. Specifikacija 8 kaže "Sve na engleskom: UI, README, kod" (Q54).
- Dokaz: `README.md:5`, `README.md:150-154`.
- Kome: README.

**V10. README ne objašnjava izbor biblioteka.**
Brief kaže: "Explain the non-obvious choices in your README". Nema ni riječi o Flywayu, Testcontainersu, OAuth2 resource serveru i Nimbusu za JWT, Tailwindu 4, Vitestu ni o Next 16 s `cacheComponents`.
- Kome: README.

**V11. README je uglavnom o procesu, a ne o aplikaciji.**
Specifikacija 8 (Q53.5) kaže "README ga kratko spominje". README procesu daje oko 90 redaka (7–94) i tvrdi "The main deliverable is the process" (`README.md:9`). Brief kaže da je isporuka aplikacija.
- Kome: README.

## SITNO

1. **Komentar odluke vidi i voditelj koji ne smije odobriti.** Q44 i Q45 kažu da komentar odluke vide "vlasnik i oni koji smiju odobravati". Voditelj ipak vidi pune zahtjeve drugog voditelja koji je član njegova tima, iako ih ne smije odobriti (L27 točka 4, sučelje T7, red 509). Specifikacija 7 o tome šuti. Kome: specifikacija.
2. **D8 ne bilježi da odstupa od Q24.** Q24 kaže "vremenska zona servera", a D8 u Composeu fiksira Europe/Zagreb bez obzira na server. To nije zapisano kao odstupanje. Kome: specifikacija.
3. **Q39 tvrdi odluku koju aplikacija ne slijedi.** Q39 u atomarnim pitanjima kaže "Naša odluka: preklapanje se računa samo po radnim danima", a specifikacija 5 to ne kaže, i `TimeOffRequest.overlaps` broji kalendarske dane. Ne predlažem promjenu ponašanja; samo dokument tvrdi nešto što aplikacija ne radi. Kome: specifikacija.
4. **Zaglavlje specifikacije je zastarjelo.** Piše "Status: nacrt za potvrdu. Šalje se naručitelju", s datumom 2026-10-28, iako je D10 kasnije promijenjen. Kome: specifikacija.
5. **Coding log ne prati vlastita pravila.**
   - L14, L28 i L32 su i dalje "odlučeno", iako su napravljeni (`docker-compose.yml:27`, `decidedByName` u `RequestView`).
   - "zamijenjeno s L39" nije dopušteno stanje (`coding-log.md:15`).
   - L39 ima raspravu ispod Gdje (red 553).
   - `coding-log-arhiva.md` ne postoji, iako pravilo kaže da se riješeni unosi sele tamo (red 13).

   Kome: orkestrator.
6. **Arhitektura je zastarjela.**
   - Red 69 kaže `RequestController.approve`, a to je `ApprovalController`.
   - Red 108 kaže `requestRepository.save`, a kod ne zove `save`.
   - 5.2 "User: uloga": uloga se ne sprema.
   - 5.1 Team ima samo naziv.

   Kome: arhitektura.
7. **Sučelje ne prati kod.** Sučelje T2 (red 118) kaže da je bez prijave dostupan "još samo" `/actuator/health`, a kod pušta i `/error` (L14 točka 1). Sučelje T1 ne navodi `findByStatus`. Kome: developer.
8. **`taskovi.md` je zastario.**
   - Kaže da tester vidi samo `backend/src/test/`, a od T8 vidi i `frontend/tests/` (L31 točka 10).
   - "Svaka uloga radi u svom chatu" ne slaže se s `way-of-working.md:7` (od T4 agente pokreće orkestrator).
   - Piše "Revieweri" u množini, a radio je jedan reviewer.

   Kome: orkestrator.
9. **Brojke u dokumentima ne slažu se.**
   - README kaže "~50 atomic questions", a pitanja ima 57 + 18 = 75.
   - `za-procjenu.md:7` u istom retku kaže "443 + 86" i "~460 testova sad".
   - README kaže "T1–T9", a postoji i T8.1.

   Kome: README.
10. **Naredbe za testove u README-u ne rade jedna za drugom.** `cd backend && ./mvnw test` pa `cd frontend ...` su u istom bloku. Pokrenute redom u istoj ljusci, druga `cd` pada jer je ljuska već u `backend`. Kome: README.
11. **`frontend/README.md` je predložak iz create-next-app.** Kaže samo `npm run dev` i ne spominje backend ni `API_URL`. Kome: developer.
12. **Neki pogledi postoje samo u API-ju.** Voditelj i admin nemaju ekran za zahtjeve tima u svim stanjima ni za salde članova (Q16, Q17). Brief to ne traži, ali README to ne navodi ni kao proširenje (navodi samo L28). Kome: README.

## Provjereno i u redu

- **R1–R14 imaju kod koji se slaže sa specifikacijom:** tablica ovlasti (s D2, D15, Q4, Q5.1), prijelazi stanja, otkazivanje (R6, R7, Q23, Q30.1), redoslijed provjera kod kreiranja (R8, R9, R10, D4) i saldo (R11–R14, Tom ima 12 dana).
- **Vidljivost je ista kroz specifikaciju 7, sučelje T7 i kod:** kolega vidi samo odobrene, ime i datume; odbijeni i otkazani nikad nisu na kalendaru; korisnika bez tima vide samo admini.
- **README API tablica** odgovara controllerima (svih 13 endpointa).
- **Docker** (`JWT_SECRET` bez zadane vrijednosti, `TZ`, portovi) odgovara L14, L38 i D8.
- **Seed** odgovara D16 i briefu.
- **Log** ima 42 unosa, a E2E 345/345 slaže se s izvještajem.
- **Servisi** ne znaju za HTTP.

## Nisam stigao provjeriti

- **Testove nisam pokretao**, pa "~530" nije potvrđeno. Izbrojio sam 250 backend i 64 frontend testne metode, s parametriziranima.
- **Docker i Next build nisam pokretao** (`cacheComponents`, Tailwind preko turbopacka).
- **Sadržaj testova nisam čitao**, pa ne znam pokrivaju li specifikaciju.
- **Cijeli `way-of-working.md` i `e2e-scenariji.md`** nisam pročitao.
- **Google Doc** nisam usporedio s `atomarna-pitanja.md`.
- **Stil (pravila kodiranja)** sam provjerio samo uzorkom, ne redak po redak.
