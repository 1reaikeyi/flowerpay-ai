import { createPinia } from 'pinia'

const pinia = createPinia()

export { pinia }

// 导出所有 stores
export { useAdminStore } from './modules/admin.js'
