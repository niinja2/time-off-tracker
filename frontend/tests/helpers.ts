import { vi } from "vitest";

// Lažni backend za T8: svaki odgovor je funkcija, jer se tijelo Responsea smije pročitati samo jednom.

export const TOKEN_KEY = "timeoff.token";

export type Reply = () => Response;

export type Routes = Record<string, Reply | Reply[]>;

export type FetchMock = ReturnType<typeof fakeFetch>;

export function ok(body: unknown, status = 200): Reply {
  return () =>
    new Response(JSON.stringify(body), {
      status,
      headers: { "Content-Type": "application/json" },
    });
}

export function problem(status: number, detail: string): Reply {
  return () =>
    new Response(JSON.stringify({ type: "about:blank", title: "Error", status, detail }), {
      status,
      headers: { "Content-Type": "application/problem+json" },
    });
}

// Ključ rute je "METODA put", npr. "GET /api/me" ili "POST /api/requests/7/approve".
// Niz odgovora vraća se redom; zadnji se ponavlja.
export function fakeFetch(routes: Routes) {
  const used: Record<string, number> = {};
  const mock = vi.fn(async (url: string, init?: RequestInit) => {
    const key = keyOf(url, init);
    const route = routes[key];
    if (route === undefined) {
      return problem(599, `Unexpected request: ${key}`)();
    }
    if (!Array.isArray(route)) {
      return route();
    }
    const count = used[key] ?? 0;
    used[key] = count + 1;
    const index = Math.min(count, route.length - 1);
    return route[index]();
  });
  vi.stubGlobal("fetch", mock);
  return mock;
}

export function callsTo(mock: FetchMock, key: string): RequestInit[] {
  return mock.mock.calls
    .filter(([url, init]) => keyOf(url, init) === key)
    .map(([, init]) => init ?? {});
}

export function bodyOf(init: RequestInit): unknown {
  return JSON.parse(init.body as string);
}

export function signIn(token = "test-token"): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function storedToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export async function errorOf(promise: Promise<unknown>): Promise<{ status: number; message: string }> {
  try {
    await promise;
  } catch (error) {
    return error as { status: number; message: string };
  }
  throw new Error("Expected the call to fail");
}

function keyOf(url: string, init?: RequestInit): string {
  const method = init?.method ?? "GET";
  return `${method} ${url}`;
}

// Seed (T1): Ana vodi Platform, Eva je admin, Ivan je zaposlenik.

export const IVAN = { id: 2, username: "ivan", name: "Ivan", admin: false, teamLead: false };
export const ANA = { id: 1, username: "ana", name: "Ana", admin: false, teamLead: true };
export const EVA = { id: 8, username: "eva", name: "Eva", admin: true, teamLead: false };

export function request(overrides: Record<string, unknown> = {}): Record<string, unknown> {
  return {
    id: 1,
    ownerId: 2,
    ownerName: "Ivan",
    type: "VACATION",
    startDate: "2026-11-02",
    endDate: "2026-11-06",
    reason: "Trip",
    status: "PENDING",
    createdAt: "2026-10-30T09:00:00Z",
    decisionComment: null,
    decidedById: null,
    decidedByName: null,
    decidedAt: null,
    cancelReason: null,
    cancelledById: null,
    cancelledByName: null,
    cancelledAt: null,
    ...overrides,
  };
}
