"use client";

import { useEffect, useState } from "react";
import { Menu, Printer, X } from "lucide-react";
import Link from "next/link";

import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";
import { navItems } from "./nav-items";
import { PartFour } from "./sections/PartFour";
import { PartOne } from "./sections/PartOne";
import { PartThree } from "./sections/PartThree";
import { PartTwo } from "./sections/PartTwo";

export function ReportApp() {
  const [active, setActive] = useState<string>(navItems[0].id);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const headings = navItems
      .map((item) => document.getElementById(item.id))
      .filter((el): el is HTMLElement => Boolean(el));

    if (headings.length === 0) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((entry) => entry.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio);
        if (visible[0]?.target.id) {
          setActive(visible[0].target.id);
        }
      },
      { rootMargin: "-20% 0px -65% 0px", threshold: [0.1, 0.25, 0.5] }
    );

    headings.forEach((el) => observer.observe(el));
    return () => observer.disconnect();
  }, []);

  const nav = (
    <nav className="space-y-1">
      {navItems.map((item) => (
        <a
          key={item.id}
          href={`#${item.id}`}
          onClick={() => setOpen(false)}
          className={cn(
            "block rounded-md px-3 py-2 text-[13px] leading-6 transition-colors",
            active === item.id
              ? "bg-sidebar-accent text-sidebar-primary"
              : "text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-foreground"
          )}
        >
          {item.label}
        </a>
      ))}
    </nav>
  );

  return (
    <div className="min-h-screen bg-background">
      <div className="lg:hidden sticky top-0 z-30 flex items-center justify-between border-b border-border bg-background/95 px-4 py-3 backdrop-blur">
        <div>
          <p className="text-xs text-amber-800">课题报告</p>
          <p className="font-semibold text-slate-900">接点接触电阻监测系统</p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="icon" asChild>
            <Link href="/print" aria-label="打开打印稿">
              <Printer className="h-4 w-4" />
            </Link>
          </Button>
        <Button variant="outline" size="icon" onClick={() => setOpen((v) => !v)}>
          {open ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
        </Button>
        </div>
      </div>

      {open ? (
        <div className="lg:hidden border-b border-border bg-sidebar px-3 py-4">
          {nav}
        </div>
      ) : null}

      <div className="mx-auto flex max-w-7xl">
        <aside className="sticky top-0 hidden h-screen w-72 shrink-0 bg-sidebar lg:block">
          <div className="px-5 pb-3 pt-6">
            <p className="text-[11px] font-semibold tracking-[0.2em] text-sidebar-primary">
              课题报告
            </p>
            <h1 className="mt-2 text-lg font-semibold leading-7 text-sidebar-foreground">
              接点接触电阻
              <br />
              在线监测系统
            </h1>
            <p className="mt-3 text-xs leading-6 text-sidebar-foreground/70">
              功能实现分析，共十五节。
            </p>
            <Link
              href="/print"
              className="mt-4 inline-flex items-center gap-1.5 text-xs text-sidebar-primary hover:underline"
            >
              <Printer className="h-3.5 w-3.5" />
              打开打印稿
            </Link>
          </div>
          <Separator className="bg-sidebar-border" />
          <ScrollArea className="h-[calc(100vh-210px)] px-3 py-4">{nav}</ScrollArea>
        </aside>

        <main className="min-w-0 flex-1 px-4 py-8 md:px-10 md:py-12">
          <header className="mb-10 max-w-3xl">
            <p className="text-sm font-medium text-amber-800">
              软件工程课题 · 功能实现分析
            </p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-900 md:text-4xl">
              接点接触电阻在线监测系统功能实现分析
            </h1>
            <p className="mt-4 text-[16px] leading-8 text-slate-600">
              依据 ZzhejiPanel、DianZu00000、JiaoL、ExecuteCommon
              源码，将系统归纳为 15 项功能，并按 15
              节说明各模块的实现方法。两份 ExecuteCommon 内容相同。
            </p>
          </header>

          <div className="max-w-3xl space-y-16">
            <PartOne />
            <PartTwo />
            <PartThree />
            <PartFour />
          </div>

          <footer className="mt-16 max-w-3xl border-t border-border pt-6 text-sm text-slate-500">
            分析范围：ZzhejiPanel.java、DianZu00000.java、JiaoL.java、ExecuteCommon.java。
            PLC 读写类 RRuANDWone 未包含在这批文件中，其行为根据主界面调用还原。
            完整可粘贴正文见仓库 <code>docs/课题报告-功能实现分析.md</code>
            ，或打开
            <Link href="/print" className="mx-1 text-primary underline-offset-4 hover:underline">
              打印稿
            </Link>
            导出 PDF。
          </footer>
        </main>
      </div>
    </div>
  );
}
