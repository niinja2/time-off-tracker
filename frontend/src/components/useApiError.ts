"use client";

import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import { ApiError } from "@/lib/api";

// One place for screen errors: 401 goes to /login without a message, anything else shows `detail`.
export function useApiError() {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);

  const handleError = useCallback(
    (caught: unknown) => {
      const isUnauthorized = caught instanceof ApiError && caught.status === 401;
      if (isUnauthorized) {
        router.replace("/login");
        return;
      }

      const message = caught instanceof Error ? caught.message : String(caught);
      setError(message);
    },
    [router],
  );

  const clearError = useCallback(() => setError(null), []);

  return { error, handleError, clearError };
}
