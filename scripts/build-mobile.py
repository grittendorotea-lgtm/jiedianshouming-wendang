#!/usr/bin/env python3
"""Build a phone-friendly HTML reading page with a jump catalog."""

from __future__ import annotations

import re
from pathlib import Path

import markdown
from markdown.extensions.toc import slugify

ROOT = Path(__file__).resolve().parents[1]
MD_SRC = ROOT / "docs" / "课题报告-功能实现分析.md"
OUT_HTML = ROOT / "public" / "md.html"


def prepare_markdown(text: str) -> str:
    text = re.sub(
        r"```mermaid[\s\S]*?```",
        "\n> 结构图见对应章节正文。\n",
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
    converter = markdown.Markdown(
        extensions=["tables", "fenced_code", "sane_lists", "nl2br", "toc"],
        extension_configs={
            "toc": {
                "permalink": False,
                "toc_depth": "2-3",
                "slugify": slugify,
            }
        },
    )
    body = converter.convert(prepare_markdown(raw))
    toc = converter.toc
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
      --navy: #1e2a4a;
    }}
    * {{ box-sizing: border-box; }}
    html {{ scroll-behavior: smooth; }}
    html, body {{ margin: 0; padding: 0; background: var(--bg); color: var(--ink); }}
    body {{
      font-family: "PingFang SC", "Hiragino Sans GB", "Noto Sans SC", "Microsoft YaHei", sans-serif;
      line-height: 1.75;
      font-size: 16.5px;
      padding: 0 0 5.5rem;
    }}
    .bar {{
      position: sticky; top: 0; z-index: 40;
      display: flex; gap: .5rem; flex-wrap: wrap; align-items: center;
      padding: .7rem .9rem;
      background: var(--navy);
      color: #fff;
    }}
    .bar button, .bar a {{
      color: #fff; text-decoration: none; border: 0; cursor: pointer;
      background: #3a4a73; border-radius: 999px;
      padding: .4rem .8rem; font-size: 14px;
      font-family: inherit;
    }}
    .bar .primary {{ background: #c47a2b; }}
    .bar .menu-btn {{ background: #c47a2b; font-weight: 650; }}
    article {{
      max-width: 42rem; margin: 0 auto;
      padding: 1.1rem 1rem 2rem;
    }}
    h1 {{ font-size: 1.55rem; line-height: 1.35; margin: 1.1rem 0 .6rem; }}
    h2, h3, h4 {{ scroll-margin-top: 4.2rem; }}
    h2 {{ font-size: 1.25rem; margin: 1.6rem 0 .7rem; padding-top: .4rem; border-top: 1px solid var(--line); }}
    h3, h4 {{ font-size: 1.05rem; margin: 1.2rem 0 .5rem; }}
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
    table {{ border-collapse: collapse; width: 100%; font-size: 13.5px; background: var(--card); }}
    th, td {{ border: 1px solid var(--line); padding: .4rem .5rem; vertical-align: top; }}
    th {{ background: #f3eee4; text-align: left; }}
    .table-wrap {{ overflow-x: auto; margin: .8rem 0 1rem; }}
    img {{ max-width: 100%; height: auto; border: 1px solid var(--line); border-radius: 8px; background: #fff; }}
    .foot {{ margin-top: 2rem; padding-top: 1rem; border-top: 1px solid var(--line); color: var(--muted); font-size: 13px; }}
    .mask {{
      display: none; position: fixed; inset: 0; background: rgba(15, 23, 42, .45); z-index: 50;
    }}
    .mask.open {{ display: block; }}
    .drawer {{
      position: fixed; top: 0; bottom: 0; left: 0; z-index: 60;
      width: min(20rem, 86vw);
      background: var(--navy); color: #f7f4ee;
      transform: translateX(-105%);
      transition: transform .2s ease;
      display: flex; flex-direction: column;
    }}
    .drawer.open {{ transform: translateX(0); }}
    .drawer h2 {{
      margin: 0; padding: 1rem 1rem .4rem;
      font-size: 1rem; border: 0; color: #fff;
    }}
    .drawer p {{ margin: 0 1rem .8rem; color: #c9d0df; font-size: 12.5px; line-height: 1.55; }}
    .toc {{
      overflow: auto; padding: .2rem .7rem 1.4rem;
      -webkit-overflow-scrolling: touch;
    }}
    .toc ul {{ list-style: none; margin: 0; padding: 0; }}
    .toc > ul > li {{ margin: .15rem 0; }}
    .toc > ul > li > ul {{ margin: .15rem 0 .35rem .7rem; }}
    .toc a {{
      display: block; color: #e8edf6; text-decoration: none;
      padding: .55rem .65rem; border-radius: 8px; font-size: 14.5px; line-height: 1.45;
    }}
    .toc a:active, .toc a:focus {{ background: #3a4a73; }}
    .toc li li a {{ font-size: 13.5px; color: #c9d0df; padding: .4rem .65rem; }}
    .fab {{
      position: fixed; right: 1rem; bottom: 1.1rem; z-index: 35;
      border: 0; border-radius: 999px; background: #c47a2b; color: #fff;
      padding: .8rem 1.15rem; font-size: 15px; font-weight: 650;
      box-shadow: 0 8px 20px rgba(0,0,0,.18);
      font-family: inherit;
    }}
  </style>
</head>
<body>
  <nav class="bar">
    <button type="button" class="menu-btn" id="openMenu">目录</button>
    <a class="primary" id="wordBtn" href="/word">下载 Word</a>
  </nav>
  <div class="mask" id="mask"></div>
  <aside class="drawer" id="drawer">
    <h2>课题报告目录</h2>
    <p>点一项即跳到对应章节。</p>
    <div class="toc">{toc}</div>
  </aside>
  <button type="button" class="fab" id="fab">目录</button>
  <article>
    {body}
    <p class="foot">点右下角或顶栏「目录」可跳转各节。建议先下载 Word 存到手机。</p>
  </article>
  <script>
    function wrapTables() {{
      document.querySelectorAll("table").forEach(function (table) {{
        if (table.parentElement && table.parentElement.classList.contains("table-wrap")) return;
        var wrap = document.createElement("div");
        wrap.className = "table-wrap";
        table.parentNode.insertBefore(wrap, table);
        wrap.appendChild(table);
      }});
    }}
    function setOpen(open) {{
      document.getElementById("drawer").classList.toggle("open", open);
      document.getElementById("mask").classList.toggle("open", open);
      document.body.style.overflow = open ? "hidden" : "";
    }}
    document.getElementById("openMenu").onclick = function () {{ setOpen(true); }};
    document.getElementById("fab").onclick = function () {{ setOpen(true); }};
    document.getElementById("mask").onclick = function () {{ setOpen(false); }};
    document.querySelectorAll(".toc a").forEach(function (link) {{
      link.addEventListener("click", function (event) {{
        var id = decodeURIComponent((link.getAttribute("href") || "").replace("#", ""));
        var target = document.getElementById(id);
        if (!target) return;
        event.preventDefault();
        setOpen(false);
        window.setTimeout(function () {{
          target.scrollIntoView({{ behavior: "smooth", block: "start" }});
          history.replaceState(null, "", "#" + id);
        }}, 80);
      }});
    }});
    wrapTables();
  </script>
</body>
</html>
"""
    OUT_HTML.write_text(html, encoding="utf-8")
    print(f"wrote {OUT_HTML} ({OUT_HTML.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
