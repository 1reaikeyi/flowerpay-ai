/**
 * 通用工具函数
 */

import dayjs from 'dayjs'

// 格式化时间：2024年01月01日
export const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY年MM月DD日')
}

// 格式化时间：2024-01-01 12:00:00
export const formatDateTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 统一图片 URL 解析策略
 * 数据库 image 字段可能有多种形态：
 * 1. "https://xxx.com/xxx" → 外链直接返回
 * 2. "/image/1.png" → 拼接后端地址
 * 3. "uuid-xxx.png" → 走本地文件下载接口
 */
export const resolveImageUrl = (image) => {
  if (!image) return ''
  if (/^https?:\/\//i.test(image)) return image
  if (image.startsWith('/image/')) return 'http://localhost:8080' + image
  if (image.startsWith('/img/')) return 'http://localhost:8080/image/' + image.slice(5)
  if (image.startsWith('/')) return 'http://localhost:8080' + image
  // 其余视为上传文件名，走本地文件下载接口
  return `http://localhost:8080/local?fileName=${encodeURIComponent(image)}`
}

// 价格格式化：保留两位小数 + " 元"
export const formatPrice = (price) => {
  if (price == null) return '-'
  return Number(price).toFixed(2) + ' 元'
}

// 显示轻提示
export const showToast = (title, icon = 'none') => {
  uni.showToast({ title, icon, duration: 2000 })
}

// 显示加载
export const showLoading = (title = '加载中...') => {
  uni.showLoading({ title, mask: true })
}

// 隐藏加载
export const hideLoading = () => {
  uni.hideLoading()
}

// 确认弹窗
export const showConfirm = (content, title = '提示') => {
  return new Promise((resolve) => {
    uni.showModal({
      title,
      content,
      success: (res) => {
        resolve(res.confirm)
      }
    })
  })
}
