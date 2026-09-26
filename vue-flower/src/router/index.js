import { createRouter, createWebHistory } from 'vue-router'
import { useEmployeeStore } from '@/stores'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // 根路径：重定向到 admin 默认页
        {
            path: '/',
            redirect: '/admin'
        },
        // Level 1:
        {
            path: '/admin/login',
            component: () => import('@/views/admin/login/admin.vue'),
            meta: { requiresAuth: false }
        },
        // Level 2:
        {
            path: '/user/login',
            component: () => import('@/views/user/login/user.vue'),
            meta: { requiresAuth: false }
        },
        // Level 3:
        {
            path: '/emp/login',
            component: () => import('@/views/employee/login/employee.vue'),
            meta: { requiresAuth: false }
        },

        // Level 1 - 分支 A: 管理员体系
        {
            path: '/admin',
            component: () => import('@/layout/admin.vue'), 
            redirect: '/admin/flower/index',
            children: [

                { path: 'category', component: () => import('@/views/admin/category/category.vue') },
                { path: 'category/add', component: () => import('@/views/admin/category/addCategory.vue') },

                { path: 'flower', component: () => import('@/views/admin/flower/flower.vue') },
                { path: 'flower/add', component: () => import('@/views/admin/flower/addFlower.vue') },
                { path: 'flower/detail', component: () => import('@/views/admin/flower/detail.vue') },
                { path: 'flower/give', component: () => import('@/views/admin/flower/giveFlower.vue') },

                { path: 'festival', component: () => import('@/views/admin/festival/festival.vue') },
                { path: 'festival/add', component: () => import('@/views/admin/festival/addFestival.vue') },
                { path: 'festival/detail', component: () => import('@/views/admin/festival/detail.vue') },
                { path: 'festival/give', component: () => import('@/views/admin/festival/giveFestival.vue') },

                { path: 'statistics/line', component: () => import('@/views/admin/statistics/lineChart.vue') },
                { path: 'statistics/bar', component: () => import('@/views/admin/statistics/barChart.vue') },
                { path: 'statistics/fan', component: () => import('@/views/admin/statistics/fanChart.vue') },

                { path: 'shop', component: () => import('@/views/admin/shop/shop.vue') },

                { path: 'order/pay', component: () => import('@/views/admin/order/pay.vue') },
                { path: 'order/refund', component: () => import('@/views/admin/order/refund.vue') },
                { path: 'order/detail', component: () => import('@/views/admin/order/detail.vue') },

                { path: 'employee', component: () => import('@/views/admin/employee/employee.vue') },
                { path: 'employee/add', component: () => import('@/views/admin/employee/addEmployee.vue') },
                { path: 'employee/profile', component: () => import('@/views/admin/employee/profile.vue') },   // 当前员工信息
                { path: 'employee/avatar', component: () => import('@/views/admin/employee/avatar.vue') },    // 更换头像
                { path: 'employee/password', component: () => import('@/views/admin/employee/password.vue') } // 重置密码
            ]
        },

        // Level 2 - 分支 B: user体系
        {
            path: '/user', // 用户体系的根路径
            component: () => import('@/layout/user.vue'), 
            redirect: '/user/category',
            children: [
                { path: 'category', component: () => import('@/views/user/category/category.vue') },
                { path: 'flower', component: () => import('@/views/user/flower/index.vue') },
                { path: 'festival', component: () => import('@/views/user/festival/index.vue') },
                { path: 'shop', component: () => import('@/views/user/shop/shop.vue') },
                { path: 'shoppingCart', component: () => import('@/views/user/shop/shop.vue') },
                { path: 'order', component: () => import('@/views/user/order/order.vue') },
                { path: 'ai', component: () => import('@/views/user/ai/index.vue') }
            ]
        },

        // Level 3 - 分支 C: employee体系
        {
            path: '/emp', // 员工体系的根路径
            component: () => import('@/layout/emp.vue'), 
            redirect: '/emp/category',
            children: [
                { path: 'category', component: () => import('@/views/emp/category/category.vue') },
            ]
        }
    ]
})

// 各端登录态配置：路由前缀 -> { localStorage 存储 key, 登录页路径 }
const AUTH_SCOPES = [
    { prefix: '/admin', key: 'flower:admin', login: '/admin/login' },
    { prefix: '/user', key: 'flower:user', login: '/user/login' },
    { prefix: '/emp', key: 'flower:emp', login: '/emp/login' },
]

// 取某端 localStorage 中的 token
// 守卫仅判断“是否已登录”；token 是否有效/过期交给后端校验，后端无 Authorization 会返回 401，
// 由各端的 request 拦截器统一处理（清登录态 + 跳转对应登录页）
const getToken = (key) => {
    const raw = localStorage.getItem(key)
    return raw ? JSON.parse(raw).token : ''
}

// 登录访问拦截：按路由前缀匹配所属体系，仅校验是否已登录
router.beforeEach((to) => {
    const scope = AUTH_SCOPES.find((s) => to.path.startsWith(s.prefix))
    if (!scope) return

    // 对应体系的登录页放行，避免重定向死循环
    if (to.path === scope.login) return

    // 未登录：清掉该端登录态，跳转到对应登录页
    if (!getToken(scope.key)) {
        localStorage.removeItem(scope.key)
        return scope.login
    }
})

export default router
