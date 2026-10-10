import request from '@/utils/request.js'

export const createFestivalDetail = (data) => {
  return request({ url: '/admin/festivalDetail', method: 'post', data })
}

export const getFestivalDetailById = (id) => {
  return request({ url: '/admin/festivalDetail', method: 'get', params: { id } })
}

export const updateFestivalDetail = (data) => {
  return request({ url: '/admin/festivalDetail', method: 'put', data })
}

export const deleteFestivalDetails = (ids) => {
  return request({ url: '/admin/festivalDetail', method: 'delete', params: { ids } })
}

export const getFestivalDetailsByFestivalId = (id) => {
  return request({ url: '/admin/festivalDetail/of/festival', method: 'get', params: { id } })
}
