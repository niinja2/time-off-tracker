"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { getToken } from "@/lib/session";

// "/" only redirects: with a token to /my-time-off, without one to /login (interface T8, Rute).
export function HomeRedirect() {
  const router = useRouter();

  useEffect(() => {
    const target = getToken() ? "/my-time-off" : "/login";
    router.replace(target);
  }, [router]);

  return null;
}
