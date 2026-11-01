# Coding log

Jedini kanal između uloga (developer, tester, revieweri). Svaki problem ima svoj unos s oznakom `L`. Uloge se pozivaju na oznaku, npr. "vidi L4", i ništa ne prepisuju.

Pravila:

- Developer i tester dodaju novi unos na dno: naslov sa stanjem `otvoreno` i **Problem**.
- Rasprava ide ispod Problema, jedan redak po javljanju, najviše dva kruga. Sudac je [specifikacija](specifikacija.md): argument pokazuje na oznaku (R, Q, D), ne na mišljenje. Ako se ne slože ili specifikacija ne odgovara, odlučuje vlasnik specifikacije.
- Tehnička pitanja (sučelje, tipovi, testna infrastruktura) rješavaju developer i tester sami, u raspravi, i sami upisuju **Odluka**, **Zašto** i **Gdje**.
- Orkestrator ulazi samo kad se radi o ponašanju (specifikacija ne odgovara) ili kad se developer i tester ne slože ni nakon dva kruga. Tada **Odluku** i **Zašto** upisuje on, uz odluku vlasnika specifikacije.
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

## L21 · riješeno · T4 · pretpostavke

**Problem** (developer): Sučelje T4 radi po pretpostavkama koje specifikacija ne kaže. Neka se potvrde ili isprave:

1. Greška je 400 ako je zahtjev neispravan sam po sebi (fali polje, krivi redoslijed datuma, preko granice godine, nema radnog dana), a 409 ako se sudara s podacima u bazi (prije datuma zaposlenja, preklapanje, saldo). Specifikacija (odjeljak 8) izričito navodi samo završni datum prije početnog (400) te preklapanje i saldo (409).
2. Provjere idu fiksnim redom i javlja se samo prva koja padne, ne sve odjednom.
3. Prazan razlog (ili samo razmaci) sprema se kao da razloga nema. Razlog smije imati najviše 500 znakova, koliko stane u bazu.
4. Godišnji koji troši točno sve preostale dane je dopušten; odbija se tek kad traži više.
5. Saldo se provjerava za godinu u koju zahtjev pada, ne za tekuću (D5: zahtjev troši saldo godine u koju pada).
6. Odgovor na kreiranje vraća samo osnovna polja zahtjeva; polja odluke i otkazivanja dodaju T5 i T6. Popis vlastitih zahtjeva dolazi u T7 (Vidljivost), ne u T4.
7. Poruke grešaka (`detail`) su na engleskom (Q54: sve na engleskom), s tekstom iz sučelja T4.

- tester: slažem se sa svim pretpostavkama.

**Odluka:** Svih sedam pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; ništa ne proturječi specifikaciji ni sučelju T4.
**Gdje:** `service/RequestService.java` (provjere 3–8 redom, prazan razlog u `null`), `api/RequestController.java` (provjere 1–2, `@Size(max = 500)`), `api/ApiExceptionHandler.java` (400 i 409, `detail` imenuje neispravno polje)

## L22 · riješeno · T4 · pravila kodiranja (Stil 2)

**Problem** (reviewer, važno, za testera): U `RequestApiTest.java` poziv je na dva mjesta ugniježđen tri razine duboko, pa se kod debugiranja ne vidi tijelo odgovora ni pročitani tip: `assertThat(JsonPath.<String>read(response.body(), "$.type"))` u `sickLeaveRequestIsCreated_T4` (red 116) i `dayOffRequestIsCreated_T4` (red 124). Isti uzorak kao u L20. Pravilo: "Ugniježđeni pozivi razbijaju se u varijable s imenom."

- tester: slažem se. Na oba mjesta tijelo odgovora i pročitani tip su sada u varijablama s imenom (`body`, `type`).

**Odluka:** Tijelo odgovora i pročitani tip izdvojeni su u varijable s imenom na oba mjesta.
**Zašto:** Pravilo "Ugniježđeni pozivi razbijaju se u varijable s imenom."; kod debugiranja se vide tijelo odgovora i pročitani tip.
**Gdje:** `RequestApiTest.java`: `sickLeaveRequestIsCreated_T4` i `dayOffRequestIsCreated_T4` (`body`, `type`)

## L23 · riješeno · T5 · pretpostavke

**Problem** (developer): Sučelje T5 radi po pretpostavkama koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Popis za odobravanje (`GET /api/approvals`) je u T5, a ne u T7 (Vidljivost), jer ga određuje pravo odobravanja (Ovlasti), a ne pravila vidljivosti.
2. Tko ne smije odobriti nijedan zahtjev, dobiva praznu listu, ne 403.
3. Zahtjev koji postoji, ali ga korisnik ne smije odlučiti, daje 403, ne 404. Tako korisnik sazna da zahtjev postoji, ali specifikacija 8 razlikuje 403 (nema pravo) i 404 (ne postoji).
4. Ovlasti se provjeravaju prije stanja: tko nema pravo, dobiva 403 i kad zahtjev više nije na čekanju.
5. Kod odobrenja se preklapanje (R9) i saldo (R13) ne provjeravaju ponovo: zahtjev na čekanju ih je prošao pri kreiranju i već troši saldo. Iznimka bi bila ručna izmjena baze (D6: korekcije se rade izravno u bazi).
6. Za zahtjev koji nije na čekanju `detail` je poruka iz domene (T1), npr. "Request cannot change from APPROVED to APPROVED".
7. Prazan komentar kod odobrenja sprema se kao `null`, kao `reason` u T4.
8. `<Request>` dobiva `ownerName`, da ekran Approvals ne mora posebno tražiti imena. To polje se pojavljuje i u odgovoru `POST /api/requests` iz T4.

- tester: OK: 1–8. Uz 7 jedno pitanje: vrijedi li isto za odbijanje? Pretpostavljam da prazan komentar (ili samo razmaci) znači da komentara nema, pa odbijanje pada (R5: odbijanje traži komentar). Tako bih testirao; neka developer potvrdi.
- developer: potvrđujem. Prazan komentar ili samo razmaci znači da komentara nema; odbijanje tada pada s 400, `comment must not be blank`. Dopisano u sučelje T5, Provjere, red 2.

**Odluka:** Svih osam pretpostavki vrijedi. Kod odbijanja prazan komentar ili samo razmaci znači da komentara nema: 400.
**Zašto:** Developer i tester se slažu; komentar od samih razmaka ne objašnjava odbijanje (R5: odbijanje traži komentar).
**Gdje:** sučelje T5, Provjere, red 2

## L24 · riješeno · T5 · pretpostavke

**Problem** (developer): Kod za T5 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Odbijanje bez ikakvog tijela pada isto kao odbijanje bez komentara: 400, `comment must not be blank` (R5: odbijanje traži komentar).
2. `GET /api/approvals` čita sve zahtjeve na čekanju iz baze, a u Javi bira one koje korisnik smije odobriti. Za POC od oko 20 korisnika (Q53.1) to je dovoljno; kod puno podataka filtriranje bi se selilo u SQL (arhitektura, Napomene).
3. Kad `id` u adresi nije broj, status je 400, a `detail` je Springova zadana poruka, npr. "Failed to convert 'id' ...".
4. Ako prijavljeni korisnik nestane iz baze usred poziva, odgovor je 500, kao u T2 i T3 (L14 točka 3, L19 točka 4).

- tester: OK: 1–4. Uz 1 sam dodao slučaj "odbijanje bez tijela" u `rejectionWithoutCommentIsBadRequest_R5`, jer to ponašanje nijedan test nije provjeravao. Uz 3 test provjerava samo status 400, ne tekst poruke.

**Odluka:** Sve četiri pretpostavke vrijede.
**Zašto:** Developer i tester se slažu; nema konflikta.
**Gdje:** kod T5 (bez izmjena sučelja); `ApprovalApiTest.java`: `rejectionWithoutCommentIsBadRequest_R5` (slučaj bez tijela)

## L25 · riješeno · T6 · pretpostavke

**Problem** (developer): Sučelje T6 radi po pretpostavkama koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Vlasnik se gleda prije onoga tko smije odobriti (Ovlasti T6, red 1 prije reda 2). Zato admin otkazuje svoj zahtjev bez razloga (R6, R7: vlasnik otkazuje, razlog opcionalan).
2. Zahtjev na čekanju smije se otkazati i kad su mu datumi prošli. Specifikacija 4 "počeo" gleda samo kod odobrenih (R7: odobreni zahtjev koji nije počeo).
3. Kad otkazuje netko tko nije vlasnik, a razloga nema, odgovor je 400 (D3: tko otkazuje tuđi zahtjev mora upisati razlog). Ta se provjera radi nakon ovlasti, jer ovisi o tome tko otkazuje; tko nema pravo, dobiva 403, ne 400.
4. Razlog se provjerava prije stanja: Ana koja bez razloga otkazuje Ivanov odbijeni zahtjev dobiva 400, ne 409.
5. Odobreni zahtjev koji je počeo daje 409, `detail` `Request has already started` (R7, Q23: zahtjev koji počinje danas je počeo). Poruka je nova.
6. Polje u tijelu je `reason` (ne `cancelReason`), najviše 500 znakova kao komentar u T5. Prazan razlog ili samo razmaci sprema se kao `null`.
7. Novi su `CancelPolicy` (red 2 zove `ApprovalPolicy` iz T5, ne ponavlja pravila) i `CancelService`. Nove iznimke ne trebaju.

- tester: OK: 1–7.

**Odluka:** Svih sedam pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; nema konflikta. Pretpostavke 1–5 slijede tablicu otkazivanja u specifikaciji 4 (R6, R7, D3, D14, Q23).
**Gdje:** sučelje T6 (bez izmjena)

## L26 · riješeno · T6 · pretpostavke

**Problem** (developer): Kod za T6 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Odobreni zahtjev koji je počeo daje 409 preko postojeće `RuleViolationException`, kao preklapanje (R9) i saldo (R13) u T4 (R7: otkazuje se odobreni zahtjev koji nije počeo; Q23: zahtjev koji počinje danas je počeo).
2. Razlog se prvo očisti (prazan ili samo razmaci postaje `null`), pa se tek onda provjerava je li obavezan. Voditelj ili admin koji otkazuje tuđi zahtjev s razlogom od samih razmaka dobiva 400, `reason must not be blank` (D3: tko otkazuje tuđi zahtjev mora upisati razlog).
3. Otkazivanje ima svoj `CancelController`, ne ide u `ApprovalController`; svaki task ima svoj controller, kao T4 i T5.
4. Ako prijavljeni korisnik nestane iz baze usred poziva, odgovor je 500, kao u T2–T5 (L24 točka 4).

- tester: OK: 1–4. Točku 2 već provjerava `cancellingOthersRequestWithoutReasonIsBadRequest_D3` (slučaj samo s razmacima).

**Odluka:** Sve četiri pretpostavke vrijede.
**Zašto:** Developer i tester se slažu; nema konflikta.
**Gdje:** kod T6 (bez izmjena sučelja ni testova)

## L27 · riješeno · T7 · pretpostavke

**Problem** (developer): Sučelje T7 radi po pretpostavkama koje specifikacija ne kaže. Neka se potvrde ili isprave:

1. Za ekran My time off dodaje se popis vlastitih zahtjeva (`GET /api/me/requests`), u svim stanjima, najnoviji prvi. Ostalo za taj ekran (kreiranje, otkazivanje, saldo) već postoji iz T3, T4 i T6.
2. Iako ga nijedan ekran ne treba, dodaje se popis i dohvat jednog zahtjeva sa svim detaljima za voditelja i admina (`GET /api/requests`, `GET /api/requests/{id}`) i saldo drugih (`GET /api/users/{id}/balance`). Brief kaže da će API zvati izravno kao razni korisnici, a specifikacija 7 nabraja što voditelj i admin vide (Q16: voditelj vidi saldo članova; Q17: admin vidi sve salde).
3. Jedno pravilo vidljivosti s tri razine (sve, samo ime i datumi, ništa) vrijedi za sve: zahtjeve, saldo i kalendar. Tko vidi sve o nekome, vidi i njegov saldo i pune unose na kalendaru.
4. Voditelj vidi sve zahtjeve člana svog tima i kad je taj član voditelj nekog drugog tima, iako ih ne smije odobriti (D2: zahtjev voditelja ide adminu je pravilo odobravanja, ne vidljivosti).
5. Kad dvoje dijele više timova, vrijedi viša razina, a osoba je na kalendaru samo jednom.
6. Unos na kalendaru nikad nema razlog, ni za voditelja ni za admina; Q42 kaže samo "s tipom". Razlog se čita iz popisa zahtjeva. Unos ima i stanje (na čekanju ili odobren), da se razlikuju.
7. Kolegama se na kalendaru ne šalju tip i stanje ni kao prazna vrijednost: ta polja u odgovoru ne postoje.
8. Zahtjev koji samo dijelom pada u mjesec prikazuje se s cijelim rasponom datuma. Grupiranje po danima radi frontend.
9. Filtar po timu samo sužava popis; vidljivost se ne mijenja. Tim u kojem korisnik nikoga ne vidi, ili tim koji ne postoji, daje praznu listu, ne grešku. Admin nema poseban filtar "bez tima": korisnike bez tima vidi kad filtra nema.
10. Popis timova za filtar: admin dobiva sve timove, ostali timove u kojima su član.
11. Kad zahtjev ili korisnik ne postoji: 404; kad postoji, ali ga korisnik ne smije vidjeti: 403, kao u T5 i T6 (L23, točka 3). Popisi nikad ne daju 403, nego praznu listu.
12. Frontend nudi otkazivanje za zahtjeve na čekanju i odobrene; je li odobreni počeo, odlučuje backend (409, `Request has already started`), a ekran prikaže poruku. `<Request>` ne dobiva posebno polje "smije se otkazati".
13. Filtriranje je u Javi iz svih zahtjeva, kao u T5 (L24, točka 2); dovoljno za POC od oko 20 korisnika (Q53.1).

- tester: 1: OK, Q44–Q46 (zaposlenik vidi svoje zahtjeve sa svim detaljima).
- tester: 2: OK, specifikacija 7 (voditelj i admin vide zahtjeve s detaljima), Q16, Q17 (saldo članova, svi saldi).
- tester: 3: OK, slaže se sa specifikacijom 7, kalendar: voditelj "za ostale timove u kojima je član vidi isto što i zaposlenik".
- tester: 4: OK, specifikacija 7 kaže "sve zahtjeve članova timova koje vodi"; D2 (zahtjev voditelja ide adminu) je samo odobravanje.
- tester: 5: OK, specifikacija 7: zaposlenik u više timova vidi se na kalendaru svakog svog tima (s filtrom), bez filtra jednom.
- tester: 6: OK, brief: kolege vide samo ime i datume; Q42 za voditelja traži samo tip.
- tester: 7: OK, Q47 (bez tipa i zahtjeva na čekanju).
- tester: 8: OK.
- tester: 9: OK, Q43 (admin bez filtra vidi sve, i korisnike bez tima; filtar po timu).
- tester: 10: OK, Q3 (voditelj je član timova koje vodi), D12 (jedan kalendar s filtrom).
- tester: 11: OK, isto kao L23, točka 3; specifikacija 8 razlikuje 403 i 404.
- tester: 12: OK, R7 i Q23 provjerava backend (T6); frontend samo prikaže poruku.
- tester: 13: OK, Q53.1.
- tester: testovi za T7 su u `VisibilityApiTest.java` i `CalendarApiTest.java`; pokrivaju 3–5, 7, 9–11 izravno.

**Odluka:** Svih trinaest pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; sve se slaže sa specifikacijom 7 (Vidljivost) i sučeljem T7, nema konflikta.
**Gdje:** sučelje T7 (bez izmjena); testovi `VisibilityApiTest.java`, `CalendarApiTest.java`

## L28 · odlučeno (orkestrator) · T7 · specifikacija 12 (Approvals), Q16

**Problem** (developer): Specifikacija ne kaže treba li ekran Approvals uz zahtjev za godišnji pokazati saldo podnositelja (koliko mu dana ostaje). Voditelj smije vidjeti saldo članova svog tima (Q16), a endpoint za to T7 dodaje (`GET /api/users/{id}/balance`), ali specifikacija 12 za Approvals navodi samo popis zahtjeva na čekanju s odobravanjem i odbijanjem. Odluka utječe samo na frontend (T8).

**Odluka:** Ekran Approvals ne pokazuje saldo podnositelja. Endpoint `GET /api/users/{id}/balance` ostaje u API-ju.
**Zašto:** Specifikacija 12 za Approvals navodi samo popis zahtjeva na čekanju s odobravanjem i odbijanjem; 80/20. Saldo na Approvals ekranu je proširenje za README.
**Gdje:** T8 (Approvals ekran bez salda)

## L29 · riješeno · T7 · pretpostavke

**Problem** (developer): Kod za T7 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Vidljivost i kalendar imaju dva controllera: `VisibilityController` (`/api/me/requests`, `/api/requests`, `/api/requests/{id}`, `/api/users/{id}/balance`) i `CalendarController` (`/api/calendar`, `/api/teams`), kao što svaki task ima svoj controller (L26, točka 3).
2. Ključevi `type` i `status` izostaju iz JSON-a jer API piše unos kalendara kroz svoj zapis koji preskače prazne vrijednosti (L27, točka 7). Servisni `CalendarEntry` ostaje bez JSON oznaka (arhitektura: servis ne zna za HTTP).
3. "`viewer` je `owner`" (Vidljivost, red 1) uspoređuje id-jeve korisnika, jer se u servisu ne može koristiti usporedba iz domene (L13).
4. Kad `month` nedostaje ili nije `yyyy-MM` (npr. `2026-13`), ili `teamId` nije broj, status je 400, a `detail` je Springova zadana poruka, kao `id` koji nije broj u T5 (L24, točka 3).
5. Ako prijavljeni korisnik nestane iz baze usred poziva, odgovor je 500, kao u T2–T6 (L24, točka 4).

- tester: OK: 1–5. Novi testovi ne trebaju: 2 već provjeravaju testovi s točnim popisom ključeva (`LIMITED_KEYS`, npr. `ivanSeesColleaguesApprovedWithoutTypeAndStatus_Q47`); 3 API testovi i tako učitavaju korisnika odvojeno (`ivanSeesOwnPendingAndApprovedWithTypeAndStatus_Q41`, `requestDetailOnlyWithFullAccess_brief`); uz 4 `missingOrInvalidMonthIsBadRequest_T7` i `nonNumericTeamIdIsBadRequest_T7` traže samo da `detail` spominje ime polja (`month`, `teamId`), što Springova zadana poruka radi.

**Odluka:** Svih pet pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; nema konflikta sa specifikacijom 7 ni sučeljem T7, a postojeći testovi već pokrivaju 2–4.
**Gdje:** kod T7 (bez izmjena sučelja ni testova)

## L30 · riješeno · T7 · specifikacija 7 (Kalendar, Voditelj), Q42, Q47

**Problem** (reviewer, važno, za testera): Nijedan test ne provjerava što voditelj vidi o timu u kojem je samo član, a ne voditelj. Specifikacija 7 kaže da tamo "vidi isto što i zaposlenik": samo odobrene, samo ime i datume (Q47: kolege bez tipa i bez zahtjeva na čekanju). Ana se dodaje u Mobile u `lukaSeesAnaFullWhenAnaIsInMobile_D2` (`CalendarApiTest.java`) i `lukaSeesAnaWhenAnaIsInMobileButCannotApprove_D2` (`VisibilityApiTest.java`), ali se gleda samo što Luka i Sara vide o Ani, ne što Ana vidi o Sari i Tomu. Zato bi prošao i kod koji voditelju daje pun pristup u svakom timu u kojem je član (Sarin `PENDING` s tipom na Aninom kalendaru, Sarini zahtjevi i saldo u `GET /api/requests` i `GET /api/users/{id}/balance`). Prijedlog: test u kojem je Ana u Mobileu, a Sara ima `PENDING` i `APPROVED`; Ana na kalendaru vidi samo Sarin `APPROVED` s ključevima `LIMITED_KEYS`, a na Sarin zahtjev i saldo dobiva 403.

- tester: slažem se; dodana dva testa u kojima je Ana u Mobileu: na kalendaru vidi samo Sarin odobreni, bez tipa i stanja, a Sarine zahtjeve (i odobrene) ni saldo ne vidi (403, nema je u `GET /api/requests`).

**Odluka:** Dodani testovi da voditelj u timu u kojem je samo član vidi isto što i zaposlenik.
**Zašto:** Specifikacija 7 (voditelj za ostale timove u kojima je član vidi isto što i zaposlenik), Q47 (kolege bez tipa i bez zahtjeva na čekanju); bez testa bi prošao kod koji voditelju daje pun pristup u svakom svom timu.
**Gdje:** `CalendarApiTest.java`: `anaInMobileSeesSaraOnlyApprovedWithoutTypeAndStatus_Q47`; `VisibilityApiTest.java`: `anaInMobileCannotSeeSarasRequestsOrBalance_Q47`

## L31 · riješeno · T8 · pretpostavke

**Problem** (developer): Sučelje T8 radi po pretpostavkama koje specifikacija ne kaže. Neka se potvrde ili isprave:

1. Frontend ništa ne provjerava sam (prazna polja, datumi, komentar kod odbijanja, je li odobreni zahtjev počeo); sve šalje backendu i prikaže `detail` greške (arhitektura, sloj 1: frontend ne odlučuje ništa; specifikacija 12: ekran prikazuje poruku greške).
2. Token je u `localStorage` (`timeoff.token`). Tko je prijavljen i koje uloge ima, frontend čita s `GET /api/me` kod svakog otvaranja stranice, ne sprema ga, pa izmjena u bazi vrijedi odmah (D6: korekcije se rade izravno u bazi).
3. Odjava samo zaboravi token (T2: odjava ne postoji na serveru). Na 401 iz bilo kojeg poziva osim prijave token se briše i korisnik ide na `/login`, bez poruke.
4. Poveznicu Approvals vide samo voditelji i admini (specifikacija 12). Tko ručno otvori `/approvals`, dobije praznu listu iz backenda, ne posebnu poruku ni preusmjeravanje.
5. Na My time off gumb Cancel postoji za zahtjeve na čekanju i odobrene; otkazivanje tuđih zahtjeva (voditelj, admin) nema ekran (vidi L33).
6. Kalendar prikazuje samo dane s barem jednom odsutnošću, od ponedjeljka do petka, unutar odabranog mjeseca; mjesec se mijenja gumbima prethodni/sljedeći, početni je tekući mjesec po satu preglednika.
7. Datumi se prikazuju kako ih vrati API (`yyyy-MM-dd`); tip i stanje engleskim riječima (Q54: sve na engleskom).
8. Stupac Comment na My time off pokazuje komentar odluke, a ako ga nema, razlog otkazivanja.
9. Frontend zove API relativnim putem (`/api/...`), a Next.js ga prosljeđuje backendu, pa backend ne treba CORS. Adresa backenda je varijabla `API_URL` (za Docker u T9).
10. Frontend testovi su u `frontend/tests/` (Vitest, React Testing Library, jsdom) i ne trebaju backend: odgovore daje lažni `fetch`. Tester za T8 zato treba vidjeti i `frontend/tests/`, ne samo `backend/src/test/` (za Rajka).
    - orkestrator: da, tester čita i piše `frontend/tests/`; `frontend/src/` ne otvara, isto kao `backend/src/main`.
11. `@types/node` je podignut s 20 na 22, jer ga Vitest traži, a lokalni Node je 22.

- tester: 1: OK, arhitektura sloj 1 i specifikacija 12 (ekran prikazuje poruku greške); test: prazna forma za prijavu i za novi zahtjev šalje prazne vrijednosti i prikaže `detail`.
- tester: 2: OK, D6 (izmjena u bazi vrijedi odmah); test: `AppShell` zove `GET /api/me` kod svakog otvaranja.
- tester: 3: OK, T2 (odjava ne postoji na serveru); test: `Log out` ne zove backend, 401 briše token i ide na `/login` bez `alert`.
- tester: 4: OK, specifikacija 12 i specifikacija 7 (skrivanje u frontendu se ne računa, API provodi pravila).
- tester: 5: OK, R6, R7 (vlasnik otkazuje na čekanju i odobren); L33 (tuđi zahtjevi samo u API-ju).
- tester: 6: OK, R10 (vikendi se ne broje), specifikacija 12 (popis po danima); test: prijelaz godine kod prethodnog i sljedećeg mjeseca.
- tester: 7: OK, Q54 (sve na engleskom).
- tester: 8: OK, specifikacija 7 (zaposlenik vidi komentar odluke); test: komentar odluke ima prednost pred razlogom otkazivanja.
- tester: 9: OK, nema utjecaja na testove (lažni `fetch` gleda relativan put).
- tester: 10: OK, testovi su u `frontend/tests/`, ne trebaju backend.
- tester: 11: OK, nema utjecaja na testove.

**Odluka:** Svih jedanaest pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; sve se slaže sa specifikacijom 7 i 12, arhitekturom (sloj 1: frontend ne odlučuje) i odlukama L28, L32, L33.
**Gdje:** sučelje T8 (bez izmjena); testovi u `frontend/tests/`

## L32 · odlučeno (orkestrator) · T8 · Q46, Q30, Q31, specifikacija 7

**Problem** (developer): Specifikacija 7 kaže da zaposlenik vidi svoje zahtjeve "uključujući tko je odlučio i tko je otkazao" (Q46: tko je odlučio; Q30, Q31: tko je otkazao). API (T5, T6) za to vraća samo broj korisnika (`decidedById`, `cancelledById`), ne ime, pa ekran My time off ne može napisati "Approved by Ana". Pitanje: treba li My time off prikazati ime onoga tko je odlučio ili otkazao? Ako da, backend bi u `<Request>` dodao `decidedByName` i `cancelledByName` (kao `ownerName` u T5); ako ne, ekran to ne prikazuje, a podatak ostaje u API-ju. Dok se ne odluči, sučelje T8 to ne prikazuje.

**Odluka:** Da. My time off prikazuje ime onoga tko je odlučio i tko je otkazao. Backend u `<Request>` dodaje `decidedByName` i `cancelledByName` (null kad ih nema), kao `ownerName` u T5.
**Zašto:** Specifikacija 7 izričito kaže da zaposlenik vidi tko je odlučio i tko je otkazao (Q46, Q30, Q31); broj korisnika to ne pokazuje. Promjena je mala i u postojećem `<Request>`.
**Gdje:** sučelje T7 `<Request>` (dva nova polja), sučelje T8 My time off; backend test i frontend test (tester)

## L33 · zamijenjeno s L39 · T8 · specifikacija 4, specifikacija 12, Q30, Q31, Q32, D14

**Problem** (developer): Voditelj i admin smiju otkazati tuđi zahtjev, uz razlog (specifikacija 4; Q30, Q31, Q32: voditelj i admin otkazuju; D14: tko smije odobriti, smije i otkazati). Specifikacija 12 ne daje im za to ekran: Approvals ima samo zahtjeve na čekanju s odobravanjem i odbijanjem, a My time off samo vlastite. Pitanje: treba li frontend ponuditi otkazivanje tuđih zahtjeva (npr. odobrenih) ili to ostaje samo u API-ju (`POST /api/requests/{id}/cancel`)? Dok se ne odluči, sučelje T8 ga nema (80/20).

**Odluka:** Ne. Otkazivanje tuđih zahtjeva ostaje samo u API-ju (`POST /api/requests/{id}/cancel`); frontend ga nema.
**Zašto:** Specifikacija 12 za to ne daje ekran; 80/20. Ekran za to je proširenje za README.
**Gdje:** sučelje T8 (bez promjene)

## L34 · riješeno · T8 · pretpostavke

**Problem** (developer): Kod za T8 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Adresa backenda (`API_URL`) čita se kad se frontend gradi (`npm run build`), ne kad se pokrene; Next.js tada upiše proslijeđivanje `/api` u build. Za Docker (T9) `API_URL` zato mora biti zadan već kod gradnje slike.
2. Dok se ne učita `GET /api/me`, `AppShell` ne prikazuje ništa (ni navigaciju ni ekran); ako `GET /api/me` padne s greškom koja nije 401, prikaže se samo `alert` s `detail`.
3. Kalendar piše `No absences this month.` i kad unosi postoje, ali u tom mjesecu padaju samo na subotu i nedjelju (npr. zahtjev od petka do ponedjeljka preko granice mjeseca), jer se vikendi ne prikazuju (R10).
4. Kad se na Approvals odluči jedan zahtjev, komentari upisani u druge retke ostaju; na My time off otvoreno polje `Cancel reason` zatvori se tek nakon uspješnog otkazivanja ili klika na `Keep request`.
5. Mrežna greška (backend ne radi) prikaže se kao `alert` `Cannot reach the server` na svakom ekranu, i na prijavi.
6. Fontovi s Google Fontsa (iz predloška Next.js) su izbačeni, jer bi `npm run build` bez interneta pao; koristi se sistemski font.

- tester: 1: OK, tiče se T9 (Docker); frontend testovi ne ovise o `API_URL` (lažni `fetch` gleda relativan put).
- tester: 2: OK, sučelje T8 (Greška: `detail` u `alert`); test: `GET /api/me` s 500 prikaže samo `alert`, bez navigacije i ekrana.
- tester: 3: OK, R10 (vikendi se ne prikazuju); test ne treba.
- tester: 4: OK, sučelje T8 ne traži drugačije; test ne treba.
- tester: 5: OK, sučelje T8 (API klijent: mrežna greška daje `Cannot reach the server`); test: prijava kad backend ne radi prikaže tu poruku u `alert`.
- tester: 6: OK, nema utjecaja na testove.

**Odluka:** Svih šest pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; sve se slaže sa specifikacijom 12 i sučeljem T8, nema konflikta. Točke 2 i 5 su ponašanje koje nijedan test nije provjeravao, pa su dodani testovi.
**Gdje:** kod T8 (bez izmjena sučelja); `frontend/tests/AppShell.test.tsx`: `other error from /api/me shows only the alert, without navigation or screen (L34.2)`; `frontend/tests/LoginScreen.test.tsx`: `network error shows Cannot reach the server (L34.5)`

## L35 · riješeno · T8 · pravila kodiranja (Stil 1)

**Problem** (reviewer, važno, za developera): Funkcija koja slaže radne dane mjeseca za kalendar ima petlju, a u petlji `if`, dakle dvije razine gniježđenja. Mjesto: `workingDaysOfMonth` u `frontend/src/lib/month.ts` (redovi 34–40, `for` pa `if (!isWeekend)`). Pravilo: "Najviše jedna razina gniježđenja. Ako bi trebalo dvije, razbij kod u međukorake ili rane izlaze." Npr. prvo popis svih dana mjeseca, pa `filter` koji izbaci vikende.

- developer: slažem se; sada je prvo popis svih dana mjeseca, pa `filter` izbaci vikende (mala funkcija `isWeekend`), bez petlje s `if`-om.

**Odluka:** Petlja s `if`-om zamijenjena popisom dana i `filter`-om; provjera vikenda je u svojoj funkciji.
**Zašto:** Pravilo "Najviše jedna razina gniježđenja. Ako bi trebalo dvije, razbij kod u međukorake ili rane izlaze."
**Gdje:** `frontend/src/lib/month.ts`: `workingDaysOfMonth`, `isWeekend`

## L36 · riješeno · T8 · pravila kodiranja (Stil 2)

**Problem** (reviewer, važno, za testera): U `frontend/tests/MyTimeOffScreen.test.tsx` poziv je na više mjesta ugniježđen tri razine duboko, pa se kod debugiranja ne vidi koji je element pronađen: `within(form())` stoji unutar `fireEvent.change(...)`, `user.type(...)`, `user.click(...)` ili `expect(...)`, npr. `fireEvent.change(within(form()).getByLabelText("Start date"), ...)`. Mjesta: redovi 189–192, 200–203, 216, 233–236 i 239–241. Isti uzorak kao u L20. Pravilo: "Ugniježđeni pozivi razbijaju se u varijable s imenom." Npr. polja forme prvo u varijable (`startDate`, `reason`, `submit`), pa tek onda radnja ili provjera.

- tester: slažem se; polja forme sada daje pomoćna funkcija `formFields()` kao varijable s imenom (`type`, `startDate`, `endDate`, `reason`, `submit`), pa radnja i provjera dobivaju gotov element; popravljena su sva navedena mjesta i `submit` u testu "error stays until the next action succeeds".

**Odluka:** Polja forme izdvojena su u varijable s imenom prije radnje ili provjere.
**Zašto:** Pravilo "Ugniježđeni pozivi razbijaju se u varijable s imenom."; kod debugiranja se vidi koji je element pronađen.
**Gdje:** `frontend/tests/MyTimeOffScreen.test.tsx`: `formFields()` umjesto `form()`; testovi u `new request form (T4)`

## L37 · riješeno · T8 · sučelje T8 (Rute, Komponente)

**Problem** (reviewer, važno, za developera pa testera): Početna adresa `/` nema nijedan test. Sučelje T8 (Rute) kaže da ona samo preusmjerava: s tokenom na `/my-time-off`, bez tokena na `/login`. Ako se to pokvari, tko otvori aplikaciju na početnoj adresi vidi praznu stranicu. Tester to ne može testirati jer sučelje (Komponente) ne navodi komponentu za `/`; u kodu je to `HomeRedirect` (`frontend/src/components/HomeRedirect.tsx`). Prijedlog: developer dopiše `HomeRedirect` u tablicu Komponente, a tester doda test za oba slučaja (s tokenom i bez njega).

- developer: slažem se; `HomeRedirect` je dopisan u sučelje T8, tablica Komponente: s tokenom `router.replace("/my-time-off")`, bez tokena `router.replace("/login")`, bez poziva API-ja. Test je na testeru.
- tester: slažem se; dodana su dva testa: s tokenom ide na `/my-time-off`, bez tokena na `/login`, u oba slučaja bez poziva API-ja.

**Odluka:** `HomeRedirect` je u sučelju T8 (Komponente) i ima test za oba slučaja.
**Zašto:** Sučelje T8 (Rute): `/` samo preusmjerava; bez testa bi pokvareno preusmjeravanje ostavilo praznu početnu stranicu.
**Gdje:** sučelje T8, Komponente (`HomeRedirect`); `frontend/tests/HomeRedirect.test.tsx`: `with token goes to My time off without calling the API (L37)`, `without token goes to login without calling the API (L37)`

## L38 · riješeno · T9 · pretpostavke

**Problem** (developer): Docker i README za T9 rade po pretpostavkama koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Backend slika se gradi Mavenom iz službene slike `maven:3.9-eclipse-temurin-21`, ne s `./mvnw`, jer je `maven-wrapper.jar` u `.gitignore`. Testovi se kod gradnje slike ne pokreću (`-DskipTests`): trebaju Docker (Testcontainers), a pokreću se zasebno s `./mvnw test`.
2. Backend dobiva bazu preko `SPRING_DATASOURCE_URL`, `_USERNAME`, `_PASSWORD`; Spring ih čita prije postavki iz `application.properties` (`DB_URL` i ostale), pa se `application.properties` ne mijenja.
3. Backend je izložen i izravno na portu 8080 (uz frontend na 3000), da se API može zvati izravno kao razni korisnici (brief: "We will call your API directly"). Baza ostaje izložena na 5432 kao i prije.
4. Frontend čeka samo da backend krene, ne da bude zdrav: proslijeđivanje `/api` ide tek kod svakog poziva, a slika backenda nema `curl` za provjeru zdravlja.
5. `API_URL` je build arg (`http://backend:8080`) i postavlja se i kod pokretanja, za svaki slučaj (L34, točka 1: čita se kod gradnje).
6. `TZ: Europe/Zagreb` zadan je i backendu i frontendu, ne samo bazi (D8: vremenska zona se postavlja varijablom `TZ`; "danas" i tekuća godina čitaju se u backendu).
7. `.env.example` ima vrijednost-primjer koju treba zamijeniti; `.env` je u root `.gitignore`.
8. README spominje korekcije u bazi (D6) samo kao odluku, bez SQL postupka.

- orkestrator: dodano u README (odjeljak Data corrections).
- tester: OK: 1–8. T9 se provjerava ručno, ne automatskim testovima. Uz 4: odmah nakon `docker compose up` prvi pozivi mogu pokazati `Cannot reach the server` dok backend ne krene; kod ručne provjere to nije greška.

**Odluka:** Svih osam pretpostavki vrijedi.
**Zašto:** Developer i tester se slažu; nema konflikta.
**Gdje:** T9 (Docker, README); bez izmjena sučelja ni testova

## L39 · riješeno · T8.1 · Q30, Q31, Q32, D14, L33

**Problem** (orkestrator): L33 je otkazivanje tuđih zahtjeva ostavio samo u API-ju. Naručitelj je na Q30 (voditelj otkazuje tuđi zahtjev), Q31 (admin otkazuje) i Q32 (povlačenje danog odobrenja) odgovorio "da", pa korisnik to mora moći napraviti u aplikaciji. Odluka L33 se mijenja.

**Odluka:** Ekran Approvals dobiva drugu tablicu "Approved requests": odobreni zahtjevi koje prijavljeni smije otkazati, a koji još nisu počeli. Svaki red ima polje "Cancel reason" i gumb "Cancel". Razlog je obavezan (D3), a provjerava ga backend. Pravila ne idu u frontend: tko smije i je li zahtjev počeo odlučuje backend (403 ili 409 se prikaže kao greška).
**Zašto:** Q30, Q31, Q32: naručitelj to traži; backend to već podržava (T6).
**Gdje:** sučelje T8.1, ApprovalsScreen

- developer: postojeći API takav popis ne daje (`GET /api/requests` vraća i zahtjeve koji su počeli i one koje korisnik ne smije otkazati). orkestrator: novi backend endpoint koji koristi postojeći `CancelPolicy` i provjeru početka iz T6, bez ponavljanja pravila. T8.1 zato dobiva i slojeve 3 API i 4 Servisi (taskovi.md).

## L40 · riješeno · T8.1 · pretpostavke

**Problem** (developer): Sučelje T8.1 radi po pretpostavkama koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Vlastiti odobreni zahtjevi nisu na popisu `GET /api/approvals/cancellable`, ni za admina; svoje zahtjeve korisnik otkazuje na My time off (T8; R6, R7: vlasnik otkazuje svoj zahtjev).
2. "Nije počeo" znači da je `startDate` poslije danas; zahtjev koji počinje danas je počeo i nije na popisu (Q23: zahtjev koji počinje danas je već započeo).
3. Endpoint je pod `/api/approvals/...`, uz popis za odobravanje (T5), a ne pod `/api/requests`.
4. Tablica `Approved requests` nema stupac `Reason`, nego `Approved by` (`decidedByName`, L32): razlog zahtjeva ne treba za otkazivanje, a tko je odobrio treba.
5. Obje tablice na Approvals dobivaju naslov razine 2 (`Pending requests`, `Approved requests`). Za `Pending requests` to je mala dopuna T8 koja ne mijenja ništa što testovi T8 traže.
6. Ako pri otvaranju ekrana padne jedan od dva poziva (`getApprovals`, `getCancellableRequests`), prikaže se `alert` s `detail`, kao u T8.

- tester: OK: 1–4. Konflikt: 5 i 6.
  - 5: dopuna T8 mijenja što testovi T8 traže. Problem nisu naslovi, nego drugi poziv pri otvaranju ekrana. Postojeći test `lists pending requests in API order without loading balances (L28)` traži da ekran zove samo `/api/approvals`, pa će pasti. Ostali testovi u `ApprovalsScreen.test.tsx` ne daju odgovor za `/api/approvals/cancellable`, pa ekran prikaže `alert`, a test `Reject without comment goes to the backend and shows its 400` tada nađe krivi `alert`. Te testove mijenjam ja (tester): svaki dobiva odgovor i za drugi popis. Developer ne mijenja ništa; samo neka točka 5 ne kaže "ne mijenja ništa što testovi T8 traže".
  - 6: nije rečeno što se vidi kad padne samo jedan poziv. Pitanje: prikaže li se tablica koja se učitala (npr. `Pending requests`) uz `alert`, ili samo `alert`? I kad padnu oba poziva: jedan `alert` ili dva? Pretpostavljam: tablica koja se učitala ostaje, a `alert` je jedan, s `detail` prve greške. Neka developer potvrdi ili ispravi.

- developer: 5: potvrđujem; rečenica "ne mijenja ništa što testovi T8 traže" bila je kriva. Drugi poziv pri otvaranju mijenja što testovi T8 traže, pa ih tester prilagođava; developer ne mijenja ništa. 6: potvrđujem testerovu pretpostavku; tablica koja se nije učitala ne prikazuje se, ni tekst za prazan popis. Dopisano u sučelje T8.1, Approvals.

**Odluka:** Pretpostavke 1–4 vrijede. Točka 5 je ispravljena: testove T8 za `ApprovalsScreen` prilagođava tester (odgovor i za `/api/approvals/cancellable`). Točka 6: tablica koja se učitala ostaje, tablica koja se nije učitala ne prikazuje se; `alert` je jedan, s `detail` prve greške, i kad padnu oba poziva.
**Zašto:** Developer i tester se slažu. Jedan `alert` drži pravilo iz T8 (greška u elementu s `role="alert"`), a učitani podaci ostaju korisni.
**Gdje:** sučelje T8.1, Approvals; `ApprovalsScreen.test.tsx` (tester)

## L41 · riješeno · T8.1 · sučelje T8.1 (Approvals), L40, Q30, Q32

**Problem** (tester, za developera): Dvije stvari na ekranu Approvals nisu rečene:

1. "Prva greška" kad padnu oba poziva. Ekran oba popisa traži odjednom, pa nije jasno koja je greška prva: ona od poziva koji je prvi poslan, ili od onog koji je prvi odgovorio? Pretpostavljam: greška iz `getApprovals()`, jer je taj poziv prvi naveden (sučelje T8.1, "Pri otvaranju zove `getApprovals()` i `getCancellableRequests()`"). Dok se ne potvrdi, test `both lists failing show one alert and no tables (L40.6)` provjerava samo da je `alert` jedan i da ima `detail` jedne od dvije greške.
2. Što se ponovo učita nakon odobrenja. T8 kaže: nakon `Approve` ponovo `getApprovals()`. Odobreni zahtjev koji nije počeo sad pripada i tablici `Approved requests` (Q32: povlačenje danog odobrenja). Ako se `getCancellableRequests()` ne zove ponovo, voditelj ga ne vidi dok ne otvori ekran ponovo. Pretpostavljam: nakon `Approve` i `Reject` ponovo se zovu oba popisa, kao nakon `Cancel`. Neka developer potvrdi ili ispravi; test za to još ne postoji.

- developer: potvrđujem 1 i 2. Uz 1: ekran pričeka oba odgovora pa bira grešku po redoslijedu iz sučelja, ne po tome koji poziv prvi odgovori, pa test ne ovisi o vremenu. Dopisano u sučelje T8.1, Approvals.
- tester: OK. Test `both lists failing show one alert with the approvals error and no tables (L40.6, L41.1)` sada traži grešku iz `getApprovals()`. Dodana su tri testa, jer ponašanje nijedan test nije provjeravao: `approvals error wins even when the cancellable error answers first (L41.1)` (odgovor na `getApprovals()` stiže kasnije), `Approve reloads both lists and the approved request joins Approved requests (L41.2, Q32)`, `Reject reloads both lists (L41.2)`.

**Odluka:** 1: kad padnu oba poziva, `alert` ima `detail` greške iz `getApprovals()`. 2: nakon uspješnog `Approve` i `Reject` ponovo se zovu oba popisa, `getApprovals()` i `getCancellableRequests()`, kao nakon `Cancel`.
**Zašto:** Developer i tester se slažu. Redoslijed iz sučelja je uvijek isti, a vrijeme odgovora nije. Odobreni zahtjev koji nije počeo odmah se smije otkazati (Q32: povlačenje danog odobrenja), pa ga voditelj treba vidjeti bez ponovnog otvaranja ekrana.
**Gdje:** sučelje T8.1, Approvals; `ApprovalsScreen.test.tsx` (tester)

## L42 · riješeno · T8.1 · pretpostavke

**Problem** (developer): Kod za T8.1 radi po još nekoliko pretpostavki koje nigdje nisu izrečene. Neka se potvrde ili isprave:

1. Kad ponovno učitavanje nakon `Approve`, `Reject` ili `Cancel` padne, tablica koja se nije učitala zadržava stare podatke, ne nestaje. Sučelje T8.1 (L40) kaže samo što se vidi pri otvaranju ekrana, kad starih podataka još nema.
2. Naslov i tablica su zajedno u `<section>` bez imena, pa to nije `region` i test ga ne nalazi po ulozi; tablice se i dalje traže po imenu (`Pending requests`, `Approved requests`).
3. `Comment` i `Cancel reason` pamte se po retku i ostaju upisani kad akcija padne (kao upisane vrijednosti forme u T8).
4. Gumb `Cancel` u tablici `Approved requests` ima isti sporedni stil kao `Reject`.

- tester: OK: 1–4. Za 1 i 3 dodana su četiri testa, jer ponašanje nijedan test nije provjeravao: `failed reload after Cancel keeps the old approved rows and shows the alert (L42.1)`, `failed reload after Approve keeps the old pending rows and shows the alert (L42.1)`, `failed Cancel keeps the typed reason in its row (L42.3)`, `failed Reject keeps the typed comment in its row (L42.3)`. Za 2 testovi već traže tablice po imenu, a naslove po ulozi `heading`. Za 4 test ne treba.

**Odluka:** Sve četiri pretpostavke vrijede.
**Zašto:** Developer i tester se slažu; nema konflikta. Stari podaci i upisane vrijednosti ostaju kao u T8 (upisane vrijednosti forme ostaju kad akcija padne).
**Gdje:** kod T8.1 (bez izmjena sučelja); `frontend/tests/ApprovalsScreen.test.tsx`: četiri testa s oznakama L42.1 i L42.3

## L43 · otvoreno · T6 · R4

**Problem** (reviewer testera, manje važno, za testera): Dva testa nose oznaku pravila koje ne provjeravaju. `finishedRequestCannotBeCancelled_R4` (`CancelApiTest.java:375`) i `approverCannotCancelFinishedRequest_R4` (`CancelApiTest.java:386`) provjeravaju da se odbijeni ili otkazani zahtjev ne može otkazati. R4 govori o odobravanju i odbijanju, ne o otkazivanju: "Only pending requests can be approved or rejected." (`brief.md:72`; `specifikacija.md:59`: "Odobrava se i odbija samo zahtjev na čekanju"). Otkazivanje završenog zahtjeva je provjera 6 iz sučelja T6 (`sucelje.md:416`: "Zahtjev je `REJECTED` ili `CANCELLED` | 409"), koja nema oznaku pravila. Testovi očekuju točno ono što kaže sučelje; kriva je samo oznaka. Prijedlog: oznaka `_T6`, kao ostale provjere iz sučelja u istoj klasi, i R4 izbaciti iz `Pokriva` (`CancelApiTest.java:38`). Ista oznaka za otkazivanje postoji i u T1 (`domain/TimeOffRequestTest.java:146`, `cancelledRequestCannotChangeAgain_R4`); to je izvan T6, samo napomena.

## L44 · otvoreno · T6 · Q30, Q32

**Problem** (reviewer testera, manje važno, za testera): Test nosi oznaku Q30 (voditelj otkazuje tuđi zahtjev), a pola njegovih slučajeva pripada Q32 (voditelj ili admin povlači dano odobrenje). `anaCancelsIvansRequestWithReason_Q30` (`CancelApiTest.java:197–199`) vrti se za `PENDING` i `APPROVED`. Specifikacija ih dijeli: na čekanju Q30, Q31, a odobren koji nije počeo Q32 (`specifikacija.md:70`: "Odobren, nije počeo | Otkazuje, razlog obavezan (Q32, D3, D14)"). Testovi s oznakom `_Q32` (`approverCancelsApprovedRequestStartingTomorrow_Q32`, `CancelApiTest.java:226`) gledaju samo status, ne `cancelReason` i `cancelledById`, pa tu provjeru za odobrene radi samo test s oznakom Q30. Prijedlog: razdvojiti u dva testa, `PENDING` s oznakom `_Q30` i `APPROVED` s oznakom `_Q32`, s istim provjerama.

## L45 · otvoreno · T6 · metodologija

**Problem** (reviewer testera, važno, za testera): Zaglavlje kaže da su ispunjene sve kombinacije, a testovi pokrivaju samo dio. `Mreža` (`CancelApiTest.java:30–35`) piše uloga × stanje × početak × razlog, a metodologija pod tim podrazumijeva sve ostvarive kombinacije (`TESTING_METHODOLOGY.md:19`: "N×N×N and beyond: all viable combinations"). `Ne pokriva` (`CancelApiTest.java:39–42`) ne kaže da kombinacije nedostaju. Primjeri kombinacija bez testa:

1. Vlasnik voditelj (Ana) i vlasnik admin (Eva) samo na `PENDING` (`:131`, `:141`); nema odobrenog koji počinje sutra ili danas, ni odbijenog.
2. Voditelj preko drugog tima (Luka, Ivan u Mobileu) samo na `PENDING` s razlogom (`:277`); nema odobrenog ni slučaja bez razloga.
3. Admin nijednom na `REJECTED` ili `CANCELLED` (`:386` ide samo s Anom).
4. Razlog `null`, 500 i 501 znak samo kad otkazuje voditelj (`:289`, `:427`, `:437`), nikad kad otkazuje vlasnik.

Prijedlog: ili popuniti mrežu, ili u `Mreža` napisati koje su kombinacije odabrane (npr. "parovi"), a ostale navesti u `Ne pokriva`.

## L46 · otvoreno · T6 · Q23, D8, Q24

**Problem** (reviewer testera, manje važno, za testera): Zaglavlje `CancelDayBoundaryApiTest` ne navodi pravilo koje test zapravo provjerava. Test postavlja sat na 23:30 UTC, što je u Zagrebu već sljedeći dan, pa provjerava u kojoj se zoni računa "danas" (`CancelDayBoundaryApiTest.java:24`: "danas je datum u Zagrebu, ne u UTC-u"). To pravilo dolazi iz D8 (vremenska zona se postavlja varijablom `TZ`, Europe/Zagreb), Q24 (u kojoj se zoni računa "danas") i sučelja T3, Današnji datum (`sucelje.md:200`: "Datum se računa u zoni sata (`LocalDate.now(clock)`), ne u zoni računala"). `Izvori` (`:26`) navode samo specifikaciju 4 i sučelje T6, a `Pokriva` (`:29`) samo Q23 (zahtjev koji počinje danas je počeo). Prijedlog: u `Izvori` dodati sučelje T3 (Današnji datum), a u `Pokriva` i u komentar odjeljka (`:68`) dodati D8 i Q24.
