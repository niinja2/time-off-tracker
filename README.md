# Time-off tracker

Employees request time off, team leads approve or reject it, and everyone sees who is away. Spring Boot (Java 21) + Next.js (TypeScript) + PostgreSQL, all in Docker Compose.

The detailed project documents are in Croatian; this README links to them.

**Start here**
- **Run:** set `JWT_SECRET` in `.env` (see [Run](#run)), `docker compose up -d --build`, then open http://localhost:3000 (users under [Seeded users](#seeded-users)).
- **The rules in code** are decision tables, one per concern:
  [ApprovalPolicy](backend/src/main/java/com/example/timeoff/service/ApprovalPolicy.java) (who may approve or reject),
  [CancelPolicy](backend/src/main/java/com/example/timeoff/service/CancelPolicy.java) (who may cancel, in which state),
  [VisibilityPolicy](backend/src/main/java/com/example/timeoff/service/VisibilityPolicy.java) (who sees what),
  [Status](backend/src/main/java/com/example/timeoff/domain/Status.java) (allowed state changes).
- **Balance** is computed, never stored: [BalanceService](backend/src/main/java/com/example/timeoff/service/BalanceService.java)
  (20 days a year, prorated in the hiring year), working days in [WorkingDays](backend/src/main/java/com/example/timeoff/service/WorkingDays.java).
- **What is missing:** [Known gaps](#known-gaps-decided-or-found-not-built-yet).

## How this was built

The main deliverable is the process: several LLM agents build the code in small, separate tasks,
and talk only through documents. I own the specification.

**Specification first.** The brief was broken into ~75 atomic questions; the client's answers and
the decisions D1–D18 became the [specification](docs/zajednicko/specifikacija.md). Features that would
add logic and conflicts were cut early and listed as extensions. From the spec came the
[architecture](docs/arhitektura.md) and the [tasks](docs/taskovi.md) (T1–T9 plus T8.1, small and separate).
The [coding rules](docs/pravila-kodiranja.md) were added separately, as my preference:
code that a human can read and debug easily.

**Roles**, each in its own context: developer, tester (never sees the code), reviewer,
and an orchestrator that runs the rounds.

**One round per task:**
1. **Developer writes the interface** for the task: endpoints, fields, screens, error messages.
   Every unstated assumption goes into the [coding log](docs/zajednicko/coding-log.md) as one entry.
2. **Tester reads the interface and the assumptions**, answers each one in the log, and raises conflicts.
   Developer and tester settle them in the log; the specification is the judge.
3. **Tester writes the tests** from the specification and the interface only, following the
   [testing methodology](tester/TESTING_METHODOLOGY.md) (so far only partly; the full combination
   grids are planned). The tests must fail: there is no code yet.
4. **Developer writes the code** until all tests pass, seeing only the test output, never the test code.
   A test that looks wrong is not changed: the developer raises it in the log, the tester answers there,
   and the tester changes the test only if they agree it is wrong (the specification decides).
5. **Reviewer checks** line, function and whole (coding rules, spec rules, layers),
   and writes each finding to the log, addressed to the developer or the tester.
   They answer in the log until the reviewer is satisfied.
6. **Fixes:** the developer fixes code, the tester fixes tests; all tests must pass again.
   Then the reviewer reviews again. When there are no findings left, the task is committed.

**Escalation:** the roles settle technical questions themselves. Only behaviour questions
(the spec has no answer) and disputes after two rounds come to the specification owner.

**Fixed formats:** every hand-off always looks the same, so the next role can read it without guessing:
interface sections in the same order, test names that carry the rule code (`..._R1`),
log entries with problem, decision, why and where.

**Checks:** 568 tests at the last run (482 backend through a real PostgreSQL, 86 frontend screens and API client),
a review after every task, and happy-path user journeys clicked through in a browser
(5 chains, 345 checks: [scenarios](tester/e2e-scenariji.md), [report](tester/e2e-izvjestaj.md)).
Result: 344 of 345 passed. The one failure was a wrong check in the scenario, not in the app
(the saved page text shows the expected behaviour); the check was corrected afterwards but not re-run.
The journeys are written so a human can follow them by hand. This first run was done by an LLM (Haiku);
the next step is a script (Playwright) that runs the same steps on demand or on a schedule.

**External reviews after the code.** Independent LLM reviews checked the finished project:
"shiva" against the brief, the client's answers and the specification, "integrator" the code against itself
(duplicated rules, layers, dead code), and two consistency checks of the specification itself
(every statement listed, then compared). All findings are in one list:
[llm-shiva/nalazi.md](llm-shiva/nalazi.md); how each specification finding was resolved is at the bottom of
[spec-sprega.md](llm-shiva/spec-sprega.md) and [spec-sprega2.md](llm-shiva/spec-sprega2.md).
What they found and is not built yet is listed below under "Known gaps".

Details and the role prompts: [way-of-working.md](docs/way-of-working.md).

### Who owns what

- I own the specification: the brief, the client questions and the decisions D1–D18.
- The coding log owns the code. Every implementation choice (56 entries, a few hundred assumptions)
  is written there with why and where, and most were settled by the agents themselves.
  That is why the log is so detailed: it is the documentation a fresh chat or a person needs
  to change the code safely.

### What the human reads

The brief, the specification with its decisions, and the way of working; plus samples of the
agents' documents (interface, log entries, test reports). Not the code.

### The general approach

**Rectangular and broad, with atomization.**
- **Rectangular:** rule following. No messy network of ad-hoc connections: keep the structure organized
  so conflicts do not happen in the first place. Applied mostly to the LLM way of working
  (fixed roles, fixed hand-offs, one log), less to the code itself.
- **Broad:** cover the space of possibilities, to fish out possible faults or inconsistencies.
- **Atomized decisions:** each one simple to understand, handle and explain.
- **Generalize:** find the general rule or pattern instead of repeating the same work
  (e.g. permissions, status changes and visibility are each one decision table, not scattered `if`s).
- **Offload and surface:** give the LLMs whatever they can do, and make them state their assumptions,
  so the space of possibilities gets covered.

LLMs excel at simple tasks; as complexity grows, they pay a penalty. The whole point is to atomize
the work and give the LLMs clear rules and constraints. So this is not one-shotting the application
from a model: it is a step-by-step, documented process where each LLM has a simple, clear role
that any other LLM could fill later on. It is not "trust the LLM"; it is the tests, the review,
the log and the step-by-step process.

- **Software has no ground truth like ML.** The same spec has many valid implementations, and the
  client's intent gets lost on the way. That is why the spec and the way of working get most of the attention.
- **Agreement is not proof.** Similar strong models share blind spots (but so do humans).
  Next step: mix model versions and add a "breaker" role whose only job is to break the app.
- **80/20 is for features, never for tests.** Early on the tester followed the developer's list of
  cases instead of the testing methodology; the tester prompt now says the methodology wins.
- **Deliberately out:** concurrent requests (about 20 users), public holidays, carry-over,
  because the way of working showed their complexity rising.

## Run

Needs Docker. Running the tests outside Docker also needs JDK 21 and Node.js.

```sh
cp .env.example .env    # then set JWT_SECRET to a random string of at least 32 bytes
docker compose up -d --build
```

- App: http://localhost:3000
- API: http://localhost:8080/api (also through the frontend at http://localhost:3000/api)

Compose refuses to start without `JWT_SECRET`; there is no default, because a secret committed to the repo would let anyone sign a valid token (L14: no default JWT secret in Docker).

## Test

From the repository root (backend tests need Docker running, Testcontainers starts PostgreSQL):

```sh
(cd backend && ./mvnw test)
(cd frontend && npm ci && npm test)
```

On Linux or macOS run `chmod +x backend/mvnw` once first (the file is stored without the execute bit).

## Choices

| What | Why |
| --- | --- |
| Spring Boot (Java 21) + Spring Data JPA | the brief's stack; rules live in plain Java services, the database keeps only simple constraints |
| Flyway | schema and seed as versioned SQL (`V1__schema.sql`, `V2__seed.sql`), so every start gives the same database |
| Spring Security OAuth2 resource server (JWT) | stateless login with a signed token; the token carries only who you are, roles and teams are read from the database on every call (L9), so a correction in the database (D6) counts at once |
| ProblemDetail (`application/problem+json`) | one error format for every 400/401/403/404/409 (D11) |
| Testcontainers | backend tests run against a real PostgreSQL 17, not an in-memory substitute |
| Next.js (TypeScript) + Tailwind | the brief's stack; the frontend proxies `/api` to the backend, so no CORS is needed |
| Vitest + React Testing Library | fast frontend tests with a fake `fetch`, no backend needed |
| Docker Compose | the client asked for Docker (Q53); one command starts database, backend and frontend |

## Seeded users

Type the username on the login form (D10: login is a form, no user picker). The password is `password` for everyone (D16: seed values).

| Username | Role | Team |
| --- | --- | --- |
| `ana` | Team lead | Platform |
| `ivan` | Employee | Platform |
| `marko` | Employee | Platform |
| `petra` | Employee | Platform |
| `luka` | Team lead | Mobile |
| `sara` | Employee | Mobile |
| `tom` | Employee (hired 2026-06-01, so 12 vacation days in 2026) | Mobile |
| `eva` | Admin | none |

Usernames are case-sensitive (`ana` works, `Ana` does not; open question, see Known gaps). The seed has no requests (D9: seed without demo requests).

## API

JWT in `Authorization: Bearer <token>`; errors are `application/problem+json` (D11) with 400 / 401 / 403 / 404 / 409. Full contract: [sucelje.md](docs/zajednicko/sucelje.md). Flow and sequence diagrams (the client asked for these instead of Swagger, Q51): [diagrams.md](docs/diagrams.md).

| Endpoint | What |
| --- | --- |
| `POST /api/auth/login` | log in, returns token and user |
| `GET /api/me`, `GET /api/me/balance` | who I am, my vacation balance |
| `POST /api/requests` | new request |
| `POST /api/requests/{id}/approve`, `/reject`, `/cancel` | decide or cancel |
| `GET /api/approvals` | pending requests I may decide |
| `GET /api/approvals/cancellable` | approved requests of others I may still cancel |
| `GET /api/me/requests`, `GET /api/requests`, `GET /api/requests/{id}` | requests I may see |
| `GET /api/users/{id}/balance` | someone else's balance (lead: own team, admin: all) |
| `GET /api/calendar?month=yyyy-MM&teamId=`, `GET /api/teams` | team calendar and team filter |

## Decisions and assumptions

- Decisions D1–D18: [specification](docs/zajednicko/specifikacija.md), section 10.
- Deviations from the brief, based on the client's answers: section 11.
- Assumptions made while coding: the "pretpostavke" entries in the [coding log](docs/zajednicko/coding-log.md), one per task.

**Why no carry-over (D5).** The brief says 20 days per calendar year (R11), so each year has its own
20 days and a request uses the balance of the year it falls in. This is a deliberate deviation:
asked which year's balance a request uses (Q18), the client answered "the current one, and days left
from the previous year are used first". "Current" only differs for requests in next year, and
"previous year first" is carry-over. The client's answers describe carry-over:
unused days move to the next year, are valid until 30 June, and the oldest days are used first.
That is not one rule but several new pieces of logic (a balance per year of origin, an expiry date,
an order of use), and each of them collides with rules that already exist:

- a pending request reserves days that may expire before it is decided;
- cancelling a request must return days to the right year, possibly to days that have already expired;
- a request across 30 June uses carried days for part of it and new days for the rest;
- a hire date corrected in the database (D6) changes every balance after it.

Each collision needs its own decision and its own tests. Following 80/20 (D7: brief rules first,
extensions in the README), carry-over is an extension; the balance calculation is the one place it would go.

Layers and request flow: [architecture](docs/arhitektura.md). Coding rules: [pravila-kodiranja.md](docs/pravila-kodiranja.md). Task list: [taskovi.md](docs/taskovi.md).

## Data corrections

There is no admin screen for data fixes, so corrections are SQL in the database (D6: corrections directly in the database). Connect with:

```sh
docker compose exec db psql -U timeoff -d timeoff
```

```sql
-- Change a hire date (the balance follows it at once)
UPDATE users SET hire_date = '2026-03-01' WHERE username = 'tom';

-- Add a member to a team
INSERT INTO team_members (team_id, user_id)
SELECT t.id, u.id FROM teams t, users u WHERE t.name = 'Mobile' AND u.username = 'ana';

-- Make Ivan the lead of Platform (a lead is also a member of the team, Q3)
UPDATE teams SET lead_id = (SELECT id FROM users WHERE username = 'ivan') WHERE name = 'Platform';

-- Cancel a request that has already started (the app does not allow it, Q30.1)
UPDATE time_off_requests
SET status = 'CANCELLED', cancel_reason = 'Came back early',
    cancelled_by_id = (SELECT id FROM users WHERE username = 'eva'), cancelled_at = now()
WHERE id = 7;
```

## Extensions (not built)

Where each one would go is in the specification, section 9.

- Carry-over of unused days to the next year, valid until 30 June, oldest used first (balance calculation).
- Croatian public holidays not counted as working days (working-day function).
- A request across the year boundary stays one request, days counted per year (validation and balance).
- Sick leave may overlap approved vacation, which then gets refunded (overlap check and balance).

## Known gaps (decided or found, not built yet)

Decided, waiting for a frontend task:
- **Team requests screen (D18).** Leads and admins may see all requests of the people they may see,
  in every state (including rejected with the comment), and their balances (spec section 7, Q16, Q17),
  but today this is only in the API (`GET /api/requests`, `GET /api/users/{id}/balance`).
  D18 adds a screen: one table (name, type, dates, status, reason, decision comment or cancel reason,
  decided or cancelled by), a small balance table, and a team filter like the calendar.
  The backend already has everything; only the frontend is missing.

Found by the reviews (details in [llm-shiva/nalazi.md](llm-shiva/nalazi.md)):
- A lead or admin can cancel someone else's **pending** request only through the API, not on a screen (D17);
  on the screen they reject it instead.
- The Cancel button is also shown on an approved request that has already started; the backend refuses it with 409.
- Request timestamps (submitted, decided, cancelled) are returned by the API but not shown.
- The Comment column shows the decision comment, and the cancel reason only when there is no decision comment,
  so a request approved with a comment and then cancelled hides its cancel reason.
- `.env.example` contains a placeholder secret long enough to pass the 32-byte check, so copying it unchanged
  starts the app with a publicly known secret; `application.properties` has a fallback secret for runs without Compose.
- `docs/taskovi.md` and `frontend/README.md` are out of date.

Open questions for the specification owner:
- Usernames: should login ignore upper and lower case (`Ana` = `ana`)?
- A person in two teams appears once on the calendar, with the strongest access the viewer has
  (as the lead of one team the viewer sees type and status). The code does this; the specification does not say it.
- The year boundary, to plan together: a request across 31 December (Q28), carry-over (Q14),
  and a late request for last year when last year's balance is used up.
- Past dates are tested for vacation only; sick leave and day off have no test yet.

## What's next

- The Team requests screen and the review findings above, as the next iteration.
- The extensions above, starting with carry-over and public holidays.
- Audit history per request and a notification to the team lead (optional extras from the brief).
- A script (Playwright) that runs the end-to-end journeys on demand; the first run was done by hand by an LLM.
