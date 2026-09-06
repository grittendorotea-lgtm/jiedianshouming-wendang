import type { Metadata } from "next";
import { Noto_Sans_SC } from "next/font/google";

import "./globals.css";

const notoSans = Noto_Sans_SC({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  display: "swap",
});

export const metadata: Metadata = {
  title: "接点接触电阻监测系统 · 功能实现分析",
  description:
    "接点接触电阻在线监测系统功能实现分析。前十五节为基础实现，第十六节为 4 拖 1 方案。",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh-CN">
      <body className={`${notoSans.className} antialiased`}>{children}</body>
    </html>
  );
}
