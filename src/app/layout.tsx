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
    "对照 Java 源码讲解接点接触电阻在线监测软件实现了哪些功能、每个功能如何实现。",
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
