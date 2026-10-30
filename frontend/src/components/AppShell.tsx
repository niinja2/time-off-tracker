"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState, type ReactNode } from "react";
import { ErrorAlert } from "@/components/ErrorAlert";
import { useApiError } from "@/components/useApiError";
import { secondaryButtonClass } from "@/components/styles";
import { getMe } from "@/lib/api";
import { clearToken, getToken } from "@/lib/session";
import type { Me } from "@/lib/types";

const linkClass = "text-sm font-medium text-zinc-700 hover:text-zinc-950 hover:underline";

// Wrapper for signed-in routes: checks the token, loads <Me> on every open (D6), shows navigation.
export function AppShell({ children }: { children: ReactNode }) {
  const router = useRouter();
  const [me, setMe] = useState<Me | null>(null);
  const { error, handleError } = useApiError();

  useEffect(() => {
    if (!getToken()) {
      router.replace("/login");
      return;
    }

    getMe()
      .then(setMe)
      .catch(handleError);
  }, [router, handleError]);

  function handleLogout() {
    clearToken();
    router.replace("/login");
  }

  if (!me) {
    return <ErrorAlert message={error} />;
  }

  const canApprove = me.teamLead || me.admin;

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-6 px-4 py-6">
      <header className="flex flex-wrap items-center justify-between gap-4 border-b border-zinc-200 pb-4">
        <nav aria-label="Main" className="flex gap-4">
          <Link href="/my-time-off" className={linkClass}>
            My time off
          </Link>
          {canApprove && (
            <Link href="/approvals" className={linkClass}>
              Approvals
            </Link>
          )}
          <Link href="/calendar" className={linkClass}>
            Team calendar
          </Link>
        </nav>
        <div className="flex items-center gap-3 text-sm">
          <span>{`Signed in as ${me.name}`}</span>
          <button type="button" className={secondaryButtonClass} onClick={handleLogout}>
            Log out
          </button>
        </div>
      </header>
      <main>{children}</main>
    </div>
  );
}
