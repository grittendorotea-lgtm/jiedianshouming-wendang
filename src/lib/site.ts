const basePath = process.env.NEXT_PUBLIC_BASE_PATH || "";

export function withBase(path: string): string {
  const normalized = path.startsWith("/") ? path : `/${path}`;
  return `${basePath}${normalized}`;
}

/** Static Word file in /public/downloads — works on GitHub Pages. */
export const WORD_FILE = withBase(
  "/downloads/relay-life-test-report.docx"
);

export const MARKDOWN_FILE = withBase("/md.html");

export const GITHUB_REPO_URL =
  "https://github.com/grittendorotea-lgtm/footboll";
export const GITHUB_PAGES_URL =
  "https://grittendorotea-lgtm.github.io/footboll/";
export const QR_FILE = withBase("/qr-phone.png");
