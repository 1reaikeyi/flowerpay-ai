import request from '@/utils/request.js'

export const createFlowerDetail = (data) => {
  return request({ url: '/admin/flowerDetail', method: 'post', data })
}

export const getFlowerDetailById = (id) => {
  return request({ url: '/admin/flowerDetail', method: 'get', params: { id } })
}

export const updateFlowerDetail = (data) => {
  return request({ url: '/admin/flowerDetail', method: 'put', data })
}

export const deleteFlowerDetails = (ids) => {
  return request({ url: '/admin/flowerDetail', method: 'delete', params: { ids } })
}

export const getFlowerDetailsByFlowerId = (id) => {
  return request({ url: '/admin/flowerDetail/of/flower', method: 'get', params: { id } })
}
