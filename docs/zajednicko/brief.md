# Take-home task: time-off tracker

Thanks for taking the time to do this. We'd like to see how you design and build a small full-stack feature from start to finish. The domain is close to an internal tool we use every day, so it also gives us something concrete to talk about in the follow-up interview.

## Time and submission

- Plan for about 6 to 8 hours of work. Please don't spend much more than that. We would rather see a smaller app with solid rules and tests than a large app with gaps.
- You have 7 days from the day you receive this brief.
- Send us a link to a Git repository (a private repo shared with us is fine) or a zip that includes the `.git` folder. We read the commit history, so commit as you normally would.

## The app

Build an app where employees request time off, team leads approve or reject those requests, and everyone can see who is away.

### Stack

Please use:

- Java with Spring Boot for the backend
- Next.js with TypeScript for the frontend
- PostgreSQL for the database

You can pick any libraries you like within that stack. Explain the non-obvious choices in your README.

### Users and roles

There are three roles:

- **Employee.** Requests time off and sees their own requests.
- **Team lead.** Leads one team. Approves or rejects requests from that team. A team lead is also an employee and can request time off.
- **Admin.** Can see and act on everything in the company.

You don't need real authentication or a user management screen. Seed the database with these users and give us a simple way to act as each one. A login form with seeded passwords works, and so does a "log in as" picker.

| Name | Role | Team |
| --- | --- | --- |
| Ana | Team lead | Platform |
| Ivan | Employee | Platform |
| Marko | Employee | Platform |
| Petra | Employee | Platform |
| Luka | Team lead | Mobile |
| Sara | Employee | Mobile |
| Tom | Employee | Mobile |
| Eva | Admin | none |

### Time-off requests

A request has a type, a start date, an end date (both inclusive) and an optional reason.

The types are:

- Vacation
- Sick leave
- Day off

A request is in one of these states:

- **Pending.** Every new request starts here.
- **Approved.**
- **Rejected.**
- **Cancelled.**

### Rules

These rules carry most of the weight in our review.

Approvals:

1. A team lead can approve or reject requests from members of their own team only.
2. An admin can approve or reject any request.
3. Nobody can approve or reject their own request. A team lead's request goes to an admin.
4. Only pending requests can be approved or rejected.
5. A rejection needs a comment. An approval comment is optional.

Cancelling:

6. An employee can cancel their own request while it is pending.
7. An employee can cancel their own approved request if it hasn't started yet.

Validation:

8. The end date can't be before the start date.
9. A user can't have two pending or approved requests whose dates overlap.
10. The app counts working days only, Monday to Friday. A request that covers only weekend days is invalid.

Vacation balance:

11. Every user gets 20 vacation days per calendar year.
12. For the current year, show each user how many days they have used (approved), pending, and remaining.
13. A vacation request can't exceed the remaining balance. Pending requests count against the balance.
14. Sick leave and days off don't count against the vacation balance.

You can assume a request falls within one calendar year. Note any other assumptions in your README.

### Visibility

Different people see different things:

| Viewer | What they can see |
| --- | --- |
| Employee | Their own requests with every detail. Teammates' approved absences on the team calendar, showing only the name and dates. No type, no reason, nothing pending. |
| Team lead | Every request from their team, including type, reason and pending requests. |
| Admin | Every request in the company. |

The API must enforce these rules. Hiding data in the frontend doesn't count. We will call your API directly as different users.

### Screens

We need at least these three screens. Plain, clean UI is enough. We won't grade visual design.

1. **My time off.** A list of my requests with their status, a form to create a new one, a way to cancel, and my vacation balance.
2. **Approvals.** For team leads and admins only: a list of pending requests they are allowed to act on, with approve and reject actions.
3. **Team calendar.** Who is away during a selected month, following the visibility rules above. A list grouped by day is fine. You don't need a calendar grid.

Show useful error messages when the backend rejects something, for example an overlap or an exceeded balance.

### Tests

Write automated tests for the rules and visibility sections. We care more about which cases you chose to test than about a coverage percentage. Frontend tests are welcome but not required.

## README

Include a README that covers:

- How to run the app locally, ideally with one or two commands
- How to switch between the seeded users
- The assumptions you made where this brief is unclear
- The design decisions you'd want us to know about
- What you would do next with more time

## Optional extras

Only take these on if the core is done and tested. They won't make up for gaps in the core.

- Exclude public holidays from day counts. Pick Croatia or Belgium.
- Half-day requests.
- An audit history for each request showing who changed it and when.
- A notification to the team lead when a new request arrives. Logging it to the console is fine.
- Docker Compose to start the database, backend and frontend together.

## Out of scope

- Real authentication or SSO
- Deployment
- User and team management screens
- Translations
- Pixel-perfect design

## Next steps

In the follow-up interview we'll walk through your code together. Expect to explain a few of your decisions and to make a small change live.

If something in this brief is unclear, make a reasonable assumption, write it down in the README and keep going. You can also email us with questions. We'll answer, but we may not reply within the hour.
