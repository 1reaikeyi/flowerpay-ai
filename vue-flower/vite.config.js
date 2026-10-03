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
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/api/ai': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/ai/, '')
      },
    
      '/image': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
