import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MyTimeOffScreen } from "@/components/MyTimeOffScreen";
import { bodyOf, callsTo, fakeFetch, ok, problem, request, signIn, storedToken } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));

const BALANCE = { year: 2026, entitlement: 20, used: 5, pending: 3, remaining: 12 };

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
  signIn("tok-ivan");
});

function cellTexts(row: HTMLElement): string[] {
  return within(row)
    .getAllByRole("cell")
    .map((cell) => cell.textContent?.trim() ?? "");
}

async function dataRows(): Promise<HTMLElement[]> {
  const table = await screen.findByRole("table", { name: "My requests" });
  const rows = within(table).getAllByRole("row");
  return rows.slice(1);
}

function formFields() {
  const form = screen.getByRole("form", { name: "New request" });
  const scope = within(form);
  return {
    type: scope.getByRole("combobox", { name: "Type" }),
    startDate: scope.getByLabelText("Start date"),
    endDate: scope.getByLabelText("End date"),
    reason: scope.getByRole("textbox", { name: "Reason" }),
    submit: scope.getByRole("button", { name: "Submit request" }),
  };
}

// Stupci: Type, Start date, End date, Reason, Status, Comment, Actions
const STATUS = 4;
const COMMENT = 5;

describe("balance (R12)", () => {
  it("shows entitlement, used, pending and remaining for the current year", async () => {
    fakeFetch({ "GET /api/me/balance": ok(BALANCE), "GET /api/me/requests": ok([]) });

    render(<MyTimeOffScreen />);

    expect(await screen.findByRole("heading", { level: 1, name: "My time off" })).toBeInTheDocument();
    const region = await screen.findByRole("region", { name: "Vacation balance 2026" });
    const terms = within(region)
      .getAllByRole("term")
      .map((term) => term.textContent?.trim());
    const definitions = within(region)
      .getAllByRole("definition")
      .map((definition) => definition.textContent?.trim());
    expect(terms).toEqual(["Entitlement", "Used", "Pending", "Remaining"]);
    expect(definitions).toEqual(["20", "5", "3", "12"]);
  });
});

describe("my requests (Q44–Q46, L32)", () => {
  it("shows rows in API order with type, status, names and comments", async () => {
    fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([
        request({ id: 4, startDate: "2026-12-07", endDate: "2026-12-11", reason: null }),
        request({
          id: 3,
          type: "SICK_LEAVE",
          startDate: "2026-11-23",
          endDate: "2026-11-24",
          status: "APPROVED",
          decisionComment: "Get well",
          decidedByName: "Ana",
        }),
        request({
          id: 2,
          type: "DAY_OFF",
          startDate: "2026-11-16",
          endDate: "2026-11-16",
          status: "REJECTED",
          decisionComment: "Busy week",
          decidedByName: "Ana",
        }),
        request({
          id: 1,
          startDate: "2026-11-02",
          endDate: "2026-11-06",
          status: "CANCELLED",
          cancelReason: "Reorg",
          cancelledByName: "Eva",
        }),
      ]),
    });

    render(<MyTimeOffScreen />);

    const rows = await dataRows();
    const table = screen.getByRole("table", { name: "My requests" });
    const headers = within(table)
      .getAllByRole("columnheader")
      .map((header) => header.textContent?.trim());
    expect(headers).toEqual(["Type", "Start date", "End date", "Reason", "Status", "Comment", "Actions"]);
    expect(rows.map((row) => cellTexts(row).slice(0, 6))).toEqual([
      ["Vacation", "2026-12-07", "2026-12-11", "", "Pending", ""],
      ["Sick leave", "2026-11-23", "2026-11-24", "Trip", "Approved by Ana", "Get well"],
      ["Day off", "2026-11-16", "2026-11-16", "Trip", "Rejected by Ana", "Busy week"],
      ["Vacation", "2026-11-02", "2026-11-06", "Trip", "Cancelled by Eva", "Reorg"],
    ]);
  });

  it("Cancel button only for Pending and Approved (R6, R7)", async () => {
    fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([
        request({ id: 4, status: "PENDING" }),
        request({ id: 3, status: "APPROVED", decidedByName: "Ana" }),
        request({ id: 2, status: "REJECTED", decidedByName: "Ana", decisionComment: "No" }),
        request({ id: 1, status: "CANCELLED", cancelledByName: "Ivan" }),
      ]),
    });

    render(<MyTimeOffScreen />);

    const rows = await dataRows();
    const hasCancel = rows.map((row) => within(row).queryByRole("button", { name: "Cancel" }) !== null);
    expect(hasCancel).toEqual([true, true, false, false]);
  });

  it("owner cancellation, missing name and decision comment before cancel reason", async () => {
    fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([
        request({ id: 3, status: "CANCELLED", cancelledByName: "Ivan" }),
        request({ id: 2, status: "APPROVED", decidedByName: null }),
        request({
          id: 1,
          status: "CANCELLED",
          decisionComment: "ok",
          decidedByName: "Ana",
          cancelReason: "Reorg",
          cancelledByName: "Eva",
        }),
      ]),
    });

    render(<MyTimeOffScreen />);

    const rows = await dataRows();
    expect(cellTexts(rows[0])[STATUS]).toBe("Cancelled by Ivan");
    expect(cellTexts(rows[1])[STATUS]).toBe("Approved");
    expect(cellTexts(rows[2])[COMMENT]).toBe("ok");
  });

  it("without requests shows a message instead of the table", async () => {
    fakeFetch({ "GET /api/me/balance": ok(BALANCE), "GET /api/me/requests": ok([]) });

    render(<MyTimeOffScreen />);

    expect(await screen.findByText("No requests yet.")).toBeInTheDocument();
    expect(screen.queryByRole("table", { name: "My requests" })).not.toBeInTheDocument();
  });

  it("401 on load goes to login without an alert", async () => {
    fakeFetch({
      "GET /api/me/balance": problem(401, "Invalid or expired token"),
      "GET /api/me/requests": problem(401, "Invalid or expired token"),
    });

    render(<MyTimeOffScreen />);

    await waitFor(() => expect(router.replace).toHaveBeenCalledWith("/login"));
    expect(storedToken()).toBeNull();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
  });
});

describe("new request form (T4)", () => {
  it("sends entered values, resets the form and reloads list and balance", async () => {
    const created = request({ id: 9, type: "SICK_LEAVE", startDate: "2026-11-09", endDate: "2026-11-10", reason: "Flu" });
    const fetchMock = fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": [ok([]), ok([created])],
      "POST /api/requests": ok(created, 201),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    await screen.findByText("No requests yet.");

    const { type, startDate, endDate, reason, submit } = formFields();
    expect(type).toHaveDisplayValue("Vacation");
    await user.selectOptions(type, "Sick leave");
    fireEvent.change(startDate, { target: { value: "2026-11-09" } });
    fireEvent.change(endDate, { target: { value: "2026-11-10" } });
    await user.type(reason, "Flu");
    await user.click(submit);

    const rows = await dataRows();
    expect(cellTexts(rows[0]).slice(0, 5)).toEqual(["Sick leave", "2026-11-09", "2026-11-10", "Flu", "Pending"]);
    const [init] = callsTo(fetchMock, "POST /api/requests");
    expect(bodyOf(init)).toEqual({ type: "SICK_LEAVE", startDate: "2026-11-09", endDate: "2026-11-10", reason: "Flu" });
    expect(callsTo(fetchMock, "GET /api/me/requests")).toHaveLength(2);
    expect(callsTo(fetchMock, "GET /api/me/balance")).toHaveLength(2);
    const resetFields = formFields();
    expect(resetFields.type).toHaveDisplayValue("Vacation");
    expect(resetFields.startDate).toHaveValue("");
    expect(resetFields.endDate).toHaveValue("");
    expect(resetFields.reason).toHaveValue("");
  });

  it("empty form is sent as is and the backend detail is shown (L31.1)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([]),
      "POST /api/requests": problem(400, "startDate must not be null"),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    await screen.findByText("No requests yet.");

    const { submit } = formFields();
    await user.click(submit);

    expect(await screen.findByRole("alert")).toHaveTextContent("startDate must not be null");
    const [init] = callsTo(fetchMock, "POST /api/requests");
    expect(bodyOf(init)).toEqual({ type: "VACATION", startDate: "", endDate: "", reason: "" });
  });

  it("409 shows the detail and keeps entered values (R13)", async () => {
    fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([]),
      "POST /api/requests": problem(409, "Not enough vacation days: requested 13, remaining 12"),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    await screen.findByText("No requests yet.");

    const { startDate, endDate, reason, submit } = formFields();
    fireEvent.change(startDate, { target: { value: "2026-11-02" } });
    fireEvent.change(endDate, { target: { value: "2026-11-18" } });
    await user.type(reason, "Trip");
    await user.click(submit);

    expect(await screen.findByRole("alert")).toHaveTextContent("Not enough vacation days: requested 13, remaining 12");
    const keptFields = formFields();
    expect(keptFields.startDate).toHaveValue("2026-11-02");
    expect(keptFields.endDate).toHaveValue("2026-11-18");
    expect(keptFields.reason).toHaveValue("Trip");
  });

  it("error stays until the next action succeeds", async () => {
    fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([]),
      "POST /api/requests": [
        problem(409, "Request overlaps an existing pending or approved request"),
        ok(request({ id: 9 }), 201),
      ],
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    await screen.findByText("No requests yet.");
    const { submit } = formFields();

    await user.click(submit);
    expect(await screen.findByRole("alert")).toBeInTheDocument();
    await user.click(submit);

    await waitFor(() => expect(screen.queryByRole("alert")).not.toBeInTheDocument());
  });
});

describe("cancel (R6, R7)", () => {
  it("Confirm cancel sends the reason and reloads list and balance", async () => {
    const fetchMock = fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": [
        ok([request({ id: 7 })]),
        ok([request({ id: 7, status: "CANCELLED", cancelReason: "Plans changed", cancelledByName: "Ivan" })]),
      ],
      "POST /api/requests/7/cancel": ok(request({ id: 7, status: "CANCELLED" })),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    const [row] = await dataRows();

    await user.click(within(row).getByRole("button", { name: "Cancel" }));
    await user.type(within(row).getByRole("textbox", { name: "Cancel reason" }), "Plans changed");
    await user.click(within(row).getByRole("button", { name: "Confirm cancel" }));

    await waitFor(() => expect(screen.getByText("Cancelled by Ivan")).toBeInTheDocument());
    const [init] = callsTo(fetchMock, "POST /api/requests/7/cancel");
    expect(bodyOf(init)).toEqual({ reason: "Plans changed" });
    expect(callsTo(fetchMock, "GET /api/me/requests")).toHaveLength(2);
    expect(callsTo(fetchMock, "GET /api/me/balance")).toHaveLength(2);
  });

  it("empty cancel reason is sent as empty string", async () => {
    const fetchMock = fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([request({ id: 7 })]),
      "POST /api/requests/7/cancel": ok(request({ id: 7, status: "CANCELLED" })),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    const [row] = await dataRows();

    await user.click(within(row).getByRole("button", { name: "Cancel" }));
    await user.click(within(row).getByRole("button", { name: "Confirm cancel" }));

    await waitFor(() => expect(callsTo(fetchMock, "POST /api/requests/7/cancel")).toHaveLength(1));
    const [init] = callsTo(fetchMock, "POST /api/requests/7/cancel");
    expect(bodyOf(init)).toEqual({ reason: "" });
  });

  it("Keep request closes without calling the backend", async () => {
    const fetchMock = fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([request({ id: 7 })]),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    const [row] = await dataRows();

    await user.click(within(row).getByRole("button", { name: "Cancel" }));
    await user.click(within(row).getByRole("button", { name: "Keep request" }));

    expect(within(row).queryByRole("textbox", { name: "Cancel reason" })).not.toBeInTheDocument();
    expect(callsTo(fetchMock, "POST /api/requests/7/cancel")).toHaveLength(0);
  });

  it("started approved request: backend 409 is shown (R7, Q23)", async () => {
    fakeFetch({
      "GET /api/me/balance": ok(BALANCE),
      "GET /api/me/requests": ok([
        request({ id: 7, status: "APPROVED", startDate: "2026-01-05", endDate: "2026-01-09", decidedByName: "Ana" }),
      ]),
      "POST /api/requests/7/cancel": problem(409, "Request has already started"),
    });
    const user = userEvent.setup();
    render(<MyTimeOffScreen />);
    const [row] = await dataRows();

    await user.click(within(row).getByRole("button", { name: "Cancel" }));
    await user.click(within(row).getByRole("button", { name: "Confirm cancel" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Request has already started");
  });
});
