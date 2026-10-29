# Sučelje

Opis sučelja po tasku, bez koda. Ponašanje određuje [specifikacija](specifikacija.md); ovdje su imena, oblici i ugovori koje testovi mogu koristiti.

## T1 Shema i seed

Slojevi: 5 Domena, 6 Repozitorij, 7 Baza (arhitektura). Paket: `com.example.timeoff.domain` za domenu i repozitorije.

### Baza (Flyway)

`V1__schema.sql`:

| Tablica | Stupci | Ograničenja |
| --- | --- | --- |
| `users` | `id`, `username`, `name`, `password_hash`, `admin`, `hire_date` | `username` jedinstven; sve obavezno; `admin` default `false` |
| `teams` | `id`, `name`, `lead_id` | `name` jedinstven; `lead_id` obavezan, FK na `users` (Q6, Q7: tim ima točno jednog voditelja) |
| `team_members` | `team_id`, `user_id` | primarni ključ (`team_id`, `user_id`); FK na `teams` i `users` |
| `time_off_requests` | `id`, `owner_id`, `type`, `start_date`, `end_date`, `reason`, `status`, `decision_comment`, `decided_by_id`, `decided_at`, `cancel_reason`, `cancelled_by_id`, `cancelled_at`, `created_at` | `owner_id`, `type`, `start_date`, `end_date`, `status`, `created_at` obavezni; `end_date >= start_date` (R8: završni datum nije prije početnog); `type` jedna od 3 vrijednosti; `status` jedna od 4; FK `decided_by_id`, `cancelled_by_id` na `users` |

- Datumi su `date`, vremena `timestamptz`.
- Voditelj je upisan i u `team_members` svakog tima koji vodi (Q3: voditelj je član svakog tima koji vodi).
- Uloga voditelja se ne sprema: korisnik je voditelj ako vodi barem jedan tim. Admin je stupac `admin` (Q2: korisnik može imati više uloga).

`V2__seed.sql`: korisnici i timovi iz briefa, bez zahtjeva (Q55: seed bez primjera zahtjeva).

| `username` | `name` | Admin | Vodi | Član | `hire_date` |
| --- | --- | --- | --- | --- | --- |
| `ana` | Ana | ne | Platform | Platform | 2020-01-01 |
| `ivan` | Ivan | ne | | Platform | 2020-01-01 |
| `marko` | Marko | ne | | Platform | 2020-01-01 |
| `petra` | Petra | ne | | Platform | 2020-01-01 |
| `luka` | Luka | ne | Mobile | Mobile | 2020-01-01 |
| `sara` | Sara | ne | | Mobile | 2020-01-01 |
| `tom` | Tom | ne | | Mobile | 2026-06-01 |
| `eva` | Eva | da | | | 2020-01-01 |

- Lozinka je `password` za sve korisnike, u bazi kao BCrypt hash (`BCryptPasswordEncoder` iz Spring Securityja) (D16: seed, lozinka `password` za sve).
- Datumi zaposlenja su iz D16 (seed: 2020-01-01 za sve osim Toma, 2026-06-01). Tom zato za 2026. ima razmjerni saldo: 20 × 7 / 12 = 11,67, zaokruženo 12 dana (Q15: tko počne raditi usred godine dobiva razmjerno manje dana; D5: broje se mjeseci od mjeseca zaposlenja do prosinca).

### Domena

Java tipovi: datumi (`hireDate`, `startDate`, `endDate`) su `LocalDate`, vremena (`createdAt`, `now`, `decidedAt`, `cancelledAt`) su `Instant`. Svako polje ima getter `getX()` (npr. `getStatus()`, `getHireDate()`); iznimka je `isAdmin()`.

**`Role`**: nema enuma. Uloge se izvode: `isAdmin()` iz stupca, `isTeamLead()` iz timova koje vodi.

**`TimeOffType`** (enum): `VACATION`, `SICK_LEAVE`, `DAY_OFF`.

**`Status`** (enum) s tablicom dopuštenih prijelaza:

| Iz | U |
| --- | --- |
| `PENDING` | `APPROVED`, `REJECTED`, `CANCELLED` |
| `APPROVED` | `CANCELLED` |
| `REJECTED` | nema |
| `CANCELLED` | nema |

- `canTransitionTo(target)`: `true` samo za red iz tablice.

**`Team`**: `id`, `name`, `lead` (`User`), `members` (kolekcija `User`). U T1 timovi nastaju samo seedom.

**`User`**: `id`, `username`, `name`, `passwordHash`, `admin`, `hireDate`, `teams` (kolekcija `Team` u kojima je član).

| Metoda | Vraća |
| --- | --- |
| konstruktor (`username`, `name`, `passwordHash`, `admin`, `hireDate`) | novi korisnik bez timova |
| `isAdmin()` | je li admin |
| `leads(team)` | je li voditelj tog tima |
| `isMemberOf(team)` | je li član tog tima |
| `isTeamLead()` | vodi li barem jedan tim |

**`TimeOffRequest`**: polja kao stupci tablice; `decidedBy` i `cancelledBy` su `User`.

| Metoda | Radi |
| --- | --- |
| konstruktor (`owner`, `type`, `startDate`, `endDate`, `reason`, `createdAt`) | stanje `PENDING` |
| `isSubmittedBy(user)` | je li `user` vlasnik |
| `isPending()` | je li stanje `PENDING` |
| `overlaps(start, end)` | preklapa li se s rasponom, oba kraja uključiva |
| `approve(actor, comment, now)` | `APPROVED`; upisuje `decisionComment`, `decidedBy`, `decidedAt` |
| `reject(actor, comment, now)` | `REJECTED`; isto |
| `cancel(actor, reason, now)` | `CANCELLED`; upisuje `cancelReason`, `cancelledBy`, `cancelledAt` |

- `approve`, `reject` i `cancel` provjeravaju samo prijelaz iz tablice `Status`. Nedopušten prijelaz baca `InvalidStatusTransitionException` (servis je kasnije pretvara u 409).
- Domena ne provjerava ovlasti, obavezan komentar (R5) ni obavezan razlog (D3); to su servisi u T5 i T6.
- Vrijeme (`now`, `createdAt`) dolazi izvana, da testovi ne ovise o satu.

### Repozitoriji (Spring Data JPA)

| Repozitorij | Metode osim standardnih (`findById`, `save`, `findAll`) |
| --- | --- |
| `UserRepository` | `findByUsername(username)` vraća `Optional<User>` |
| `TeamRepository` | nema |
| `TimeOffRequestRepository` | `findByOwner(user)` vraća `List<TimeOffRequest>` |

Ostali upiti dodaju se u taskovima koji ih trebaju.

### Što testovi mogu provjeriti

- Seed: 8 korisnika, 2 tima, voditelji, članstva, Eva kao admin bez tima, datumi zaposlenja i da hash odgovara lozinki `password`.
- Tablica `Status`: svaki red i svaki nedopušten prijelaz.
- `approve`, `reject`, `cancel`: novo stanje, upisana polja, iznimka iz završnog stanja.
- `overlaps`: dodir na rubu je preklapanje.
- Baza odbija `end_date < start_date`, nepoznat `type` i `status`, zahtjev bez vlasnika.
