/**
 * uni-app 请求封装 - 替代 axios
 * 后端统一返回 Result { code, msg, data }：成功 code=200，失败 code=500
 */

// 后端基础地址（小程序不能用 vite 代理，需要直连后端地址）
// 开发环境：localhost 需在微信开发者工具中勾选「不校验合法域名」
// 生产环境：需配置为 https 域名
const BASE_URL = 'http://localhost:8080'
const AI_BASE_URL = 'http://localhost:8081'

// 各端存储 key
const STORAGE_KEYS = {
  admin: 'flower:admin',
  user: 'flower:user',
  emp: 'flower:emp'
}

// 获取 token（从对应端的 storage 中读取）
const getToken = (scope) => {
  const key = STORAGE_KEYS[scope] || STORAGE_KEYS.admin
  try {
    const raw = uni.getStorageSync(key)
    return raw ? JSON.parse(raw).token : ''
  } catch (e) {
    return ''
  }
}

// 清理某端登录态
const clearAuth = (scope) => {
  const key = STORAGE_KEYS[scope] || STORAGE_KEYS.admin
  uni.removeStorageSync(key)
}

/**
 * 通用请求
 * @param {Object} options
 * @param {string} options.url - 接口路径（不含 baseURL）
 * @param {string} options.method - GET/POST/PUT/DELETE
 * @param {Object} options.data - 请求体（POST/PUT）
 * @param {Object} options.params - 查询参数（GET/DELETE）
 * @param {string} options.scope - 认证范围：admin/user/emp，默认 admin
 * @param {string} options.baseURL - 覆盖默认 baseURL（如 AI 服务用 8081）
 * @param {boolean} options.skipAuth - 是否跳过 token 注入（登录接口用）
 * @param {string} options.responseType - 响应类型，如 'arraybuffer' 用于下载
 */
const request = (options) => {
  const {
    url,
    method = 'GET',
    data = {},
    params = {},
    scope = 'admin',
    baseURL = BASE_URL,
    skipAuth = false,
    responseType = 'text',
    header = {}
  } = options

  // 拼接查询参数
  let fullUrl = baseURL + url
  const queryStr = Object.keys(params)
    .filter((k) => params[k] !== undefined && params[k] !== null && params[k] !== '')
    .map((k) => `${encodeURIComponent(k)}=${encodeURIComponent(params[k])}`)
    .join('&')
  if (queryStr) {
    fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryStr
  }

  // 组装 header
  const headers = {
    'Content-Type': 'application/json',
    ...header
  }

  // 注入 token
  if (!skipAuth) {
    const token = getToken(scope)
    if (token) {
      headers.Authorization = `Bearer ${token}`
    }
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method,
      data,
      header: headers,
      responseType,
      success: (res) => {
        // HTTP 层成功（200~299）
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const body = res.data
          // 后端统一 Result 结构
          if (body && typeof body === 'object' && 'code' in body) {
            if (body.code === 200) {
              resolve(body)
            } else {
              uni.showToast({
                title: body.msg || '操作失败',
                icon: 'none',
                duration: 2000
              })
              reject(body)
            }
          } else {
            // 非 Result 结构（如 AI 接口返回数组），原样返回
            resolve(body)
          }
        } else if (res.statusCode === 401) {
          // 未登录：清理登录态，跳转对应登录页
          clearAuth(scope)
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/admin' })
          }, 1500)
          reject(res)
        } else {
          const errMsg = res.data?.msg || '服务异常'
          uni.showToast({ title: errMsg, icon: 'none', duration: 2000 })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败，请检查网络', icon: 'none', duration: 2000 })
        reject(err)
      }
    })
  })
}

export default request
export { BASE_URL, AI_BASE_URL, STORAGE_KEYS, getToken, clearAuth }
