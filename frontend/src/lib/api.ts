// API client: adds the token, sends the request, turns errors into ApiError (interface T8).

import { clearToken, getToken, setToken } from "@/lib/session";
import type {
  Balance,
  CalendarEntry,
  LoginResponse,
  Me,
  NewRequest,
  Team,
  TimeOffRequest,
} from "@/lib/types";

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

type Method = "GET" | "POST";

interface CallOptions {
  body?: unknown;
  withToken?: boolean;
}

function buildHeaders(hasBody: boolean, withToken: boolean): Record<string, string> {
  const headers: Record<string, string> = {};
  const token = withToken ? getToken() : null;

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (hasBody) {
    headers["Content-Type"] = "application/json";
  }

  return headers;
}

async function readDetail(response: Response): Promise<string> {
  const fallback = `Request failed with status ${response.status}`;

  try {
    const body = await response.json();
    const detail = body?.detail;
    if (typeof detail === "string" && detail !== "") {
      return detail;
    }
    return fallback;
  } catch {
    return fallback;
  }
}

async function call<T>(method: Method, url: string, options: CallOptions = {}): Promise<T> {
  const withToken = options.withToken ?? true;
  const hasBody = options.body !== undefined;
  const init: RequestInit = {
    method,
    headers: buildHeaders(hasBody, withToken),
  };
  if (hasBody) {
    init.body = JSON.stringify(options.body);
  }

  let response: Response;
  try {
    response = await fetch(url, init);
  } catch {
    throw new ApiError(0, "Cannot reach the server");
  }

  if (response.ok) {
    return (await response.json()) as T;
  }

  // Only calls that carry the token lose it on 401; login does not (interface T8, Stanje).
  if (response.status === 401 && withToken) {
    clearToken();
  }
  const detail = await readDetail(response);
  throw new ApiError(response.status, detail);
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const result = await call<LoginResponse>("POST", "/api/auth/login", {
    body: { username, password },
    withToken: false,
  });
  setToken(result.token);
  return result;
}

export function getMe(): Promise<Me> {
  return call<Me>("GET", "/api/me");
}

export function getMyBalance(): Promise<Balance> {
  return call<Balance>("GET", "/api/me/balance");
}

export function getMyRequests(): Promise<TimeOffRequest[]> {
  return call<TimeOffRequest[]>("GET", "/api/me/requests");
}

export function createRequest(request: NewRequest): Promise<TimeOffRequest> {
  const body = {
    type: request.type,
    startDate: request.startDate,
    endDate: request.endDate,
    reason: request.reason,
  };
  return call<TimeOffRequest>("POST", "/api/requests", { body });
}

export function cancelRequest(id: number, reason: string): Promise<TimeOffRequest> {
  return call<TimeOffRequest>("POST", `/api/requests/${id}/cancel`, { body: { reason } });
}

export function getApprovals(): Promise<TimeOffRequest[]> {
  return call<TimeOffRequest[]>("GET", "/api/approvals");
}

export function getCancellableRequests(): Promise<TimeOffRequest[]> {
  return call<TimeOffRequest[]>("GET", "/api/approvals/cancellable");
}

export function approveRequest(id: number, comment: string): Promise<TimeOffRequest> {
  return call<TimeOffRequest>("POST", `/api/requests/${id}/approve`, { body: { comment } });
}

export function rejectRequest(id: number, comment: string): Promise<TimeOffRequest> {
  return call<TimeOffRequest>("POST", `/api/requests/${id}/reject`, { body: { comment } });
}

export function getCalendar(month: string, teamId?: number): Promise<CalendarEntry[]> {
  const params = new URLSearchParams({ month });
  if (teamId !== undefined) {
    params.set("teamId", String(teamId));
  }
  return call<CalendarEntry[]>("GET", `/api/calendar?${params.toString()}`);
}

export function getTeams(): Promise<Team[]> {
  return call<Team[]>("GET", "/api/teams");
}
