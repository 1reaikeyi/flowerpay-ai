import request from '@/utils/request.js'

export const getShopStatus = () => {
  return request({ url: '/admin/shop/status', method: 'get' })
}

export const setShopStatus = (status) => {
  return request({ url: `/admin/shop/status/${status}`, method: 'put' })
}
