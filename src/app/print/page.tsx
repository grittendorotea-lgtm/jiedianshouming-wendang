import type { Metadata } from "next";
import Link from "next/link";

import { PartFive } from "@/components/report/sections/PartFive";
import { PartFour } from "@/components/report/sections/PartFour";
import { PartOne } from "@/components/report/sections/PartOne";
import { PartThree } from "@/components/report/sections/PartThree";
import { PartTwo } from "@/components/report/sections/PartTwo";

export const metadata: Metadata = {
  title: "接点接触电阻监测系统 · 打印稿",
};

export default function PrintPage() {
  return (
    <article className="mx-auto max-w-3xl bg-white px-6 py-10 text-slate-800 print:max-w-none print:px-0 print:py-0">
      <p className="mb-4 text-sm print:hidden">
        <Link href="/" className="text-primary underline-offset-4 hover:underline">
          返回阅读版
        </Link>
        <span className="text-slate-400"> · 用浏览器打印可导出 PDF</span>
      </p>
      <p className="text-sm text-amber-800">课题报告讲义 · 打印稿</p>
      <h1 className="mt-2 text-3xl font-semibold tracking-tight">
        接点接触电阻在线监测系统功能实现分析
      </h1>
      <p className="mt-4 leading-8 text-slate-600">
        前十五节对照基础版源码；第十六节单独写 4 拖 1
        方案。正文含操作流程、通信时序和流程图。浏览器打印即可导出 PDF。
      </p>
      <div className="mt-10 space-y-14">
        <PartOne />
        <PartTwo />
        <PartThree />
        <PartFour />
        <PartFive />
      </div>
    </article>
  );
}
