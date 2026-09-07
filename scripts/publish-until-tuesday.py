#!/usr/bin/env python3
"""Publish a standalone report page that does not depend on this VM."""

from __future__ import annotations

import base64
import json
import mimetypes
import subprocess
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PUBLIC = ROOT / "public"
MD = PUBLIC / "md.html"
FIG1 = PUBLIC / "figures" / "fig1-module-architecture.png"
FIG2 = PUBLIC / "figures" / "fig2-control-flow.png"
DOCX = PUBLIC / "downloads" / "relay-life-test-report.docx"
OUT_DIR = ROOT / "public" / "hosted"
OUT_HTML = OUT_DIR / "index.html"
RESULT = OUT_DIR / "publish.json"


def data_uri(path: Path) -> str:
    mime = mimetypes.guess_type(path.name)[0] or "application/octet-stream"
    return f"data:{mime};base64,{base64.b64encode(path.read_bytes()).decode('ascii')}"


def build_standalone() -> Path:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    html = MD.read_text(encoding="utf-8")
    html = html.replace("/figures/fig1-module-architecture.png", data_uri(FIG1))
    html = html.replace("/figures/fig2-control-flow.png", data_uri(FIG2))
    word_uri = data_uri(DOCX)
    html = html.replace('href="/word"', f'href="{word_uri}" download="继电器接点寿命测试软件-课题报告.docx"')
    html = html.replace('href="/"', 'href="#top"')
    html = html.replace('href="/print"', f'href="{word_uri}" download="继电器接点寿命测试软件-课题报告.docx"')
    notice = """
  <p style="margin:0 0 1rem;padding:.65rem .8rem;background:#fff4d6;color:#6a3b00;font-size:13.5px;line-height:1.6;">
    点右下角或顶栏「目录」可跳到对应章节。本页独立托管，目标看到
    <strong>2026年9月8日周二北京时间 17:00</strong>。请先下载 Word。
  </p>
"""
    html = html.replace("<article>", "<article>" + notice, 1)
    html = html.replace('id="wordBtn" href="/word"', 'id="wordBtn" href="' + word_uri + '" download="继电器接点寿命测试软件-课题报告.docx"')
    OUT_HTML.write_text(html, encoding="utf-8")
    return OUT_HTML


def curl_upload(args: list[str]) -> str:
    proc = subprocess.run(args, check=False, capture_output=True, text=True)
    out = (proc.stdout or "").strip()
    err = (proc.stderr or "").strip()
    if proc.returncode != 0:
        raise RuntimeError(f"{args[:3]} failed: {err or out}")
    return out


def try_hosts(html_path: Path) -> dict:
    results: dict[str, str] = {}

    try:
        url = curl_upload(
            [
                "curl",
                "-sS",
                "-F",
                f"file=@{html_path};filename=index.html;type=text/html",
                "https://0x0.st",
            ]
        )
        if url.startswith("http"):
            results["0x0"] = url.split()[0]
    except Exception as exc:
        results["0x0_error"] = str(exc)

    try:
        url = curl_upload(
            [
                "curl",
                "-sS",
                "-H",
                "Max-Days: 7",
                "--upload-file",
                str(html_path),
                "https://transfer.sh/report.html",
            ]
        )
        if url.startswith("http"):
            results["transfer"] = url.split()[0]
    except Exception as exc:
        results["transfer_error"] = str(exc)

    try:
        url = curl_upload(
            [
                "curl",
                "-sS",
                "-F",
                "reqtype=fileupload",
                "-F",
                "time=72h",
                "-F",
                f"fileToUpload=@{html_path};filename=report.html",
                "https://litterbox.catbox.moe/resources/internals/api.php",
            ]
        )
        if url.startswith("http"):
            results["litterbox72h"] = url.split()[0]
    except Exception as exc:
        results["litterbox_error"] = str(exc)

    try:
        url = curl_upload(
            [
                "curl",
                "-sS",
                "-F",
                "reqtype=fileupload",
                "-F",
                f"fileToUpload=@{html_path};filename=report.html",
                "https://catbox.moe/user/api.php",
            ]
        )
        if url.startswith("http"):
            results["catbox"] = url.split()[0]
    except Exception as exc:
        results["catbox_error"] = str(exc)

    return results


def check_url(url: str) -> dict:
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=25) as resp:
            body = resp.read(800)
            return {
                "status": resp.status,
                "type": resp.headers.get("Content-Type", ""),
                "looks_html": b"<html" in body.lower() or b"<!doctype" in body.lower(),
                "len": resp.headers.get("Content-Length", ""),
            }
    except Exception as exc:
        return {"error": str(exc)}


def main() -> None:
    path = build_standalone()
    print("standalone", path, path.stat().st_size)
    hosts = try_hosts(path)
    print(json.dumps(hosts, ensure_ascii=False, indent=2))
    checks = {name: check_url(url) for name, url in hosts.items() if name.endswith("error") is False and url.startswith("http")}
    print(json.dumps(checks, ensure_ascii=False, indent=2))
    RESULT.write_text(json.dumps({"hosts": hosts, "checks": checks}, ensure_ascii=False, indent=2), encoding="utf-8")
    chosen = None
    for key in ("litterbox72h", "catbox", "0x0", "transfer"):
        if hosts.get(key, "").startswith("http") and checks.get(key, {}).get("looks_html"):
            chosen = hosts[key]
            break
    if chosen:
        import qrcode

        qr = qrcode.QRCode(border=2, box_size=10)
        qr.add_data(chosen)
        qr.make(fit=True)
        img = qr.make_image(fill_color="#1e2a4a", back_color="white")
        img.save(PUBLIC / "qr-phone.png")
        (OUT_DIR / "PHONE-URL.txt").write_text(
            chosen + "\n带目录跳转的手机阅读页。72小时托管，覆盖周二17:00。\n",
            encoding="utf-8",
        )
        print("chosen", chosen)


if __name__ == "__main__":
    main()
