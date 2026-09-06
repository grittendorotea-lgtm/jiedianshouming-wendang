import { readFile } from "node:fs/promises";
import path from "node:path";

export const dynamic = "force-static";

const FILE_NAME = "继电器接点寿命测试软件-课题报告.docx";
const FILE_PATH = path.join(
  process.cwd(),
  "public/downloads/relay-life-test-report.docx"
);

export async function GET() {
  const data = await readFile(FILE_PATH);
  const encoded = encodeURIComponent(FILE_NAME);
  return new Response(data, {
    headers: {
      "Content-Type":
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      "Content-Disposition": `attachment; filename="relay-life-test-report.docx"; filename*=UTF-8''${encoded}`,
      "Cache-Control": "public, max-age=0, must-revalidate",
    },
  });
}
