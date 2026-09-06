import type { Metadata } from "next";
import Link from "next/link";

import { PartFive } from "@/components/report/sections/PartFive";
import { PartFour } from "@/components/report/sections/PartFour";
import { PartOne } from "@/components/report/sections/PartOne";
import { PartSeven } from "@/components/report/sections/PartSeven";
import { PartSix } from "@/components/report/sections/PartSix";
import { PartThree } from "@/components/report/sections/PartThree";
import { PartTwo } from "@/components/report/sections/PartTwo";

export const metadata: Metadata = {
  title: "继电器接点寿命测试软件 · 打印稿",
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
      <p className="text-sm text-amber-800">课题报告整理稿 · 2026年7月</p>
      <h1 className="mt-2 text-3xl font-semibold tracking-tight">
        继电器接点寿命测试软件
      </h1>
      <p className="mt-1 text-lg text-slate-700">功能实现与关键技术分析</p>
      <p className="mt-4 leading-8 text-slate-600">
        前十五节写基础版功能与关键技术，第十六节单独写 4 拖 1
        方案，第十七节归纳重难点，第十八节给出模块框图与关键算法。浏览器打印即可导出
        PDF。
      </p>
      <div className="mt-10 space-y-14">
        <PartOne />
        <PartTwo />
        <PartThree />
        <PartFour />
        <PartFive />
        <PartSix />
        <PartSeven />
      </div>
    </article>
  );
}
