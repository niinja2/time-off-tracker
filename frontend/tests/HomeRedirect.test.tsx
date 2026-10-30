import { render } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { HomeRedirect } from "@/components/HomeRedirect";
import { fakeFetch, ok, signIn, IVAN } from "./helpers";

const router = vi.hoisted(() => ({ replace: vi.fn(), push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => router }));

beforeEach(() => {
  router.replace.mockClear();
  router.push.mockClear();
});

// T8 Rute: `/` samo preusmjerava (L37)

describe("HomeRedirect", () => {
  it("with token goes to My time off without calling the API (L37)", async () => {
    signIn("tok-ivan");
    const fetchMock = fakeFetch({ "GET /api/me": ok(IVAN) });

    render(<HomeRedirect />);

    await vi.waitFor(() => expect(router.replace).toHaveBeenCalledWith("/my-time-off"));
    expect(router.replace).not.toHaveBeenCalledWith("/login");
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("without token goes to login without calling the API (L37)", async () => {
    const fetchMock = fakeFetch({ "GET /api/me": ok(IVAN) });

    render(<HomeRedirect />);

    await vi.waitFor(() => expect(router.replace).toHaveBeenCalledWith("/login"));
    expect(router.replace).not.toHaveBeenCalledWith("/my-time-off");
    expect(fetchMock).not.toHaveBeenCalled();
  });
});
