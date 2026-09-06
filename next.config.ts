import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  allowedDevOrigins: ["127.0.0.1", "localhost", "*.trycloudflare.com"],
  async redirects() {
    return [{ source: "/md", destination: "/md.html", permanent: false }];
  },
};

export default nextConfig;
