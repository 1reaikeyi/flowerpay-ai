import request from '@/utils/request.js'

export const createFestival = (data) => {
  return request({ url: '/admin/festival', method: 'post', data })
}

export const getFestivalById = (id) => {
  return request({ url: '/admin/festival', method: 'get', params: { id } })
}

export const pageFestivalList = (params) => {
  return request({ url: '/admin/festival/all', method: 'get', params })
}

export const updateFestival = (data) => {
  return request({ url: '/admin/festival', method: 'put', data })
}

export const deleteFestivals = (ids) => {
  return request({ url: '/admin/festival', method: 'delete', params: { ids } })
}

export const getFestivalDetailsByFestivalId = (id) => {
  return request({ url: '/admin/festival/of/festivalDetail', method: 'get', params: { id } })
}

export const getFestivalsByFlowerId = (id) => {
  return request({ url: '/admin/festival/of/flower', method: 'get', params: { id } })
}

export const getFestivalDetailsByObject = (object) => {
  return request({ url: '/admin/festival/of/object', method: 'get', params: { object } })
}

export const getFestivalDetailsByOption = (option) => {
  return request({ url: '/admin/festival/of/option', method: 'get', params: { option } })
}
