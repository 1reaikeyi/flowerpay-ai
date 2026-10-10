import request from '@/utils/request.js'

export const getOrderById = (id) => {
  return request({ url: '/admin/flowerOrder', method: 'get', params: { id } })
}

export const pageOrderList = (params) => {
  return request({ url: '/admin/flowerOrder/all', method: 'get', params })
}

export const updateOrderToCooking = (id) => {
  return request({ url: `/admin/flowerOrder/cooking/${id}`, method: 'put' })
}

export const updateOrderToGo = (id) => {
  return request({ url: `/admin/flowerOrder/go/${id}`, method: 'put' })
}

export const updateOrderToDelivering = (id) => {
  return request({ url: `/admin/flowerOrder/delivering/${id}`, method: 'put' })
}

export const updateOrderToArrived = (id) => {
  return request({ url: `/admin/flowerOrder/arrived/${id}`, method: 'put' })
}

export const updateOrderToComplete = (id) => {
  return request({ url: `/admin/flowerOrder/complete/${id}`, method: 'put' })
}

export const cancelOrder = (id) => {
  return request({ url: `/admin/flowerOrder/canceled/${id}`, method: 'put' })
}
