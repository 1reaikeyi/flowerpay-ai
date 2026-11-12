import axios from 'axios'

/**
 * 地图找房 API
 * 后端控制器：GET /house/search（branch-navigation 模块）
 */

// 按地图可视范围查询行政区聚合数据
// 响应：[{ code, name, price, total, longitude, latitude }]
export const searchHouse = (params) => {
    return axios.get('/api/house/search', { params })
}
