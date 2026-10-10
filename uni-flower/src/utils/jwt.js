/**
 * JWT 工具函数
 * 小程序环境没有 atob，需自己实现 Base64 解码
 */

// Base64 解码（兼容小程序）
const base64Decode = (str) => {
  // 替换 URL-safe 字符
  const base64 = str.replace(/-/g, '+').replace(/_/g, '/')
  const padding = '='.repeat((4 - (base64.length % 4)) % 4)
  const padded = base64 + padding

  // 小程序可用的 Base64 解码
  try {
    // #ifdef H5
    return decodeURIComponent(
      atob(padded)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    // #endif
    // #ifndef H5
    const buffer = uni.base64ToArrayBuffer(padded)
    const bytes = new Uint8Array(buffer)
    let result = ''
    for (let i = 0; i < bytes.length; i++) {
      result += String.fromCharCode(bytes[i])
    }
    return decodeURIComponent(escape(result))
    // #endif
  } catch (e) {
    console.error('Base64 解码失败:', e)
    return ''
  }
}

export const cleanToken = (token) => {
  if (!token) return ''
  return token
}

export const parseJWT = (token) => {
  if (!token) return null
  try {
    const cleanedToken = cleanToken(token)
    const base64Payload = cleanedToken.split('.')[1]
    const decoded = base64Decode(base64Payload)
    return JSON.parse(decoded)
  } catch (e) {
    console.error('JWT 解析失败:', e)
    return null
  }
}

export const getUserIdFromToken = (token) => {
  const payload = parseJWT(token)
  return payload?.adminId ?? payload?.empId ?? payload?.userId ?? null
}

export const getUserNameFromToken = (token) => {
  const payload = parseJWT(token)
  return payload?.adminName ?? payload?.empName ?? payload?.userName ?? null
}
