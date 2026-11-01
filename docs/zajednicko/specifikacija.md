# Time-off tracker: specifikacija

Datum: 2026-10-28
Status: nacrt za potvrdu. Šalje se naručitelju na potvrdu.
Izvori: [brief](brief.md), [atomarna pitanja](atomarna-pitanja.md) (iteracije 1 i 2 odgovorene), [Google Doc s pitanjima](https://docs.google.com/document/d/1Cfb7SYzos2wvA-kimolamtTCHPieWZ1pANXhMILuLTw/edit).

Ovo je jedini izvor istine za kod. Uz svako pravilo u zagradi piše odakle dolazi.

Načelo: gradimo pravila iz briefa s testovima, na jednostavnoj strukturi u koju se proširenja mogu kasnije dodati na jednom mjestu. Kad je odgovor dvosmislen, tumačimo ga tako da se ne sudara s briefom.

## Legenda

| Oznaka | Izvor | Primjer |
| --- | --- | --- |
| [🟦 `R5`](brief.md#rules) | Pravilo 5 iz briefa ([🟦 `brief`](brief.md) ako pravilo nema broj) | Odbijanje traži komentar ([🟦 `R5`](brief.md#rules)). |
| [🟩 `Q3`](atomarna-pitanja.md#q3) | Odgovor kolege na pitanje 3 iz `atomarna-pitanja.md` | Voditelj može voditi više timova ([🟩 `Q3`](atomarna-pitanja.md#q3)). |
| 🟨 `AN` | Pretpostavka: kolega još nije odgovorio na pitanje N. Trenutno nema otvorenih pretpostavki | |
| [🟪 `D1`](#d1) | Naša odluka broj 1, popis u odjeljku 10 | Blagdani su proširenje ([🟪 `D1`](#d1)). |

## 1. Uloge i timovi

- Uloge su Employee, Team lead i Admin. Korisnik može imati više uloga, npr. admin koji je i voditelj ([🟩 `Q2`](atomarna-pitanja.md#q2)).
- Svaki korisnik je zaposlenik, pa i admin ([🟩 `Q1`](atomarna-pitanja.md#q1)).
- Tim ima točno jednog voditelja ([🟩 `Q6`](atomarna-pitanja.md#q6), [🟩 `Q7`](atomarna-pitanja.md#q7)).
- Voditelj je član svakog tima koji vodi i može voditi više timova ([🟩 `Q3`](atomarna-pitanja.md#q3)).
- Zaposlenik može biti u nijednom, jednom ili više timova ([🟩 `Q4`](atomarna-pitanja.md#q4), [🟩 `Q5`](atomarna-pitanja.md#q5)).
- Može biti više admina ([🟩 `Q8`](atomarna-pitanja.md#q8)).

## 2. Zahtjev

Polja:

| Polje | Obavezno | Izvor |
| --- | --- | --- |
| Podnositelj | da | [🟦 `brief`](brief.md) |
| Tip: Vacation, Sick leave, Day off | da | [🟦 `brief`](brief.md) |
| Početni i završni datum, oba uključiva | da | [🟦 `brief`](brief.md) |
| Razlog | ne | [🟦 `brief`](brief.md) |
| Stanje: Pending, Approved, Rejected, Cancelled | da | [🟦 `brief`](brief.md) |
| Komentar odluke | samo kod odbijanja | [🟦 `R5`](brief.md#rules) |
| Tko je odlučio | uz odluku | [🟩 `Q46`](atomarna-pitanja.md#q46) |
| Razlog otkazivanja | obavezan kad ne otkazuje vlasnik | [🟩 `Q37`](atomarna-pitanja.md#q37), [🟪 `D3`](#d3) |
| Tko je otkazao | uz otkazivanje | [🟩 `Q30`](atomarna-pitanja.md#q30), [🟩 `Q31`](atomarna-pitanja.md#q31) |
| Vrijeme stvaranja, vrijeme odluke ili otkazivanja | da | [🟪 `D13`](#d13) |

- Day off i Sick leave nemaju limit ni vlastiti saldo ([🟩 `Q10`](atomarna-pitanja.md#q10), [🟩 `Q11`](atomarna-pitanja.md#q11)). Mogu trajati više dana ([🟩 `Q12`](atomarna-pitanja.md#q12)). Potvrda se ne prilaže ([🟩 `Q13`](atomarna-pitanja.md#q13)).
- Aplikacija ne prati kako se stječu slobodni dani ([🟩 `Q9.1`](atomarna-pitanja.md#q9-1)).
- Zahtjev se ne uređuje: otkaže se i podnese novi ([🟩 `Q33`](atomarna-pitanja.md#q33), [🟩 `Q34`](atomarna-pitanja.md#q34)).
- Zahtjev podnosi samo vlasnik, nitko u ime drugoga ([🟩 `Q29`](atomarna-pitanja.md#q29)).

## 3. Odobravanje

- Voditelj odobrava i odbija zahtjeve članova svih timova koje vodi ([🟦 `R1`](brief.md#rules), [🟩 `Q3`](atomarna-pitanja.md#q3)).
- Zahtjev člana više timova smije odobriti voditelj bilo kojeg od tih timova; dovoljno je jedno odobrenje ([🟩 `Q5.1`](atomarna-pitanja.md#q5-1)).
- Zahtjev voditelja ide adminu, i kad je voditelj član tuđeg tima ([🟦 `R3`](brief.md#rules), [🟪 `D2`](#d2)).
- Zahtjev zaposlenika bez tima odobrava admin ([🟩 `Q4`](atomarna-pitanja.md#q4)).
- Admin odobrava i odbija bilo koji zahtjev ([🟦 `R2`](brief.md#rules)).
- Nitko ne odobrava ni ne odbija vlastiti zahtjev ([🟦 `R3`](brief.md#rules)). Iznimka: admin smije odobriti vlastiti zahtjev ([🟩 `Q1.1`](atomarna-pitanja.md#q1-1)), bilo kojeg tipa ([🟪 `D15`](#d15)).
- Odobrava se i odbija samo zahtjev na čekanju ([🟦 `R4`](brief.md#rules)), i kad su mu datumi prošli ([🟩 `Q35`](atomarna-pitanja.md#q35)).
- Odbijanje traži komentar, odobrenje ne ([🟦 `R5`](brief.md#rules)).
- Zahtjev na čekanju ne istječe ([🟩 `Q36`](atomarna-pitanja.md#q36)).

## 4. Otkazivanje

Tko smije odobriti zahtjev, smije ga i otkazati ([🟪 `D14`](#d14)).

| Tko | Na čekanju | Odobren, nije počeo | Odobren, počeo |
| --- | --- | --- | --- |
| Vlasnik | Otkazuje, razlog opcionalan ([🟦 `R6`](brief.md#rules)) | Otkazuje, razlog opcionalan ([🟦 `R7`](brief.md#rules)) | Ne |
| Tko smije odobriti (odjeljak 3) | Otkazuje, razlog obavezan ([🟩 `Q30`](atomarna-pitanja.md#q30), [🟩 `Q31`](atomarna-pitanja.md#q31), [🟪 `D3`](#d3), [🟪 `D14`](#d14)) | Otkazuje, razlog obavezan ([🟩 `Q32`](atomarna-pitanja.md#q32), [🟪 `D3`](#d3), [🟪 `D14`](#d14)) | Ne ([🟩 `Q30.1`](atomarna-pitanja.md#q30-1), [🟩 `Q31.1`](atomarna-pitanja.md#q31-1)) |

- Zahtjev koji počinje danas je počeo ([🟩 `Q23`](atomarna-pitanja.md#q23)).
- Otkazani zahtjev prelazi u Cancelled.
- Izmjene već započetog zahtjeva rade se naknadno, izravno u bazi ([🟩 `Q30.1`](atomarna-pitanja.md#q30-1), [🟪 `D6`](#d6)).

## 5. Validacija

- Aplikacija broji samo radne dane. Preskaču se vikendi ([🟦 `R10`](brief.md#rules)). Blagdani su proširenje ([🟪 `D1`](#d1)).
- Zahtjev bez ijednog radnog dana je nevaljan ([🟦 `R10`](brief.md#rules)).
- Završni datum nije prije početnog ([🟦 `R8`](brief.md#rules)).
- Korisnik nema dva zahtjeva na čekanju ili odobrena čiji se datumi preklapaju, bez obzira na tip ([🟦 `R9`](brief.md#rules)).
- Zahtjev ne počinje prije datuma zaposlenja ([🟪 `D4`](#d4)).
- Zahtjev je unutar jedne kalendarske godine ([🟦 `brief`](brief.md)). Zahtjev preko granice godine je proširenje (odjeljak 9).
- Datumi u prošlosti su dopušteni za sve tipove, bez granice unatrag ([🟩 `Q19`](atomarna-pitanja.md#q19)–[`Q21`](atomarna-pitanja.md#q21), [🟩 `Q19.1`](atomarna-pitanja.md#q19-1)).
- Datumi u budućnosti su dopušteni bez granice ([🟩 `Q22`](atomarna-pitanja.md#q22), [🟩 `Q25`](atomarna-pitanja.md#q25)).
- Nema minimalnog roka najave ni najduljeg trajanja ([🟩 `Q26`](atomarna-pitanja.md#q26), [🟩 `Q27`](atomarna-pitanja.md#q27)).
- Nema najvećeg broja zahtjeva na čekanju ([🟩 `Q38`](atomarna-pitanja.md#q38)) ni minimalne prisutnosti u timu ([🟩 `Q40`](atomarna-pitanja.md#q40)).

## 6. Saldo godišnjeg

- Svaki korisnik dobiva 20 radnih dana godišnjeg za svaku kalendarsku godinu ([🟦 `R11`](brief.md#rules), [🟪 `D5`](#d5)).
- U godini zaposlenja dobiva razmjerni dio: 20 × broj mjeseci / 12, zaokruženo prema gore. Broje se mjeseci od mjeseca zaposlenja do prosinca, uključivo ([🟩 `Q15`](atomarna-pitanja.md#q15), [🟩 `Q15.1`](atomarna-pitanja.md#q15-1), [🟩 `Q15.2`](atomarna-pitanja.md#q15-2), [🟪 `D5`](#d5)). Primjer: zaposlen u lipnju, 7 mjeseci, 20 × 7 / 12 = 11,67, dakle 12 dana.
- Za tekuću godinu korisnik vidi iskorištene (odobrene), dane na čekanju i preostale dane ([🟦 `R12`](brief.md#rules)).
- Zahtjev za godišnji ne smije premašiti preostali saldo godine u koju pada. Zahtjevi na čekanju troše saldo ([🟦 `R13`](brief.md#rules), [🟪 `D5`](#d5)).
- Bolovanje i slobodni dani ne troše saldo ([🟦 `R14`](brief.md#rules)).
- Neiskorišteni dani se ne prenose u sljedeću godinu; prijenos je proširenje ([🟪 `D5`](#d5)).
- Korekcije se rade izravno u bazi, izmjenom zahtjeva ili datuma zaposlenja ([🟪 `D6`](#d6)).

## 7. Vidljivost

API provodi sva pravila vidljivosti. Skrivanje u frontendu se ne računa ([🟦 `brief`](brief.md)).

Zahtjevi s detaljima:

| Tko | Vidi |
| --- | --- |
| Zaposlenik | Svoje zahtjeve sa svim detaljima, uključujući komentar odluke, tko je odlučio i tko je otkazao ([🟦 `brief`](brief.md), [🟩 `Q44`](atomarna-pitanja.md#q44)–[`Q46`](atomarna-pitanja.md#q46)) |
| Voditelj | Sve zahtjeve članova timova koje vodi, u svim stanjima, s tipom i razlogom ([🟦 `brief`](brief.md)). Saldo članova timova koje vodi ([🟩 `Q16`](atomarna-pitanja.md#q16)) |
| Admin | Sve zahtjeve i sve salde u tvrtki ([🟦 `brief`](brief.md), [🟩 `Q17`](atomarna-pitanja.md#q17)) |

Kalendar:

| Tko gleda | Svoje zahtjeve | Zahtjeve drugih |
| --- | --- | --- |
| Zaposlenik | Na čekanju i odobrene ([🟩 `Q41`](atomarna-pitanja.md#q41)) | Samo odobrene, kolega iz timova u kojima je član, samo ime i datumi; bez tipa, razloga i zahtjeva na čekanju ([🟦 `brief`](brief.md), [🟩 `Q47`](atomarna-pitanja.md#q47)) |
| Voditelj | Na čekanju i odobrene | Na čekanju i odobrene, članova timova koje vodi, s tipom ([🟩 `Q42`](atomarna-pitanja.md#q42)). Za ostale timove u kojima je član vidi isto što i zaposlenik |
| Admin | Na čekanju i odobrene | Na čekanju i odobrene, svih u tvrtki ([🟩 `Q43`](atomarna-pitanja.md#q43)) |

- Odbijeni i otkazani zahtjevi ne prikazuju se nikome na kalendaru ([🟩 `Q48`](atomarna-pitanja.md#q48)).
- Adminov kalendar po defaultu prikazuje sve timove, uključujući korisnike bez tima, s filtrom po timu ([🟩 `Q43`](atomarna-pitanja.md#q43)).
- Zaposlenik u više timova vidi se na kalendaru svakog svog tima. Odsutnost korisnika bez tima vide samo admini.

## 8. API i isporuka

- Prijava lozinkom sa seedanim lozinkama vraća JWT; svaki poziv šalje `Authorization: Bearer` ([🟩 `Q49`](atomarna-pitanja.md#q49), [🟩 `Q49.1`](atomarna-pitanja.md#q49-1)).
- Standardni HTTP statusi, format `application/problem+json` ([🟩 `Q50`](atomarna-pitanja.md#q50), [🟪 `D11`](#d11)):
  - 400: neispravan unos (npr. nedostaje polje, završni datum prije početnog);
  - 401: korisnik nije prijavljen;
  - 403: korisnik nema pravo na radnju;
  - 404: zahtjev ne postoji;
  - 409: kršenje pravila (preklapanje datuma, premašen saldo, krivo stanje zahtjeva).
- Bez Swaggera; dijagrami tokova i sekvencijski dijagrami ([🟩 `Q51`](atomarna-pitanja.md#q51)).
- Cijela aplikacija u Docker kontejnerima, pokretanje jednom naredbom; deploy na server ne treba ([🟩 `Q53`](atomarna-pitanja.md#q53), [🟩 `Q53.2`](atomarna-pitanja.md#q53-2)).
- Sve na engleskom: UI, README, kod ([🟩 `Q54`](atomarna-pitanja.md#q54)).
- Seed: korisnici, timovi, lozinke i datumi zaposlenja, bez zahtjeva ([🟩 `Q55`](atomarna-pitanja.md#q55)). Svi imaju lozinku `password`; svi su zaposleni 2020-01-01, osim Toma, zaposlenog 2026-06-01 ([🟪 `D16`](#d16)).
- Repozitorij: GitHub ([🟩 `Q56`](atomarna-pitanja.md#q56), svejedno).

### Opseg i očekivanja

- POC za oko 20 korisnika ([🟩 `Q53.1`](atomarna-pitanja.md#q53-1)).
- Prvo pravila iz briefa s testovima, po pravilu 80/20; proširenja se opisuju u README-u ([🟩 `Q53.3`](atomarna-pitanja.md#q53-3): "?", [🟪 `D7`](#d7)).
- U aplikaciji nema AI funkcionalnosti ([🟩 `Q53.4`](atomarna-pitanja.md#q53-4)).
- Proces rada s AI-jem raspravljamo na razgovoru; README ga kratko spominje ([🟩 `Q53.5`](atomarna-pitanja.md#q53-5)).
- Rok je strog: 29. listopada 2026. ([🟩 `Q53.6`](atomarna-pitanja.md#q53-6)).
- Opcionalni dodaci iz briefa ne ocjenjuju se ako nisu napravljeni ([🟩 `Q53.7`](atomarna-pitanja.md#q53-7)).
- Razumijevanje koda: zašto je nešto napravljeno na taj način, a ne neki drugi ([🟩 `Q53.8`](atomarna-pitanja.md#q53-8)).

## 9. Proširenja

Kolega ih je opisao u odgovorima, ali ih ne radimo u POC-u. Opisujemo ih u README-u, s tim gdje bi se dodala ([🟪 `D7`](#d7)).

| Proširenje | Izvor | Gdje bi se dodalo |
| --- | --- | --- |
| Prijenos neiskorištenih dana u sljedeću godinu, vrijede do 30. lipnja, bez gornje granice; prvo se troše najstariji dani | [🟩 `Q14`](atomarna-pitanja.md#q14), [🟩 `Q14.1`](atomarna-pitanja.md#q14-1), [🟩 `Q14.2`](atomarna-pitanja.md#q14-2), [🟩 `Q18`](atomarna-pitanja.md#q18) | Izračun salda |
| Hrvatski blagdani se ne broje kao radni dani | [🟦 `brief`](brief.md) (opcionalni dodatak), [🟪 `D1`](#d1) | Funkcija koja broji radne dane |
| Zahtjev preko granice godine ostaje jedan zahtjev, a dani se broje po godinama | [🟩 `Q28`](atomarna-pitanja.md#q28) | Validacija i izračun salda |
| Bolovanje smije preklopiti odobreni godišnji: "onda ti se GO otkazuje / refundira" | [🟩 `Q57`](atomarna-pitanja.md#q57) | Provjera preklapanja i izračun salda |

## 10. Naše odluke

| # | Odluka | Zašto |
| --- | --- | --- |
| <a id="d1"></a>🟪 `D1` | Preskaču se samo vikendi; hrvatski blagdani su proširenje | Brief ih navodi kao opcionalni dodatak ("Only take these on if the core is done"). Dodaju se u funkciju koja broji radne dane |
| <a id="d2"></a>🟪 `D2` | Zahtjev voditelja uvijek ide adminu, i kad je voditelj član tuđeg tima | [🟦 `R3`](brief.md#rules) ("A team lead's request goes to an admin") ima prednost pred [🟩 `Q5.1`](atomarna-pitanja.md#q5-1) |
| <a id="d3"></a>🟪 `D3` | Tko otkazuje tuđi zahtjev (voditelj, admin) mora upisati razlog | [🟩 `Q37`](atomarna-pitanja.md#q37) ("Može, neka bude polje") je dvosmislen. Tumačimo ga tako da se ne sudara s [🟦 `R5`](brief.md#rules) ("A rejection needs a comment"), jer bi se inače odbijanje bez komentara moglo zamijeniti otkazivanjem |
| <a id="d4"></a>🟪 `D4` | Zahtjev ne počinje prije datuma zaposlenja | Prije datuma zaposlenja korisnik nije zaposlenik, pa vrijedi za sve tipove |
| <a id="d5"></a>🟪 `D5` | Svaka godina ima svojih 20 dana, bez prijenosa; zahtjev troši saldo godine u koju pada. U godini zaposlenja broje se mjeseci od mjeseca zaposlenja do prosinca | [🟦 `R11`](brief.md#rules); [🟩 `Q18`](atomarna-pitanja.md#q18) ("Za tekucu, ako ima od prijasnje godine, prijasnji se prvi trosi") opisuje prijenos, a prijenos je proširenje ([🟪 `D7`](#d7)); [🟩 `Q15.1`](atomarna-pitanja.md#q15-1) kaže "po mjesecima", ali ne od kojeg |
| <a id="d6"></a>🟪 `D6` | Korekcije (datum zaposlenja, već započet zahtjev) rade se izravno u bazi (SQL), postupak u README-u. Nema posebne tablice korekcija salda | Upravljanje korisnicima je izvan opsega briefa; [🟩 `Q30.1`](atomarna-pitanja.md#q30-1) ("after the fact izmjene") |
| <a id="d7"></a>🟪 `D7` | 80/20: prvo pravila iz briefa s testovima, na strukturi u koju se proširenja lako dodaju. Proširenja iz odgovora kolege ne radimo, nego ih opisujemo u README-u | [🟦 `brief`](brief.md): "a smaller app with solid rules and tests than a large app with gaps". Kolega na [🟩 `Q53.3`](atomarna-pitanja.md#q53-3) nije dao odgovor ("?"), pa smo odlučili sami |
| <a id="d8"></a>🟪 `D8` | Vremenska zona se postavlja varijablom `TZ`, u `docker-compose` Europe/Zagreb | [🟩 `Q24`](atomarna-pitanja.md#q24); kontejner je inače u UTC-u |
| <a id="d9"></a>🟪 `D9` | Seed bez demo zahtjeva | [🟩 `Q55`](atomarna-pitanja.md#q55); bitno je da aplikacija radi |
| <a id="d10"></a>🟪 `D10` | Ekran prijave je forma: korisnik upisuje korisničko ime i lozinku, bez popisa korisnika | [🟦 `brief`](brief.md): "A login form with seeded passwords works". Popis bi trebao javni endpoint ili drugi izvor istine u frontendu; korisnička imena su u README-u (L10) |
| <a id="d11"></a>🟪 `D11` | Format grešaka `application/problem+json` | [🟩 `Q50`](atomarna-pitanja.md#q50), po volji |
| <a id="d12"></a>🟪 `D12` | Voditelj s više timova ima jedan kalendar s filtrom po timu | [🟩 `Q3`](atomarna-pitanja.md#q3) |
| <a id="d13"></a>🟪 `D13` | Zahtjev bilježi vrijeme stvaranja, odluke i otkazivanja | Uz "tko je odlučio" i "tko je otkazao" ([🟩 `Q46`](atomarna-pitanja.md#q46), [🟩 `Q30`](atomarna-pitanja.md#q30), [🟩 `Q31`](atomarna-pitanja.md#q31)) zna se i kada |
| <a id="d14"></a>🟪 `D14` | Tko smije odobriti zahtjev, smije ga i otkazati | [🟩 `Q30`](atomarna-pitanja.md#q30) i [🟩 `Q31`](atomarna-pitanja.md#q31) dopuštaju otkazivanje voditelju i adminu. Jedno pravilo umjesto dva, pa se ne sudara s [🟪 `D2`](#d2): voditelj ne otkazuje zahtjev drugog voditelja |
| <a id="d15"></a>🟪 `D15` | Admin smije odobriti vlastiti zahtjev bilo kojeg tipa | [🟩 `Q1.1`](atomarna-pitanja.md#q1-1) pitao je samo za godišnji. Jedno pravilo, bez provjere tipa |
| <a id="d16"></a>🟪 `D16` | Seed: lozinka `password` za sve; datum zaposlenja 2020-01-01 za sve osim Toma (2026-06-01) | Specifikacija nije određivala vrijednosti. Ista lozinka je najjednostavniji način da se djeluje kao svaki korisnik ([🟦 `brief`](brief.md)); Tom pokazuje razmjerni saldo u godini zaposlenja ([🟩 `Q15`](atomarna-pitanja.md#q15)): 7 mjeseci, 12 dana |
| <a id="d17"></a>🟪 `D17` | Voditelj i admin otkazuju tuđi odobreni zahtjev koji nije počeo kroz ekran Approvals (tablica "Approved requests", razlog obavezan) | [🟩 `Q30`](atomarna-pitanja.md#q30), [🟩 `Q31`](atomarna-pitanja.md#q31), [🟩 `Q32`](atomarna-pitanja.md#q32): naručitelj dopušta otkazivanje tuđeg zahtjeva, pa ga korisnik mora moći napraviti u aplikaciji (L39, T8.1). Otkazivanje tuđeg zahtjeva na čekanju kroz UI još nije napravljeno (samo API) |

## 11. Odstupanja od briefa

Za README. Slijedimo odgovor naručitelja, a ne brief.

| Brief | Sad | Izvor |
| --- | --- | --- |
| "Team lead. Leads one team." | Voditelj može voditi više timova | [🟩 `Q3`](atomarna-pitanja.md#q3) |
| Admin: "Can see and act on everything" | Admin je zaposlenik i ima saldo | [🟩 `Q1`](atomarna-pitanja.md#q1) |
| [🟦 `R3`](brief.md#rules): "Nobody can approve or reject their own request." | Admin smije odobriti vlastiti zahtjev | [🟩 `Q1.1`](atomarna-pitanja.md#q1-1) |
| [🟦 `R6`](brief.md#rules) i [🟦 `R7`](brief.md#rules): otkazuje samo vlasnik | Otkazuje i tko smije odobriti, uz obavezan razlog | [🟩 `Q30`](atomarna-pitanja.md#q30), [🟩 `Q31`](atomarna-pitanja.md#q31), [🟪 `D3`](#d3), [🟪 `D14`](#d14) |
| [🟦 `R11`](brief.md#rules): 20 dana godišnje | Razmjerni saldo u godini zaposlenja | [🟩 `Q15`](atomarna-pitanja.md#q15) |
| Docker je opcionalni dodatak | Docker je obavezan | [🟩 `Q53`](atomarna-pitanja.md#q53) |

## 12. Ekrani

Tri ekrana iz briefa ([🟦 `brief`](brief.md)):

1. **My time off:** moji zahtjevi sa stanjem, forma za novi zahtjev, otkazivanje i saldo godišnjeg.
2. **Approvals:** samo za voditelje i admine; zahtjevi na čekanju koje smiju odobriti, s odobravanjem i odbijanjem; ispod njih odobreni tuđi zahtjevi koje smiju otkazati, a nisu počeli ([🟪 `D17`](#d17)).
3. **Team calendar:** tko je odsutan u odabranom mjesecu, po pravilima vidljivosti; popis po danima.

Kad backend odbije zahtjev, ekran prikazuje poruku greške, npr. preklapanje ili premašen saldo ([🟦 `brief`](brief.md)).

## 13. Otvoreno

- ~~Dijagrami tokova i sekvencijski dijagrami ([🟩 `Q51`](atomarna-pitanja.md#q51))~~: napravljeno u [diagrams.md](../diagrams.md).
- Gdje se radi promjena uživo na intervjuu.
