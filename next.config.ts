import type { NextConfig } from "next";

const basePath = process.env.NEXT_PUBLIC_BASE_PATH || "";

const nextConfig: NextConfig = {
  output: "export",
  images: { unoptimized: true },
  trailingSlash: true,
  basePath: basePath || undefined,
  assetPrefix: basePath || undefined,
  reactStrictMode: true,
  allowedDevOrigins: ["127.0.0.1", "localhost", "*.trycloudflare.com"],
};

export default nextConfig;
