// Month and day helpers for the team calendar. Months are "yyyy-MM", days "yyyy-MM-dd".

function pad(value: number): string {
  return String(value).padStart(2, "0");
}

function formatMonth(year: number, monthIndex: number): string {
  return `${year}-${pad(monthIndex + 1)}`;
}

function parseMonth(month: string): { year: number; monthIndex: number } {
  const [year, monthNumber] = month.split("-").map(Number);
  return { year, monthIndex: monthNumber - 1 };
}

// Current month in the browser's local time (interface T8, Team calendar).
export function currentMonth(): string {
  const now = new Date();
  return formatMonth(now.getFullYear(), now.getMonth());
}

export function shiftMonth(month: string, delta: number): string {
  const { year, monthIndex } = parseMonth(month);
  const shifted = new Date(year, monthIndex + delta, 1);
  return formatMonth(shifted.getFullYear(), shifted.getMonth());
}

// Monday to Friday of the month, ascending (R10: weekends are not shown).
export function workingDaysOfMonth(month: string): string[] {
  const { year, monthIndex } = parseMonth(month);
  const lastDay = new Date(year, monthIndex + 1, 0).getDate();
  const dayNumbers = Array.from({ length: lastDay }, (_, index) => index + 1);

  return dayNumbers
    .filter((day) => !isWeekend(year, monthIndex, day))
    .map((day) => `${month}-${pad(day)}`);
}

function isWeekend(year: number, monthIndex: number, day: number): boolean {
  const weekday = new Date(year, monthIndex, day).getDay();
  return weekday === 0 || weekday === 6;
}
