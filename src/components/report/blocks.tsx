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

export function ExtBox({
  title = "四工位八表拓展",
  tech,
  children,
}: {
  title?: string;
  tech: string;
  children: ReactNode;
}) {
  return (
    <div className="rounded-lg border border-amber-300/80 bg-amber-50/90 px-4 py-3">
      <p className="mb-1 text-sm font-semibold text-amber-950">{title}</p>
      <div className="text-sm leading-7 text-slate-700">{children}</div>
      <p className="mt-2 text-xs leading-6 text-slate-600">
        <span className="font-semibold text-slate-800">关键技术：</span>
        {tech}
      </p>
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
