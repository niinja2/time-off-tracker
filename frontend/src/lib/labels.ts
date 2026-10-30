// How API values are shown in the UI, the same on every screen (interface T8, Komponente).

import type { RequestStatus, TimeOffType } from "@/lib/types";

export const TYPE_LABELS: Record<TimeOffType, string> = {
  VACATION: "Vacation",
  SICK_LEAVE: "Sick leave",
  DAY_OFF: "Day off",
};

export const STATUS_LABELS: Record<RequestStatus, string> = {
  PENDING: "Pending",
  APPROVED: "Approved",
  REJECTED: "Rejected",
  CANCELLED: "Cancelled",
};

export const TYPE_OPTIONS: TimeOffType[] = ["VACATION", "SICK_LEAVE", "DAY_OFF"];
