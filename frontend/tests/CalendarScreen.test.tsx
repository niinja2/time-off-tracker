import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { CalendarScreen } from "@/components/CalendarScreen";
import { callsTo, fakeFetch, ok, signIn } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));

// API vraća timove po imenu; ovdje namjerno drugim redom, da se vidi da ekran ne presložuje.
const TEAMS = [
  { id: 2, name: "Platform" },
  { id: 1, name: "Mobile" },
];

const DAY = /^\d{4}-\d{2}-\d{2}$/;

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
  signIn("tok-ivan");
  vi.useFakeTimers({ toFake: ["Date"] });
  vi.setSystemTime(new Date(2026, 10, 15, 12, 0, 0));
});

afterEach(() => {
  vi.useRealTimers();
});

function dayNames(): string[] {
  return screen
    .queryAllByRole("region")
    .map((region) => {
      const label = region.getAttribute("aria-label");
      const heading = within(region).queryByRole("heading", { level: 3 });
      return (label ?? heading?.textContent ?? "").trim();
    })
    .filter((name) => DAY.test(name));
}

function itemsOf(day: string): string[] {
  const region = screen.getByRole("region", { name: day });
  return within(region)
    .getAllByRole("listitem")
    .map((item) => item.textContent?.trim() ?? "");
}

// T8 Team calendar, specifikacija 12; R10; Q41, Q42, Q47; D12

describe("month", () => {
  it("opens on the current month from the browser clock", async () => {
    const fetchMock = fakeFetch({ "GET /api/teams": ok(TEAMS), "GET /api/calendar?month=2026-11": ok([]) });

    render(<CalendarScreen />);

    expect(await screen.findByRole("heading", { level: 1, name: "Team calendar" })).toBeInTheDocument();
    expect(await screen.findByRole("heading", { level: 2, name: "2026-11" })).toBeInTheDocument();
    expect(callsTo(fetchMock, "GET /api/calendar?month=2026-11")).toHaveLength(1);
    expect(callsTo(fetchMock, "GET /api/teams")).toHaveLength(1);
  });

  it.each([
    { now: new Date(2026, 10, 15, 12), button: "Next month", expected: "2026-12" },
    { now: new Date(2026, 10, 15, 12), button: "Previous month", expected: "2026-10" },
    { now: new Date(2026, 11, 10, 12), button: "Next month", expected: "2027-01" },
    { now: new Date(2026, 0, 10, 12), button: "Previous month", expected: "2025-12" },
  ])("$button from $now goes to $expected", async ({ now, button, expected }) => {
    vi.setSystemTime(now);
    const fetchMock = fakeFetch({
      "GET /api/teams": ok(TEAMS),
      "GET /api/calendar?month=2026-11": ok([]),
      "GET /api/calendar?month=2026-12": ok([]),
      "GET /api/calendar?month=2026-10": ok([]),
      "GET /api/calendar?month=2027-01": ok([]),
      "GET /api/calendar?month=2026-01": ok([]),
      "GET /api/calendar?month=2025-12": ok([]),
    });
    const user = userEvent.setup();
    render(<CalendarScreen />);
    await screen.findByText("No absences this month.");

    await user.click(screen.getByRole("button", { name: button }));

    expect(await screen.findByRole("heading", { level: 2, name: expected })).toBeInTheDocument();
    expect(callsTo(fetchMock, `GET /api/calendar?month=${expected}`)).toHaveLength(1);
  });
});

describe("team filter (D12, Q43)", () => {
  it("offers All teams and API teams; team choice sends teamId and is kept when the month changes", async () => {
    const fetchMock = fakeFetch({
      "GET /api/teams": ok(TEAMS),
      "GET /api/calendar?month=2026-11": ok([]),
      "GET /api/calendar?month=2026-11&teamId=1": ok([]),
      "GET /api/calendar?month=2026-12&teamId=1": ok([]),
      "GET /api/calendar?month=2026-12": ok([]),
    });
    const user = userEvent.setup();
    render(<CalendarScreen />);
    const select = await screen.findByRole("combobox", { name: "Team" });
    await waitFor(() => expect(within(select).getAllByRole("option")).toHaveLength(3));

    const options = within(select)
      .getAllByRole("option")
      .map((option) => option.textContent?.trim());
    expect(options).toEqual(["All teams", "Platform", "Mobile"]);

    await user.selectOptions(select, "Mobile");
    await waitFor(() => expect(callsTo(fetchMock, "GET /api/calendar?month=2026-11&teamId=1")).toHaveLength(1));

    await user.click(screen.getByRole("button", { name: "Next month" }));
    await waitFor(() => expect(callsTo(fetchMock, "GET /api/calendar?month=2026-12&teamId=1")).toHaveLength(1));

    await user.selectOptions(select, "All teams");
    await waitFor(() => expect(callsTo(fetchMock, "GET /api/calendar?month=2026-12")).toHaveLength(1));
  });
});

describe("days (R10, specifikacija 12)", () => {
  // Studeni 2026.: 1. je nedjelja, 6. petak, 7. i 8. vikend, 30. ponedjeljak.
  const ENTRIES = [
    { name: "Marko", startDate: "2026-10-29", endDate: "2026-11-03" },
    { name: "Ana", startDate: "2026-11-03", endDate: "2026-11-03", type: "SICK_LEAVE", status: "APPROVED" },
    { name: "Ivan", startDate: "2026-11-06", endDate: "2026-11-10", type: "VACATION", status: "PENDING" },
    { name: "Sara", startDate: "2026-11-30", endDate: "2026-12-02" },
  ];

  it("shows only weekdays of the month with absences, ascending", async () => {
    fakeFetch({ "GET /api/teams": ok(TEAMS), "GET /api/calendar?month=2026-11": ok(ENTRIES) });

    render(<CalendarScreen />);

    await screen.findByRole("region", { name: "2026-11-02" });
    expect(dayNames()).toEqual(["2026-11-02", "2026-11-03", "2026-11-06", "2026-11-09", "2026-11-10", "2026-11-30"]);
  });

  it("entry from Friday to Tuesday skips the weekend; FULL shows type and status (Q42)", async () => {
    fakeFetch({ "GET /api/teams": ok(TEAMS), "GET /api/calendar?month=2026-11": ok(ENTRIES) });

    render(<CalendarScreen />);

    await screen.findByRole("region", { name: "2026-11-06" });
    expect(itemsOf("2026-11-06")).toEqual(["Ivan (Vacation, Pending)"]);
    expect(itemsOf("2026-11-09")).toEqual(["Ivan (Vacation, Pending)"]);
    expect(itemsOf("2026-11-10")).toEqual(["Ivan (Vacation, Pending)"]);
    expect(screen.queryByRole("region", { name: "2026-11-07" })).not.toBeInTheDocument();
    expect(screen.queryByRole("region", { name: "2026-11-08" })).not.toBeInTheDocument();
  });

  it("entries crossing the month edge show only days of this month; LIMITED shows only the name (Q47)", async () => {
    fakeFetch({ "GET /api/teams": ok(TEAMS), "GET /api/calendar?month=2026-11": ok(ENTRIES) });

    render(<CalendarScreen />);

    await screen.findByRole("region", { name: "2026-11-02" });
    expect(itemsOf("2026-11-02")).toEqual(["Marko"]);
    expect(itemsOf("2026-11-03")).toEqual(["Marko", "Ana (Sick leave, Approved)"]);
    expect(itemsOf("2026-11-30")).toEqual(["Sara"]);
    expect(screen.queryByRole("region", { name: "2026-10-30" })).not.toBeInTheDocument();
    expect(screen.queryByRole("region", { name: "2026-12-01" })).not.toBeInTheDocument();
  });

  it("month without entries shows a message", async () => {
    fakeFetch({ "GET /api/teams": ok(TEAMS), "GET /api/calendar?month=2026-11": ok([]) });

    render(<CalendarScreen />);

    expect(await screen.findByText("No absences this month.")).toBeInTheDocument();
    expect(dayNames()).toEqual([]);
  });
});
