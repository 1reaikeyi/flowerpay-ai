import request from '@/utils/request.js'

export const uploadFile = (filePath, formData = {}) => {
  return new Promise((resolve, reject) => {
    const token = (() => {
      try {
        const raw = uni.getStorageSync('flower:admin')
        return raw ? JSON.parse(raw).token : ''
      } catch (e) {
        return ''
      }
    })()

    uni.uploadFile({
      url: 'http://localhost:8080/local',
      filePath,
      name: 'file',
      formData,
      header: token ? { Authorization: `Bearer ${token}` } : {},
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            uni.showToast({ title: data.msg || '上传失败', icon: 'none' })
            reject(data)
          }
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}
