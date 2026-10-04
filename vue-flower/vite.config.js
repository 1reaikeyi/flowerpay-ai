import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  // 全局注入 SCSS 变量（使用 @use 替代已弃用的 @import）
  // _theme.scss 集中了系统色板 + 主题色 + iOS 设计令牌，全局共享
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/styles/_theme" as *;`,
        quietDeps: true
      }
    }
  },
  server: {
    proxy: {
      // ⚠️ 顺序敏感：更具体的 /api/ai 必须放在 /api 之前，
      // 否则 /api/ai/chat 会被 /api 拦截错发到 8080，导致 AI 无响应
      '/api/ai': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/ai/, '')
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },

      '/image': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
