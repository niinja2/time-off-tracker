# Time-off tracker: atomarna pitanja

Datum: 2026-10-23
Izvor: [time-off-tracker-task.md](../time-off-tracker-task.md)
Status: iteracija 1 odgovorena 2026-10-24, iteracija 2 odgovorena 2026-10-27.
Pravila izvedena iz odgovora: [specifikacija.md](specifikacija.md)
Verzija za kolegu (odgovori stižu ovdje): [Google Doc](https://docs.google.com/document/d/1Cfb7SYzos2wvA-kimolamtTCHPieWZ1pANXhMILuLTw/edit)

## Kako odgovoriti

Odgovori u obliku: broj, odgovor, napomena (ako treba). Jedan redak po pitanju. Na primjer:

```
1, Ne
9, Plaćeni slobodan dan za prekovremene sate
24, Europe/Zagreb
```

Ako odgovor ne stigne, gradimo prema pretpostavci i navodimo je u README-u.

## Metoda

- **Atomarno pitanje** pita jednu stvar, s odgovorom Da/Ne ili jednom vrijednošću.
- **Korijen** je pitanje koje ne ovisi ni o jednom drugom odgovoru. Svaka iteracija sadrži samo korijene.
- Pitanje oblika "ako ovo, onda ono" ide u kasniju iteraciju, tek kad stigne odgovor na ono o čemu ovisi.
- Na popis ide samo ono što brief ne kaže izričito i što mijenja ponašanje aplikacije.

Stupac **Brief**:

| Vrijednost | Značenje |
| --- | --- |
| Ne spominje | Brief o tome ne kaže ništa |
| Nejasno | Brief se može čitati na više načina |
| Implicira se | Ne piše kao pravilo, ali slijedi iz primjera ili pravila |

# Iteracija 1

## Uloge i timovi

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q1"></a>🟩 1 | Je li admin zaposlenik? | Nejasno | Ne | Da (npr. direktor koji prati i svoj godišnji) |
| <a id="q2"></a>🟩 2 | Može li admin biti voditelj tima? | Ne spominje | Ne | Da |
| <a id="q3"></a>🟩 3 | Je li voditelj član tima koji vodi? | Implicira se | Da | Da, i može voditi više timova |
| <a id="q4"></a>🟩 4 | Može li zaposlenik biti bez tima? | Ne spominje | Ne | Da (npr. radi sam); njegovi zahtjevi idu adminu |
| <a id="q5"></a>🟩 5 | Može li zaposlenik biti u više timova? | Ne spominje | Ne | Da |
| <a id="q6"></a>🟩 6 | Može li tim imati više voditelja? | Ne spominje | Ne | Ne |
| <a id="q7"></a>🟩 7 | Može li tim biti bez voditelja? | Ne spominje | Ne | Ne |
| <a id="q8"></a>🟩 8 | Može li biti više admina? | Ne spominje | Da (seed ima jednog) | Da |

## Tipovi odsustva

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q9"></a>🟩 9 | Što je "Day off"? | Ne spominje | Slobodan dan koji ne troši godišnji, izvor se ne razlikuje | Slobodan dan |
| <a id="q10"></a>🟩 10 | Ima li bolovanje limit dana? | Ne spominje | Ne | Ne |
| <a id="q11"></a>🟩 11 | Ima li slobodan dan limit dana? | Ne spominje | Ne | Ne |
| <a id="q12"></a>🟩 12 | Smije li slobodan dan trajati više dana zaredom? | Ne spominje | Da | Da |
| <a id="q13"></a>🟩 13 | Treba li uz bolovanje priložiti liječničku potvrdu? | Ne spominje | Ne | Ne |

## Saldo

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q14"></a>🟩 14 | Prenose li se neiskorišteni dani u sljedeću godinu? | Ne spominje | Ne | Da, kao u Hrvatskoj |
| <a id="q15"></a>🟩 15 | Dobiva li manje dana tko počne raditi usred godine? | Ne spominje | Ne | Da, razmjerno |
| <a id="q16"></a>🟩 16 | Vidi li voditelj saldo članova svog tima? | Ne spominje | Da | Da |
| <a id="q17"></a>🟩 17 | Vidi li admin saldo svih korisnika? | Ne spominje | Da | Da |
| <a id="q18"></a>🟩 18 | Provjerava li se saldo za godinu u kojoj zahtjev pada, a ne za tekuću godinu? | Implicira se (pravilo 11 "per calendar year", pravilo 12 "current year") | Da | Za tekucu, ako ima od prijasnje godine, prijasnji se prvi trosi |

## Datumi

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q19"></a>🟩 19 | Smije li godišnji počinjati u prošlosti? | Ne spominje | Ne | Da, ako se zaboravi unijeti |
| <a id="q20"></a>🟩 20 | Smije li bolovanje počinjati u prošlosti? | Ne spominje | Da | Da, ako se zaboravi unijeti |
| <a id="q21"></a>🟩 21 | Smije li slobodan dan počinjati u prošlosti? | Ne spominje | Ne | Da, ako se zaboravi unijeti |
| <a id="q22"></a>🟩 22 | Smije li bolovanje počinjati u budućnosti? | Ne spominje | Da | Da |
| <a id="q23"></a>🟩 23 | Je li zahtjev koji počinje danas već "započeo" (pravilo 7)? | Nejasno | Da | Da |
| <a id="q24"></a>🟩 24 | U kojoj se vremenskoj zoni računa "danas"? | Ne spominje | Europe/Zagreb | Vremenska zona servera na koji se deploya |
| <a id="q25"></a>🟩 25 | Koliko se unaprijed smije tražiti? | Ne spominje | Najviše do kraja sljedeće godine | Proizvoljno |
| <a id="q26"></a>🟩 26 | Postoji li minimalan rok najave, npr. godišnji najmanje X dana unaprijed? | Ne spominje | Nema | Nema |
| <a id="q27"></a>🟩 27 | Postoji li najdulje trajanje jednog zahtjeva? | Ne spominje | Nema | Nema |
| <a id="q28"></a>🟩 28 | Što API radi sa zahtjevom preko granice godine: odbija ili dijeli? | Nejasno | Odbija | Dijeli; i dalje je jedan zahtjev |

## Stanja i otkazivanje

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q29"></a>🟩 29 | Smije li netko podnijeti zahtjev u ime drugog korisnika? | Ne spominje | Ne | Ne |
| <a id="q30"></a>🟩 30 | Smije li voditelj otkazati tuđi zahtjev? | Ne spominje | Ne | Moze |
| <a id="q31"></a>🟩 31 | Smije li admin otkazati tuđi zahtjev? | Nejasno ("act on everything") | Ne | Da |
| <a id="q32"></a>🟩 32 | Smije li voditelj ili admin povući već dano odobrenje? | Ne spominje | Ne | Da (isto kao otkazivanje, vidi 30 i 31) |
| <a id="q33"></a>🟩 33 | Smije li se uređivati zahtjev na čekanju? | Ne spominje | Ne, otkaži i podnesi novi | Ne |
| <a id="q34"></a>🟩 34 | Smije li se uređivati odobreni zahtjev? | Ne spominje | Ne, otkaži i podnesi novi | Ne, otkaži i podnesi novi |
| <a id="q35"></a>🟩 35 | Smije li se odobriti zahtjev čiji su datumi već prošli? | Ne spominje | Da | Da |
| <a id="q36"></a>🟩 36 | Ističe li zahtjev na čekanju kad prođu njegovi datumi? | Ne spominje | Ne | Ne |
| <a id="q37"></a>🟩 37 | Treba li razlog za otkazivanje zahtjeva? | Ne spominje | Ne | Moze, nema bude field |
| <a id="q38"></a>🟩 38 | Postoji li najveći broj zahtjeva na čekanju po korisniku? | Ne spominje | Nema | Ne |

## Preklapanja

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q39"></a>🟩 39 | Je li preklapanje samo na vikendu preklapanje? | Nejasno | Da | Nije razumio pitanje. Naša odluka: preklapanje se računa samo po radnim danima |
| <a id="q40"></a>🟩 40 | Postoji li minimalan broj ljudi koji moraju biti prisutni u timu? | Ne spominje | Nema | Ne |

## Vidljivost

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q41"></a>🟩 41 | Vidi li zaposlenik svoje zahtjeve na čekanju na kalendaru tima? | Nejasno | Da | Da |
| <a id="q42"></a>🟩 42 | Vidi li voditelj zahtjeve na čekanju na kalendaru tima? | Implicira se | Da | Da |
| <a id="q43"></a>🟩 43 | Vidi li admin kalendar svih timova odjednom ili po timu? | Ne spominje | Svi timovi, s filtrom po timu | Oboje, s filtrom; da se vidi stanje ljudi u firmi |
| <a id="q44"></a>🟩 44 | Tko vidi komentar odbijanja? | Ne spominje | Vlasnik i oni koji smiju odobravati | Da (slaže se) |
| <a id="q45"></a>🟩 45 | Tko vidi komentar odobrenja? | Ne spominje | Vlasnik i oni koji smiju odobravati | Da (slaže se) |
| <a id="q46"></a>🟩 46 | Vidi li zaposlenik tko je odobrio ili odbio njegov zahtjev? | Ne spominje | Da | Da |
| <a id="q47"></a>🟩 47 | Vidi li zaposlenik bolovanje kolege na kalendaru, kao "odsutan" bez tipa? | Implicira se | Da | Da |
| <a id="q48"></a>🟩 48 | Prikazuje li kalendar voditelju i adminu odbijene i otkazane zahtjeve? | Nejasno ("Who is away") | Ne | Ne, inače je pretrpano |

## API

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q49"></a>🟩 49 | Kojim mehanizmom ćete zvati API kao određeni korisnik? | Ne spominje | Basic auth sa seedanim lozinkama | JWT |
| <a id="q50"></a>🟩 50 | Imate li preferencije za HTTP statuse i format grešaka? | Ne spominje | 400, 403, 404, 409; `application/problem+json` | Standardni statusi, obrada grešaka po volji |
| <a id="q51"></a>🟩 51 | Treba li dokumentacija API-ja (npr. Swagger)? | Ne spominje | Da, Swagger | Ne Swagger; dijagrami tokova i sekvencijski dijagrami |

## Isporuka

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q52"></a>🟩 52 | Treba li prijaviti stvarno utrošeno vrijeme? | Ne spominje | Ne | Ne |
| <a id="q53"></a>🟩 53 | Imate li Docker na računalu? | Ne spominje | Da | Neka bude u kontejneru |
| <a id="q54"></a>🟩 54 | Na kojem jeziku su UI, README i kod? | Implicira se (engleski, bez prijevoda) | Engleski | Svejedno, preferira engleski |
| <a id="q55"></a>🟩 55 | Trebaju li u seedu biti i primjeri zahtjeva? | Ne spominje | Da | Ne trebaju; upute za pokretanje u README |
| <a id="q56"></a>🟩 56 | S kojim računom i na kojoj platformi dijelimo privatni repozitorij (npr. GitHub), ili šaljemo zip? | Ne spominje ("a private repo shared with us is fine") | GitHub | Svejedno |

## Prijedlozi izmjene pravila

Brief ovo izričito propisuje, ali u praksi stvara problem. Pitamo želite li pravilo promijeniti.

| # | Pitanje | Brief | Pretpostavka | Odgovor |
| --- | --- | --- | --- | --- |
| <a id="q57"></a>🟩 57 | Smije li bolovanje preklopiti odobreni godišnji? | Pravilo 9 kaže ne | Ne, prema pravilu 9 | Brate mili mozes otic na bolovanje kad si na GO. onda ti se GO otkazuje / refundira. Ali to je edge case, nike toliko bitno. |

## Odgođeno iz iteracije 1

| Pitanje | Ovisi o | Status |
| --- | --- | --- |
| Tko odobrava zahtjev admina? | 1 | Postalo pitanje 1.1 |
| Ima li admin saldo? | 1 | Riješeno: da, admin je zaposlenik |
| Je li admin član tima? | 1, 2 | Riješeno: kad je voditelj, član je tima koji vodi |
| Na čijem se kalendaru vidi odsustvo admina? | 1, 2 | Riješeno: na kalendaru timova kojima pripada; bez tima ga vide samo admini |
| Prikazuje li se saldo i za sljedeću godinu? | 25 | Tema ekrana, ovisi o 14.1 |
| Koje verzije Jave i Nodea imate? | 53 | Otpada: aplikacija je u kontejneru |
| Gdje se radi promjena uživo na intervjuu? | Kasnije, po dogovoru | Otvoreno |

## Maknuto s popisa

| Pitanje | Zašto |
| --- | --- |
| Od kojeg datuma teče rok od 7 dana? | Poznato: od 2026-10-22 |
| Smiju li se koristiti AI alati? | Odgovoreno: smiju |
| Jesu li 20 dana godišnjeg radni ili kalendarski dani? | Pravilo 10: broje se samo radni dani |
| Može li se odbijeni zahtjev ponovno otvoriti? | Isto kao novi zahtjev |
| Može li se zahtjev djelomično odobriti? | Isto kao odbijanje pa novi zahtjev |
| Prikazuje li kalendar vikend dane unutar zahtjeva? | Ne mijenja ponašanje |
| Smije li se tražiti odsustvo za sljedeću godinu? | Pokriva ga pitanje 25 |
| Vraća li API 404 umjesto 403 za tuđi zahtjev? | Pokriva ga pretpostavka uz pitanje 50 |
| Država za blagdane, komentar od samih razmaka, način prijave u UI-ju | Naša odluka, ne pitanje |

# Iteracija 2

Pitanja koja su otvorili odgovori iz iteracije 1. Broj pokazuje iz kojeg pitanja dolaze.

| # | Pitanje | Pretpostavka | Odgovor |
| --- | --- | --- | --- |
| <a id="q1-1"></a>🟩 1.1 | Smije li admin odobriti vlastiti zahtjev za godišnji? | Da | Da |
| <a id="q5-1"></a>🟩 5.1 | Smije li zahtjev zaposlenika u više timova odobriti voditelj bilo kojeg od tih timova? | Da, dovoljno je jedno odobrenje | Da |
| <a id="q9-1"></a>🟩 9.1 | Treba li aplikacija pratiti kako zaposlenik stječe slobodne dane (npr. za prekovremene)? | Ne, prati se izvan aplikacije | Not for now |
| <a id="q14-1"></a>🟩 14.1 | Do kojeg datuma vrijede preneseni dani? | 30. lipnja sljedeće godine | Yes |
| <a id="q14-2"></a>🟩 14.2 | Postoji li najveći broj dana koji se smije prenijeti? | Nema | Nope |
| <a id="q15-1"></a>🟩 15.1 | Računa li se razmjerni saldo po mjesecima ili po danima? | Po mjesecima | Mjeseci |
| <a id="q15-2"></a>🟩 15.2 | Kako se zaokružuje razmjerni saldo? | Na najbliži cijeli dan | Ceil |
| <a id="q19-1"></a>🟩 19.1 | Postoji li granica koliko se unatrag smije podnijeti zahtjev? | Nema | Nope |
| <a id="q30-1"></a>🟩 30.1 | Smije li voditelj otkazati odobreni zahtjev člana tima koji je već počeo? | Ne, isto kao vlasnik (pravilo 7) | Nope, nema dizanja sa godisnjeg. Po potrebi ako se i dogodi, neka after the fact izmjene |
| <a id="q31-1"></a>🟩 31.1 | Smije li admin otkazati odobreni zahtjev koji je već počeo? | Ne, isto kao vlasnik (pravilo 7) | Vidi gore |
| <a id="q43-1"></a>🟩 43.1 | Treba li adminu pregled tko je danas prisutan u tvrtki? | Ne, dovoljan je kalendar sa svim timovima | Nope |
| <a id="q49-1"></a>🟩 49.1 | Traži li prijava lozinku? | Da, seedane lozinke; na ekranu prijave popis korisnika za brzi odabir | Da |
| <a id="q53-1"></a>🟩 53.1 | Koliko otprilike korisnika aplikacija treba podržati? | 50–100, malo istovremenih pristupa | Za PoC racunaj i manje 20-ak |
| <a id="q53-2"></a>🟩 53.2 | Treba li POC biti deployan negdje, ili je dovoljno lokalno u Dockeru? | Lokalno; o deployu razgovaramo | Dovoljno u dockeru |
| <a id="q53-3"></a>🟩 53.3 | Ako je vrijeme kratko, je li važnije da pravila iz briefa rade s testovima, ili da su pokrivena proširenja iz vaših odgovora? | Pravila i testovi prvo, proširenja opisana u README | ? |
| <a id="q53-4"></a>🟩 53.4 | Očekuje li se AI funkcionalnost unutar same aplikacije? | Ne | Ne |
| <a id="q53-5"></a>🟩 53.5 | Želite li vidjeti proces rada s AI-jem (specifikacija, testovi, pregledi), ili samo rezultat? | Kratak opis procesa u README | To cemo raspavljat u razgovoru |
| <a id="q53-6"></a>🟩 53.6 | Je li rok od 7 dana (29. listopada) strog? | Da | Sure |
| <a id="q53-7"></a>🟩 53.7 | Ocjenjuju li se opcionalni dodaci iz briefa ako nisu napravljeni? | Ne | Ne |
| <a id="q53-8"></a>🟩 53.8 | Primjer načina rada s LLM-ovima. Developer: piše kod. Reviewer 1: pregledava kod na razini koda. Reviewer 2: pregledava kod na razini funkcija i procesa. Tester: piše i izvršava testove prema specifikaciji i sučelju koje je napisao developer. LLM-ovi međusobno komuniciraju, osim testera: on ne vidi kod ni razgovor ostalih, kako bi testovi bili neovisni o implementaciji. Ovo je jedan od prijedloga, ne nužno onaj koji će se koristiti; proces koji se bude koristio bit će opisan u README-u. S obzirom na to, koliko duboko se očekuje razumijevanje koda: na razini linije, funkcije ili cjeline? | Na razini funkcije | Funkcionalno, as in zasto je nesto napravljeno na taj nacin a ne neki drugi. |

## Odgođeno iz iteracije 2

| Pitanje | Ovisi o |
| --- | --- |
| Nema; 1.1 je Da, 9.1 je Ne | |
