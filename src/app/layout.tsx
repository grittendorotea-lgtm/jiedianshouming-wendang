import type { Metadata } from "next";

import "./globals.css";

export const metadata: Metadata = {
  title: "继电器接点寿命测试软件 · 功能实现与关键技术分析",
  description:
    "继电器接点寿命测试软件课题报告。网页、Word、插图和源码都放在 GitHub，用 GitHub Pages 打开。",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh-CN">
      <body className="antialiased">{children}</body>
    </html>
  );
}
