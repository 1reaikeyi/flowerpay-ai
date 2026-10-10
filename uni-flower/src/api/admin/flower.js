import request from '@/utils/request.js'

export const createFlower = (data) => {
  return request({ url: '/admin/flower', method: 'post', data })
}

export const getFlowerById = (id) => {
  return request({ url: '/admin/flower', method: 'get', params: { id } })
}

export const pageFlowerList = (params) => {
  return request({ url: '/admin/flower/all', method: 'get', params })
}

export const updateFlower = (data) => {
  return request({ url: '/admin/flower', method: 'put', data })
}

export const deleteFlowers = (ids) => {
  return request({ url: '/admin/flower', method: 'delete', params: { ids } })
}

export const getFlowerDetailsByFlowerId = (id) => {
  return request({ url: '/admin/flower/of/flowerDetail', method: 'get', params: { id } })
}

export const getFlowerDetailsByObject = (object) => {
  return request({ url: '/admin/flower/of/object', method: 'get', params: { object } })
}

export const getFlowerDetailsByOption = (option) => {
  return request({ url: '/admin/flower/of/option', method: 'get', params: { option } })
}
