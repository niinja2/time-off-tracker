import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApprovalsScreen } from "@/components/ApprovalsScreen";
import { bodyOf, callsTo, fakeFetch, ok, problem, request, signIn } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));

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

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
  signIn("tok-ana");
});

async function rowOf(ownerName: string): Promise<HTMLElement> {
  const table = await screen.findByRole("table", { name: "Pending requests" });
  const rows = within(table).getAllByRole("row");
  const row = rows.find((candidate) => within(candidate).queryByText(ownerName) !== null);
  if (row === undefined) {
    throw new Error(`No row for ${ownerName}`);
  }
  return row;
}

function cellTexts(row: HTMLElement): string[] {
  return within(row)
    .getAllByRole("cell")
    .map((cell) => cell.textContent?.trim() ?? "");
}

// T8 Approvals, specifikacija 12; R1, R5; L28: bez salda podnositelja

describe("ApprovalsScreen", () => {
  it("lists pending requests in API order without loading balances (L28)", async () => {
    const fetchMock = fakeFetch({ "GET /api/approvals": ok([IVANS, MARKOS]) });

    render(<ApprovalsScreen />);

    expect(await screen.findByRole("heading", { level: 1, name: "Approvals" })).toBeInTheDocument();
    const table = await screen.findByRole("table", { name: "Pending requests" });
    const rows = within(table).getAllByRole("row").slice(1);
    expect(rows.map((row) => cellTexts(row).slice(0, 5))).toEqual([
      ["Ivan", "Vacation", "2026-11-02", "2026-11-06", "Trip"],
      ["Marko", "Day off", "2026-11-09", "2026-11-09", ""],
    ]);
    const urls = fetchMock.mock.calls.map(([url]) => url);
    expect(urls).toEqual(["/api/approvals"]);
  });

  it("Approve sends the comment from its own row and the row disappears (R1)", async () => {
    const fetchMock = fakeFetch({
      "GET /api/approvals": [ok([IVANS, MARKOS]), ok([IVANS])],
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
    fakeFetch({ "GET /api/approvals": ok([]) });

    render(<ApprovalsScreen />);

    expect(await screen.findByText("No requests waiting for approval.")).toBeInTheDocument();
    expect(screen.queryByRole("table", { name: "Pending requests" })).not.toBeInTheDocument();
  });
});
