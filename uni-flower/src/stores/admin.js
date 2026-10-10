import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginAdmin, logoutAdmin, getEmployeeById } from '@/api/admin/admin.js'
import { registerEmployee } from '@/api/employee/employee.js'
import { getUserIdFromToken } from '@/utils/jwt.js'

export const useAdminStore = defineStore('flower:admin', () => {
  const token = ref('')
  const user = ref({})

  // 从 uni storage 恢复
  const restore = () => {
    try {
      const raw = uni.getStorageSync('flower:admin')
      if (raw) {
        const parsed = JSON.parse(raw)
        token.value = parsed.token || ''
        user.value = parsed.user || {}
      }
    } catch (e) {
      console.error('恢复登录态失败:', e)
    }
  }

  // 持久化到 uni storage
  const persist = () => {
    uni.setStorageSync('flower:admin', JSON.stringify({
      token: token.value,
      user: user.value
    }))
  }

  const setToken = (newToken) => {
    token.value = newToken
    persist()
  }

  const removeToken = () => {
    token.value = ''
    user.value = {}
    uni.removeStorageSync('flower:admin')
  }

  const userId = computed(() => getUserIdFromToken(token.value))

  const setUser = (obj) => {
    user.value = obj
    persist()
  }

  const login = async (data) => {
    const res = await loginAdmin(data)
    return res
  }

  const register = async (data) => {
    const res = await registerEmployee(data)
    return res
  }

  const getUser = async () => {
    if (!userId.value) {
      console.error('无法获取用户ID，token可能无效')
      return
    }
    try {
      const res = await getEmployeeById(userId.value)
      setUser(res.data)
    } catch (e) {
      removeToken()
      uni.reLaunch({ url: '/pages/login/admin' })
    }
  }

  const logout = async () => {
    try {
      await logoutAdmin()
    } catch (e) {
      // 后端登出失败也清本地状态
    }
    removeToken()
  }

  return {
    token, setToken, removeToken,
    user, getUser, setUser, userId,
    login, register, logout,
    restore
  }
})
