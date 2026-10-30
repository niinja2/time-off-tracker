// JSON shapes from the backend (interfaces T2 to T7). Field names match the JSON exactly.

export type TimeOffType = "VACATION" | "SICK_LEAVE" | "DAY_OFF";

export type RequestStatus = "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED";

export interface Me {
  id: number;
  username: string;
  name: string;
  admin: boolean;
  teamLead: boolean;
}

export interface Balance {
  year: number;
  entitlement: number;
  used: number;
  pending: number;
  remaining: number;
}

export interface TimeOffRequest {
  id: number;
  ownerId: number;
  ownerName: string;
  type: TimeOffType;
  startDate: string;
  endDate: string;
  reason: string | null;
  status: RequestStatus;
  createdAt: string;
  decisionComment: string | null;
  decidedById: number | null;
  decidedByName: string | null;
  decidedAt: string | null;
  cancelReason: string | null;
  cancelledById: number | null;
  cancelledByName: string | null;
  cancelledAt: string | null;
}

// LIMITED entries have no type and status keys (interface T7).
export interface CalendarEntry {
  name: string;
  startDate: string;
  endDate: string;
  type?: TimeOffType;
  status?: RequestStatus;
}

export interface Team {
  id: number;
  name: string;
}

export interface NewRequest {
  type: TimeOffType;
  startDate: string;
  endDate: string;
  reason: string;
}

export interface LoginResponse {
  token: string;
  user: Me;
}
