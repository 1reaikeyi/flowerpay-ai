import request from '@/utils/request.js'

export const getFlower = () => {
  return request({ url: '/admin/statistics/flower', method: 'get' })
}

export const getFestival = () => {
  return request({ url: '/admin/statistics/festival', method: 'get' })
}

export const getTop1 = () => {
  return request({ url: '/admin/statistics/top1', method: 'get' })
}

export const getTop2 = () => {
  return request({ url: '/admin/statistics/top2', method: 'get' })
}

export const getOrder = () => {
  return request({ url: '/admin/statistics/order', method: 'get' })
}

export const getTodayOrder = () => {
  return request({ url: '/admin/statistics/today', method: 'get' })
}
