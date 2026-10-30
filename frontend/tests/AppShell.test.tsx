import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import type { ReactNode } from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { AppShell } from "@/components/AppShell";
import { ANA, EVA, IVAN, fakeFetch, ok, problem, signIn, storedToken } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));
vi.mock("next/link", () => ({
  default: ({ href, children, ...rest }: { href: string; children: ReactNode }) => (
    <a href={href} {...rest}>
      {children}
    </a>
  ),
}));

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
});

function renderShell() {
  render(
    <AppShell>
      <p>Screen content</p>
    </AppShell>,
  );
}

// T8 AppShell i navigacija; specifikacija 12: Approvals samo za voditelje i admine

describe("AppShell", () => {
  it("without token goes to login without calling the API", async () => {
    const fetchMock = fakeFetch({ "GET /api/me": ok(IVAN) });

    renderShell();

    await vi.waitFor(() => expect(router.replace).toHaveBeenCalledWith("/login"));
    expect(fetchMock).not.toHaveBeenCalled();
    expect(screen.queryByText("Screen content")).not.toBeInTheDocument();
  });

  it("employee sees navigation without Approvals", async () => {
    signIn("tok-ivan");
    fakeFetch({ "GET /api/me": ok(IVAN) });

    renderShell();

    const nav = await screen.findByRole("navigation", { name: "Main" });
    expect(nav).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "My time off" })).toHaveAttribute("href", "/my-time-off");
    expect(screen.getByRole("link", { name: "Team calendar" })).toHaveAttribute("href", "/calendar");
    expect(screen.queryByRole("link", { name: "Approvals" })).not.toBeInTheDocument();
    expect(screen.getByText("Signed in as Ivan")).toBeInTheDocument();
    expect(screen.getByText("Screen content")).toBeInTheDocument();
  });

  it.each([
    { who: "team lead Ana", me: ANA },
    { who: "admin Eva", me: EVA },
  ])("$who sees the Approvals link", async ({ me }) => {
    signIn("tok");
    fakeFetch({ "GET /api/me": ok(me) });

    renderShell();

    const link = await screen.findByRole("link", { name: "Approvals" });
    expect(link).toHaveAttribute("href", "/approvals");
    expect(screen.getByText(`Signed in as ${me.name}`)).toBeInTheDocument();
  });

  it("Log out clears the token and goes to login without calling the backend", async () => {
    signIn("tok-ana");
    const fetchMock = fakeFetch({ "GET /api/me": ok(ANA) });
    const user = userEvent.setup();
    renderShell();

    await user.click(await screen.findByRole("button", { name: "Log out" }));

    expect(storedToken()).toBeNull();
    expect(router.replace).toHaveBeenCalledWith("/login");
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("401 from /api/me goes to login without an alert", async () => {
    signIn("expired");
    fakeFetch({ "GET /api/me": problem(401, "Invalid or expired token") });

    renderShell();

    await vi.waitFor(() => expect(router.replace).toHaveBeenCalledWith("/login"));
    expect(storedToken()).toBeNull();
    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
  });

  it("other error from /api/me shows only the alert, without navigation or screen (L34.2)", async () => {
    signIn("tok-ana");
    fakeFetch({ "GET /api/me": problem(500, "Internal server error") });

    renderShell();

    expect(await screen.findByRole("alert")).toHaveTextContent("Internal server error");
    expect(screen.queryByRole("navigation", { name: "Main" })).not.toBeInTheDocument();
    expect(screen.queryByText("Screen content")).not.toBeInTheDocument();
    expect(router.replace).not.toHaveBeenCalled();
  });
});
