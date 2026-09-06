import type { ReactNode } from "react";

import { cn } from "@/lib/utils";

export function Figure({
  no,
  title,
  children,
}: {
  no: string;
  title: string;
  children: ReactNode;
}) {
  return (
    <figure className="my-5 break-inside-avoid rounded-xl border border-slate-200 bg-slate-50/90 px-3 py-4 md:px-5">
      <div className="overflow-x-auto">{children}</div>
      <figcaption className="mt-3 text-center text-[13.5px] font-semibold tracking-wide text-slate-800">
        图{no}　{title}
      </figcaption>
    </figure>
  );
}

export function VChart({ children }: { children: ReactNode }) {
  return (
    <div className="mx-auto flex min-w-[17rem] flex-col items-center py-1">
      {children}
    </div>
  );
}

export function ChartNode({
  children,
  kind = "process",
  className,
}: {
  children: ReactNode;
  kind?: "start" | "process" | "io" | "end";
  className?: string;
}) {
  const kindClass =
    kind === "start"
      ? "rounded-full border-2 border-emerald-800 bg-emerald-50"
      : kind === "io"
        ? "border-2 border-sky-800 bg-sky-50 [clip-path:polygon(0.7rem_0,100%_0,calc(100%-0.7rem)_100%,0_100%)]"
        : kind === "end"
          ? "rounded-full border-2 border-rose-800 bg-rose-50"
          : "rounded-md border-2 border-slate-700 bg-white";
  return (
    <div
      className={cn(
        "w-[15.5rem] max-w-[80vw] px-3 py-2 text-center text-[13px] font-medium leading-5 text-slate-800",
        kindClass,
        className
      )}
    >
      {children}
    </div>
  );
}

export function Decision({ children }: { children: ReactNode }) {
  return (
    <div className="relative my-1 flex h-[6.2rem] w-[6.2rem] items-center justify-center">
      <div className="absolute h-[4.6rem] w-[4.6rem] rotate-45 border-2 border-amber-800 bg-amber-50" />
      <span className="relative z-10 w-[5.4rem] text-center text-[12px] font-medium leading-4 text-slate-800">
        {children}
      </span>
    </div>
  );
}

export function ArrowDown({ label }: { label?: string }) {
  return (
    <div className="flex flex-col items-center py-0.5 text-slate-600">
      {label ? (
        <span className="mb-0.5 text-[11px] text-slate-500">{label}</span>
      ) : null}
      <div className="h-5 w-px bg-slate-500" />
      <div className="h-0 w-0 border-x-[5px] border-t-[7px] border-x-transparent border-t-slate-500" />
    </div>
  );
}

export function Split({
  left,
  right,
  leftLabel = "是",
  rightLabel = "否",
}: {
  left: ReactNode;
  right: ReactNode;
  leftLabel?: string;
  rightLabel?: string;
}) {
  return (
    <div className="grid w-full max-w-xl grid-cols-2 gap-3">
      <div className="flex flex-col items-center">
        <span className="mb-1 text-[11px] font-semibold text-emerald-700">
          {leftLabel}
        </span>
        {left}
      </div>
      <div className="flex flex-col items-center">
        <span className="mb-1 text-[11px] font-semibold text-rose-700">
          {rightLabel}
        </span>
        {right}
      </div>
    </div>
  );
}

export function LayerStack({
  layers,
}: {
  layers: { title: string; detail: string; tone?: "ui" | "comm" | "data" | "dev" }[];
}) {
  const toneClass = {
    ui: "border-violet-700 bg-violet-50",
    comm: "border-blue-700 bg-blue-50",
    data: "border-emerald-700 bg-emerald-50",
    dev: "border-slate-700 bg-white",
  };
  return (
    <div className="mx-auto flex w-full max-w-xl flex-col items-center">
      {layers.map((layer, index) => (
        <div key={layer.title} className="flex w-full flex-col items-center">
          <div
            className={cn(
              "w-full rounded-md border-2 px-3 py-2.5 text-center",
              toneClass[layer.tone ?? "dev"]
            )}
          >
            <p className="text-sm font-semibold text-slate-900">{layer.title}</p>
            <p className="mt-0.5 text-[12.5px] leading-6 text-slate-600">
              {layer.detail}
            </p>
          </div>
          {index < layers.length - 1 ? <ArrowDown /> : null}
        </div>
      ))}
    </div>
  );
}

export function SeqTable({
  rows,
}: {
  rows: { step: string; who: string; frame?: string; purpose: string }[];
}) {
  return (
    <div className="overflow-x-auto rounded-lg border border-slate-300 bg-white">
      <table className="w-full min-w-[36rem] text-left text-sm">
        <thead className="bg-slate-100 text-slate-600">
          <tr>
            <th className="px-3 py-2">顺序</th>
            <th className="px-3 py-2">占用总线</th>
            <th className="px-3 py-2">请求帧</th>
            <th className="px-3 py-2">目的</th>
          </tr>
        </thead>
        <tbody className="text-slate-700">
          {rows.map((row) => (
            <tr key={row.step} className="border-t border-slate-200 align-top">
              <td className="whitespace-nowrap px-3 py-2 font-medium">
                {row.step}
              </td>
              <td className="px-3 py-2">{row.who}</td>
              <td className="px-3 py-2 font-mono text-[12px]">
                {row.frame ?? "—"}
              </td>
              <td className="px-3 py-2 leading-6">{row.purpose}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
