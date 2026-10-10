import request from '@/utils/request.js'

export const loginAdmin = (data) => {
  return request({
    url: '/admin/login',
    method: 'post',
    data,
    skipAuth: true
  })
}

export const logoutAdmin = () => {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

export const getEmployeeById = (id) => {
  return request({
    url: '/admin/employee',
    method: 'get',
    params: { id }
  })
}

export const pageEmployeeList = (params) => {
  return request({
    url: '/admin/all',
    method: 'get',
    params
  })
}

export const updateEmployeePassword = (data) => {
  return request({
    url: '/admin/password',
    method: 'put',
    data
  })
}

export const updateEmployeeInfo = (data) => {
  return request({
    url: '/admin/employee',
    method: 'put',
    data
  })
}

export const deleteEmployees = (ids) => {
  return request({
    url: '/admin',
    method: 'delete',
    params: { ids }
  })
}
