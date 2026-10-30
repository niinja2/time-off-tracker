"use client";

import { useRouter } from "next/navigation";
import { useState, type FormEvent } from "react";
import { ErrorAlert } from "@/components/ErrorAlert";
import { buttonClass, inputClass } from "@/components/styles";
import { login } from "@/lib/api";

export function LoginScreen() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  // No checks here: an empty field goes to the backend, which answers 400 (interface T8, Login).
  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      await login(username, password);
    } catch (caught) {
      const message = caught instanceof Error ? caught.message : String(caught);
      setError(message);
      return;
    }

    setError(null);
    router.replace("/my-time-off");
  }

  return (
    <main className="mx-auto mt-24 w-full max-w-sm px-4">
      <h1 className="mb-6 text-2xl font-semibold">Sign in</h1>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
        <label className="flex flex-col gap-1 text-sm">
          Username
          <input
            className={inputClass}
            type="text"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            autoComplete="username"
          />
        </label>
        <label className="flex flex-col gap-1 text-sm">
          Password
          <input
            className={inputClass}
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            autoComplete="current-password"
          />
        </label>
        <ErrorAlert message={error} />
        <button type="submit" className={buttonClass}>
          Sign in
        </button>
      </form>
    </main>
  );
}
