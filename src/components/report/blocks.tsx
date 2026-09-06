import type { ReactNode } from "react";

import { cn } from "@/lib/utils";

export function Section({
  id,
  title,
  kicker,
  children,
}: {
  id: string;
  title: string;
  kicker?: string;
  children: ReactNode;
}) {
  return (
    <section id={id} className="prose-report scroll-mt-24">
      <div className="mb-6">
        {kicker ? (
          <p className="mb-2 text-xs font-semibold tracking-[0.18em] text-amber-800 uppercase">
            {kicker}
          </p>
        ) : null}
        <h2 className="text-2xl font-semibold tracking-tight text-slate-900 md:text-3xl">
          {title}
        </h2>
      </div>
      <div className="space-y-4 text-[15.5px] leading-8 text-slate-700">
        {children}
      </div>
    </section>
  );
}

export function Sub({
  id,
  title,
  children,
}: {
  id?: string;
  title: string;
  children: ReactNode;
}) {
  return (
    <div id={id} className="pt-2">
      <h3 className="mb-3 text-lg font-semibold text-slate-900">{title}</h3>
      <div className="space-y-4 text-[15.5px] leading-8 text-slate-700">
        {children}
      </div>
    </div>
  );
}

export function CodeBlock({
  title,
  children,
}: {
  title?: string;
  children: string;
}) {
  return (
    <div className="overflow-hidden rounded-lg border border-slate-800/20 bg-slate-950 shadow-sm">
      {title ? (
        <div className="border-b border-white/10 px-4 py-2 text-xs text-slate-300">
          {title}
        </div>
      ) : null}
      <pre className="overflow-x-auto p-4 text-[13px] leading-6 text-slate-100">
        <code>{children}</code>
      </pre>
    </div>
  );
}

export function QA({ q, a }: { q: string; a: ReactNode }) {
  return (
    <article className="overflow-hidden rounded-lg border border-amber-200/80 bg-white">
      <p className="bg-amber-50 px-4 py-2.5 text-[14.5px] font-semibold leading-7 text-slate-900">
        <span className="mr-2 text-amber-800">问</span>
        {q}
      </p>
      <div className="space-y-2.5 px-4 py-3 text-[14.5px] leading-7 text-slate-700">
        {a}
      </div>
    </article>
  );
}

export function QABody({
  contradiction,
  implementation,
  bound,
}: {
  contradiction: ReactNode;
  implementation: ReactNode;
  bound?: ReactNode;
}) {
  return (
    <>
      <p>
        <span className="mr-1.5 font-semibold text-emerald-800">矛盾</span>
        {contradiction}
      </p>
      <p>
        <span className="mr-1.5 font-semibold text-emerald-800">实现</span>
        {implementation}
      </p>
      {bound ? (
        <p>
          <span className="mr-1.5 font-semibold text-amber-800">边界</span>
          {bound}
        </p>
      ) : null}
    </>
  );
}

export function QAList({
  title = "重难点分析",
  items,
}: {
  title?: string;
  items: { q: string; a: ReactNode }[];
}) {
  return (
    <div className="space-y-3">
      <h3 className="text-lg font-semibold text-slate-900">{title}</h3>
      {items.map((item) => (
        <QA key={item.q} q={item.q} a={item.a} />
      ))}
    </div>
  );
}

export function Callout({
  title,
  children,
  tone = "note",
}: {
  title: string;
  children: ReactNode;
  tone?: "note" | "warn" | "idea";
}) {
  const toneClass =
    tone === "warn"
      ? "border-red-200 bg-red-50"
      : tone === "idea"
        ? "border-amber-200 bg-amber-50"
        : "border-sky-200 bg-sky-50";
  return (
    <div className={cn("rounded-lg border px-4 py-3", toneClass)}>
      <p className="mb-1 text-sm font-semibold text-slate-900">{title}</p>
      <div className="text-sm leading-7 text-slate-700">{children}</div>
    </div>
  );
}

export function Flow({ steps }: { steps: string[] }) {
  return (
    <ol className="grid gap-2">
      {steps.map((step, index) => (
        <li
          key={step}
          className="flex items-start gap-3 rounded-lg border border-border bg-white px-3 py-2.5"
        >
          <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary text-xs font-semibold text-primary-foreground">
            {index + 1}
          </span>
          <span className="text-sm leading-7 text-slate-700">{step}</span>
        </li>
      ))}
    </ol>
  );
}

export function PaperImg({
  src,
  no,
  title,
}: {
  src: string;
  no: string;
  title: string;
}) {
  return (
    <figure className="my-5 break-inside-avoid rounded-xl border border-slate-200 bg-white px-2 py-3 md:px-4">
      {/* eslint-disable-next-line @next/next/no-img-element */}
      <img
        src={src}
        alt={`图${no} ${title}`}
        className="mx-auto max-h-[44rem] w-full object-contain"
      />
      <figcaption className="mt-3 text-center text-[13.5px] font-semibold tracking-wide text-slate-800">
        图{no}　{title}
      </figcaption>
    </figure>
  );
}

export function Formula({
  no,
  children,
}: {
  no: string;
  children: ReactNode;
}) {
  return (
    <p className="my-3 text-center font-serif text-[15.5px] leading-8 text-slate-800">
      {children}
      <span className="ml-6 text-sm text-slate-500">({no})</span>
    </p>
  );
}

export function Algo({
  no,
  title,
  inputs,
  outputs,
  source,
  steps,
  complexity,
  correctness,
}: {
  no: string;
  title: string;
  inputs: string;
  outputs: string;
  source: string;
  steps: string[];
  complexity: string;
  correctness: string;
}) {
  return (
    <article className="overflow-hidden rounded-lg border border-slate-200 bg-white">
      <header className="border-b border-slate-200 bg-slate-900 px-4 py-2.5 text-[14.5px] font-semibold text-white">
        算法 {no}　{title}
      </header>
      <div className="grid gap-px bg-slate-200 sm:grid-cols-2">
        <p className="bg-white px-4 py-2.5 text-[13.5px] leading-7 text-slate-700">
          <span className="font-semibold text-slate-900">输入　</span>
          {inputs}
        </p>
        <p className="bg-white px-4 py-2.5 text-[13.5px] leading-7 text-slate-700">
          <span className="font-semibold text-slate-900">输出　</span>
          {outputs}
        </p>
        <p className="bg-slate-50 px-4 py-2.5 text-[13px] leading-6 text-slate-600 sm:col-span-2">
          <span className="font-semibold text-slate-800">源码　</span>
          {source}
        </p>
      </div>
      <ol className="list-decimal space-y-1.5 px-8 py-3 text-[14.5px] leading-7 text-slate-700">
        {steps.map((step) => (
          <li key={step}>{step}</li>
        ))}
      </ol>
      <div className="space-y-1.5 border-t border-slate-100 px-4 py-3 text-[13.5px] leading-7 text-slate-700">
        <p>
          <span className="font-semibold text-slate-900">复杂度　</span>
          {complexity}
        </p>
        <p>
          <span className="font-semibold text-slate-900">正确性条件　</span>
          {correctness}
        </p>
      </div>
    </article>
  );
}

export function KvTable({
  rows,
}: {
  rows: { k: string; v: string }[];
}) {
  return (
    <div className="overflow-x-auto rounded-lg border border-border bg-white">
      <table className="w-full text-left text-sm">
        <thead className="bg-slate-50 text-slate-600">
          <tr>
            <th className="px-3 py-2 font-medium">项目</th>
            <th className="px-3 py-2 font-medium">实现说明</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.k} className="border-t border-border align-top">
              <td className="whitespace-nowrap px-3 py-2 font-medium text-slate-800">
                {row.k}
              </td>
              <td className="px-3 py-2 leading-7 text-slate-700">{row.v}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
