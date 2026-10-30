"use client";

import { useCallback, useEffect, useState, type FormEvent } from "react";
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
import { cancelRequest, createRequest, getMyBalance, getMyRequests } from "@/lib/api";
import { STATUS_LABELS, TYPE_LABELS, TYPE_OPTIONS } from "@/lib/labels";
import type { Balance, NewRequest, TimeOffRequest, TimeOffType } from "@/lib/types";

const EMPTY_FORM: NewRequest = { type: "VACATION", startDate: "", endDate: "", reason: "" };

// Status text with who decided or cancelled (L32); without a name only the status.
function statusText(request: TimeOffRequest): string {
  const label = STATUS_LABELS[request.status];
  const byName = request.status === "CANCELLED" ? request.cancelledByName : request.decidedByName;

  if (request.status === "PENDING" || !byName) {
    return label;
  }

  return `${label} by ${byName}`;
}

function commentText(request: TimeOffRequest): string {
  return request.decisionComment ?? request.cancelReason ?? "";
}

function isCancellable(request: TimeOffRequest): boolean {
  return request.status === "PENDING" || request.status === "APPROVED";
}

function BalanceSection({ balance }: { balance: Balance }) {
  const title = `Vacation balance ${balance.year}`;
  const rows: [string, number][] = [
    ["Entitlement", balance.entitlement],
    ["Used", balance.used],
    ["Pending", balance.pending],
    ["Remaining", balance.remaining],
  ];

  return (
    <section aria-label={title} className="rounded border border-zinc-200 p-4">
      <h2 className="mb-3 text-lg font-semibold">{title}</h2>
      <dl className="grid grid-cols-4 gap-4">
        {rows.map(([term, value]) => (
          <div key={term}>
            <dt className="text-xs uppercase text-zinc-500">{term}</dt>
            <dd className="text-2xl font-semibold">{value}</dd>
          </div>
        ))}
      </dl>
    </section>
  );
}

export function MyTimeOffScreen() {
  const [balance, setBalance] = useState<Balance | null>(null);
  const [requests, setRequests] = useState<TimeOffRequest[] | null>(null);
  const [form, setForm] = useState<NewRequest>(EMPTY_FORM);
  const [cancellingId, setCancellingId] = useState<number | null>(null);
  const [cancelReason, setCancelReason] = useState("");
  const { error, handleError, clearError } = useApiError();

  const reload = useCallback(async () => {
    const loadedRequests = await getMyRequests();
    setRequests(loadedRequests);
    const loadedBalance = await getMyBalance();
    setBalance(loadedBalance);
  }, []);

  useEffect(() => {
    getMyBalance()
      .then(setBalance)
      .catch(handleError);
    getMyRequests()
      .then(setRequests)
      .catch(handleError);
  }, [handleError]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      await createRequest(form);
    } catch (caught) {
      handleError(caught);
      return;
    }

    clearError();
    setForm(EMPTY_FORM);
    await reload().catch(handleError);
  }

  function startCancel(id: number) {
    setCancellingId(id);
    setCancelReason("");
  }

  function keepRequest() {
    setCancellingId(null);
    setCancelReason("");
  }

  async function confirmCancel(id: number) {
    try {
      await cancelRequest(id, cancelReason);
    } catch (caught) {
      handleError(caught);
      return;
    }

    clearError();
    keepRequest();
    await reload().catch(handleError);
  }

  function renderActions(request: TimeOffRequest) {
    if (!isCancellable(request)) {
      return null;
    }
    if (cancellingId !== request.id) {
      return (
        <button type="button" className={secondaryButtonClass} onClick={() => startCancel(request.id)}>
          Cancel
        </button>
      );
    }

    return (
      <div className="flex flex-col gap-2">
        <label className="flex flex-col gap-1 text-xs">
          Cancel reason
          <input
            className={inputClass}
            type="text"
            value={cancelReason}
            onChange={(event) => setCancelReason(event.target.value)}
          />
        </label>
        <div className="flex gap-2">
          <button type="button" className={buttonClass} onClick={() => confirmCancel(request.id)}>
            Confirm cancel
          </button>
          <button type="button" className={secondaryButtonClass} onClick={keepRequest}>
            Keep request
          </button>
        </div>
      </div>
    );
  }

  function renderRequests() {
    if (requests === null) {
      return null;
    }
    if (requests.length === 0) {
      return <p className="text-sm text-zinc-600">No requests yet.</p>;
    }

    return (
      <table aria-label="My requests" className={tableClass}>
        <thead>
          <tr>
            <th className={headerCellClass}>Type</th>
            <th className={headerCellClass}>Start date</th>
            <th className={headerCellClass}>End date</th>
            <th className={headerCellClass}>Reason</th>
            <th className={headerCellClass}>Status</th>
            <th className={headerCellClass}>Comment</th>
            <th className={headerCellClass}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {requests.map((request) => (
            <tr key={request.id}>
              <td className={cellClass}>{TYPE_LABELS[request.type]}</td>
              <td className={cellClass}>{request.startDate}</td>
              <td className={cellClass}>{request.endDate}</td>
              <td className={cellClass}>{request.reason ?? ""}</td>
              <td className={cellClass}>{statusText(request)}</td>
              <td className={cellClass}>{commentText(request)}</td>
              <td className={cellClass}>{renderActions(request)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">My time off</h1>
      <ErrorAlert message={error} />
      {balance && <BalanceSection balance={balance} />}

      <form
        aria-label="New request"
        onSubmit={handleSubmit}
        noValidate
        className="flex flex-wrap items-end gap-3 rounded border border-zinc-200 p-4"
      >
        <label className="flex flex-col gap-1 text-sm">
          Type
          <select
            className={inputClass}
            value={form.type}
            onChange={(event) => setForm({ ...form, type: event.target.value as TimeOffType })}
          >
            {TYPE_OPTIONS.map((type) => (
              <option key={type} value={type}>
                {TYPE_LABELS[type]}
              </option>
            ))}
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm">
          Start date
          <input
            className={inputClass}
            type="date"
            value={form.startDate}
            onChange={(event) => setForm({ ...form, startDate: event.target.value })}
          />
        </label>
        <label className="flex flex-col gap-1 text-sm">
          End date
          <input
            className={inputClass}
            type="date"
            value={form.endDate}
            onChange={(event) => setForm({ ...form, endDate: event.target.value })}
          />
        </label>
        <label className="flex flex-1 flex-col gap-1 text-sm">
          Reason
          <input
            className={inputClass}
            type="text"
            value={form.reason}
            onChange={(event) => setForm({ ...form, reason: event.target.value })}
          />
        </label>
        <button type="submit" className={buttonClass}>
          Submit request
        </button>
      </form>

      {renderRequests()}
    </div>
  );
}
