import request from '@/utils/admin/request.js'

/**
 * 文章（近期热点信息）管理 API
 */

// 新增文章分类 - POST /article
// 请求体 ArticleDTO: { categoryName, categoryAlias }
// 响应 Result<ArticleDTO>
export const createArticleCategory = (data) => {
  return request({
    url: '/article',
    method: 'post',
    data
  })
}

// 分页查询文章分类 - GET /article/all
// 请求参数 ArticlePageDTO: { page, pageSize(1~20) }
// 响应 Result<PageResult<ArticleVO>>：data = { total, list, pageNum, pageSize }
export const pageArticleCategoryList = (params) => {
  return request({
    url: '/article/all',
    method: 'get',
    params
  })
}

// 根据 ID 查询文章分类 - GET /article/{id}
// 响应 Result<ArticleVO>
export const getArticleCategoryById = (id) => {
  return request({
    url: `/article/${id}`,
    method: 'get'
  })
}

// 更新文章分类 - PUT /article
// 请求体 ArticleDTO: { id(必填), categoryName, categoryAlias }
export const updateArticleCategory = (data) => {
  return request({
    url: '/article',
    method: 'put',
    data
  })
}

// 批量删除文章分类 - DELETE /article/{id}
// 后端用 @PathVariable("id") List<Long> 接收，ID 列表以逗号拼接在路径上
export const deleteArticleCategories = (ids) => {
  return request({
    url: `/article/${ids.join(',')}`,
    method: 'delete'
  })
}

// ==================== 文章正文 ArticleDetailController ====================

// 新增文章 - POST /article/detail
// 请求体 ArticleDetailDTO: { title, content, coverImg, state, categoryId }
// state 只能是「已发布」或「草稿」
export const createArticle = (data) => {
  return request({
    url: '/article/detail',
    method: 'post',
    data
  })
}

// 分页查询已发布文章 - GET /article/detail/all
// 请求参数 ArticleDetailPageDTO: { page, pageSize(1~20) }
// 响应 Result<PageResult<ArticleVO>>：data = { total, list, pageNum, pageSize }
export const pageArticleList = (params) => {
  return request({
    url: '/article/detail/all',
    method: 'get',
    params
  })
}

// 根据 ID 查询文章 - GET /article/detail/{id}
// 响应 Result<ArticleVO>
export const getArticleById = (id) => {
  return request({
    url: `/article/detail/${id}`,
    method: 'get'
  })
}

// 更新文章 - PUT /article/detail
// 请求体 ArticleDetailDTO: { id(必填), title, content, coverImg, state, categoryId }
export const updateArticle = (data) => {
  return request({
    url: '/article/detail',
    method: 'put',
    data
  })
}

// 批量删除文章 - DELETE /article/detail
// 后端方法 delete(List<Long> ids) 未加注解，按查询参数绑定：?ids=1&ids=2
export const deleteArticles = (ids) => {
  return request({
    url: '/article/detail',
    method: 'delete',
    params: { ids }
  })
}
