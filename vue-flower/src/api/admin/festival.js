import request from '@/utils/admin/request.js'

/**
 * 节日多花礼盒管理 API（按 admin 接口文档对齐）
 * 后端控制器根路径：/admin/festival
 */

// 2.1 新增节日多花礼盒 - POST /admin/festival
// 请求体 FestivalDTO: { name(必填), categoryId(必填), price(必填), status, description, image }
// 鲜花数量在明细表 festival_detail.spec_number 维护，礼盒本身无数量字段
// 响应 Result<FestivalDTO>（带自增 ID）
export const createFestival = (data) => {
  return request({
    url: '/admin/festival',
    method: 'post',
    data
  })
}

// 2.2 根据 ID 查询节日多花礼盒 - GET /admin/festival
// 请求参数: id (Long, 必填)
// 响应 Result<FestivalVO>
export const getFestivalById = (id) => {
  return request({
    url: '/admin/festival',
    method: 'get',
    params: { id }
  })
}

// 2.3 分页查询多花礼盒列表 - GET /admin/festival/all
// 请求参数 FestivalPageDTO (Query): { page, pageSize, name }
// 支持按名称模糊搜索，响应 Result<PageResult<FestivalVO>>
export const pageFestivalList = (params) => {
  return request({
    url: '/admin/festival/all',
    method: 'get',
    params
  })
}

// 2.4 更新节日多花礼盒 - PUT /admin/festival
// 请求体 FestivalDTO: { id(必填), name, categoryId, price, status, description, image }
// 响应 Result<FestivalDTO>
export const updateFestival = (data) => {
  return request({
    url: '/admin/festival',
    method: 'put',
    data
  })
}

// 2.5 批量删除节日多花礼盒 - DELETE /admin/festival
// 请求参数: ids (List<Long>, 必填)
// 删除后遍历清除 Redis 缓存，响应 Result<List<Long>>
export const deleteFestivals = (ids) => {
  return request({
    url: '/admin/festival',
    method: 'delete',
    params: { ids }
  })
}

// 2.6 查询多花礼盒下的明细列表 - GET /admin/festival/of/detail
// 请求参数: id (Long, 必填) 多花礼盒主键 ID
// 在 festival_detail 表按 festival_id 查询，响应 Result<List<FestivalDetailVO>>
export const getFestivalDetailsByFestivalId = (id) => {
  return request({
    url: '/admin/festival/of/detail',
    method: 'get',
    params: { id }
  })
}

// 2.7 查询某鲜花被哪些多花礼盒包含 - GET /admin/festival/of/flower
// 请求参数: id (Long, 必填) 鲜花主键 ID
// 在 festival_detail 表按 flower_id 查询，响应 Result<List<FestivalDetailVO>>
export const getFestivalsByFlowerId = (id) => {
  return request({
    url: '/admin/festival/of/flower',
    method: 'get',
    params: { id }
  })
}

// 2.8 按送人对象查询多花礼盒明细 - GET /admin/festival/of/object
// 请求参数: object (String, 必填) 送人对象关键词，如 "女友"（spec_object LIKE %object%）
// 响应 Result<List<FestivalDetailVO>>
export const getFestivalDetailsByObject = (object) => {
  return request({
    url: '/admin/festival/of/object',
    method: 'get',
    params: { object }
  })
}

// 2.9 按用途场景查询多花礼盒明细 - GET /admin/festival/of/option
// 请求参数: option (String, 必填) 用途场景关键词，如 "生日"（spec_option LIKE %option%）
// 响应 Result<List<FestivalDetailVO>>
export const getFestivalDetailsByOption = (option) => {
  return request({
    url: '/admin/festival/of/option',
    method: 'get',
    params: { option }
  })
}

// 2.10 按鲜花数量查询多花礼盒明细 - GET /admin/festival/of/number
// 请求参数: number (int, 必填) 规格鲜花数量
// 响应 Result<List<FestivalDetailVO>>
export const getFestivalDetailsByNumber = (number) => {
  return request({
    url: '/admin/festival/of/number',
    method: 'get',
    params: { number }
  })
}

// 2.11 送人对象字典 - GET /admin/festival/get/object
// 响应 Result<List<DictionaryVO>>: { code, name }
export const getFestivalObjectDictionary = () => {
  return request({
    url: '/admin/festival/get/object',
    method: 'get'
  })
}

// 2.12 用途场景字典 - GET /admin/festival/get/option
// 响应 Result<List<DictionaryVO>>: { code, name }
export const getFestivalOptionDictionary = () => {
  return request({
    url: '/admin/festival/get/option',
    method: 'get'
  })
}

// 2.13 鲜花数量字典 - GET /admin/festival/get/number
// 响应 Result<List<DictionaryVO>>: { code, name }
export const getFestivalNumberDictionary = () => {
  return request({
    url: '/admin/festival/get/number',
    method: 'get'
  })
}
