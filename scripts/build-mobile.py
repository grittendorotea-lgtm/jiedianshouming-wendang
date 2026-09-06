#!/usr/bin/env python3
"""Build a phone-friendly HTML reading page and a scan QR code."""

from __future__ import annotations

import re
from pathlib import Path

import markdown
import qrcode

ROOT = Path(__file__).resolve().parents[1]
MD_SRC = ROOT / "docs" / "课题报告-功能实现分析.md"
OUT_HTML = ROOT / "public" / "md.html"
OUT_QR = ROOT / "public" / "qr-phone.png"
PHONE_BASE = "https://facts-surgeon-ecommerce-perhaps.trycloudflare.com"
PHONE_MD = f"{PHONE_BASE}/md.html"


def prepare_markdown(text: str) -> str:
    text = re.sub(
        r"```mermaid[\s\S]*?```",
        "\n> 结构图见网页阅读版对应章节。\n",
        text,
    )
    text = text.replace(
        "图 18-1 软件总体模块框图（论文图 1）",
        "![图 18-1 软件总体模块框图](/figures/fig1-module-architecture.png)\n\n**图 18-1** 软件总体模块框图（论文图 1）",
    )
    text = text.replace(
        "图 18-2 核心测试控制算法流程图（论文图 2）",
        "![图 18-2 核心测试控制算法流程图](/figures/fig2-control-flow.png)\n\n**图 18-2** 核心测试控制算法流程图（论文图 2）",
    )
    return text


def main() -> None:
    raw = MD_SRC.read_text(encoding="utf-8")
    body = markdown.markdown(
        prepare_markdown(raw),
        extensions=["tables", "fenced_code", "sane_lists", "nl2br"],
    )
    html = f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
  <meta name="theme-color" content="#1e2a4a" />
  <title>继电器接点寿命测试软件 · 课题报告</title>
  <style>
    :root {{
      --bg: #f7f4ee;
      --ink: #1b2437;
      --muted: #5b6475;
      --line: #e4dccf;
      --card: #fffdf8;
      --accent: #9a4b12;
    }}
    * {{ box-sizing: border-box; }}
    html, body {{ margin: 0; padding: 0; background: var(--bg); color: var(--ink); }}
    body {{
      font-family: "PingFang SC", "Hiragino Sans GB", "Noto Sans SC", "Microsoft YaHei", sans-serif;
      line-height: 1.75;
      font-size: 16.5px;
      padding: 0 0 5rem;
    }}
    .bar {{
      position: sticky; top: 0; z-index: 20;
      display: flex; gap: .5rem; flex-wrap: wrap;
      padding: .7rem .9rem;
      background: #1e2a4a;
      color: #fff;
    }}
    .bar a {{
      color: #fff; text-decoration: none;
      background: #3a4a73; border-radius: 999px;
      padding: .35rem .75rem; font-size: 13px;
    }}
    .bar a.primary {{ background: #c47a2b; }}
    article {{
      max-width: 42rem; margin: 0 auto;
      padding: 1.1rem 1rem 2rem;
    }}
    h1 {{ font-size: 1.55rem; line-height: 1.35; margin: 1.1rem 0 .6rem; }}
    h2 {{ font-size: 1.25rem; margin: 1.6rem 0 .7rem; padding-top: .4rem; border-top: 1px solid var(--line); }}
    h3, h4 {{ font-size: 1.05rem; margin: 1.2rem 0 .5rem; }}
    p, li {{ color: var(--ink); }}
    blockquote {{
      margin: .8rem 0; padding: .2rem .8rem;
      border-left: 3px solid #c47a2b; color: var(--muted);
      background: #fff7ea;
    }}
    code {{
      font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
      font-size: .86em; background: #efe8dc; padding: .1em .3em; border-radius: 4px;
    }}
    pre {{
      overflow-x: auto; background: #1b2437; color: #f4efe6;
      padding: .8rem; border-radius: 8px; font-size: 13px;
    }}
    pre code {{ background: none; color: inherit; padding: 0; }}
    table {{
      border-collapse: collapse; width: 100%;
      font-size: 13.5px; background: var(--card);
    }}
    th, td {{
      border: 1px solid var(--line); padding: .4rem .5rem; vertical-align: top;
    }}
    th {{ background: #f3eee4; text-align: left; }}
    .table-wrap {{ overflow-x: auto; margin: .8rem 0 1rem; }}
    img {{ max-width: 100%; height: auto; border: 1px solid var(--line); border-radius: 8px; background: #fff; }}
    strong {{ font-weight: 650; }}
    .foot {{
      margin-top: 2rem; padding-top: 1rem; border-top: 1px solid var(--line);
      color: var(--muted); font-size: 13px;
    }}
  </style>
</head>
<body>
  <nav class="bar">
    <a class="primary" href="/word">下载 Word</a>
    <a href="/">完整网页版</a>
    <a href="/print">打印稿</a>
  </nav>
  <article>
    {body}
    <p class="foot">手机阅读版由课题报告 Markdown 生成。可先把 Word 存到手机，离线也能看。</p>
  </article>
  <script>
    document.querySelectorAll("table").forEach(function (table) {{
      if (table.parentElement && table.parentElement.classList.contains("table-wrap")) return;
      var wrap = document.createElement("div");
      wrap.className = "table-wrap";
      table.parentNode.insertBefore(wrap, table);
      wrap.appendChild(table);
    }});
  </script>
</body>
</html>
"""
    OUT_HTML.write_text(html, encoding="utf-8")
    qr = qrcode.QRCode(border=2, box_size=10)
    qr.add_data(PHONE_MD)
    qr.make(fit=True)
    qr.make_image(fill_color="#1e2a4a", back_color="white").save(OUT_QR)
    print(f"wrote {OUT_HTML} ({OUT_HTML.stat().st_size} bytes)")
    print(f"wrote {OUT_QR}")
    print(f"phone url {PHONE_MD}")


if __name__ == "__main__":
    main()
