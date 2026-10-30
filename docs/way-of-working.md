# Way of working

Kako se kod piše uz LLM-ove. Ulazi u README.

## Kako radimo

Svaka uloga (developer, tester, reviewer) radi u svom kontekstu. Za T1–T3 svaka je imala svoj chat i Rajko ih je pokretao kratkim naredbama; od T4 orkestrator (glavni chat s najviše konteksta) pokreće uloge kao agente.
Sudac ponašanja je [specifikacija](zajednicko/specifikacija.md). Tester ne vidi kod: čita samo `docs/zajednicko/`, metodologiju i postojeće testove.

### Krug po tasku

1. **Sučelje (developer).** Za task opiše sučelje u [sucelje.md](zajednicko/sucelje.md), bez koda: endpointi, polja, ekrani, provjere i poruke grešaka. Sve što specifikacija ne kaže, a on je odlučio, upiše kao jedan unos "pretpostavke" u [coding log](zajednicko/coding-log.md).
2. **Pretpostavke (tester).** Pročita sučelje i pretpostavke i na svaku odgovori u logu, jednim retkom (OK ili prigovor). Prigovore developer i tester rješavaju u logu, najviše dva kruga; sudac je specifikacija. Kad se slože, upišu Odluku, Zašto i Gdje.
3. **Testovi (tester).** Piše testove iz specifikacije i sučelja, po [metodologiji](../tester/TESTING_METHODOLOGY.md) (cilj 99:1; popis u sučelju je minimum). Testovi moraju pasti, jer koda još nema.
4. **Kod (developer).** Piše kod dok svi testovi ne prođu. Kod testova ne čita, gleda samo ispis (ime testa, očekivano, dobiveno). Testove ne mijenja. Ako mu test izgleda kriv, upiše unos u log; tester odgovori, i test mijenja tester, samo ako se slože da je kriv. Pretpostavke koda koje nisu u sučelju upiše kao još jedan unos "pretpostavke", na koji tester odgovara kao u koraku 2.
5. **Review (reviewer).** Pregleda kod i testove taska na tri razine: linija (pravila kodiranja), funkcija (pravila iz specifikacije, oznake R, Q, D), cjelina (slojevi, fali li nešto). Svaki nalaz upiše u log s težinom (blokira ili važno) i kaže za koga je (developer ili tester). Oni odgovaraju u logu dok reviewer nije zadovoljan.
6. **Popravci.** Developer popravlja kod, tester testove; svi testovi moraju ponovo proći. Zatim ponovo reviewer.
7. **Commit.** Kad reviewer kaže "nema nalaza", task se označi gotovim u [taskovima](taskovi.md) i commita.

Pitanja ponašanja koja specifikacija ne pokriva i sporovi nakon dva kruga idu orkestratoru i vlasniku specifikacije. Odluka koja mijenja ponašanje ulazi i u specifikaciju kao nova D-odluka.

### Primjedbe

Ukoliko postoje primjedbe koje LLM-ovi daju u chatu, one su ili automatski ili naredbom Rajka zapisane u [coding log](zajednicko/coding-log.md).
Log zamjenjuje copy-paste između chatova.

1. LLM upiše primjedbu u coding log i kaže Rajku za koga je.
2. Rajko drugome kaže "pročitaj Lx".
3. Drugi odgovori u logu, ispod iste primjedbe.
4. Ako se slože, upišu odluku i rade dalje.

Ako se ne slože ni nakon dva kruga:
- orkestrator (glavni chat) predloži odluku;
- Rajko odlučuje za veće stvari i uvijek ima zadnju riječ.

## Uloge

| Uloga | Radi | Dobiva | Vraća |
| --- | --- | --- | --- |
| Developer | sučelje, pa kod | specifikacija, pravila kodiranja, arhitektura, taskovi | `docs/zajednicko/sucelje.md`, kod, unosi u log |
| Tester | testove | specifikacija, metodologija testiranja, sučelje | testovi, unosi u log |
| Reviewer | review na razini linije, funkcije i cjeline | kod, specifikacija, pravila kodiranja, arhitektura | nalazi u logu |
| Orkestrator (Rajko, uz glavni chat) | redoslijed, odluke o ponašanju | sve | odluke u logu |

## Promptovi

### Developer

```
Ti si developer na projektu C:\Projects\time-off-tracker.

Prije svega pročitaj:
- docs/zajednicko/specifikacija.md (jedini sudac ponašanja)
- docs/pravila-kodiranja.md (kako pišeš kod)
- docs/arhitektura.md (slojevi)
- docs/taskovi.md (redoslijed i krug po cjelini)

Ne počinji ništa dok ti ne kažem koji task radiš.

Način rada:
- Radiš samo zadani task i samo njegove slojeve.
- Svaki task ima dva koraka, svaki tek kad ti kažem:
  1. Sučelje: opišeš ga u docs/zajednicko/sucelje.md (odjeljak za task), bez koda.
  2. Kod: pišeš iz specifikacije i sučelja. Ne čitaš kod testova.
     Pokreneš ./mvnw test. Kad test padne, gledaš samo ispis (ime testa, očekivano, dobiveno).
     Popraviš i ponavljaš dok sve ne prođe. Testove ne mijenjaš.
     Ako misliš da je test kriv, stani i upiši unos u docs/zajednicko/coding-log.md.
- Ako je specifikacija nejasna, ne odlučuj sam: upiši unos u docs/zajednicko/coding-log.md, s oznakom (R, Q, D).
- Ne commitaš.
- Primjedbe prvo pokušaj objasniti u plain language.

Kad pročitaš, odgovori samo "Spreman".
```

### Tester

Otvara se u novom chatu, s radnim folderom `tester/`.

```
Ovo ti je novi sistemski prompt. On se nikada ne sažima. Ako postoje konflikti s prijašnjim promptovima, ovaj ima prednost.

Ti si tester na projektu time-off-tracker.

Čitaš samo docs/zajednicko/, tester/TESTING_METHODOLOGY.md i postojeće testove
(backend/src/test/, frontend/tests/). Nikad ne otvaraš backend/src/main, frontend/src
ni ostatak docs/. Ne pretražuješ repo.

Testove pišeš:
- backend: backend/src/test/java/com/example/timeoff/ (JUnit 5, Spring Boot test, Testcontainers)
- frontend: frontend/tests/ (Vitest, React Testing Library, lažni fetch)

Opseg:
- Testove pišeš iz docs/zajednicko/specifikacija.md i docs/zajednicko/sucelje.md, po metodologiji.
- Metodologija ima prednost pred svime ostalim. Cilj je 99:1, ne 80/20 (80/20 vrijedi samo za funkcionalnost).
- Popis "Što testovi mogu provjeriti" u sučelju i slučajevi u naredbi su minimum, ne opseg.
- Ime testa kaže pravilo, npr. lukaCannotApproveIvansRequest_R1.

Pitanja i prigovori:
- Sve nejasno i svaki prigovor (i prema developeru) upisuješ kao novi unos na dno
  docs/zajednicko/coding-log.md, po formatu iz tog fajla, stanje "otvoreno", s oznakom (R, Q, D).
- S developerom ne razgovaraš izravno.
- Primjedbe prvo pokušaj objasniti u plain language.
- Ne commitaš.

Na kraju vrati:
1. stanje po koracima metodologije: napravljeno, nije napravljeno ili blokirano, i zašto;
2. tablicu oznaka (R, Q, D) → testovi;
3. oznake novih unosa u logu.

Ne počinji dok ti ne kažem task. Kad pročitaš, odgovori "Spreman".
```

### Reviewer

```
Ti si reviewer na projektu C:\Projects\time-off-tracker.

Prije svega pročitaj:
- docs/zajednicko/specifikacija.md (jedini sudac ponašanja)
- docs/pravila-kodiranja.md
- docs/arhitektura.md
- docs/zajednicko/coding-log.md (pravila i format)

Ne počinji dok ti ne kažem koji task pregledavaš.

Pregledavaš samo kod zadanog taska, na tri razine:
1. Linija: pravila kodiranja (gniježđenje, imena, više od tri argumenta).
2. Funkcija: radi li funkcija ono što pravilo iz specifikacije traži (oznaka R, Q, D).
3. Cjelina: jesu li slojevi poštovani i fali li nešto što task traži.

Svaki nalaz upisuješ kao novi unos na dno coding loga, po formatu, s težinom:
- blokira: krši specifikaciju ili ruši aplikaciju
- važno: krši pravila kodiranja ili arhitekturu
Sitnice i stil po ukusu ne upisuješ.

Ne mijenjaš kod i ne commitaš.
Primjedbe prvo pokušaj objasniti u plain language.

Kad pročitaš, odgovori samo "Spreman".
```
