"use client";

import { useCallback, useEffect, useState } from "react";
import { ErrorAlert } from "@/components/ErrorAlert";
import {
  buttonClass,
  cellClass,
  headerCellClass,
  inputClass,
  secondaryButtonClass,
  tableClass,
} from "@/components/styles";
import { useApiError } from "@/components/useApiError";
import {
  approveRequest,
  cancelRequest,
  getApprovals,
  getCancellableRequests,
  rejectRequest,
} from "@/lib/api";
import { TYPE_LABELS } from "@/lib/labels";
import type { TimeOffRequest } from "@/lib/types";

type Act = (id: number, text: string) => Promise<TimeOffRequest>;
type ListResults = [PromiseSettledResult<TimeOffRequest[]>, PromiseSettledResult<TimeOffRequest[]>];

function loadLists(): Promise<ListResults> {
  return Promise.allSettled([getApprovals(), getCancellableRequests()]);
}

export function ApprovalsScreen() {
  const [pending, setPending] = useState<TimeOffRequest[] | null>(null);
  const [approved, setApproved] = useState<TimeOffRequest[] | null>(null);
  const [comments, setComments] = useState<Record<number, string>>({});
  const [reasons, setReasons] = useState<Record<number, string>>({});
  const { error, handleError, clearError } = useApiError();

  // Both lists load together. A list that fails keeps what it had (nothing on first load);
  // the alert shows the error of getApprovals() first, whichever answers first (L40, L41).
  const showLists = useCallback((results: ListResults) => {
    const [pendingResult, approvedResult] = results;

    if (pendingResult.status === "fulfilled") {
      setPending(pendingResult.value);
    }
    if (approvedResult.status === "fulfilled") {
      setApproved(approvedResult.value);
    }

    const failed = [pendingResult, approvedResult].find((result) => result.status === "rejected");
    if (failed) {
      handleError(failed.reason);
    }
  }, [handleError]);

  useEffect(() => {
    loadLists().then(showLists);
  }, [showLists]);

  function setComment(id: number, comment: string) {
    setComments((current) => ({ ...current, [id]: comment }));
  }

  function setReason(id: number, reason: string) {
    setReasons((current) => ({ ...current, [id]: reason }));
  }

  // No check that a comment or reason is filled in: the backend answers 400 (R5, D3).
  async function act(action: Act, id: number, text: string) {
    try {
      await action(id, text);
    } catch (caught) {
      handleError(caught);
      return;
    }

    clearError();
    const results = await loadLists();
    showLists(results);
  }

  function renderPending() {
    if (pending === null) {
      return null;
    }
    if (pending.length === 0) {
      return <p className="text-sm text-zinc-600">No requests waiting for approval.</p>;
    }

    return (
      <table aria-label="Pending requests" className={tableClass}>
        <thead>
          <tr>
            <th className={headerCellClass}>Employee</th>
            <th className={headerCellClass}>Type</th>
            <th className={headerCellClass}>Start date</th>
            <th className={headerCellClass}>End date</th>
            <th className={headerCellClass}>Reason</th>
            <th className={headerCellClass}>Decision</th>
          </tr>
        </thead>
        <tbody>
          {pending.map((request) => (
            <tr key={request.id}>
              <td className={cellClass}>{request.ownerName}</td>
              <td className={cellClass}>{TYPE_LABELS[request.type]}</td>
              <td className={cellClass}>{request.startDate}</td>
              <td className={cellClass}>{request.endDate}</td>
              <td className={cellClass}>{request.reason ?? ""}</td>
              <td className={cellClass}>
                <div className="flex flex-wrap items-end gap-2">
                  <label className="flex flex-col gap-1 text-xs">
                    Comment
                    <input
                      className={inputClass}
                      type="text"
                      value={comments[request.id] ?? ""}
                      onChange={(event) => setComment(request.id, event.target.value)}
                    />
                  </label>
                  <button
                    type="button"
                    className={buttonClass}
                    onClick={() => act(approveRequest, request.id, comments[request.id] ?? "")}
                  >
                    Approve
                  </button>
                  <button
                    type="button"
                    className={secondaryButtonClass}
                    onClick={() => act(rejectRequest, request.id, comments[request.id] ?? "")}
                  >
                    Reject
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  // Who may cancel and whether a request has started is decided by the backend (T6, L39).
  function renderApproved() {
    if (approved === null) {
      return null;
    }
    if (approved.length === 0) {
      return <p className="text-sm text-zinc-600">No approved requests to cancel.</p>;
    }

    return (
      <table aria-label="Approved requests" className={tableClass}>
        <thead>
          <tr>
            <th className={headerCellClass}>Employee</th>
            <th className={headerCellClass}>Type</th>
            <th className={headerCellClass}>Start date</th>
            <th className={headerCellClass}>End date</th>
            <th className={headerCellClass}>Approved by</th>
            <th className={headerCellClass}>Cancel</th>
          </tr>
        </thead>
        <tbody>
          {approved.map((request) => (
            <tr key={request.id}>
              <td className={cellClass}>{request.ownerName}</td>
              <td className={cellClass}>{TYPE_LABELS[request.type]}</td>
              <td className={cellClass}>{request.startDate}</td>
              <td className={cellClass}>{request.endDate}</td>
              <td className={cellClass}>{request.decidedByName ?? ""}</td>
              <td className={cellClass}>
                <div className="flex flex-wrap items-end gap-2">
                  <label className="flex flex-col gap-1 text-xs">
                    Cancel reason
                    <input
                      className={inputClass}
                      type="text"
                      value={reasons[request.id] ?? ""}
                      onChange={(event) => setReason(request.id, event.target.value)}
                    />
                  </label>
                  <button
                    type="button"
                    className={secondaryButtonClass}
                    onClick={() => act(cancelRequest, request.id, reasons[request.id] ?? "")}
                  >
                    Cancel
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">Approvals</h1>
      <ErrorAlert message={error} />
      <section className="flex flex-col gap-3">
        <h2 className="text-lg font-semibold">Pending requests</h2>
        {renderPending()}
      </section>
      <section className="flex flex-col gap-3">
        <h2 className="text-lg font-semibold">Approved requests</h2>
        {renderApproved()}
      </section>
    </div>
  );
}
