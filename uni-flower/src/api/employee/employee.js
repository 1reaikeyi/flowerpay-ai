import request from '@/utils/request.js'

export const registerEmployee = (data) => {
  return request({
    url: '/employee/register',
    method: 'post',
    data,
    skipAuth: true
  })
}

export const updateEmployee = (data) => {
  return request({
    url: '/employee',
    method: 'put',
    data,
    scope: 'emp'
  })
}
