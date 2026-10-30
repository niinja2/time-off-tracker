"use client";

import { useEffect, useState } from "react";
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
import { approveRequest, getApprovals, rejectRequest } from "@/lib/api";
import { TYPE_LABELS } from "@/lib/labels";
import type { TimeOffRequest } from "@/lib/types";

type Decide = (id: number, comment: string) => Promise<TimeOffRequest>;

export function ApprovalsScreen() {
  const [requests, setRequests] = useState<TimeOffRequest[] | null>(null);
  const [comments, setComments] = useState<Record<number, string>>({});
  const { error, handleError, clearError } = useApiError();

  useEffect(() => {
    getApprovals()
      .then(setRequests)
      .catch(handleError);
  }, [handleError]);

  function setComment(id: number, comment: string) {
    setComments((current) => ({ ...current, [id]: comment }));
  }

  // No check that a rejection has a comment: the backend answers 400 (R5).
  async function decide(decideRequest: Decide, id: number) {
    const comment = comments[id] ?? "";
    try {
      await decideRequest(id, comment);
    } catch (caught) {
      handleError(caught);
      return;
    }

    clearError();
    const loaded = await getApprovals().catch(handleError);
    if (loaded) {
      setRequests(loaded);
    }
  }

  function renderRequests() {
    if (requests === null) {
      return null;
    }
    if (requests.length === 0) {
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
          {requests.map((request) => (
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
                    onClick={() => decide(approveRequest, request.id)}
                  >
                    Approve
                  </button>
                  <button
                    type="button"
                    className={secondaryButtonClass}
                    onClick={() => decide(rejectRequest, request.id)}
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

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">Approvals</h1>
      <ErrorAlert message={error} />
      {renderRequests()}
    </div>
  );
}
