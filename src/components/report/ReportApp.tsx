"use client";

import { useEffect, useState } from "react";
import { ChevronLeft, ChevronRight, List, Printer, X } from "lucide-react";
import Link from "next/link";

import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";
import { navItems } from "./nav-items";
import { PartFive } from "./sections/PartFive";
import { PartFour } from "./sections/PartFour";
import { PartOne } from "./sections/PartOne";
import { PartSix } from "./sections/PartSix";
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

  useEffect(() => {
    document.body.style.overflow = open ? "hidden" : "";
    return () => {
      document.body.style.overflow = "";
    };
  }, [open]);

  const activeIndex = navItems.findIndex((item) => item.id === active);
  const current = navItems[activeIndex] ?? navItems[0];
  const prev = activeIndex > 0 ? navItems[activeIndex - 1] : null;
  const next =
    activeIndex >= 0 && activeIndex < navItems.length - 1
      ? navItems[activeIndex + 1]
      : null;

  const nav = (
    <nav className="space-y-1">
      {navItems.map((item) => (
        <a
          key={item.id}
          href={`#${item.id}`}
          onClick={() => setOpen(false)}
          className={cn(
            "block rounded-md px-3 py-2.5 text-[14px] leading-6 transition-colors",
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

  const sidebarHead = (
    <div className="px-5 pb-3 pt-6">
      <p className="text-[11px] font-semibold tracking-[0.2em] text-sidebar-primary">
        课题报告
      </p>
      <h1 className="mt-2 text-lg font-semibold leading-7 text-sidebar-foreground">
        继电器接点
        <br />
        寿命测试软件
      </h1>
      <p className="mt-3 text-xs leading-6 text-sidebar-foreground/70">
        前十五节为基础实现，第十六节为 4 拖 1，第十七节归纳难点问—答。点目录即可跳转。
      </p>
      <Link
        href="/print"
        className="mt-4 inline-flex items-center gap-1.5 text-xs text-sidebar-primary hover:underline"
        onClick={() => setOpen(false)}
      >
        <Printer className="h-3.5 w-3.5" />
        打开打印稿
      </Link>
    </div>
  );

  return (
    <div className="min-h-screen bg-background">
      <div className="lg:hidden sticky top-0 z-30 border-b border-border bg-background/95 px-4 py-3 backdrop-blur">
        <div className="flex items-center justify-between gap-3">
          <div className="min-w-0">
            <p className="text-xs text-amber-800">课题报告 · 点目录跳转</p>
            <p className="truncate font-semibold text-slate-900">{current.label}</p>
          </div>
          <div className="flex shrink-0 items-center gap-2">
            <Button variant="outline" size="icon" asChild>
              <Link href="/print" aria-label="打开打印稿">
                <Printer className="h-4 w-4" />
              </Link>
            </Button>
            <Button
              variant="default"
              className="px-3"
              onClick={() => setOpen(true)}
            >
              <List className="h-4 w-4" />
              目录
            </Button>
          </div>
        </div>
      </div>

      {open ? (
        <div className="lg:hidden fixed inset-0 z-50">
          <button
            type="button"
            className="absolute inset-0 bg-slate-900/50"
            aria-label="关闭目录"
            onClick={() => setOpen(false)}
          />
          <aside className="relative flex h-full w-[min(20rem,86vw)] flex-col bg-sidebar shadow-2xl">
            <div className="flex items-center justify-between px-4 pt-4">
              <p className="text-sm text-sidebar-foreground/80">十七节目录</p>
              <Button
                variant="ghost"
                size="icon"
                className="text-sidebar-foreground hover:bg-sidebar-accent"
                onClick={() => setOpen(false)}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
            {sidebarHead}
            <Separator className="bg-sidebar-border" />
            <ScrollArea className="flex-1 px-3 py-4">{nav}</ScrollArea>
          </aside>
        </div>
      ) : null}

      <button
        type="button"
        className="lg:hidden fixed bottom-5 right-4 z-40 flex items-center gap-2 rounded-full bg-primary px-4 py-3 text-sm font-medium text-primary-foreground shadow-lg"
        onClick={() => setOpen(true)}
      >
        <List className="h-4 w-4" />
        目录
      </button>

      <div className="mx-auto flex max-w-7xl">
        <aside className="sticky top-0 hidden h-screen w-72 shrink-0 bg-sidebar lg:block">
          {sidebarHead}
          <Separator className="bg-sidebar-border" />
          <ScrollArea className="h-[calc(100vh-210px)] px-3 py-4">{nav}</ScrollArea>
        </aside>

        <main className="min-w-0 flex-1 px-4 py-8 pb-28 md:px-10 md:py-12 lg:pb-12">
          <header className="mb-10 max-w-3xl">
            <p className="text-sm font-medium text-amber-800">
              课题报告整理稿 · 2026年7月
            </p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-900 md:text-4xl">
              继电器接点寿命测试软件
            </h1>
            <p className="mt-2 text-lg text-slate-700">
              功能实现与关键技术分析
            </p>
            <p className="mt-4 text-[16px] leading-8 text-slate-600">
              对照源码整理。前十五节写基础版功能实现与关键技术，第十六节单独写
              4 拖 1 多工位方案，第十七节归纳难点与代码解答。各功能节末尾都有问—答。
            </p>
            <div className="mt-6 rounded-xl border border-amber-200 bg-amber-50/70 px-4 py-4">
              <p className="text-sm font-semibold text-amber-950">摘要</p>
              <p className="mt-2 text-[14.5px] leading-7 text-slate-700">
                该软件是一套电气接点寿命试验的自动测试与实时监测系统。系统以
                PLC/IO
                状态识别机械动作阶段，分时读取三路接触电阻和一路电流，完成实时显示、趋势分析、联锁停机、故障记录和历史追溯，形成“控制—采集—分析—报警—存储—追溯”闭环。后续
                4 拖 1 方案将控制与测量分到 COM4、COM5，由统一读取器轮询八台仪表。
              </p>
              <p className="mt-2 text-[13px] text-slate-600">
                关键词：继电器接点寿命；状态驱动采集；Modbus-RTU；安全联锁；4拖1
              </p>
            </div>
          </header>

          <div className="max-w-3xl space-y-16">
            <PartOne />
            <PartTwo />
            <PartThree />
            <PartFour />
            <PartFive />
            <PartSix />
          </div>

          <div className="mx-auto mt-10 flex max-w-3xl items-center justify-between gap-3 lg:hidden">
            {prev ? (
              <a
                href={`#${prev.id}`}
                className="flex min-w-0 flex-1 items-center gap-1 rounded-lg border border-border bg-white px-3 py-3 text-sm text-slate-700"
              >
                <ChevronLeft className="h-4 w-4 shrink-0" />
                <span className="truncate">{prev.label}</span>
              </a>
            ) : (
              <span />
            )}
            {next ? (
              <a
                href={`#${next.id}`}
                className="flex min-w-0 flex-1 items-center justify-end gap-1 rounded-lg border border-border bg-white px-3 py-3 text-sm text-slate-700"
              >
                <span className="truncate">{next.label}</span>
                <ChevronRight className="h-4 w-4 shrink-0" />
              </a>
            ) : (
              <span />
            )}
          </div>

          <footer className="mt-16 max-w-3xl border-t border-border pt-6 text-sm text-slate-500">
            前十五节分析范围：ZzhejiPanel.java、DianZu00000.java、JiaoL.java、ExecuteCommon.java。第十六、十七节另据{" "}
            <code>SixMeterInstrumentReader.java</code>、
            <code>ZzhejiPanel-20260811.java</code>
            。PLC 读写类 RRuANDWone 未包含在这批文件中，其行为根据主界面调用还原。
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
