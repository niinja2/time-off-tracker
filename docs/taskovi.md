# Taskovi

Cjeline se rade redom. Svaka uloga radi u svom chatu, a Rajko im daje kratke naredbe. Tester vidi samo `docs/zajednicko/` i `backend/src/test/` ([way of working](way-of-working.md)).

Krug po cjelini:

1. Developer opiše sučelje u [sucelje.md](zajednicko/sucelje.md), bez koda.
2. Tester iz [specifikacije](zajednicko/specifikacija.md) i sučelja napiše testove u `backend/src/test/`.
3. Testovi se pokrenu (`./mvnw test`). Moraju pasti.
4. Developer napiše kod dok testovi ne prođu.
5. Revieweri pregledaju (linija, funkcija, cjelina).
6. Pitanja, sporovi i nalazi idu u [coding log](zajednicko/coding-log.md).

Cjelina se može razbiti na manje taskove kad krene rad.

| # | Cjelina | Slojevi ([arhitektura](arhitektura.md)) | Stanje |
| --- | --- | --- | --- |
| T1 | Shema i seed | 5 Domena, 6 Repozitorij, 7 Baza | gotovo |
| T2 | Login (JWT) | 2 Security, 3 API | gotovo |
| T3 | Radni dani i saldo | 3 API, 4 Servisi | čeka |
| T4 | Kreiranje zahtjeva i validacija | 3 API, 4 Servisi | čeka |
| T5 | Odobravanje i odbijanje | 3 API, 4 Servisi | čeka |
| T6 | Otkazivanje | 3 API, 4 Servisi | čeka |
| T7 | Vidljivost i kalendar | 3 API, 4 Servisi | čeka |
| T8 | Frontend, 3 ekrana | 1 Frontend | čeka |
| T9 | Docker i README; backend u Composeu dobiva `JWT_SECRET` iz `.env` (u `.gitignore`, uz `.env.example`), bez zadane šifre (L14) | | čeka |
