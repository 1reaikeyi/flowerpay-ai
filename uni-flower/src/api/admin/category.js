import request from '@/utils/request.js'

export const createCategory = (data) => {
  return request({ url: '/admin/category', method: 'post', data })
}

export const getCategoryByType = (type) => {
  return request({ url: '/admin/category', method: 'get', params: { type } })
}

export const pageCategoryList = (params) => {
  return request({ url: '/admin/category/all', method: 'get', params })
}

export const updateCategory = (data) => {
  return request({ url: '/admin/category', method: 'put', data })
}

export const deleteCategories = (ids) => {
  return request({ url: '/admin/category', method: 'delete', params: { ids } })
}

export const getFlowersByCategoryId = (id) => {
  return request({ url: '/admin/category/of/flower', method: 'get', params: { id } })
}

export const getFestivalsByCategoryId = (id) => {
  return request({ url: '/admin/category/of/festival', method: 'get', params: { id } })
}
