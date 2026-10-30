import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApprovalsScreen } from "@/components/ApprovalsScreen";
import { bodyOf, callsTo, fakeFetch, ok, problem, request, signIn } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));

const PENDING_TABLE = "Pending requests";
const APPROVED_TABLE = "Approved requests";
const CANCELLABLE = "GET /api/approvals/cancellable";

const IVANS = request({ id: 7, ownerId: 2, ownerName: "Ivan", reason: "Trip" });
const MARKOS = request({
  id: 8,
  ownerId: 3,
  ownerName: "Marko",
  type: "DAY_OFF",
  startDate: "2026-11-09",
  endDate: "2026-11-09",
  reason: null,
});
const IVANS_APPROVED = request({
  id: 9,
  ownerId: 2,
  ownerName: "Ivan",
  startDate: "2026-11-16",
  endDate: "2026-11-20",
  status: "APPROVED",
  decidedById: 1,
  decidedByName: "Ana",
});
const SARAS_APPROVED = request({
  id: 10,
  ownerId: 5,
  ownerName: "Sara",
  type: "SICK_LEAVE",
  startDate: "2026-11-23",
  endDate: "2026-11-24",
  status: "APPROVED",
  decidedById: 4,
  decidedByName: null,
});

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
  signIn("tok-ana");
});

async function rowOf(ownerName: string, tableName = PENDING_TABLE): Promise<HTMLElement> {
  const table = await screen.findByRole("table", { name: tableName });
  const rows = within(table).getAllByRole("row");
  const row = rows.find((candidate) => within(candidate).queryByText(ownerName) !== null);
  if (row === undefined) {
    throw new Error(`No row for ${ownerName} in ${tableName}`);
  }
  return row;
}

function cellTexts(row: HTMLElement): string[] {
  return within(row)
    .getAllByRole("cell")
    .map((cell) => cell.textContent?.trim() ?? "");
}

// T8 Approvals, specifikacija 12; R1, R5; L28: bez salda podnositelja.
// Od T8.1 ekran pri otvaranju zove i /api/approvals/cancellable (L40, točka 5).

describe("ApprovalsScreen", () => {
  it("lists pending requests in API order without loading balances (L28)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": ok([IVANS, MARKOS]),
      [CANCELLABLE]: ok([]),
    });

    render(<ApprovalsScreen />);

    expect(await screen.findByRole("heading", { level: 1, name: "Approvals" })).toBeInTheDocument();
    const table = await screen.findByRole("table", { name: PENDING_TABLE });
    const rows = within(table).getAllByRole("row").slice(1);
    expect(rows.map((row) => cellTexts(row).slice(0, 5))).toEqual([
      ["Ivan", "Vacation", "2026-11-02", "2026-11-06", "Trip"],
      ["Marko", "Day off", "2026-11-09", "2026-11-09", ""],
    ]);
    const urls = fetchMock.mock.calls.map(([url]) => url);
    const sortedUrls = [...urls].sort();
    expect(sortedUrls).toEqual(["/api/approvals", "/api/approvals/cancellable"]);
  });

  it("Approve sends the comment from its own row and the row disappears (R1)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": [ok([IVANS, MARKOS]), ok([IVANS])],
      [CANCELLABLE]: ok([]),
      "POST /api/requests/8/approve": ok({ ...MARKOS, status: "APPROVED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const ivanRow = await rowOf("Ivan");
    const markoRow = await rowOf("Marko");

    await user.type(within(ivanRow).getByRole("textbox", { name: "Comment" }), "not this one");
    await user.type(within(markoRow).getByRole("textbox", { name: "Comment" }), "ok");
    await user.click(within(markoRow).getByRole("button", { name: "Approve" }));

    await waitFor(() => expect(screen.queryByText("Marko")).not.toBeInTheDocument());
    const [init] = callsTo(fetchMock, "POST /api/requests/8/approve");
    expect(bodyOf(init)).toEqual({ comment: "ok" });
    expect(callsTo(fetchMock, "GET /api/approvals")).toHaveLength(2);
    expect(screen.getByText("Ivan")).toBeInTheDocument();
  });

  it("Approve without comment sends an empty comment", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": [ok([IVANS]), ok([])],
      [CANCELLABLE]: ok([]),
      "POST /api/requests/7/approve": ok({ ...IVANS, status: "APPROVED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan");

    await user.click(within(row).getByRole("button", { name: "Approve" }));

    expect(await screen.findByText("No requests waiting for approval.")).toBeInTheDocument();
    const [init] = callsTo(fetchMock, "POST /api/requests/7/approve");
    expect(bodyOf(init)).toEqual({ comment: "" });
  });

  it("Reject sends the comment (R5)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": [ok([IVANS]), ok([])],
      [CANCELLABLE]: ok([]),
      "POST /api/requests/7/reject": ok({ ...IVANS, status: "REJECTED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan");

    await user.type(within(row).getByRole("textbox", { name: "Comment" }), "Busy week");
    await user.click(within(row).getByRole("button", { name: "Reject" }));

    expect(await screen.findByText("No requests waiting for approval.")).toBeInTheDocument();
    const [init] = callsTo(fetchMock, "POST /api/requests/7/reject");
    expect(bodyOf(init)).toEqual({ comment: "Busy week" });
  });

  it("Reject without comment goes to the backend and shows its 400 (R5, L31.1)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: ok([]),
      "POST /api/requests/7/reject": problem(400, "comment must not be blank"),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan");

    await user.click(within(row).getByRole("button", { name: "Reject" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("comment must not be blank");
    const [init] = callsTo(fetchMock, "POST /api/requests/7/reject");
    expect(bodyOf(init)).toEqual({ comment: "" });
    expect(screen.getByText("Ivan")).toBeInTheDocument();
  });

  it("empty list shows a message instead of the table", async () => {
    fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: ok([]),
    });

    render(<ApprovalsScreen />);

    expect(await screen.findByText("No requests waiting for approval.")).toBeInTheDocument();
    expect(screen.queryByRole("table", { name: PENDING_TABLE })).not.toBeInTheDocument();
  });
});

// T8.1: otkazivanje tuđih odobrenih zahtjeva na Approvals (L39, L40); Q30, Q31, Q32, D3, D14

describe("ApprovalsScreen, Approved requests (T8.1)", () => {
  it("both tables have a level 2 heading (L40.5)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: ok([IVANS_APPROVED]),
    });

    render(<ApprovalsScreen />);

    expect(await screen.findByRole("heading", { level: 2, name: PENDING_TABLE })).toBeInTheDocument();
    expect(screen.getByRole("heading", { level: 2, name: APPROVED_TABLE })).toBeInTheDocument();
  });

  it("lists approved requests in API order with who approved (L39, L32)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: ok([IVANS_APPROVED, SARAS_APPROVED]),
    });

    render(<ApprovalsScreen />);

    const table = await screen.findByRole("table", { name: APPROVED_TABLE });
    const rows = within(table).getAllByRole("row").slice(1);
    expect(rows.map((row) => cellTexts(row).slice(0, 5))).toEqual([
      ["Ivan", "Vacation", "2026-11-16", "2026-11-20", "Ana"],
      ["Sara", "Sick leave", "2026-11-23", "2026-11-24", ""],
    ]);
  });

  it("Approved requests table has its columns (L40.4)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: ok([IVANS_APPROVED]),
    });

    render(<ApprovalsScreen />);

    const table = await screen.findByRole("table", { name: APPROVED_TABLE });
    const headers = within(table)
      .getAllByRole("columnheader")
      .map((header) => header.textContent?.trim());
    expect(headers).toEqual(["Employee", "Type", "Start date", "End date", "Approved by", "Cancel"]);
  });

  it("Cancel sends the reason from its own row, reloads both lists and the row disappears (Q30, D3)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: [ok([IVANS_APPROVED, SARAS_APPROVED]), ok([SARAS_APPROVED])],
      "POST /api/requests/9/cancel": ok({ ...IVANS_APPROVED, status: "CANCELLED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const ivanRow = await rowOf("Ivan", APPROVED_TABLE);
    const saraRow = await rowOf("Sara", APPROVED_TABLE);
    const ivanReason = within(ivanRow).getByRole("textbox", { name: "Cancel reason" });
    const saraReason = within(saraRow).getByRole("textbox", { name: "Cancel reason" });
    const ivanCancel = within(ivanRow).getByRole("button", { name: "Cancel" });

    await user.type(saraReason, "not this one");
    await user.type(ivanReason, "Year-end freeze");
    await user.click(ivanCancel);

    const table = await screen.findByRole("table", { name: APPROVED_TABLE });
    await waitFor(() => expect(within(table).queryByText("Ivan")).not.toBeInTheDocument());
    const [init] = callsTo(fetchMock, "POST /api/requests/9/cancel");
    expect(bodyOf(init)).toEqual({ reason: "Year-end freeze" });
    expect(callsTo(fetchMock, "GET /api/approvals")).toHaveLength(2);
    expect(callsTo(fetchMock, CANCELLABLE)).toHaveLength(2);
    expect(within(table).getByText("Sara")).toBeInTheDocument();
  });

  it("Cancel without reason goes to the backend and shows its 400 (D3)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: ok([IVANS_APPROVED]),
      "POST /api/requests/9/cancel": problem(400, "reason must not be blank"),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan", APPROVED_TABLE);
    const cancel = within(row).getByRole("button", { name: "Cancel" });

    await user.click(cancel);

    expect(await screen.findByRole("alert")).toHaveTextContent("reason must not be blank");
    const [init] = callsTo(fetchMock, "POST /api/requests/9/cancel");
    expect(bodyOf(init)).toEqual({ reason: "" });
    const table = screen.getByRole("table", { name: APPROVED_TABLE });
    expect(within(table).getByText("Ivan")).toBeInTheDocument();
  });

  it.each([
    { status: 409, detail: "Request has already started", rule: "R7, Q30.1" },
    { status: 403, detail: "You cannot cancel this request", rule: "D14" },
  ])("$status from cancel shows $detail ($rule)", async ({ status, detail }) => {
    fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: ok([IVANS_APPROVED]),
      "POST /api/requests/9/cancel": problem(status, detail),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan", APPROVED_TABLE);
    const reason = within(row).getByRole("textbox", { name: "Cancel reason" });
    const cancel = within(row).getByRole("button", { name: "Cancel" });

    await user.type(reason, "Year-end freeze");
    await user.click(cancel);

    expect(await screen.findByRole("alert")).toHaveTextContent(detail);
  });

  it("empty cancellable list shows a message instead of the table", async () => {
    fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: ok([]),
    });

    render(<ApprovalsScreen />);

    expect(await screen.findByText("No approved requests to cancel.")).toBeInTheDocument();
    expect(screen.queryByRole("table", { name: APPROVED_TABLE })).not.toBeInTheDocument();
    expect(screen.getByRole("table", { name: PENDING_TABLE })).toBeInTheDocument();
  });

  it("failed cancellable list keeps the pending table and shows one alert (L40.6)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: problem(500, "Something went wrong"),
    });

    render(<ApprovalsScreen />);

    expect(await screen.findByRole("alert")).toHaveTextContent("Something went wrong");
    expect(screen.getAllByRole("alert")).toHaveLength(1);
    expect(await screen.findByRole("table", { name: PENDING_TABLE })).toBeInTheDocument();
    expect(screen.queryByRole("table", { name: APPROVED_TABLE })).not.toBeInTheDocument();
    expect(screen.queryByText("No approved requests to cancel.")).not.toBeInTheDocument();
  });

  it("failed approvals list keeps the approved table and shows one alert (L40.6)", async () => {
    fakeFetch({
      "GET /api/approvals": problem(500, "Something went wrong"),
      [CANCELLABLE]: ok([IVANS_APPROVED]),
    });

    render(<ApprovalsScreen />);

    expect(await screen.findByRole("alert")).toHaveTextContent("Something went wrong");
    expect(screen.getAllByRole("alert")).toHaveLength(1);
    expect(await screen.findByRole("table", { name: APPROVED_TABLE })).toBeInTheDocument();
    expect(screen.queryByRole("table", { name: PENDING_TABLE })).not.toBeInTheDocument();
    expect(screen.queryByText("No requests waiting for approval.")).not.toBeInTheDocument();
  });

  it("both lists failing show one alert with the approvals error and no tables (L40.6, L41.1)", async () => {
    fakeFetch({
      "GET /api/approvals": problem(500, "Approvals failed"),
      [CANCELLABLE]: problem(500, "Cancellable failed"),
    });

    render(<ApprovalsScreen />);

    const alert = await screen.findByRole("alert");
    expect(alert).toHaveTextContent("Approvals failed");
    expect(alert).not.toHaveTextContent("Cancellable failed");
    expect(screen.getAllByRole("alert")).toHaveLength(1);
    expect(screen.queryByRole("table", { name: PENDING_TABLE })).not.toBeInTheDocument();
    expect(screen.queryByRole("table", { name: APPROVED_TABLE })).not.toBeInTheDocument();
    expect(screen.queryByText("No requests waiting for approval.")).not.toBeInTheDocument();
    expect(screen.queryByText("No approved requests to cancel.")).not.toBeInTheDocument();
  });

  it("approvals error wins even when the cancellable error answers first (L41.1)", async () => {
    const fetchMock = vi.fn(async (url: string) => {
      if (url === "/api/approvals") {
        await new Promise((resolve) => setTimeout(resolve, 50));
        return problem(500, "Approvals failed")();
      }
      return problem(500, "Cancellable failed")();
    });
    vi.stubGlobal("fetch", fetchMock);

    render(<ApprovalsScreen />);

    const alert = await screen.findByRole("alert");
    await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(2));
    await waitFor(() => expect(alert).toHaveTextContent("Approvals failed"));
    expect(alert).not.toHaveTextContent("Cancellable failed");
    expect(screen.getAllByRole("alert")).toHaveLength(1);
  });

  it("Approve reloads both lists and the approved request joins Approved requests (L41.2, Q32)", async () => {
    const markosApproved = { ...MARKOS, status: "APPROVED", decidedById: 1, decidedByName: "Ana" };
    const fetchMock = fakeFetch({
      "GET /api/approvals": [ok([MARKOS]), ok([])],
      [CANCELLABLE]: [ok([]), ok([markosApproved])],
      "POST /api/requests/8/approve": ok(markosApproved),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Marko");
    const approve = within(row).getByRole("button", { name: "Approve" });

    await user.click(approve);

    const approvedRow = await rowOf("Marko", APPROVED_TABLE);
    expect(cellTexts(approvedRow).slice(0, 5)).toEqual(["Marko", "Day off", "2026-11-09", "2026-11-09", "Ana"]);
    expect(callsTo(fetchMock, "GET /api/approvals")).toHaveLength(2);
    expect(callsTo(fetchMock, CANCELLABLE)).toHaveLength(2);
  });

  it("Reject reloads both lists (L41.2)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": [ok([IVANS]), ok([])],
      [CANCELLABLE]: ok([]),
      "POST /api/requests/7/reject": ok({ ...IVANS, status: "REJECTED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan");
    const comment = within(row).getByRole("textbox", { name: "Comment" });
    const reject = within(row).getByRole("button", { name: "Reject" });

    await user.type(comment, "Busy week");
    await user.click(reject);

    expect(await screen.findByText("No requests waiting for approval.")).toBeInTheDocument();
    await waitFor(() => expect(callsTo(fetchMock, CANCELLABLE)).toHaveLength(2));
    expect(callsTo(fetchMock, "GET /api/approvals")).toHaveLength(2);
  });

  it("Comment in a pending row and Cancel reason in an approved row are separate fields", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: [ok([IVANS_APPROVED]), ok([])],
      "POST /api/requests/9/cancel": ok({ ...IVANS_APPROVED, status: "CANCELLED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const pendingRow = await rowOf("Ivan", PENDING_TABLE);
    const approvedRow = await rowOf("Ivan", APPROVED_TABLE);
    const comment = within(pendingRow).getByRole("textbox", { name: "Comment" });
    const reason = within(approvedRow).getByRole("textbox", { name: "Cancel reason" });
    const cancel = within(approvedRow).getByRole("button", { name: "Cancel" });

    await user.type(comment, "pending comment");
    await user.type(reason, "Plans changed");
    await user.click(cancel);

    expect(await screen.findByText("No approved requests to cancel.")).toBeInTheDocument();
    const [init] = callsTo(fetchMock, "POST /api/requests/9/cancel");
    expect(bodyOf(init)).toEqual({ reason: "Plans changed" });
  });

  it("failed reload after Cancel keeps the old approved rows and shows the alert (L42.1)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: [ok([IVANS_APPROVED]), problem(500, "Reload failed")],
      "POST /api/requests/9/cancel": ok({ ...IVANS_APPROVED, status: "CANCELLED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan", APPROVED_TABLE);
    const reason = within(row).getByRole("textbox", { name: "Cancel reason" });
    const cancel = within(row).getByRole("button", { name: "Cancel" });

    await user.type(reason, "Year-end freeze");
    await user.click(cancel);

    expect(await screen.findByRole("alert")).toHaveTextContent("Reload failed");
    const table = screen.getByRole("table", { name: APPROVED_TABLE });
    expect(within(table).getByText("Ivan")).toBeInTheDocument();
  });

  it("failed reload after Approve keeps the old pending rows and shows the alert (L42.1)", async () => {
    fakeFetch({
      "GET /api/approvals": [ok([MARKOS]), problem(500, "Reload failed")],
      [CANCELLABLE]: ok([]),
      "POST /api/requests/8/approve": ok({ ...MARKOS, status: "APPROVED" }),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Marko");
    const approve = within(row).getByRole("button", { name: "Approve" });

    await user.click(approve);

    expect(await screen.findByRole("alert")).toHaveTextContent("Reload failed");
    const table = screen.getByRole("table", { name: PENDING_TABLE });
    expect(within(table).getByText("Marko")).toBeInTheDocument();
  });

  it("failed Cancel keeps the typed reason in its row (L42.3)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([]),
      [CANCELLABLE]: ok([IVANS_APPROVED]),
      "POST /api/requests/9/cancel": problem(409, "Request has already started"),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan", APPROVED_TABLE);
    const reason = within(row).getByRole("textbox", { name: "Cancel reason" });
    const cancel = within(row).getByRole("button", { name: "Cancel" });

    await user.type(reason, "Year-end freeze");
    await user.click(cancel);

    expect(await screen.findByRole("alert")).toHaveTextContent("Request has already started");
    const rowAfter = await rowOf("Ivan", APPROVED_TABLE);
    const reasonAfter = within(rowAfter).getByRole("textbox", { name: "Cancel reason" });
    expect(reasonAfter).toHaveValue("Year-end freeze");
  });

  it("failed Reject keeps the typed comment in its row (L42.3)", async () => {
    fakeFetch({
      "GET /api/approvals": ok([IVANS]),
      [CANCELLABLE]: ok([]),
      "POST /api/requests/7/reject": problem(409, "Request cannot change from APPROVED to REJECTED"),
    });
    const user = userEvent.setup();
    render(<ApprovalsScreen />);
    const row = await rowOf("Ivan");
    const comment = within(row).getByRole("textbox", { name: "Comment" });
    const reject = within(row).getByRole("button", { name: "Reject" });

    await user.type(comment, "Busy week");
    await user.click(reject);

    expect(await screen.findByRole("alert")).toHaveTextContent("Request cannot change from APPROVED to REJECTED");
    const rowAfter = await rowOf("Ivan");
    const commentAfter = within(rowAfter).getByRole("textbox", { name: "Comment" });
    expect(commentAfter).toHaveValue("Busy week");
  });
});
