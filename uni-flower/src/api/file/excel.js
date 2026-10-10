import request from '@/utils/request.js'

export const writeExcel = () => {
  return request({ url: '/excel/write', method: 'post' })
}

export const readExcel = () => {
  return request({ url: '/excel/read', method: 'post' })
}
