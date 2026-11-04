# Procjena: Haiku 5.5 medium, 10 zasebnih leća

Otvorio sam fajl:redak za svaki nalaz. Putanje su skraćene kao u zadatku, a brojevi redaka vrijede za radnu kopiju.

## A. Tablica

| oznaka | ocjena | I/N/NOVO | obrazloženje |
|---|---|---|---|
| L1-1 | STVARAN | I43 | Ista petlja `filter/findFirst` stoji u tri policy klase (`ApprovalPolicy:26-30`, `VisibilityPolicy:26-30`, `CancelPolicy:39-41`). |
| L1-2 | STVARAN | I9 | `leadsTeamOfOwner` ima isto tijelo u `ApprovalPolicy:33-38` i `VisibilityPolicy:37-41`. |
| L1-3 | STVARAN | I14 | `findById().orElseThrow()` stoji na svim navedenim mjestima. |
| L1-4 | STVARAN | I13 (+ I15 dijelom) | `BY_START_THEN_ID` je kopiran u `ApprovalService:19` i `CancelService:20`; upiti `pending` i `cancellable` imaju isti oblik. |
| L1-5 | STVARAN (trivijalno) | NOVO | `checkMayCancel` i `loadDecidable` imaju isti oblik, ali s drugom porukom i drugom politikom. Autor sam dopušta "ostaviti kako jest". |
| L1-6 | STVARAN (trivijalno) | NOVO | Svih šest handlera u `ApiExceptionHandler:28-56` ima isto tijelo, razlikuju se u statusu. |
| L1-7 | STVARAN (trivijalno) | NOVO | Četiri klase iznimki imaju isti konstruktor. Autor sam kaže da je to bezopasno. |
| L1-8 | STVARAN | I27 | `.map(RequestView::from).toList()` stoji u `ApprovalController:59`, `CancelController:45` i `VisibilityController:55-58`. |
| L1-9 | STVARAN | I10 | `isSelf` (`VisibilityPolicy:33-35`) daje true za dva nespremljena korisnika, a `User.isSameAs` (`User.java:71-80`) false. |
| L1-10 | STVARAN (trivijalno) | NOVO | `approveRequest` i `rejectRequest` u `api.ts:133-139` imaju isto tijelo. Autor sam kaže da su jednoredne omotnice uobičajene. |
| L2-1 | STVARAN | I46 | Skup statusa stoji u `Status.java:8`, `V1__schema.sql:29`, `types.ts:5` i `labels.ts:11`. |
| L2-2 | STVARAN | I47 | Isto za tip: `TimeOffType.java:4`, `V1__schema.sql:25`, `types.ts:3`, `labels.ts:5`. |
| L2-3 | STVARAN | I47 | `TYPE_OPTIONS` (`labels.ts:18`) je ručna kopija ključeva `TYPE_LABELS`. |
| L2-4 | STVARAN | I6 | `CalendarService:78` i `RequestService:102` imaju isti uvjet PENDING ili APPROVED. |
| L2-5 | STVARAN | I6 (+ I5) | `MyTimeOffScreen:37` ponavlja isti uvjet na frontendu. |
| L2-6 | STVARAN | I48 | Literali "CANCELLED" i "PENDING" stoje u `MyTimeOffScreen:23,25`. |
| L2-7 | STVARAN (trivijalno) | NOVO | Literal "VACATION" u `EMPTY_FORM` (`MyTimeOffScreen:18`). |
| L2-8 | STVARAN | I7 | `CalendarService:90` sam tumači što LIMITED znači, a `VisibilityPolicy:10` tvrdi da je to jedino mjesto. |
| L3-1 | STVARAN | I8 | Ternarni operator za admina stoji u `CalendarService:68`. |
| L3-2 | STVARAN (refaktor kriv) | I31 | Filter u `CancelService:67` jest uvjet izvan `CancelPolicy`. Tvrdnja da `canCancel` "već isključuje vlastite zahtjeve" ne vrijedi: `CancelPolicy:22` vlasniku dopušta otkazivanje. Predloženo brisanje filtera pokvarilo bi popis. |
| L3-3 | NIJE NALAZ | nema | Autor sam kaže da je to samo prikaz uloga i da nema obavezne promjene (`UserView:19-20`). |
| L4-1 | STVARAN | I3 | `BalanceController:34` sam računa "danas". |
| L4-2 | STVARAN | I3 | `VisibilityService:90` ponavlja isto. |
| L4-3 | STVARAN | I25 | `TokenService:27` zove `Instant.now()` mimo `Clock`. |
| L4-4 | STVARAN (marginalno) | NOVO | `currentMonth` (`month.ts:18`) ne prima vrijeme izvana. To je tvrdnja o testabilnosti, a I19 govori o vremenskoj zoni. |
| L5-1 | STVARAN (svjesno prihvaćeno) | I34 | Provjera preklapanja (`RequestService:100-105`) bez zaključavanja. Nema `disabled` na gumbu (grep). |
| L5-2 | STVARAN (svjesno prihvaćeno) | I34 | Provjera salda (`RequestService:117-121`) bez zaključavanja. |
| L5-3 | STVARAN (svjesno prihvaćeno) | I40 | `TimeOffRequest` nema `@Version` (`TimeOffRequest:19-21`), a grep ne nalazi ni `@DynamicUpdate` ni `@Lock`. |
| L5-4 | STVARAN (svjesno prihvaćeno) | I41 | `CancelService:45-54`, isti razlog. |
| L5-5 | STVARAN (dijelom) | I34 (dio), dio NOVO | `V1__schema.sql:22-39` stvarno nema ograničenja za preklapanje, što je I34. Zasebna ideja o CHECK-u koji veže status s poljima odluke (`:29,31,34`) nije u `nalazi.md`. |
| L5-6 | STVARAN (svjesno prihvaćeno) | I34 | Nema postavke izolacije. Nalaz je zapravo komentar uz L5-1 do L5-4. |
| L6-1 | STVARAN | I5 | Gumb se prikazuje po statusu (`MyTimeOffScreen:36-38`), a backend odbija počeli zahtjev (`CancelService:92-96`). |
| L6-2 | STVARAN | I31 | Isto kao L3-2 (`CancelService:67`). Ovdje je opis točan: namjerno, ali je if izvan policyja. |
| L6-3 | STVARAN | S7 (nije I) | Tuđi PENDING zahtjev može se otkazati samo kroz API (`CancelPolicy:23`, `CancelService:65`). To je D17, poznato. |
| L6-4 | STVARAN | I51 | Polje "Cancel reason" nije označeno obaveznim (`ApprovalsScreen:176`). |
| L7-1 | STVARAN | I16 | `isPending` (`TimeOffRequest:81`) nema poziva u main kodu. |
| L7-2 | STVARAN | I17 | `isWorkingDay` (`WorkingDays:15`) se izvan klase zove samo iz testa. |
| L7-3 | STVARAN | I17 | `entitlement` (`BalanceService:35`) se izvan klase zove samo iz testa. |
| L7-4 | STVARAN | I32 | Šest polja u `types.ts` (25, 32, 34, 36, 38, 40) frontend nigdje ne čita (grep). |
| L8-1 | STVARAN | S22 (nije I) | `commentText` (`MyTimeOffScreen:33`) preferira `decisionComment` pred `cancelReason`. |
| L8-2 | STVARAN | I37 | `ApprovalService:44` čisti komentar, `:59` ne. |
| L8-3 | STVARAN (marginalno) | NOVO | `AuthService:30` ne obrezuje korisničko ime, pa "alice " daje 401. |
| L8-4 | STVARAN | I50 | `Texts.java:14` vraća neobrezan tekst. |
| L8-5 | NIJE NALAZ | nema | `@Size` broji i razmake, ali je odbijanje 501 razmaka zbog duljine ispravno. Autor sam preporučuje ništa ne mijenjati. |
| L9-1 | STVARAN | I42 | `AuthService:27,31-33`: BCrypt se ne izvršava za nepostojećeg korisnika. |
| L9-2 | STVARAN | S10 (nije I) | `application.properties:13` ima javni zadani ključ od 37 bajtova, a provjera u `SecurityConfig:53` ga propušta. |
| L9-3 | NETOČAN | N6 | Komentar `ApprovalsScreen:68` navodi R5 i D3, dakle odbijanje i razlog otkazivanja, za koje backend stvarno vraća 400. |
| L9-4 | STVARAN | I11 | `WorkingDays:9` tvrdi "the one place", a `month.ts:39-41` ima vlastito vikend pravilo. |
| L9-5 | NETOČAN | nije nalaz | R10 u `brief.md:84` je jedno pravilo: Pon-Pet, a zahtjev samo s vikendom je nevaljan. Komentari u `RequestService:82`, `WorkingDays:21` i `month.ts:28` ne koriste "tri različita pravila". Autor sam kaže da tekst pravila nije čitao. Vikend je ionako zatvorena tema. |
| L9-6 | STVARAN | I56 | `CalendarService:64` pretpostavlja Q3, a pravilo postoji samo u `V2__seed.sql:18`. |
| L9-7 | NETOČAN | N5 | Admin smije odobriti vlastiti zahtjev prema D15 (`specifikacija.md:58,177`), a komentar na `ApprovalPolicy:18` to kaže. |
| L9-8 | STVARAN | I55 | `UserView:5` kaže "lazy teams", a `isTeamLead()` čita `ledTeams` (`User:82-84`). |
| L10-1 | STVARAN | I5 | `MyTimeOffScreen:37` naspram `CancelService:92`. |
| L10-2 | STVARAN | I51 | `ApprovalsScreen:176`, `CancelPolicy:23`, `CancelService:86`. |
| L10-3 | REDAK KRIV | I11 | Tvrdnja je točna, ali komentar "The one place..." je na `WorkingDays.java:9`, ne `:10`. |
| L10-4 | STVARAN | I19 | `month.ts:17-18` koristi zonu preglednika. |
| L10-5 | STVARAN | I52 | `noValidate` na `MyTimeOffScreen:207`. |

## B. Sažetak brojeva

- **Brojevi:** 57 sirovih nalaza. Stvarnih je 51, netočnih 3 (L9-3, L9-5, L9-7), redak kriv 1 (L10-3), nije nalaz 2 (L3-3, L8-5), neprovjerljivih 0.
  - Od 51 stvarnih, 5 su stvarne tvrdnje o stvarima koje su svjesno prihvaćene (L5-1 do L5-4 i L5-6).
- **Pokrivenost:** 32 od 56 I-ova. To su I3, I5, I6, I7, I8, I9, I10, I11, I13, I14, I15 (dijelom), I16, I17, I19, I25, I27, I31, I32, I34, I37, I40, I41, I42, I43, I46, I47, I48, I50, I51, I52, I55 i I56.
  - Bez I15 (samo dijelom) je 31.
  - Netočni nalazi pogodili su N5 (L9-7) i N6 (L9-3), a L9-5 nema odgovarajuću N oznaku.
  - Stvarne nalaze koji su u `nalazi.md` samo kao S našli su S7 (L6-3), S10 (L9-2) i S22 (L8-1).
- **Ključni (I1–I7, I9, I11):** pokriveno je 6 od 9 (I3, I5, I6, I7, I9, I11). Fale I1, I2 i I4. Leća 6 je I4 (`hasStarted` izvan policyja) i I36 gledala, ali ih ocijenila "u redu".
- **Novi stvarni nalazi:**
  - Nijedan nije vrijedan većeg popravka, a svi su niskog prioriteta. Predlažem upis samo prva tri.
  - I57: `AuthService.login` ne obrezuje korisničko ime, pa "alice " daje 401 (`AuthService.java:30`, `AuthController.java:39`). Marginalno (L8-3).
  - I58: Baza ne veže `status` s poljima odluke i otkazivanja, pa su APPROVED bez `decided_by_id` ili CANCELLED bez `cancelled_by_id` dopušteni (`V1__schema.sql:29,31,34`). Dio L5-5.
  - I59: Literal "VACATION" u `EMPTY_FORM` (`MyTimeOffScreen.tsx:18`), L2-7. Trivijalno.
  - Ne preporučujem upis, osim kao trivijalnosti: L1-5, L1-6, L1-7 i L1-10 (isti oblik metoda ili klasa) i L4-4 (`currentMonth` bez parametra).
- **Ponavljanja među lećama:**
  - I5 su našle 3 leće: L2-5, L6-1 i L10-1.
  - I6: L2-4 i L2-5 (ista leća).
  - I11: L9-4 i L10-3.
  - I51: L6-4 i L10-2.
  - I31: L3-2 i L6-2.
  - I3: L4-1 i L4-2 (ista leća).
  - L5-1 do L5-4 su u istoj leći tri svjesno prihvaćena nalaza (I34, I40, I41).
  - Leće 1, 7 i 8 nisu se preklapale ni s jednom drugom.

## C. Pet rečenica

1. Zasebne leće daju vrlo preciznu stvarnost: svi navedeni retci i citati su točni osim jednog retka (`WorkingDays.java:10` umjesto `:9`), a 51 od 57 nalaza je stvaran.
2. Pokrivenost je dobra za duplikate, skupove vrijednosti, mrtav kod i frontend naspram backenda (32 od 56 I-ova), i leće su se na I5, I6 i I11 međusobno potvrdile.
3. Slabo hvata nalaze koji traže pravila i dokumente: fale I1 (R14 dvaput), I2 (R5 samo u API-ju) i I4 (`hasStarted` izvan policyja), a cijeli S-svijet je gotovo netaknut (nema S1 do S6).
4. Leće koje nisu čitale tekst pravila (leća 9) sama prijavljuju kriva "R10 koristi tri značenja" i "admin može odobriti vlastiti zahtjev" (N5), pa opseg bez dokumenata daje poznate lažne uzbune.
5. Dio izlaza je šum (trivijalni "isti oblik" nalazi u leći 1 i L5-6, L8-5, L3-3, L4-4), a jedan predloženi refaktor (L3-2, brisanje filtera) pokvario bi ponašanje.