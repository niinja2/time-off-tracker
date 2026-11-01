# Za procjenu

Prijedlozi koji još nisu odlučeni. Kad se odluče, idu u README, taskove ili log; ovdje se brišu.

## Reviewi: sve na jednom mjestu

Ovaj odjeljak je ulaz za sve vrste reviewa. Detalji ostaju u svojim fajlovima; kad se nešto promijeni, ovdje se ažurira popis.

**Vrste reviewa**

| Vrsta | Što gleda | Kada | Gdje je opisano |
| --- | --- | --- | --- |
| Reviewer po tasku | kod i testove jednog taska: linija, funkcija, cjelina | korak 5 kruga po tasku | [way-of-working.md](way-of-working.md) (Krug po tasku, prompt Reviewer), [taskovi.md](taskovi.md) |
| Reviewer testera | konzistentnost testova sa specifikacijom, sučeljem i metodologijom (oznake, zaglavlja, očekivanja), ne pokrivenost | nakon testera, prije developera (prijedlog) | primjer: L43–L46 u [coding logu](zajednicko/coding-log.md); prompt još nije u repou |
| Integrator | kod sa samim sobom: isto pravilo na više mjesta, slojevi, copy-paste, mrtav kod | cijela baza, povremeno | metoda ispod; nalazi lokalno (`llm-shiva/nalazi.md`) |
| Shiva | projekt prema briefu, odgovorima naručitelja, specifikaciji i README-u | prije predaje, nakon većih promjena | nalazi lokalno (`llm-shiva/nalazi.md`); otvoreni nalazi u odjeljku "Neovisni review" ispod |
| Kontinuirani review | pojednostavljenje koda bez promjene ponašanja | nakon taska (diff), cijela baza svakih 3–5 taskova | odjeljak "Kontinuirani review" ispod |
| Kršilica | napadi na aplikaciju iz specifikacije | prijedlog | odjeljak "Testiranje" ispod |

**Metoda za review cijele baze** (integrator; za shivu hipoteza). Svaki korak je zasebna, svježa sesija:

1. **Haiku: inventar.** Neutralni popis onoga što se reviewa (fajlovi, pravila, testovi, odjeljci), bez ocjena.
2. **Haiku: širina.** n sesija, svaka s dvije srodne uske leće (leća traži jednu vrstu problema). Leće predlaže vlasnik ili LLM, vlasnik odobrava.
3. **Sonnet medium: dubina.** Jedan širok prompt, za ono između leća i ono što traži pravila i dokumente. Alternativa: Opus 5.5 medium (najbolji jedan širok prompt, dvaput skuplji).
4. **Sonnet medium: sudac.** Nova sesija, samo čita: provjeri dokaze (`fajl:redak`), spoji ponavljanja, mapira na poznate nalaze, nove dopiše.

Po potrebi Opus presudi sporne. Pravila: svaki nalaz ima `fajl:redak` i citat; u prompt ide popis svjesno izostavljenog; nova vrsta propusta znači novu leću, ne jači model; što može stroj, rade alati. Trošak integrator reviewa po ovoj metodi je oko $2.

Na čemu se temelji (jedno izvođenje po varijanti, 30. 10.–1. 11. 2026.): Haiku s 5 parova leća 39 od 56 nalaza i 8 od 9 ključnih za $0,47; isto u 10 zasebnih sesija 32–33 od 56 za $0,48 (parovi bolji); Sonnet medium jednim promptom 9 od 9 ključnih; Sonnet medium kao sudac slaže se s Opusom (32 prema 33 od 56). Shiva: Sonnet s 4 leće 28 od 40, Opus 5.5 high jedini nalazi najteže sukobe s naručiteljem.

**Gdje se još spominje**

- [README.md](../README.md): "How this was built" (korak 5, reviewer) i "Agreement is not proof" (sljedeći korak: različiti modeli i kršilica).
- [atomarna-pitanja.md](zajednicko/atomarna-pitanja.md) Q53.8: primjer načina rada s dva reviewera.
- Lokalno, nije u repou (`llm-shiva/`): `metoda-reviewa.md` (ova metoda s tablicom), `usporedba.md` i `zakljucci.md` (usporedba modela), `nalazi.md` (svi nalazi I1–I56, S1–S43), `adresar-mapa-cuvari.md`, sirovi izlazi i skripte za tokene.

**Za uskladiti**

- `taskovi.md` (korak 5) kaže "Revieweri", a way-of-working ima jednog reviewera.
- Leće u "Kontinuirani review" (korak 3) su četiri široke; metoda koristi uske leće u parovima.
- `zakljucci.md` i `usporedba.md` nemaju izvođenje s 10 zasebnih leća ni Sonneta kao suca.
- Otvoreno za mjerenje: više izvođenja po varijanti, ubačeni bugovi (koliko metoda propušta), može li Haiku biti sudac, metoda na shivi.

## README

- **Testing:** brojke (backend 443, frontend 86), E2E lanci (5 lanaca, 41 korak, 345 provjera, Haiku 345/345 nakon jedne ispravke scenarija), ograničenja (dokaz prepisuje LLM; datumi vrijede do 2026-12-08), poznata rupa (~460 testova sad, ~950 po metodologiji).
- **Extensions:** dodati UX sitnice iz E2E: razlog otkazivanja se ne vidi kad postoji komentar odluke; `/login` se otvara i kad si prijavljen.

## Testiranje

- **Playwright:** prevesti E2E lance u kod. Deterministička provjera po ćeliji, sirovi dokaz (tekst, screenshot), pokretanje skriptom ili cronom.
- **Pune mreže po metodologiji** (procjena ~515 novih slučajeva) i **mutation testing** (PIT na backendu, Stryker na frontendu). Za PIT prvo brzi unit testovi za `ApprovalPolicy`, `CancelPolicy`, `VisibilityPolicy`, `WorkingDays`.
- **Kršilica:** LLM uloga koja čita samo specifikaciju i smišlja napade (čudni redoslijedi, zlouporaba ovlasti), izvršava ih kroz API, nalaze piše u log. Uz to property-based testovi (jqwik, invarijante tipa "saldo nikad ispod 0") i API fuzzing.
- **Neovisni review** drugim modelom ili više njih: napravljeno, vidi "Reviewi: sve na jednom mjestu".

## Kontinuirani review (pojednostavljenje koda)

1. Deterministički signali: gniježđenje, duljina metode, kompleksnost (PMD, Checkstyle, ESLint), duplikati (CPD, jscpd), slojevi (ArchUnit).
2. Review po jedinici (paket ili modul), prvo vruće točke iz koraka 1.
3. Leće: jednostavnost, arhitektura, specifikacija (kod bez pravila R/Q/D), konzistentnost.
4. Kirurški prijedlozi u log, jedan po unosu; ponašanje se ne mijenja, svi testovi moraju proći.
5. Ritam: diff nakon svakog taska; cijela baza svakih 3–5 taskova; metrike smiju samo padati ili stajati.

## Logovi za dugi projekt

- Slojevi: sirova arhiva transkripata, log po danu, sažetak po danu, sažetak po tasku ili mjesecu.
- Živi dokumenti (specifikacija, sučelje, arhitektura) su istina; povijest samo za "zašto".
- Pretraga: SQLite FTS5 (inverted index s BM25), po potrebi i embeddingi; dohvat na zahtjev, ne unaprijed.
- Budžet veličine po aktivnom dokumentu; višak u arhivu.

## Dokumentacija

- Previše fajlova: procijeniti što se može spojiti ili arhivirati (npr. duplicirani "Krug po cjelini" u taskovi.md i way-of-working.md).

## Neovisni review (Shiva, DeepSeek), otvoreno

Riješeno prije slanja: dijagrami (Q51), izbor biblioteka u README-u, Q18 u D5, D17 za L39, brojke i naredbe u README-u, E2E 344/345.

- **Otkazivanje tuđeg zahtjeva na čekanju kroz UI** (Q30, Q31, D14): API to radi, ekran ne. T8.1 je pokrio samo odobrene.
- **Razlog otkazivanja se ne vidi** kad postoji komentar odluke (My time off).
- **R5 u API sloju**, D3 u servisu: dva slična pravila u dva sloja (arhitektura kaže da API nema pravila).
- **Vidljivost na kalendaru** je djelomično if-ovi; uvjet "PENDING ili APPROVED" ponovljen na dva mjesta.
- **Tumačenja odgovora naručitelja** koja nisu potvrđena: D3 (obavezan razlog, Q37), D10 (bez popisa korisnika, Q49.1), D7 na Q53.3 ("?"), D4 (Q19.1), D8 (Q24), Q39 (preklapanje po radnim danima, kod broji kalendarske).
- **Odbijeni odgovori naručitelja kao proširenja:** prijenos (Q14, Q18), zahtjev preko godine (Q28), bolovanje preko godišnjeg (Q57).
- **Dokumenti na hrvatskom**, a specifikacija 8 kaže sve na engleskom (Q54).
- **Zastarjeli dokumenti:** arhitektura (RequestController.approve, save, uloga), taskovi.md (tester vidi i frontend/tests, "svaka uloga u svom chatu", revieweri), sučelje T1/T2 (findByStatus, /error), zaglavlje specifikacije ("nacrt za potvrdu"), frontend/README.md (predložak).
- **Coding log:** stanja L14, L28, L32 "odlučeno" iako napravljeno; "zamijenjeno" nije dopušteno stanje; nema coding-log-arhiva.md.
- **UI bez pogleda za Q16/Q17** (salde članova, svi zahtjevi tima): samo API.
