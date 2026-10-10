import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import { fileURLToPath, URL } from 'node:url'

const __dirname = fileURLToPath(new URL('.', import.meta.url))
const themePath = fileURLToPath(new URL('./src/styles/theme.scss', import.meta.url)).replace(/\\/g, '/')

export default defineConfig({
  plugins: [
    uni()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "${themePath}" as *;`,
        quietDeps: true
      }
    }
  }
})
