# Za procjenu

Prijedlozi koji još nisu odlučeni. Kad se odluče, idu u README, taskove ili log; ovdje se brišu.

## README

- **Testing:** brojke (backend 443, frontend 86), E2E lanci (5 lanaca, 41 korak, 345 provjera, Haiku 345/345 nakon jedne ispravke scenarija), ograničenja (dokaz prepisuje LLM; datumi vrijede do 2026-12-08), poznata rupa (~460 testova sad, ~950 po metodologiji).
- **Extensions:** dodati UX sitnice iz E2E: razlog otkazivanja se ne vidi kad postoji komentar odluke; `/login` se otvara i kad si prijavljen.

## Testiranje

- **Playwright:** prevesti E2E lance u kod. Deterministička provjera po ćeliji, sirovi dokaz (tekst, screenshot), pokretanje skriptom ili cronom.
- **Pune mreže po metodologiji** (procjena ~515 novih slučajeva) i **mutation testing** (PIT na backendu, Stryker na frontendu). Za PIT prvo brzi unit testovi za `ApprovalPolicy`, `CancelPolicy`, `VisibilityPolicy`, `WorkingDays`.
- **Kršilica:** LLM uloga koja čita samo specifikaciju i smišlja napade (čudni redoslijedi, zlouporaba ovlasti), izvršava ih kroz API, nalaze piše u log. Uz to property-based testovi (jqwik, invarijante tipa "saldo nikad ispod 0") i API fuzzing.
- **Neovisni review** drugim modelom ili više njih.

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
