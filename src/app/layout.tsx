import type { Metadata } from "next";
import { Noto_Sans_SC } from "next/font/google";

import "./globals.css";

const notoSans = Noto_Sans_SC({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  display: "swap",
});

export const metadata: Metadata = {
  title: "继电器接点寿命测试软件 · 功能实现与关键技术分析",
  description:
    "继电器接点寿命测试软件课题报告。前十五节为基础实现与关键技术，第十六节为 4 拖 1 方案。",
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
