import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // Level 1:
        {
            path: '/admin/login',
            component: () => import('@/views/admin/login/admin.vue'),
            meta: { requiresAuth: false }
        },

        // Level 1 - 分支 A: 管理员体系
        {
            path: '/admin',
            component: () => import('@/layout/admin.vue'), 
            redirect: '/admin/flower/category',
            children: [

                { path: 'flower/category', component: () => import('@/views/admin/category/category.vue') },
                { path: 'flower/category/add', component: () => import('@/views/admin/category/addCategory.vue') },
                { path: 'flower', component: () => import('@/views/admin/flower/flower.vue') },
                { path: 'flower/add', component: () => import('@/views/admin/flower/addFlower.vue') },
                { path: 'flower/detail', component: () => import('@/views/admin/flower/detail.vue') },
                { path: 'flower/give', component: () => import('@/views/admin/flower/giveFlower.vue') },
                { path: 'flower/festival', component: () => import('@/views/admin/festival/festival.vue') },
                { path: 'flower/festival/add', component: () => import('@/views/admin/festival/addFestival.vue') },
                { path: 'flower/festival/detail', component: () => import('@/views/admin/festival/detail.vue') },
                { path: 'flower/festival/give', component: () => import('@/views/admin/festival/giveFestival.vue') },


                { path: 'blog/article', component: () => import('@/views/admin/blog/article.vue') },
                { path: 'blog/article/add', component: () => import('@/views/admin/blog/addArticle.vue') },
                { path: 'blog/comment', component: () => import('@/views/admin/blog/comment.vue') },

                { path: 'statistics/line', component: () => import('@/views/admin/statistics/lineChart.vue') },
                { path: 'statistics/column', component: () => import('@/views/admin/statistics/columnChart.vue') },
                { path: 'statistics/fan', component: () => import('@/views/admin/statistics/fanChart.vue') },

                { path: 'map', component: () => import('@/views/admin/map/index.vue') },

                { path: 'order/pay', component: () => import('@/views/admin/order/pay.vue') },
                { path: 'order/refund', component: () => import('@/views/admin/order/refund.vue') },
                { path: 'order/detail', component: () => import('@/views/admin/order/detail.vue') },

                { path: 'shop', component: () => import('@/views/admin/shop/shop.vue') },
                { path: 'admin/employee', component: () => import('@/views/admin/admin/employee.vue') },
                { path: 'admin/update', component: () => import('@/views/admin/admin/updateEmployee.vue') },
                { path: 'admin/profile', component: () => import('@/views/admin/admin/profile.vue') },
                { path: 'admin/avatar', component: () => import('@/views/admin/admin/avatar.vue') },
                { path: 'admin/password', component: () => import('@/views/admin/admin/password.vue') }

                // { path: 'admin/system/login', component: () => import('@/views/admin/admin/password.vue') },
                // { path: 'admin/system/operate', component: () => import('@/views/admin/admin/password.vue') }

            ]
        }
    ]
})

// 取管理员 localStorage 中的 token
const getToken = () => {
    const raw = localStorage.getItem('flower:admin')
    return raw ? JSON.parse(raw).token : ''
}

// 登录访问拦截：仅校验管理员是否已登录
router.beforeEach((to) => {
    // 登录页放行，避免重定向死循环
    if (to.path === '/admin/login') return

    // 未登录：清掉登录态，跳转到登录页
    if (!getToken()) {
        localStorage.removeItem('flower:admin')
        return '/admin/login'
    }
})

export default router
