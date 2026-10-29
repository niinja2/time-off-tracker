# Arhitektura

Slojevi aplikacije od vrha do dna, na primjeru "Ana odobrava Ivanov zahtjev #7". Upit staje na prvom sloju koji ga odbije.

## Pregled

| Sloj | Radi | Ne radi |
| --- | --- | --- |
| 1. Frontend | prikazuje i šalje upite | ne odlučuje ništa |
| 2. Security | utvrđuje tko si | ne odlučuje što smiješ |
| 3. API | provjerava oblik ulaza, vraća JSON ili grešku | nema pravila |
| 4. Servisi | pravila: ovlasti, stanja, validacija | ne zna za HTTP ni SQL |
| 5. Domena | čuva podatke, sama mijenja stanje | ne zna za ovlasti ni bazu |
| 6. Repozitorij | Java ↔ SQL | nema pravila |
| 7. Baza | čuva podatke, jednostavna ograničenja | nema složenih pravila |

Uz to Docker Compose pokreće frontend, backend i bazu jednom naredbom.

## Tok upita

```
┌─────────────────────────────────────────────┐
│ 1. FRONTEND (Next.js)                        │
│                                              │
│   ┌──────────────────────────────────┐       │
│   │ 1.1 UI                           │       │
│   │ stranice + komponente            │       │
│   │ (navigacija, prikaz greške)      │       │
│   │ Ana klikne "Approve"             │       │
│   └───────────────┬──────────────────┘       │
│   ┌───────────────▼──────────────────┐       │
│   │ 1.2 Stanje                       │       │
│   │ token i tko je prijavljen        │       │
│   └───────────────┬──────────────────┘       │
│   ┌───────────────▼──────────────────┐       │
│   │ 1.3 API klijent                  │       │
│   │ doda token, pošalje upit         │       │
│   └───────────────┬──────────────────┘       │
└───────────────────┼──────────────────────────┘
                    │ HTTP
                    │ POST /api/requests/7/approve
                    │ Authorization: Bearer <Anin JWT>
                    │ { "comment": "ok" }
                    ▼
┌─────────────────────────────────────────────┐
│ 2. SECURITY (Spring Security filter)         │
│                                              │
│   ┌──────────────────────────────────┐       │
│   │ 2.1 ima li token?                │──ne──► 401
│   └───────────────┬──────────────────┘       │
│                   │ da                       │
│   ┌───────────────▼──────────────────┐       │
│   │ 2.2 potpis ispravan, nije istekao│──ne──► 401
│   └───────────────┬──────────────────┘       │
│                   │ da                       │
│   ┌───────────────▼──────────────────┐       │
│   │ 2.3 izvuci iz tokena:            │       │
│   │ id, uloga, tim                   │       │
│   └───────────────┬──────────────────┘       │
└───────────────────┼──────────────────────────┘
                    │ "ovo je Ana, voditeljica, Platform"
                    ▼
┌─────────────────────────────────────────────┐
│ 3. API (controlleri)                         │
│                                              │
│   ┌──────────────────────────────────┐       │
│   │ 3.1 Ruta                         │       │
│   │ POST /api/requests/7/approve     │       │
│   │ → RequestController.approve      │       │
│   └───────────────┬──────────────────┘       │
│   ┌───────────────▼──────────────────┐       │
│   │ 3.2 Validacija ulaza             │──ne──► 400
│   │ id je broj, comment je tekst     │       │
│   └───────────────┬──────────────────┘       │
│                   │ da                       │
│   ┌───────────────▼──────────────────┐       │
│   │ 3.3 Poziv servisa                │       │
│   │ approve(7, ana, "ok")            │       │
│   └───────────────┬──────────────────┘       │
└───────────────────┼──────────────────────────┘
                    ▼
┌─────────────────────────────────────────────┐
│ 4. SERVISI (ApprovalService)                 │
│                                              │
│   ┌──────────────────────────────────┐       │
│   │ 4.1 Dohvati zahtjev #7           │──nema─► 404
│   │ (preko repozitorija)             │       │
│   └───────────────┬──────────────────┘       │
│   ┌───────────────▼──────────────────┐       │
│   │ 4.2 Tablica ovlasti              │──ne───► 403
│   │ R1 svoj tim, R2 admin sve,       │       │
│   │ R3 ne vlastiti (D15 admin smije) │       │
│   └───────────────┬──────────────────┘       │
│                   │ da                       │
│   ┌───────────────▼──────────────────┐       │
│   │ 4.3 Tablica stanja               │──ne───► 409
│   │ R4 samo na čekanju               │       │
│   └───────────────┬──────────────────┘       │
│                   │ da                       │
│   ┌───────────────▼──────────────────┐       │
│   │ 4.4 Validacija                   │──ne───► 400 / 409
│   │ (ovdje ništa; kod kreiranja:     │       │
│   │ datumi, preklapanje, saldo)      │       │
│   └───────────────┬──────────────────┘       │
│   ┌───────────────▼──────────────────┐       │
│   │ 4.5 Izvrši i spremi              │       │
│   │ request.approve(ana, "ok")       │       │
│   │ requestRepository.save(request)  │       │
│   └───────────────┬──────────────────┘       │
└───────────────────┼──────────────────────────┘
                    ▼
┌─────────────────────────────────────────────┐
│ 5. DOMENA                                    │
│                                              │
│   ┌──────────────────────────────────┐       │
│   │ 5.1 Team         naziv           │       │
│   └──────────────────────────────────┘       │
│   ┌──────────────────────────────────┐       │
│   │ 5.2 User                         │       │
│   │ ime, uloga, lozinka (hash)       │       │
│   │ leads(team)  isMemberOf(team)    │       │
│   └──────────────────────────────────┘       │
│   ┌──────────────────────────────────┐       │
│   │ 5.3 TimeOffRequest               │       │
│   │ vlasnik, tip, od, do, razlog,    │       │
│   │ stanje, komentar, tko je riješio │       │
│   │ isSubmittedBy(user) isPending()  │       │
│   │ overlaps(od, do)                 │       │
│   │ approve / reject / cancel        │──ne──► 409
│   └──────────────────────────────────┘       │
│   ┌──────────────────────────────────┐       │
│   │ 5.4 Status (enum)                │       │
│   │ PENDING → APPROVED, REJECTED,    │       │
│   │           CANCELLED              │       │
│   │ APPROVED → CANCELLED             │       │
│   │ REJECTED, CANCELLED: završna     │       │
│   └──────────────────────────────────┘       │
└───────────────────┬──────────────────────────┘
                    ▼
┌─────────────────────────────────────────────┐
│ 6. REPOZITORIJ (Spring Data JPA)             │
│                                              │
│   prima Javu:   findById(7), save(request)   │
│   šalje bazi:   SELECT ... WHERE id = 7      │
│                 UPDATE ... SET status =      │
│                   'APPROVED' WHERE id = 7    │
│   vraća Javu:   TimeOffRequest               │
│   bez pravila                                │
└───────────────────┬──────────────────────────┘
                    │ SQL
                    ▼
┌─────────────────────────────────────────────┐
│ 7. BAZA (PostgreSQL 17)                      │
│                                              │
│   ┌──────────────────────────────────┐       │
│   │ 7.1 Tablice                      │       │
│   │ teams, users, team_members,      │       │
│   │ time_off_requests                │       │
│   └──────────────────────────────────┘       │
│   ┌──────────────────────────────────┐       │
│   │ 7.2 Ograničenja (zadnja obrana)  │       │
│   │ end_date >= start_date (R8)      │       │
│   │ status 4 vrijednosti, type 3     │       │
│   │ owner_id obavezan                │       │
│   └──────────────────────────────────┘       │
│   ┌──────────────────────────────────┐       │
│   │ 7.3 Flyway                       │       │
│   │ V1__schema.sql, V2__seed.sql     │       │
│   └──────────────────────────────────┘       │
└─────────────────────────────────────────────┘

POVRATAK:
  7 → 6  red tablice → Java objekt
  6 → 4  TimeOffRequest (status APPROVED)
  4 → 3  3.4 Odgovor: JSON 200 { "id": 7, "status": "APPROVED" }
         ili ProblemDetail { "status": 409, "detail": "..." }
  3 → 1  frontend prikaže rezultat ili poruku iz "detail"
```

## Napomene

- **Validacija u API-ju** provjerava samo oblik ulaza (je li datum datum). Je li ulaz dopušten, odlučuje servis.
- **Servis** baca iznimku, a API je pretvara u HTTP status. Servis ne zna za HTTP.
- **Tablice ovlasti i stanja** nabrajaju samo dopušteno. Sve ostalo je zabranjeno (403, odnosno 409).
- **Pravila su u Javi**, ne u SQL-u. Repozitorij samo dohvaća i sprema. Kod puno podataka filtriranje bi se selilo u SQL, ali i dalje u repozitorij.
- **Ograničenja u bazi** postoje zbog D6 (ispravke se rade direktno u bazi SQL-om): takav SQL ne prolazi kroz naš kod. Samo jednostavna ograničenja; preklapanje i saldo ostaju u Javi.
