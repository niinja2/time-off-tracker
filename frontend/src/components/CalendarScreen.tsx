"use client";

import { useEffect, useState } from "react";
import { ErrorAlert } from "@/components/ErrorAlert";
import { inputClass, secondaryButtonClass } from "@/components/styles";
import { useApiError } from "@/components/useApiError";
import { getCalendar, getTeams } from "@/lib/api";
import { STATUS_LABELS, TYPE_LABELS } from "@/lib/labels";
import { currentMonth, shiftMonth, workingDaysOfMonth } from "@/lib/month";
import type { CalendarEntry, Team } from "@/lib/types";

interface CalendarDay {
  day: string;
  entries: CalendarEntry[];
}

// Dates are "yyyy-MM-dd", so string comparison is date comparison; both ends inclusive.
function coversDay(entry: CalendarEntry, day: string): boolean {
  return entry.startDate <= day && day <= entry.endDate;
}

function groupByDay(month: string, entries: CalendarEntry[]): CalendarDay[] {
  const days = workingDaysOfMonth(month);

  return days
    .map((day) => ({ day, entries: entries.filter((entry) => coversDay(entry, day)) }))
    .filter((calendarDay) => calendarDay.entries.length > 0);
}

// FULL entries carry type and status, LIMITED entries only the name (interface T7).
function entryText(entry: CalendarEntry): string {
  if (!entry.type || !entry.status) {
    return entry.name;
  }

  const type = TYPE_LABELS[entry.type];
  const status = STATUS_LABELS[entry.status];
  return `${entry.name} (${type}, ${status})`;
}

export function CalendarScreen() {
  const [month, setMonth] = useState(currentMonth);
  const [teamId, setTeamId] = useState<number | undefined>(undefined);
  const [teams, setTeams] = useState<Team[]>([]);
  const [entries, setEntries] = useState<CalendarEntry[] | null>(null);
  const { error, handleError, clearError } = useApiError();

  useEffect(() => {
    getTeams()
      .then(setTeams)
      .catch(handleError);
  }, [handleError]);

  useEffect(() => {
    let current = true;

    getCalendar(month, teamId)
      .then((loaded) => {
        if (!current) {
          return;
        }
        clearError();
        setEntries(loaded);
      })
      .catch(handleError);

    return () => {
      current = false;
    };
  }, [month, teamId, handleError, clearError]);

  function handleTeamChange(value: string) {
    const selected = value === "" ? undefined : Number(value);
    setTeamId(selected);
  }

  function renderDays() {
    if (entries === null) {
      return null;
    }

    const days = groupByDay(month, entries);
    if (days.length === 0) {
      return <p className="text-sm text-zinc-600">No absences this month.</p>;
    }

    return (
      <div className="flex flex-col gap-3">
        {days.map(({ day, entries: dayEntries }) => (
          <section key={day} aria-label={day} className="rounded border border-zinc-200 p-3">
            <h3 className="mb-1 text-sm font-semibold">{day}</h3>
            <ul className="list-inside list-disc text-sm">
              {dayEntries.map((entry, index) => (
                <li key={`${entry.name}-${entry.startDate}-${index}`}>{entryText(entry)}</li>
              ))}
            </ul>
          </section>
        ))}
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">Team calendar</h1>
      <div className="flex flex-wrap items-center gap-3">
        <button type="button" className={secondaryButtonClass} onClick={() => setMonth(shiftMonth(month, -1))}>
          Previous month
        </button>
        <h2 className="text-lg font-semibold">{month}</h2>
        <button type="button" className={secondaryButtonClass} onClick={() => setMonth(shiftMonth(month, 1))}>
          Next month
        </button>
        <label className="ml-auto flex items-center gap-2 text-sm">
          Team
          <select
            className={inputClass}
            value={teamId === undefined ? "" : String(teamId)}
            onChange={(event) => handleTeamChange(event.target.value)}
          >
            <option value="">All teams</option>
            {teams.map((team) => (
              <option key={team.id} value={String(team.id)}>
                {team.name}
              </option>
            ))}
          </select>
        </label>
      </div>
      <ErrorAlert message={error} />
      {renderDays()}
    </div>
  );
}
