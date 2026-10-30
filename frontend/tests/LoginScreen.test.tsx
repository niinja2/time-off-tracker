import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { LoginScreen } from "@/components/LoginScreen";
import { ANA, bodyOf, callsTo, fakeFetch, ok, problem, storedToken } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
});

// T8 Login, D10: forma s korisničkim imenom i lozinkom

describe("LoginScreen", () => {
  it("shows the sign-in form", () => {
    render(<LoginScreen />);

    expect(screen.getByRole("heading", { name: "Sign in" })).toBeInTheDocument();
    expect(screen.getByRole("textbox", { name: "Username" })).toBeInTheDocument();
    expect(screen.getByLabelText("Password")).toHaveAttribute("type", "password");
    expect(screen.getByRole("button", { name: "Sign in" })).toBeInTheDocument();
  });

  it("successful login stores the token and goes to My time off", async () => {
    const fetchMock = fakeFetch({ "POST /api/auth/login": ok({ token: "tok-ana", user: ANA }) });
    const user = userEvent.setup();
    render(<LoginScreen />);

    await user.type(screen.getByRole("textbox", { name: "Username" }), "ana");
    await user.type(screen.getByLabelText("Password"), "password");
    await user.click(screen.getByRole("button", { name: "Sign in" }));

    await vi.waitFor(() => expect(router.replace).toHaveBeenCalledWith("/my-time-off"));
    const [init] = callsTo(fetchMock, "POST /api/auth/login");
    expect(bodyOf(init)).toEqual({ username: "ana", password: "password" });
    expect(storedToken()).toBe("tok-ana");
  });

  it("wrong password shows the backend detail and stays on the page", async () => {
    fakeFetch({ "POST /api/auth/login": problem(401, "Invalid username or password") });
    const user = userEvent.setup();
    render(<LoginScreen />);

    await user.type(screen.getByRole("textbox", { name: "Username" }), "ana");
    await user.type(screen.getByLabelText("Password"), "wrong");
    await user.click(screen.getByRole("button", { name: "Sign in" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Invalid username or password");
    expect(router.replace).not.toHaveBeenCalled();
    expect(storedToken()).toBeNull();
  });

  it("empty fields are sent to the backend without frontend checks (L31.1)", async () => {
    const fetchMock = fakeFetch({ "POST /api/auth/login": problem(400, "username must not be blank") });
    const user = userEvent.setup();
    render(<LoginScreen />);

    await user.click(screen.getByRole("button", { name: "Sign in" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("username must not be blank");
    const [init] = callsTo(fetchMock, "POST /api/auth/login");
    expect(bodyOf(init)).toEqual({ username: "", password: "" });
  });

  it("network error shows Cannot reach the server (L34.5)", async () => {
    const failingFetch = vi.fn().mockRejectedValue(new TypeError("Failed to fetch"));
    vi.stubGlobal("fetch", failingFetch);
    const user = userEvent.setup();
    render(<LoginScreen />);

    await user.type(screen.getByRole("textbox", { name: "Username" }), "ana");
    await user.type(screen.getByLabelText("Password"), "password");
    await user.click(screen.getByRole("button", { name: "Sign in" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Cannot reach the server");
    expect(router.replace).not.toHaveBeenCalled();
    expect(storedToken()).toBeNull();
  });
});
