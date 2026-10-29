# Coding log

Jedini kanal između uloga (developer, tester, revieweri). Svaki problem ima svoj unos s oznakom `L`. Uloge se pozivaju na oznaku, npr. "vidi L4", i ništa ne prepisuju.

Pravila:

- Developer i tester dodaju novi unos na dno: naslov sa stanjem `otvoreno` i **Problem**.
- Rasprava ide ispod Problema, jedan redak po javljanju, najviše dva kruga. Sudac je [specifikacija](specifikacija.md): argument pokazuje na oznaku (R, Q, D), ne na mišljenje. Ako se ne slože ili specifikacija ne odgovara, odlučuje Rajko.
- Tehnička pitanja (sučelje, tipovi, testna infrastruktura) rješavaju developer i tester sami, u raspravi, i sami upisuju **Odluka**, **Zašto** i **Gdje**.
- Orkestrator ulazi samo kad se radi o ponašanju (specifikacija ne odgovara) ili kad se developer i tester ne slože ni nakon dva kruga. Tada **Odluku** i **Zašto** upisuje on, uz Rajkovu odluku.
- Odluka koja mijenja ponašanje ulazi i u specifikaciju kao nova oznaka D.
- Čita se pretragom po oznaci (`L4`, `T3`, `R13`), ne cijeli fajl.
- Riješeni i odbačeni unosi se na kraju taska sele u `coding-log-arhiva.md`.

Stanja: `otvoreno` (čeka odluku), `odlučeno` (čeka popravak), `riješeno` (popravljeno, Gdje kaže gdje), `odbačeno` (nije problem, Zašto kaže zašto).

Format:

```markdown
## Lx · otvoreno · Tx · R4

**Problem** (developer): Jedna ili dvije rečenice.

- tester: argument s oznakom.
- developer: odgovor.

**Odluka:**
**Zašto:**
**Gdje:**
```

## L1 · riješeno · T1 · Q49.1, D16

**Problem** (developer): Specifikacija ne kaže koja je lozinka seedanih korisnika.

**Odluka:** Svi imaju lozinku `password`, upisanu u README.
**Zašto:** Najjednostavniji način da se djeluje kao svaki korisnik.
**Gdje:** specifikacija D16, sučelje T1

## L2 · riješeno · T1 · D4, D5, Q15, D16

**Problem** (developer): Specifikacija ne kaže datume zaposlenja seedanih korisnika.

**Odluka:** Svi 2020-01-01, osim Toma 2026-06-01.
**Zašto:** Tom pokazuje razmjerni saldo u godini zaposlenja (12 dana).
**Gdje:** specifikacija D16, sučelje T1

## L3 · riješeno · T1 · D16

**Problem** (developer): Sučelje T1 za lozinku i datume zaposlenja u seedu navodi samo Q15 (tko počne raditi usred godine dobiva razmjerno manje dana) i D5 (u godini zaposlenja broje se mjeseci od mjeseca zaposlenja do prosinca), a ne D16 (seed: lozinka `password` za sve, datum zaposlenja 2020-01-01 za sve osim Toma, 2026-06-01).

**Odluka:** Developer u sučelje dodaje ono što developer i tester smatraju potrebnim za testove, u postojećem formatu sučelja.
**Zašto:** Sučelje je jedino što tester vidi; mora biti dovoljno za testove.
**Gdje:** sučelje T1, Baza: lozinka i datumi zaposlenja pozivaju se na D16; navedeno je da se hash radi s `BCryptPasswordEncoder`

## L4 · riješeno · T1 · sučelje (Domena, Repozitoriji)

**Problem** (tester): Sučelje ne navodi konstruktore `User` i `Team`, Java tipove za datume i vremena, ni povratni tip `findByUsername`. Testovi u `backend/src/test/java/com/example/timeoff/` pretpostavljaju: `new User(username, name, passwordHash, admin, hireDate)` s `LocalDate`; `Instant` za `createdAt`, `now`, `decidedAt` i `cancelledAt`; `findByUsername` vraća `Optional<User>`; getteri `getLead()`, `getMembers()`, `getTeams()`, `getName()`, `getDecisionComment()`, `getDecidedBy()`, `getDecidedAt()`, `getCancelReason()`, `getCancelledBy()`, `getCancelledAt()`, `getStatus()`, `getHireDate()`, `getPasswordHash()`. Developer neka potvrdi ili ispravi sučelje.

**Odluka:** Developer u sučelje dodaje ono što developer i tester smatraju potrebnim za testove, u postojećem formatu sučelja.
**Zašto:** Sučelje je jedino što tester vidi; mora biti dovoljno za testove.
**Gdje:** sučelje T1, Domena: Java tipovi (`LocalDate`, `Instant`), getter `getX()` za svako polje osim `isAdmin()`, konstruktor `User`, tipovi `lead`, `members`, `teams`; Repozitoriji: `findByUsername` vraća `Optional<User>`, `findByOwner` vraća `List<TimeOffRequest>`. Konstruktor `Team` se ne dodaje jer timovi u T1 nastaju samo seedom

## L5 · riješeno · T1 · sučelje (Baza)

**Problem** (tester): Test seeda i shema koristi Spring Boot `@ServiceConnection`, Testcontainers PostgreSQL `postgres:16` i `BCryptPasswordEncoder` iz Spring Securityja. Sučelje to ne navodi. Ako pom koristi drugu verziju ili drugačiji pristup, neka developer javi.

**Odluka:** Tester koristi postojeći `TestcontainersConfiguration` (`@Import(TestcontainersConfiguration.class)`, `postgres:17`), ne vlastiti kontejner. `BCryptPasswordEncoder` iz Spring Securityja ostaje.
**Zašto:** Projekt koristi PostgreSQL 17; vlastiti kontejner koristi stari Testcontainers paket (`org.testcontainers.containers`) koji projekt nema.
**Gdje:** `backend/src/test/java/com/example/timeoff/SeedAndSchemaTest.java`, `@Import(TestcontainersConfiguration.class)`; vlastiti kontejner i `@ServiceConnection` uklonjeni

## L6 · riješeno · T1 · pravila kodiranja (Stil 4)

**Problem** (reviewer, važno): Helper `insertRequest` u `SeedAndSchemaTest.java` ima pet argumenata u jednom retku (deklaracija, red 172 i tijelo, red 176). Pozivi u testovima R8 i T1 (redovi 106, 113, 120, 127) stavljaju argumente u dva retka umjesto svakog u svoj. Pravilo: "Više od tri argumenta: svaki u svoj redak."

- tester: prihvaćam. `insertRequest` i svi pozivi u R8 i T1 sada imaju svaki argument u svom retku.

**Odluka:** Argumenti u svoj redak, kako pravilo traži.
**Zašto:** Pravilo "Više od tri argumenta: svaki u svoj redak."
**Gdje:** `SeedAndSchemaTest.java`, `insertRequest` i pozivi u `dbRejectsEndDateBeforeStartDate_R8`, `dbAcceptsOneDayRequest_R8`, `dbRejectsUnknownType_T1`, `dbRejectsUnknownStatus_T1`

## L7 · riješeno · T1 · pravila kodiranja (Stil 4)

**Problem** (reviewer, važno): U `TimeOffRequestTest.java`, metoda `setUp` (redovi 23 i 24) zove `new User(...)` s pet argumenata u jednom retku. Pravilo: "Više od tri argumenta: svaki u svoj redak."

- tester: prihvaćam. Oba `new User(...)` poziva u `setUp` sada imaju svaki argument u svom retku.

**Odluka:** Argumenti u svoj redak, kako pravilo traži.
**Zašto:** Pravilo "Više od tri argumenta: svaki u svoj redak."
**Gdje:** `domain/TimeOffRequestTest.java`, `setUp`

## L8 · riješeno · T1 · pravila kodiranja (Stil 5)

**Problem** (reviewer, važno): Lanac poziva u `SeedAndSchemaTest.java` stoji u jednom retku: `userRepository.findAll().stream().filter(...).map(...)` u `onlyEvaIsAdmin_T1` (red 85) i `team(teamName).getMembers().stream().map(...).toList()` u `memberNames` (red 191). Pravilo: "Lanac poziva: svaki korak u svoj redak."

- tester: prihvaćam. `onlyEvaIsAdmin_T1` i `memberNames` sada imaju svaki korak lanca u svom retku.

**Odluka:** Svaki korak lanca u svoj redak, kako pravilo traži.
**Zašto:** Pravilo "Lanac poziva: svaki korak u svoj redak."
**Gdje:** `SeedAndSchemaTest.java`, `onlyEvaIsAdmin_T1` i `memberNames`

## L9 · riješeno · T2 · arhitektura 2.3, D6

**Problem** (developer): Arhitektura 2.3 kaže da se iz tokena izvlače id, uloga i tim, a sučelje T2 u token stavlja samo id (`sub`) i `username`, dok se uloge i timovi čitaju iz baze kod svakog poziva. Razlog je D6 (korekcije se rade izravno u bazi): izmjena u bazi tada vrijedi odmah, bez nove prijave; treba odlučiti vrijedi li sučelje ili arhitektura.

**Odluka:** Token sadrži samo identitet (`sub` = id, `username`). Uloge i timovi čitaju se iz baze kod svakog poziva. Arhitektura 2.3 se ispravlja.
**Zašto:** Podaci u tokenu bili bi kopija koja zastari nakon ispravke u bazi (D6: korekcije se rade izravno u bazi). Specifikacija ne određuje sadržaj tokena (Q49, Q49.1); arhitektura je bila pretpostavka.
**Gdje:** `arhitektura 2.3` ← `sučelje T2, Token`

## L10 · riješeno · T2 · D10

**Problem** (developer): D10 (ekran prijave ima popis seedanih korisnika za brzi odabir) ne kaže odakle frontend dobiva popis. Sučelje T2 ne dodaje endpoint, nego bi frontend u T8 imao fiksni popis, jer bi javni endpoint svakome bez prijave otkrio sve korisnike.

- orkestrator: pitanje je samo jedno: tipka li korisnik na ekranu prijave svoje korisničko ime ili ga bira iz padajućeg izbornika? Lozinka se traži u oba slučaja (Q49.1: prijava traži lozinku).

**Odluka:** Korisnik tipka korisničko ime i lozinku. Nema popisa korisnika ni endpointa za njega. D10 se mijenja.
**Zašto:** Brief prihvaća formu s lozinkom; popis bi trebao javni endpoint ili hardkodirana imena u frontendu. Korisnička imena su u README-u.
**Gdje:** specifikacija D10

## L11 · riješeno · T2 · sučelje (Token)

**Problem** (tester): Testovi sami izrađuju tokene da provjere odbija li aplikacija lažne i istekle tokene, pa moraju potpisivati istim ključem kao aplikacija. Sučelje kaže samo da je ključ svojstvo `app.jwt.secret`, a ne kako se tekst pretvara u ključ. Testovi u `AuthApiTest.java` uzimaju bajtove teksta kao UTF-8 (`secret.getBytes(UTF_8)`), bez Base64 dekodiranja. Ako aplikacija radi drugačije, pada `tokenSignedWithAppSecretIsAccepted_T2`, a testovi za krivi potpis i istekao token prolaze bez stvarne provjere. Developer neka potvrdi ili upiše pravilo u sučelje T2, Token.

- developer: Tekst u `app.jwt.secret` je sama šifra i koristi se točno kako piše; nije zapis neke druge šifre koju bi trebalo prvo prevesti. Tehnički: aplikacija uzima bajtove teksta kao UTF-8, bez Base64 dekodiranja, isto kao testovi. Upisano u sučelje T2, Token, red Ključ.

**Odluka:** Tekst u `app.jwt.secret` je sama šifra, koristi se točno kako piše.
**Zašto:** Najjednostavnije je, a testovi već tako rade. Nema razloga da šifra bude zapisana na neki drugi način.
**Gdje:** sučelje T2, Token, red Ključ

## L12 · riješeno · T2 · pretpostavke

**Problem** (developer): Za T2 radim po pretpostavkama koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Tko je prijavljen: servis ne dobiva gotovog korisnika nego samo njegov broj i sam ga učita iz baze, inače ne bi mogao pročitati npr. kojim timovima korisnik pripada. Tehnički: `CurrentUser.id()` umjesto `CurrentUser.get()`; mijenja sučelje T2, Za sljedeće taskove.
2. Šifra za tokene: u postavkama postoji zadana šifra za lokalni rad i testove; u Dockeru se zadaje preko `JWT_SECRET`.
3. Korisničko ime mora se upisati točno, "Ana" nije isto što i "ana".
4. Podaci o korisniku u odgovoru na prijavu isti su kao u `GET /api/me`.
5. Sve rute osim `/actuator/health` traže prijavu, i one izvan `/api`, iako ih zasad nema.
6. Token koji je upravo istekao vrijedi još do 60 sekundi (zadana tolerancija sata u Springu); ne mijenjam je.
7. Token čiji `sub` nije broj odbija se kao neispravan: 401, "Invalid or expired token".
8. Pravilo "nedopuštena promjena stanja daje 409" ugrađujem već sada, iako ga koristi tek T5.

- tester: 1–4 i 6–8: slažem se. 5: ako backend poslužuje i frontend, stranica prijave mora biti dostupna bez prijave, inače se nitko ne može prijaviti.
- developer: 5: backend ne poslužuje frontend (arhitektura: zaseban Next.js kontejner); stranicu prijave daje frontend, a `POST /api/auth/login` je javan.
- tester: točke 3 i 7 su potvrđeno ponašanje koje nijedan test nije provjeravao, pa su dodani testovi: prijava s "Ana" ne prolazi, a token čiji `sub` nije broj odbija se. U `AuthApiTest.java`: `loginUsernameIsCaseSensitive_T2`, `tokenWithNonNumericSubjectIsUnauthorized_T2`.

**Odluka:** Svih osam pretpostavki vrijedi. Točka 1 ulazi u sučelje T2, Za sljedeće taskove: `CurrentUser.id()` umjesto `CurrentUser.get()`.
**Zašto:** Developer i tester se slažu; točka 5 ne smeta prijavi jer stranicu prijave daje frontend, ne backend.
**Gdje:** sučelje T2, Za sljedeće taskove; `security/CurrentUser.java` (`id()`)

## L13 · riješeno · T1 · pretpostavke

**Problem** (developer): Dva korisnika uspoređuju se kao isti objekt, ne po broju (id). To radi samo dok su oba učitana u istoj obradi; ako se jedan učita posebno, ista osoba ispadne "netko drugi". Tiče se `User.leads(team)`, `User.isMemberOf(team)` i `TimeOffRequest.isSubmittedBy(user)`. Treba odlučiti ostaje li tako ili se uspoređuje po broju.

- tester: Predlažem usporedbu po broju. Servisi u T5 i T6 učitavat će prijavljenog korisnika i vlasnika zahtjeva odvojeno (L12, točka 1), pa bi usporedba po objektu za istu osobu rekla da je "netko drugi". Uz jedan uvjet: korisnik koji još nije spremljen nema broj, pa dva takva korisnika ne smiju ispasti ista osoba. Moj test `onlyOwnerIsSubmitter_Q29` (`TimeOffRequestTest.java`) koristi nespremljene Ivana i Anu. Kad bi se uspoređivali samo brojevi, oba bi bila prazna, Ana bi ispala vlasnica Ivanova zahtjeva i test bi pao. Prijedlog: isti objekt je uvijek ista osoba; inače oba moraju imati broj i brojevi moraju biti jednaki.
- developer: prihvaćam prijedlog testera.
- tester: odluka mijenja ponašanje koje nijedan test nije provjeravao; postojeći testovi koriste iste objekte, pa bi prolazili i uz staru usporedbu. Zato su dodani testovi koji istu osobu učitaju dvaput, kao dva odvojena objekta, i očekuju da je prepoznata kao ista. Dodan je i test da nespremljena kopija Ivana nije vlasnik njegova zahtjeva. U `SeedAndSchemaTest.java`: `leadIsRecognisedWhenLoadedSeparately_T1`, `memberIsRecognisedWhenLoadedSeparately_T1`, `ownerIsRecognisedWhenLoadedSeparately_Q29`; u `TimeOffRequestTest.java`: `unsavedCopyOfOwnerIsNotSubmitter_T1`.

**Odluka:** Isti objekt je uvijek ista osoba; inače oba korisnika moraju imati broj (id) i brojevi moraju biti jednaki. Vrijedi za `leads`, `isMemberOf` i `isSubmittedBy`.
**Zašto:** Servisi učitavaju prijavljenog korisnika i vlasnika zahtjeva odvojeno (L12, točka 1), pa bi usporedba samo po objektu istu osobu proglasila drugom; nespremljeni korisnici bez broja ne smiju ispasti ista osoba.
**Gdje:** `domain/User.java` (`isSameAs`, `leads`, `isMemberOf`), `domain/TimeOffRequest.java` (`isSubmittedBy`)

## L14 · odlučeno · T2 · pretpostavke

**Problem** (developer): Kod za T2 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Springova unutarnja stranica za greške (`/error`) dostupna je bez prijave. Inače bi svaka neočekivana greška izgledala kao "nisi prijavljen" (401) umjesto prave greške.
2. Kad login padne zbog praznog polja, `detail` glasi npr. "username must not be blank"; ako fale oba polja, navedu se oba.
3. Ako korisnik nestane iz baze baš između provjere tokena i odgovora, `GET /api/me` vrati 500. Rijedak slučaj, nije posebno obrađen.
4. Za nepostojećeg korisnika lozinka se ne provjerava, pa je taj odgovor malo brži nego za krivu lozinku. Poruka je ista, ali po vremenu bi se moglo naslutiti koja korisnička imena postoje.
5. Docker Compose još ne zadaje `JWT_SECRET`, pa bi se u Dockeru koristila zadana šifra iz `application.properties`.

- tester: OK: 1–4. Konflikt: 5, jer proturječi L12, točki 2 (u Dockeru se šifra zadaje preko `JWT_SECRET`). Zadana šifra je javno u repozitoriju, pa bi bilo tko mogao sam napraviti valjani token. Predlažem da Compose zadaje `JWT_SECRET`, npr. iz `.env`.
- developer: 5: slažem se. Compose zasad pokreće samo bazu; backend ulazi u T9 (Docker i README). Tamo backend dobiva `JWT_SECRET: ${JWT_SECRET:?JWT_SECRET must be set}` iz `.env` (u `.gitignore`, uz `.env.example`); bez šifre se Compose ne pokrene. U T2 se ništa ne mijenja.

**Odluka:** 1–4 vrijede. 5: u T9 Compose backendu zadaje `JWT_SECRET` iz `.env`, bez zadane šifre.
**Zašto:** Zadana šifra je javna u repozitoriju, pa bi svatko mogao napraviti valjani token; backend u Composeu postoji tek od T9.
**Gdje:** taskovi T9

## L15 · riješeno · T2 · pravila kodiranja (Načelo 6), sučelje T2 (Token, Ključ)

**Problem** (reviewer, važno, za developera): Ako netko zada prekratku šifru za tokene, aplikacija se normalno pokrene i tek kod prve prijave padne s 500, a svaki token se odbija kao neispravan; nigdje ne piše da je šifra prekratka. Sučelje traži najmanje 32 bajta, a `jwtKey` u `security/SecurityConfig.java` (red 47) duljinu ne provjerava, pa se greška ne javlja na startu. Pravilo: "Fail fast. Greška se javlja odmah, s jasnom porukom."

- developer: prihvaćam. Prekratka šifra sada zaustavi aplikaciju već pri pokretanju, s porukom koja kaže koliko bajtova treba i koliko ih je zadano.

**Odluka:** Duljina šifre provjerava se pri pokretanju; ispod 32 bajta aplikacija se ne pokrene.
**Zašto:** Pravilo "Fail fast. Greška se javlja odmah, s jasnom porukom." i sučelje T2, Token, Ključ (najmanje 32 bajta).
**Gdje:** `security/SecurityConfig.java`, `jwtKey` i `MIN_KEY_BYTES`

## L16 · riješeno · T2 · pravila kodiranja (Stil 2)

**Problem** (reviewer, važno, za testera): Na tri mjesta u `AuthApiTest.java` poziv je ugniježđen u poziv tri do četiri razine duboko, pa se kod debugiranja ne vidi međurezultat. Mjesta: red 117 `assertThat(jwt.verify(new MACVerifier(secret.getBytes(...))))`, red 285 `.issueTime(Date.from(expiresAt.minus(Duration.ofHours(8))))`, red 289 `jwt.sign(new MACSigner(key.getBytes(...)))`. Pravilo: "Ugniježđeni pozivi razbijaju se u varijable s imenom."

- tester: slažem se. Sva tri mjesta su razbijena u varijable s imenom; u `sign` su u varijable izdvojeni i zaglavlje i vrijeme izdavanja.

**Odluka:** Ugniježđeni pozivi na sva tri mjesta razbijeni su u varijable s imenom.
**Zašto:** Pravilo "Ugniježđeni pozivi razbijaju se u varijable s imenom."; međurezultat se vidi kod debugiranja.
**Gdje:** `AuthApiTest.java`: `loginTokenCarriesIdentityClaims_T2` (`secretBytes`, `verifier`), `sign` (`issuedAt`, `header`, `keyBytes`, `signer`)

## L17 · riješeno · T3 · pretpostavke

**Problem** (developer): Sučelje T3 radi po pretpostavkama koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Za godine prije godine zaposlenja korisnik dobiva 0 dana godišnjeg.
2. Zahtjev pripada godini svog početnog datuma; zahtjev je unutar jedne godine (specifikacija, odjeljak 5), pa je to i godina završnog datuma.
3. Preostali dani mogu biti negativni ako je stanje mijenjano izravno u bazi (D6: korekcije se rade izravno u bazi); ne zaokružuju se na 0.
4. U T3 korisnik vidi samo svoj saldo. Saldo članova tima za voditelja (Q16) i svih za admina (Q17) dolazi u T7 (Vidljivost).
5. `GET /api/me/balance` vraća samo tekuću godinu, bez biranja godine.
6. Provjeru da zahtjev za godišnji ne smije premašiti preostali saldo (R13) radi kreiranje zahtjeva u T4, ne T3.
7. Brojanje radnih dana ne provjerava jesu li datumi u dobrom redoslijedu; to radi T4 (R8: završni datum nije prije početnog).

- tester: OK: 1–7. Konflikta nema; sve se slaže sa sučeljem T3 i specifikacijom.

**Odluka:** Svih sedam pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu, a sve je već upisano u sučelje T3; ništa se ne mijenja.
**Gdje:** sučelje T3 (bez izmjena)

## L18 · riješeno · T3 · sučelje T3 (Današnji datum, Saldo), D8

**Problem** (tester): Testovi za T3 rade po dvije pretpostavke koje sučelje ne kaže izričito. Neka se potvrde ili isprave:

1. Datum se računa u zoni sata, ne u zoni računala. Primjer: 31. 12. u 23:30 po UTC-u je u Zagrebu već 1. 1., pa je tekuća godina sljedeća (D8: zona Europe/Zagreb). Test zamijeni sat fiksnim satom sa zonom Europe/Zagreb i očekuje sljedeću godinu. Ako aplikacija uzima zonu računala, test pada kad testovi rade u UTC-u. Tehnički: `LocalDate.now(clock)`, a ne `clock.instant()` pa `ZoneId.systemDefault()`. Test: `BalanceApiNewYearTest.currentYearComesFromClockInZagrebTime_D8`.
2. `Balance` je u istom paketu kao `BalanceService` (`com.example.timeoff.service`), a polja konstruktora idu redom iz sučelja: `year`, `entitlement`, `used`, `pending`, `remaining`. Testovi u `BalanceServiceTest.java` uspoređuju cijeli saldo odjednom (`new Balance(...)`).

- developer: 1 i 2: potvrđujem. Datum se uzima kao `LocalDate.now(clock)`, u zoni sata; `Balance` je record u `com.example.timeoff.service` s poljima tim redom. Oboje dopisano u sučelje T3.

**Odluka:** Datum se računa u zoni sata, ne računala. `Balance` je u paketu `com.example.timeoff.service`, polja konstruktora redom `year`, `entitlement`, `used`, `pending`, `remaining`.
**Zašto:** D8 (zona Europe/Zagreb) vrijedi i kad računalo radi u UTC-u; testovi uspoređuju cijeli saldo pa im treba točan konstruktor.
**Gdje:** sučelje T3, Saldo (`Balance`) i Današnji datum

## L19 · riješeno · T3 · pretpostavke

**Problem** (developer): Kod za T3 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Ako brojanje radnih dana dobije datume obrnutim redom, vrati 0 umjesto da padne. Tehnički: `WorkingDays.count(start, end)` s `end` prije `start`.
2. Sat uzima zonu računala, a u Dockeru se ona zadaje varijablom `TZ` (D8: vremenska zona Europe/Zagreb). Kad se aplikacija pokrene lokalno bez `TZ`, vrijedi zona tog računala. Tehnički: `Clock.systemDefaultZone()`.
3. Tekuću godinu određuje controller (sloj 3, API) iz sata, a servis dobiva godinu kao broj, kako piše u sučelju T3 (`balance(userId, year)`).
4. Ako korisnik nestane iz baze baš usred poziva, `GET /api/me/balance` vrati 500, isto kao `GET /api/me` u T2 (L14, točka 3).

- tester: OK: 1–4. Konflikta nema. Uz 2: testovi zamjenjuju sat fiksnim satom sa zonom Europe/Zagreb, pa ne ovise o zoni računala.

**Odluka:** Sve četiri pretpostavke vrijede.
**Zašto:** Developer i tester se slažu; ništa ne proturječi specifikaciji ni sučelju T3.
**Gdje:** bez izmjena

## L20 · riješeno · T3 · pravila kodiranja (Stil 2)

**Problem** (reviewer, važno, za testera): U `BalanceApiTest.java`, test `balanceWithoutTokenIsUnauthorized_T3` (red 102), poziv je ugniježđen tri razine duboko: `assertThat(JsonPath.<String>read(response.body(), "$.detail"))`. Kod debugiranja se ne vidi tijelo odgovora ni pročitani `detail`. Isto je kao u L16. Pravilo: "Ugniježđeni pozivi razbijaju se u varijable s imenom."

- tester: slažem se. Popravljeno navedeno mjesto, a da ne bude novih unosa, razbijeni su i svi ostali pozivi ugniježđeni tri razine u mojim testovima.

**Odluka:** Svi pozivi ugniježđeni tri razine u testovima razbijeni su u varijable s imenom.
**Zašto:** Pravilo "Ugniježđeni pozivi razbijaju se u varijable s imenom."; isti uzorak bio je na više mjesta, ne samo na prijavljenom.
**Gdje:** `BalanceApiTest.java`: `balanceWithoutTokenIsUnauthorized_T3` (`body`, `detail`); `AuthApiTest.java`: `response` prije `problemDetail(...)` u svim testovima greške, `body` u `meReturnsOwnerOfLoginToken_T2`, `tokenSignedWithAppSecretIsAccepted_T2`, `adminFlagChangedInDatabaseAppliesWithoutNewLogin_D6` i `problemDetail`, `ivanId` u `loginTokenCarriesIdentityClaims_T2`; `SeedAndSchemaTest.java`: `matches` u `allUsersHavePasswordHashOfPassword_D16`, `request`, `ivan`, `ana` u `ownerIsRecognisedWhenLoadedSeparately_Q29`
