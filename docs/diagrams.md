# Diagrams

Flow and sequence diagrams for the main rules and calls (Q51: no Swagger; flow diagrams and sequence diagrams). The behaviour is defined by [the specification](zajednicko/specifikacija.md); endpoint and class names come from [the interface](zajednicko/sucelje.md) and the code. GitHub renders the `mermaid` blocks.

Rule codes: `R` = rule from the brief, `Q` = answered question, `D` = our decision (specification, Decisions), `L` = coding log entry.

## 1. Creating a request

`POST /api/requests` → `RequestController.create` → `RequestService.create`. Checks run in this order and the first failure decides the answer: 400 when the body is wrong by itself, 409 when it clashes with data in the database (interface T4).

```mermaid
flowchart TD
    start(["POST /api/requests"]) --> c1{"1. type, startDate, endDate present and valid? body is JSON?"}
    c1 -- no --> e1["400 field is not valid"]
    c1 -- yes --> c2{"2. reason at most 500 characters?"}
    c2 -- no --> e2["400 reason size must be between 0 and 500"]
    c2 -- yes --> svc["RequestService.create: load owner = logged-in user, Q29"]
    svc --> c3{"3. endDate before startDate? R8"}
    c3 -- yes --> e3["400 End date must not be before start date"]
    c3 -- no --> c4{"4. dates in different years? spec 5"}
    c4 -- yes --> e4["400 Request must be within one calendar year"]
    c4 -- no --> c5{"5. WorkingDays.count = 0? R10"}
    c5 -- yes --> e5["400 Request must contain at least one working day"]
    c5 -- no --> c6{"6. startDate before hireDate? D4"}
    c6 -- yes --> e6["409 Request must not start before hire date"]
    c6 -- no --> c7{"7. overlaps own PENDING or APPROVED request? R9"}
    c7 -- yes --> e7["409 Request overlaps an existing pending or approved request"]
    c7 -- no --> c8{"8. VACATION and working days > remaining? R13, R14"}
    c8 -- yes --> e8["409 Not enough vacation days: requested X, remaining Y"]
    c8 -- no --> ok["save as PENDING, 201 Request"]

    classDef bad fill:#fde2e2,stroke:#c0392b,color:#000
    classDef good fill:#e2f5e2,stroke:#27ae60,color:#000
    class e1,e2,e3,e4,e5,e6,e7,e8 bad
    class ok good
```

- Checks 1 and 2 are in the API (`@Valid` on `CreateRequestBody`), checks 3–8 in `RequestService`.
- R8: the end date is not before the start date. R9: no overlap with own pending or approved requests of any type; touching on an edge is an overlap. R10: only working days count; a request with none is invalid. R13: a vacation must not exceed the remaining balance of its year; pending requests use up balance. R14: sick leave and day off do not use balance. D4: a request does not start before the hire date. Q29: only the owner submits a request.

## 2. Request states

The `Status` enum allows only these transitions; anything else throws `InvalidStatusTransitionException`, which the API returns as 409 (interface T1).

```mermaid
stateDiagram-v2
    [*] --> PENDING : owner submits, Q29
    PENDING --> APPROVED : approve, approver, R1 R2 D15
    PENDING --> REJECTED : reject with comment, approver, R5
    PENDING --> CANCELLED : owner R6, or approver with reason D3 D14
    APPROVED --> CANCELLED : before start only, owner R7, or approver with reason Q32 D3
    REJECTED --> [*]
    CANCELLED --> [*]
```

- "Approver" = whoever `ApprovalPolicy.canDecide` allows (section 3).
- R4: only pending requests can be approved or rejected. R5: a rejection needs a comment, an approval comment is optional. R6: the owner can cancel a pending request. R7: the owner can cancel an approved request that has not started yet. D3: whoever cancels someone else's request must give a reason. D14: whoever may approve a request may also cancel it. Q23: a request that starts today has started.

## 3. Who may approve or reject

`ApprovalPolicy.canDecide(actor, request)`: rules are read in order and the first that applies decides; if none applies, the answer is no (interface T5). `ApprovalService` returns 403 `You cannot approve or reject this request`.

```mermaid
flowchart TD
    start(["canDecide: actor, request"]) --> a{"actor is admin?"}
    a -- yes --> yes1["YES, any request, also own of any type. R2, Q1.1, D15"]
    a -- no --> b{"actor is the owner?"}
    b -- yes --> no1["NO, nobody decides own request. R3"]
    b -- no --> c{"owner is a team lead?"}
    c -- yes --> no2["NO, a lead's request goes to an admin, even as member of another team. R3, D2"]
    c -- no --> d{"actor leads a team the owner is a member of?"}
    d -- yes --> yes2["YES, any of those teams is enough. R1, Q3, Q5.1"]
    d -- no --> no3["NO, e.g. owner without a team goes to an admin. Q4"]

    classDef bad fill:#fde2e2,stroke:#c0392b,color:#000
    classDef good fill:#e2f5e2,stroke:#27ae60,color:#000
    class no1,no2,no3 bad
    class yes1,yes2 good
```

Order of checks in `ApprovalService.approve` and `reject`: request exists (404 `Request not found`) → `canDecide` (403) → state is `PENDING` (409, R4). The reject comment (400 `comment must not be blank`, R5) is checked earlier, in `ApprovalController`.

- R1: a team lead approves requests from members of their own team only. R2: an admin approves any request. R3: nobody approves their own request; a team lead's request goes to an admin. D2: a lead's request always goes to an admin. D15: an admin may approve their own request of any type. Q4: a user without a team is decided by an admin.

## 4. Cancelling

`POST /api/requests/{id}/cancel` → `CancelController.cancel` → `CancelService.cancel`. Who may cancel and whether a reason is needed comes from `CancelPolicy`, which reuses `ApprovalPolicy` (interface T6).

```mermaid
flowchart TD
    start(["POST /api/requests/id/cancel"]) --> v{"reason at most 500 characters, body is JSON?"}
    v -- no --> e1["400 field is not valid"]
    v -- yes --> f{"request exists?"}
    f -- no --> e2["404 Request not found"]
    f -- yes --> o{"actor is the owner? R6, R7"}
    o -- yes --> started
    o -- no --> ap{"ApprovalPolicy.canDecide? D14"}
    ap -- no --> e3["403 You cannot cancel this request"]
    ap -- yes --> r{"reason given, not blank? D3"}
    r -- no --> e4["400 reason must not be blank"]
    r -- yes --> started{"APPROVED and startDate is today or earlier? R7, Q23"}
    started -- yes --> e5["409 Request has already started"]
    started -- no --> st{"status is PENDING or APPROVED?"}
    st -- no --> e6["409 Request cannot change from X to CANCELLED"]
    st -- yes --> ok["CANCELLED, cancelledBy = actor, 200 Request"]

    classDef bad fill:#fde2e2,stroke:#c0392b,color:#000
    classDef good fill:#e2f5e2,stroke:#27ae60,color:#000
    class e1,e2,e3,e4,e5,e6 bad
    class ok good
```

- The owner's reason is optional; anyone else's is required. A pending request can be cancelled even if its dates have passed: "started" is checked only for approved requests.
- R6: the owner can cancel a pending request. R7: the owner can cancel an approved request that has not started yet. Q23: a request that starts today has started. Q30, Q31, Q32: a team lead and an admin may cancel too. Q30.1, Q31.1: not once it has started. D3: someone else cancelling must give a reason. D14: whoever may approve may cancel.
- The Approvals screen lists approved requests of others that the user may still cancel with `GET /api/approvals/cancellable` (`CancelService.cancellable`, same rules, interface T8.1).

## 5. Login and an authenticated call

The browser only calls relative `/api/...` paths; Next.js forwards them to the backend (`rewrites` in `next.config.ts`). The token carries only identity; roles and teams are read from the database on every call (L9), so a change made directly in the database (D6) applies without a new login.

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant B as Browser - LoginScreen, api.ts
    participant N as Next.js - rewrites
    participant S as Spring Security filter
    participant AC as AuthController
    participant AS as AuthService
    participant TS as TokenService
    participant DB as PostgreSQL

    U->>B: username, password, click Sign in
    B->>N: POST /api/auth/login, no Authorization header
    N->>S: forward to backend
    Note over S: /api/auth/login is permitAll
    S->>AC: login body
    AC->>AS: login username, password
    AS->>DB: UserRepository.findByUsername
    DB-->>AS: User with password_hash
    AS->>AS: BCrypt matches? D16
    alt unknown user or wrong password
        AS-->>B: 401 Invalid username or password
    else ok
        AS->>TS: issue user
        TS-->>AS: JWT HS256, sub = user id, username, iat, exp
        AS-->>B: 200 token and Me
        B->>B: setToken in localStorage timeoff.token
        B->>B: router.replace to /my-time-off
    end

    Note over U,DB: Any later call, e.g. GET /api/me/balance

    B->>N: GET /api/me/balance, Authorization Bearer JWT
    N->>S: forward to backend
    S->>S: JwtDecoder checks signature and expiry
    alt no token, bad signature or expired
        S-->>B: 401 problem+json
        B->>B: clearToken, go to /login
    else valid
        S->>DB: UserExistsJwtConverter - UserRepository.existsById sub
        alt user no longer exists
            S-->>B: 401 Invalid or expired token
        else exists
            S->>AC: BalanceController, identity = sub
            Note over AC: CurrentUser.id returns sub as Long
            AC->>AS: BalanceService.balance userId, year
            AS->>DB: load User, teams, requests - roles from DB, L9
            DB-->>AS: rows
            AS-->>B: 200 Balance JSON
        end
    end
```

- In the second part `AC` and `AS` stand for the controller and service of that call (`BalanceController`, `BalanceService`); every endpoint works the same way.
- There is no server logout: `Log out` only clears the token (interface T2, T8).
- L9: the token holds only who you are; roles and teams come from the database. D6: corrections are made directly in the database. D16: seed password `password` for all users.

## 6. Ivan submits, Ana approves, Ivan sees "Approved by Ana"

One request through all layers (docs/arhitektura.md). The approval needs no explicit `save`: the request is a managed JPA entity, so the change is written as an `UPDATE` when the transaction commits.

```mermaid
sequenceDiagram
    autonumber
    actor I as Ivan
    actor A as Ana
    participant UI as UI - MyTimeOffScreen, ApprovalsScreen
    participant API as api.ts
    participant C as Controllers
    participant SV as Services
    participant P as ApprovalPolicy
    participant R as TimeOffRequestRepository
    participant DB as PostgreSQL

    I->>UI: fill New request, click Submit request
    UI->>API: createRequest type, startDate, endDate, reason
    API->>C: POST /api/requests, Bearer Ivan
    C->>SV: RequestService.create ivanId, NewRequest
    SV->>R: findByOwner Ivan - overlap R9
    SV->>SV: WorkingDays, BalanceService - R10, R13
    SV->>R: save new TimeOffRequest PENDING
    R->>DB: INSERT time_off_requests
    C-->>UI: 201 Request status PENDING
    UI->>API: getMyRequests, getMyBalance
    Note over UI: row shows Pending

    A->>UI: open Approvals
    UI->>API: getApprovals
    API->>C: GET /api/approvals, Bearer Ana
    C->>SV: ApprovalService.pending anaId
    SV->>R: findByStatus PENDING
    SV->>P: canDecide Ana, request - keep allowed ones
    C-->>UI: 200 list with Ivan's request

    A->>UI: comment ok, click Approve
    UI->>API: approveRequest id, ok
    API->>C: POST /api/requests/id/approve, Bearer Ana
    C->>SV: ApprovalService.approve id, anaId, ok
    SV->>R: findById id
    alt not found
        SV-->>UI: 404 Request not found
    end
    SV->>P: canDecide Ana, request
    Note over P: Ana leads Platform, Ivan is a member - R1
    alt not allowed
        SV-->>UI: 403 You cannot approve or reject this request
    end
    SV->>SV: request.approve Ana, ok, now - R4 else 409
    SV->>DB: on commit UPDATE status APPROVED, decided_by_id, decided_at
    C-->>UI: 200 Request status APPROVED, decidedByName Ana
    UI->>API: getApprovals, getCancellableRequests
    Note over UI: request leaves Pending requests

    I->>UI: open My time off
    UI->>API: getMyRequests
    API->>C: GET /api/me/requests, Bearer Ivan
    C->>SV: VisibilityService.mine ivanId
    SV->>R: findByOwner Ivan
    C-->>UI: 200 list, RequestView with decidedByName Ana
    Note over UI: statusText shows Approved by Ana - L32
```

- R1: a team lead approves requests from members of their own team. R4: only pending requests can be approved. R9: no overlapping requests. R10: only working days count. R13: a vacation must not exceed the remaining balance. L32: the request shows the name of who decided and who cancelled.

## 7. Calendar and visibility

`GET /api/calendar?month=yyyy-MM&teamId=` → `CalendarController.calendar` → `CalendarService.month`. For each request `VisibilityPolicy.access(viewer, owner)` gives `FULL`, `LIMITED` or `NONE` (rules in order, first match wins; interface T7). The same policy decides `GET /api/requests`, `GET /api/requests/{id}` and `GET /api/users/{id}/balance`, where only `FULL` is allowed.

```mermaid
flowchart TD
    start(["CalendarService.month: viewer, month, teamId"]) --> all["TimeOffRequestRepository.findAll"]
    all --> s{"status PENDING or APPROVED?"}
    s -- no --> hide1["not shown to anyone. Q48"]
    s -- yes --> m{"at least one day in month?"}
    m -- no --> hide2["not shown"]
    m -- yes --> t{"teamId given and owner not a member?"}
    t -- yes --> hide3["filtered out, visibility unchanged"]
    t -- no --> v1{"viewer is owner?"}
    v1 -- yes --> full["FULL"]
    v1 -- no --> v2{"viewer is admin?"}
    v2 -- yes --> full
    v2 -- no --> v3{"viewer leads a team of the owner?"}
    v3 -- yes --> full
    v3 -- no --> v4{"viewer and owner share a team?"}
    v4 -- yes --> lim["LIMITED"]
    v4 -- no --> none["NONE: not shown"]
    full --> fe["entry: name, dates, type, status. PENDING and APPROVED. Q41, Q42, Q43"]
    lim --> la{"APPROVED?"}
    la -- no --> hide4["not shown, colleagues see no pending. Q47"]
    la -- yes --> le["entry: name and dates only, no type or status keys. Q47"]
    fe --> out["sort by startDate, then name. 200 CalendarEntry list"]
    le --> out

    classDef bad fill:#fde2e2,stroke:#c0392b,color:#000
    classDef good fill:#e2f5e2,stroke:#27ae60,color:#000
    class hide1,hide2,hide3,hide4,none bad
    class fe,le good
```

- The frontend (`CalendarScreen`) only groups the entries by weekday; it shows `Ivan (Vacation, Pending)` for `FULL` and just `Marko` for `LIMITED`.
- A lead who is a member of another team is visible `FULL` to that team's lead, even though only an admin may approve their requests (D2 is about approving, not seeing).
- Q41: an employee sees own pending and approved requests on the calendar. Q42: a lead sees pending and approved requests of the members of the teams they lead, with type. Q43: an admin sees everyone, with a team filter. Q47: colleagues see only approved requests, name and dates. Q48: rejected and cancelled requests are shown to no one.
