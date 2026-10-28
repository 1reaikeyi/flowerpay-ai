import request from '@/utils/admin/request.js'

/**
 * 博客评论管理 API
 * 对齐后端 BlogCommentsController（/blog/comments）与 BlogController（/blog）
 */

// 分页查询博客 - GET /blog/all
// 请求参数 BlogPageDTO: { page, pageSize(1~20) }（查询参数）
// 响应 Result<PageResult<BlogVO>>：data = { total, list, pageNum, pageSize }
export const pageBlogList = (params) => {
  return request({
    url: '/blog/all',
    method: 'get',
    params
  })
}

// 根据 ID 查询博客详情 - GET /blog/{id}
// 响应 Result<Blog>：{ id, title, content, images, userId, liked, comments, createTime }
export const getBlogById = (id) => {
  return request({
    url: `/blog/${id}`,
    method: 'get'
  })
}

// 查询博客浏览量（HyperLogLog 去重计数） - GET /blog/comments/view/of/{id}
export const getBlogViewCount = (id) => {
  return request({
    url: `/blog/comments/view/of/${id}`,
    method: 'get'
  })
}

// 查询某博客的全部评论（按时间倒序） - GET /blog/comments/of/{id}
// 响应 Result<List<BlogComments>>
export const listBlogComments = (id) => {
  return request({
    url: `/blog/comments/of/${id}`,
    method: 'get'
  })
}

// 对博客发表评论 - POST /blog/comments/0/{id}
// 后端 @RequestBody String，需要发送 JSON 字符串（带引号）
export const createBlogComment = (id, content) => {
  return request({
    url: `/blog/comments/0/${id}`,
    method: 'post',
    data: JSON.stringify(content)
  })
}

// 回复评论 - POST /blog/comments/1/{id}
// 后端 @RequestBody String，需要发送 JSON 字符串（带引号）
export const replyBlogComment = (id, content) => {
  return request({
    url: `/blog/comments/1/${id}`,
    method: 'post',
    data: JSON.stringify(content)
  })
}
