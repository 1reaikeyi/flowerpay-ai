import request from '@/utils/admin/request.js'

/**
 * 管理员账户 API（按 admin 接口文档第 1 节对齐）
 */

// 1.1 管理员登录 - POST /admin/login（放行）
// 请求体 LoginDTO: { username, password }
// 响应 Result<String> → JWT Token 字符串
export const loginAdmin = (data) => {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 1.2 管理员登出 - POST /admin/logout
// 清除 Redis 中的 token，响应 Result<String> → "logout"
export const logoutAdmin = () => {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

// 1.3 根据 ID 查询员工 - GET /admin
// 请求参数: id (Long, 必填) 员工主键 ID
// 响应 Result<EmployeeVO>: { id, username, avatar, work, sex, email, phone, status }
export const getEmployeeById = (id) => {
  return request({
    url: '/admin/employee',
    method: 'get',
    params: { id }
  })
}

// 1.4 分页查询员工列表 - GET /admin/all
// 请求参数 EmployeePageDTO (Query): { page, pageSize, employeename }
// page 默认 1，pageSize 默认 10，支持按用户名模糊搜索
// 响应 Result<PageResult<EmployeeVO>>
export const pageEmployeeList = (params) => {
  return request({
    url: '/admin/all',
    method: 'get',
    params
  })
}

// 1.5 修改密码 - PUT /admin/password
// 请求体 PasswordDTO: { newPassword(必填), confirmPassword(必填) }
// 响应 Result<String>
export const updateEmployeePassword = (data) => {
  return request({
    url: '/admin/password',
    method: 'put',
    data
  })
}

// 1.6 修改员工信息 - PUT /admin（AdminController.updateByObject，需 ROLE_ADMIN）
// 后端 updateByObject 只 set 非空字段，支持局部更新（如仅改 avatar / status）
// 请求体 EmployeeDTO: { id(必填), username?, work?, avatar?, sex?, email?, phone?, status?, password? }
// 响应 Result<Long> → 员工 ID
export const updateEmployeeInfo = (data) => {
  return request({
    url: '/admin',
    method: 'put',
    data
  })
}

// 员工信息修改（别名，语义同 updateEmployeeInfo）
// 供员工管理列表（启停/编辑）、admin 头像更换等场景调用
export const updateEmployee = updateEmployeeInfo

// 1.7 批量删除员工 - DELETE /admin
// 请求参数: ids (List<Long>, 必填)，如 ids=1&ids=2
// 响应 Result<List<Long>> → 删除的 ID 列表
export const deleteEmployees = (ids) => {
  return request({
    url: '/admin',
    method: 'delete',
    params: { ids }
  })
}
