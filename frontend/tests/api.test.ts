import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  ApiError,
  approveRequest,
  cancelRequest,
  createRequest,
  getApprovals,
  getCalendar,
  getMe,
  getMyBalance,
  getMyRequests,
  getTeams,
  login,
  rejectRequest,
} from "@/lib/api";
import { ANA, IVAN, bodyOf, errorOf, fakeFetch, ok, problem, signIn, storedToken } from "./helpers";

// T8, API klijent: put, metoda, zaglavlja, tijelo; greške kao ApiError.

describe("calls with token", () => {
  beforeEach(() => signIn("tok-ivan"));

  it.each([
    { name: "getMe", call: () => getMe(), method: "GET", url: "/api/me", body: undefined },
    { name: "getMyBalance", call: () => getMyBalance(), method: "GET", url: "/api/me/balance", body: undefined },
    { name: "getMyRequests", call: () => getMyRequests(), method: "GET", url: "/api/me/requests", body: undefined },
    {
      name: "createRequest",
      call: () =>
        createRequest({ type: "VACATION", startDate: "2026-11-02", endDate: "2026-11-06", reason: "Trip" }),
      method: "POST",
      url: "/api/requests",
      body: { type: "VACATION", startDate: "2026-11-02", endDate: "2026-11-06", reason: "Trip" },
    },
    {
      name: "createRequest with empty reason",
      call: () => createRequest({ type: "DAY_OFF", startDate: "", endDate: "", reason: "" }),
      method: "POST",
      url: "/api/requests",
      body: { type: "DAY_OFF", startDate: "", endDate: "", reason: "" },
    },
    {
      name: "cancelRequest",
      call: () => cancelRequest(7, "Plans changed"),
      method: "POST",
      url: "/api/requests/7/cancel",
      body: { reason: "Plans changed" },
    },
    { name: "getApprovals", call: () => getApprovals(), method: "GET", url: "/api/approvals", body: undefined },
    {
      name: "approveRequest",
      call: () => approveRequest(7, "ok"),
      method: "POST",
      url: "/api/requests/7/approve",
      body: { comment: "ok" },
    },
    {
      name: "approveRequest with empty comment",
      call: () => approveRequest(7, ""),
      method: "POST",
      url: "/api/requests/7/approve",
      body: { comment: "" },
    },
    {
      name: "rejectRequest",
      call: () => rejectRequest(7, "Busy week"),
      method: "POST",
      url: "/api/requests/7/reject",
      body: { comment: "Busy week" },
    },
    {
      name: "getCalendar without team",
      call: () => getCalendar("2026-11"),
      method: "GET",
      url: "/api/calendar?month=2026-11",
      body: undefined,
    },
    {
      name: "getCalendar with team",
      call: () => getCalendar("2026-11", 1),
      method: "GET",
      url: "/api/calendar?month=2026-11&teamId=1",
      body: undefined,
    },
    { name: "getTeams", call: () => getTeams(), method: "GET", url: "/api/teams", body: undefined },
  ])("$name sends $method $url with bearer token", async ({ call, method, url, body }) => {
    const fetchMock = fakeFetch({ [`${method} ${url}`]: ok({}) });

    await call();

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [calledUrl, init] = fetchMock.mock.calls[0];
    expect(calledUrl).toBe(url);
    expect(init?.method).toBe(method);
    expect(init?.headers).toMatchObject({ Authorization: "Bearer tok-ivan" });
    if (body === undefined) {
      expect(init?.body == null).toBe(true);
    } else {
      expect(init?.headers).toMatchObject({ "Content-Type": "application/json" });
      expect(bodyOf(init ?? {})).toEqual(body);
    }
  });

  it("returns parsed JSON on 2xx", async () => {
    const balance = { year: 2026, entitlement: 20, used: 5, pending: 3, remaining: 12 };
    fakeFetch({ "GET /api/me/balance": ok(balance) });

    const result = await getMyBalance();

    expect(result).toEqual(balance);
  });
});

describe("login", () => {
  it("sends credentials without Authorization and stores the token", async () => {
    signIn("old-token");
    const fetchMock = fakeFetch({ "POST /api/auth/login": ok({ token: "tok-ana", user: ANA }) });

    const result = await login("ana", "password");

    const [, init] = fetchMock.mock.calls[0];
    const headers = (init?.headers ?? {}) as Record<string, string>;
    expect(headers.Authorization).toBeUndefined();
    expect(bodyOf(init ?? {})).toEqual({ username: "ana", password: "password" });
    expect(result).toEqual({ token: "tok-ana", user: ANA });
    expect(storedToken()).toBe("tok-ana");
  });

  it("401 on login does not clear an existing token", async () => {
    signIn("old-token");
    fakeFetch({ "POST /api/auth/login": problem(401, "Invalid username or password") });

    const error = await errorOf(login("ana", "wrong"));

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(401);
    expect(error.message).toBe("Invalid username or password");
    expect(storedToken()).toBe("old-token");
  });
});

describe("without token", () => {
  it("sends no Authorization header", async () => {
    const fetchMock = fakeFetch({ "GET /api/me": ok(IVAN) });

    await getMe();

    const [, init] = fetchMock.mock.calls[0];
    const headers = (init?.headers ?? {}) as Record<string, string>;
    expect(headers.Authorization).toBeUndefined();
  });
});

describe("errors", () => {
  beforeEach(() => signIn("tok-ivan"));

  it("ProblemDetail gives ApiError with status and detail", async () => {
    fakeFetch({
      "POST /api/requests": problem(409, "Request overlaps an existing pending or approved request"),
    });

    const error = await errorOf(
      createRequest({ type: "VACATION", startDate: "2026-11-02", endDate: "2026-11-06", reason: "" }),
    );

    expect(error).toBeInstanceOf(ApiError);
    expect(error).toBeInstanceOf(Error);
    expect(error.status).toBe(409);
    expect(error.message).toBe("Request overlaps an existing pending or approved request");
    expect(storedToken()).toBe("tok-ivan");
  });

  it("JSON body without detail gives generic message", async () => {
    fakeFetch({ "GET /api/me/requests": ok({ title: "Internal Server Error" }, 500) });

    const error = await errorOf(getMyRequests());

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(500);
    expect(error.message).toBe("Request failed with status 500");
  });

  it("non-JSON body gives generic message", async () => {
    fakeFetch({
      "GET /api/teams": () =>
        new Response("<html>Bad gateway</html>", { status: 502, headers: { "Content-Type": "text/html" } }),
    });

    const error = await errorOf(getTeams());

    expect(error.status).toBe(502);
    expect(error.message).toBe("Request failed with status 502");
  });

  it("network failure gives status 0", async () => {
    vi.stubGlobal("fetch", vi.fn().mockRejectedValue(new TypeError("Failed to fetch")));

    const error = await errorOf(getMe());

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(0);
    expect(error.message).toBe("Cannot reach the server");
  });

  it("401 outside login clears the token", async () => {
    fakeFetch({ "GET /api/me/requests": problem(401, "Invalid or expired token") });

    const error = await errorOf(getMyRequests());

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(401);
    expect(storedToken()).toBeNull();
  });
});
