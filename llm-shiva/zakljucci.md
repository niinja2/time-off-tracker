# Zaključci usporedbe reviewa

Sažetak razgovora o [usporedba.md](usporedba.md). Sve brojke su iz **jednog izvođenja po varijanti**, pa vrijede kao smjer, ne kao dokaz.

## Oznake

Model-jačina-prompt. **H** = high, **M** = medium; **S** = shiva, **I** = integrator, **SI** = ista sesija radila je oba prompta (prvo shiva, pa integrator), **L** = uske leće (svaka sesija gleda jednu vrstu problema).

| Oznaka | Model | Izvor |
| --- | --- | --- |
| Hk-M-S | Haiku 5.5 medium, shiva | `haiku 5.5 medium shiva.txt` |
| Hk-M-I | Haiku 5.5 medium, integrator | `Haiku 5.5 medium integrator.txt` |
| Hk-M-L | Haiku 5.5 medium, 10 uskih leća u 5 sesija, uz neutralnu mapu koda | `haiku 5.5 medium 10prompt.txt`, `lece-integrator/` (1. 11.) |
| So-M-I | Sonnet 5.5 medium, integrator | `Sonnet 5.5 medium integrator.txt` |
| So-M-L | Sonnet 5.5 medium, 4 shiva leće u 4 sesije (Q/D/R, log i proces, UI i uloge, README i konfiguracija) | `sonnet 5.5 medium shiva 4prompt.txt`, `lece-shiva/` (1. 11.) |
| O55-M-I | Opus 5.5 medium, integrator | `opus 5.5 medium integrator.txt` |
| O55-H-I | Opus 5.5 high, integrator | `opus 5.5 high integrator.txt` |
| O55-H-SI | Opus 5.5 high, oba prompta u jednoj sesiji | `O55-H-SI shiva report.md` i `O55-H-SI integrator report.md` (30. 10., dan prije ostalih) |
| O47-M-I, O48-M-I | Opus 4.7 i 4.8 medium, integrator | ne preporučuju se |

## Dvije vrste nalaza

| | Shiva (S1–S43) | Integrator (I1–I56) |
| --- | --- | --- |
| Uspoređuje | projekt s nečim izvan koda: brief, odgovori naručitelja, specifikacija, README | kod sa samim sobom i s pravilima arhitekture |
| Pitanje | radi li ovo ono što je dogovoreno? | je li to napisano čisto? |
| Nalaza | 43 (S1–S43), od kojih 40 još vrijedi | 56 |
| Primjer | Q18 kaže "za tekuću" godinu, a D5 i kod troše saldo godine zahtjeva | R14 (bolovanje i slobodni dani ne troše saldo) napisano na dva mjesta |
| Popravak | odluka, često pitanje naručitelju | refaktor bez promjene ponašanja |
| Ako se propusti | kriva aplikacija, sigurnosna rupa | skuplja sljedeća promjena |

Preklapaju se samo u 3 nalaza (I2, I3, I7), i to tamo gdje dokument nešto tvrdi o strukturi koda. Ukupno je **99** različitih stvarnih nalaza: 56 integrator i 43 shiva.

## Integrator: 56 nalaza

Podjela: **9 ključnih** (VAŽNO, I1–I7, I9, I11), **3 svjesno prihvaćena** (istodobnost I34, I40, I41: "Deliberately out: concurrent requests (about 20 users)", `README.md:94`) i **44 sitna**.

| Model | Ključni (od 9) | Prihvaćeni (od 3) | SITNO (od 44) | SITNO jedinstveni | Ukupno (od 56) | Netočnih | Trošak |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **Hk-M-L** | 8 (fali I4) | 3 | 28 | 15 | **39** | 1 | $0,47 |
| So-M-I | 9 | 1 | 18 | 0 | 28 | 1 | ? |
| O55-H-SI | 8 (fali I9) | 0 | 20 | 3 | 28 | 0 | ? |
| O55-M-I | 9 | 0 | 12 | 0 | 21 | 0 | ? |
| O55-H-I | 9 | 0 | 11 | 1 | 20 | 0 | ? |
| Hk-M-I | 8 (fali I9) | 0 | 12 | 1 | 20 | 0 | ? |
| O48-M-I | 3 | 0 | 6 | 1 | 9 | 0 | ? |
| O47-M-I | 4 | 0 | 3 | 1 | 7 | 1 | ? |

Koliko je modela (od 8) našlo pojedini nalaz:

| | Samo 1 | 2 | 3–4 | 5–8 |
| --- | --- | --- | --- | --- |
| Ključni (9) | 0 | 0 | 1 (I9) | 8 |
| Prihvaćeni (3) | 2 | 1 | 0 | 0 |
| SITNO (44) | 22 | 8 | 7 | 7 |

- **Ključne nalaze nađu gotovo svi:** 8 od 9 su jezgra. Hk-M-L je našao 17 novih nalaza, a nijedan nije ključan. To podupire procjenu da važnih nije ostalo puno.
- **Sitni su rep koji se ne zatvara:** polovicu sitnih (22 od 44) našao je samo jedan model. Svako novo izvođenje doda nove.

## Shiva: 43 nalaza

Od 43 nalaza 40 još vrijedi (S21, S24 i S30 su riješeni). BLOKIRA ih je 6 (S1–S4, S19, S20).

| Model | Pokriva od 40 | BLOKIRA od 6 | Jedinstveni | Netočnih | Izmišljeni redak ili citat | Trošak |
| --- | --- | --- | --- | --- | --- | --- |
| **So-M-L** | **28** (+ S3 djelomično) | 4 + S3 djelomično (fali S2) | 12 (S32–S43) | 2 | oko 10 redaka + 3 pomaknuta | $2,70 |
| O55-H-SI | 22 | 6 | 6 + 3 riješena | 0 | 2 | ? |
| Hk-M-S | 18 | 4 | 2 (S12, S17) | 0 (1 neprovjerljiv) | 0 | ? |

- **So-M-L** pokriva najviše, a od novih su važni `mvnw` bez izvršnog bita (S32), popravci samo u radnoj kopiji (S33) i pregled svih zahtjeva tima koji ne postoji u UI-ju (S31, prvi put točno obrazložen). Propušta D3 koji krivo citira Q37 (S2), iako je prošao Q po Q, i sve iz dokumenata za koje nije bilo leće: metodologiju, arhitekturu, `frontend/README.md`, L9 i L12. Na kodu UI-ja izmišlja brojeve redaka (sadržaj je točan), a iz toga što u logu nema reviewerovog unosa krivo zaključuje da reviewa nije bilo.
- **O55-H-SI** jedini nalazi sve BLOKIRA, README u kojem dominira proces i komentar koji vidi voditelj bez prava. Ima jedan izmišljen redak (`MyTimeOffScreen.tsx:613`, fajl ima 259 redaka) i krivo citiran odgovor na Q37.
- **Hk-M-S** je najprecizniji i jedini iz Q37 izvodi nalaz. Jedini nalazi testove izvan metodologije (S12, poznato i u tijeku) i L9/L12 bez D (S17).

Shiva rezultati nisu potpuno usporedivi: O55-H-SI je radio dan ranije, a dio njegovih nalaza je u međuvremenu ispravljen.

## Zaključci

1. **Uzak prompt pobjeđuje jak model.** Haiku s 10 uskih leća (Hk-M-L) pokrio je 39 od 56 integrator nalaza, gotovo dvostruko više od istog Haikua s jednim promptom (20), i više od Sonneta (28). Našao je 17 novih nalaza, a cijena je $0,47.
2. **Leće nalaze samo ono što traže.** Hk-M-L propušta ključni I4 i sve iz područja koja nijedna leća nije pokrila (problem+json, magični 500, `null` u kalendaru). Treba ih dopuniti jednim širokim prolazom.
3. **Opus 4.7 i 4.8 ne koristiti:** imaju malo nalaza, malo pokrivenosti, a Opus 4.7 ima i netočnu tvrdnju.
4. **Jednim širokim promptom za integrator najbolji su Sonnet 5.5 medium i Opus 5.5:** svih 9 ključnih. Sonnet ima najveću širinu, ali jednu netočnu tvrdnju; Opus nema nijednu i najbolje pripisuje odgovornost.
5. **Haiku 5.5 medium ≈ Opus 5.5 medium/high na širokom integratoru** (8–9 ključnih, oko 20 ukupno, nula netočnih), a puno je jeftiniji.
6. **Leće rade i na shivi.** Sonnet s 4 shiva leće pokriva 28 od 40 shiva nalaza, više od Opusa 5.5 high (22) i Haikua (18). Opus je i dalje jedini koji je našao svih 6 BLOKIRA, a Haiku je najprecizniji. Sva tri se nadopunjuju.
7. **Model to što zapisa nema čita kao da posla nije bilo.** So-M-L je iz toga što reviewer nema unosa u logu zaključio da reviewa nije bilo, a log je samo za probleme. Prompt mora reći što proces bilježi, a što ne.
8. **Prolaz "jedan po jedan" ne jamči prosudbu.** So-M-L je provjerio 77 od 77 Q-ova i točno citirao Q37, a ipak nije vidio da ga D3 krivo citira. Uputa treba izričito tražiti da se citat u D usporedi s izvorom slovo po slovo.
9. **Dva prompta u istoj sesiji dala su više nalaza, a ne manje.** Razlika O55-H-SI prema O55-H-I je u sitnima i najvjerojatnije je slučajna. Kontekst shive vjerojatno je pomogao samo pri pripisivanju odgovornosti.
10. **Prompt određuje što se nalazi, a model koliko.** Novi prompt, ili uža leća, donosi više od jačeg modela.
11. **Reviewer mora znati što je svjesno izostavljeno.** Istodobnost su prijavila dva modela kao VAŽNO, a odluka je zapisana u `README.md:94`. Uputa u promptu treba glasiti: navedi i označi kao prihvaćeno, ne preskači.
12. **Dokaze i težinu uvijek provjeriti.** Netočne tvrdnje i izmišljeni redci javljaju se kod usamljenih modela. Najviše izmišljenih brojeva redaka ima So-M-L, i to u kodu (oko 10), iako su mu tvrdnje točne; u dokumentima su mu redci točni. Hk-M-L precjenjuje težinu (skupove statusa i tipova označava kao VAŽNO), a So-M-L je podcjenjuje (nijedan BLOKIRA). Treći model neka otvori fajl:redak i provjeri dokaz, a ne da glasa.

## Kategorije za ocjenu modela

- **Težina:** BLOKIRA, VAŽNO, SITNO, uz posebnu oznaku **svjesno prihvaćeno** (stvarno, ali odlučeno da se ne radi).
- **Učestalost:** jezgra (nalazi je većina modela), rijetki (2–4 modela), jedinstveni (1 model).
- **Preciznost:** netočne tvrdnje, izmišljeni redci i citati, precijenjena težina.
- **Prosudba:** je li nalaz točno pripisan (npr. R5 u API-ju je odluka sučelja T5, a ne bug developera).
- **Trošak:** dolari po izvođenju i po novom stvarnom nalazu.
- **Neotkriveni:** ne broje se, ali se procjenjuju. Ključni su skoro svi u jezgri, a 17 novih nalaza leća nije donijelo nijedan ključni, pa ih je realno 9–11. Ta granica vrijedi samo za ono što prompt traži: integratori nisu vidjeli nijedan važan shiva nalaz.

## Preporuka

- **Shiva:** Sonnet 5.5 medium s lećama + Opus 5.5 high (širok prolaz, BLOKIRA) + Haiku 5.5 medium (preciznost). Lećama dodati petu: "procesni i tehnički dokumenti prema kodu" (arhitektura, pravila kodiranja, metodologija, `frontend/README.md`).
- **Integrator:** Haiku 5.5 medium s uskim lećama + jedan širok prolaz (Sonnet 5.5 medium ili Opus 5.5 medium).
- Svaka uloga ili leća u svježoj sesiji. Ako treba kontekst, dati neutralnu mapu koda ili kratak sažetak, ne cijelu sesiju.
- U prompt staviti popis svjesno izostavljenog (README, odluke D) i što proces bilježi, a što ne (log samo za probleme, review bez nalaza bez traga).
- Spojiti nalaze, a na kraju provjeriti dokaze i težinu.
- Mehaničke sitnice (copy-paste, mrtav kod, slojevi) jeftinije je loviti alatima (CPD, ArchUnit, knip) nego dodatnim LLM izvođenjima.

## Što bi trebalo izmjeriti

- Isti model i prompt 3+ puta u svježoj sesiji i 3+ puta nakon drugog prompta, uz 99 nalaza kao rješenje: smeta li kontekst i koliko izvođenja treba da se pokriju svi ključni.
- Integrator leće na Sonnetu ili Opusu, i shiva leće na Haikuu: koliko daje model, a koliko leća (sad je leća na integratoru bila na Haikuu, a na shivi na Sonnetu, pa se to ne može razdvojiti).
- Trošak ostalih izvođenja, da se usporedi cijena po stvarnom nalazu.
- Opus 5.5 medium i Sonnet 5.5 na shivi, pokrenuti isti dan kad i ostali.
