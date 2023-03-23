/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  output: 'standalone',
  images: {
    remotePatterns: [
      {
        hostname: 'api.yas.local',
      },
    ],
  },
};

module.exports = nextConfig;
