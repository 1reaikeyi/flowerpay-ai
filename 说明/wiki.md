# Wiki

## sql

```
database\sql.txt                     #32张表
```

## 后端spring

```
spring-flower/
├── pom.xml                          # 父 POM，统一管理依赖版本与子模块
│
├── framework/                       # 基础设施（framework 包）
│   ├── aop / bo / interceptor / thread            # 操作日志切面、UserBO、敏感词、线程池
│   ├── exceptionhandle / mybatis / redis          # 全局异常、字段填充 + DruidFilter、Redis 指标
│   ├── properties / security                      # Jwt / AliOss 配置、Admin / User 认证
│   └── wechat / zhifubao                          # 微信 / 支付宝支付封装
│
├── branch-system/                   # 公共基础
│   ├── common/                      # 常量、异常、系统枚举（file/ 文件异常）
│   ├── result/                      # Result / PageResult / ScrollResult
│   └── controller / domain / mapper / service     # 操作日志 OperateLog
│
├── branch-file/                     # 文件上传
│   └── config / controller / util   # OSS 配置、本地与 OSS 上传、AliOssUtil
│
├── branch-flower/                   # 鲜花商品
│   ├── controller/admin · user      # 鲜花 / 详情 / 分类 / 节日
│   ├── domain/                      # bo / dto / entity / vo / wrapper
│   └── mapper / service             # Mapper + Service 实现
│
├── branch-pay/                      # 订单与支付
│   ├── controller/admin · user      # 订单
│   ├── domain/                      # dto / entity / enums / vo
│   ├── mapper / service             # 订单、明细、支付
│   └── wallet/                      # 支付宝支付 / 授权登录
│
├── branch-emp/                      # 员工、角色、权限、登录
│   ├── controller/                  # 管理员 / 角色 / 权限 / 用户 / Excel / 登录日志
│   ├── domain/                      # dto（含 page/）/ entity / excel / vo
│   ├── filter / mapper / util       # 刷新过滤器、Mapper XML、JwtUtil
│   └── service/                     # Service + impl，login/ 登录服务
│
├── branch-user/                     # C 端用户
│   └── controller / domain / mapper / service     # 收货地址、购物车
│
├── branch-blog/                     # 博客与文章
│   └── controller / domain / mapper / service     # 博客 / 文章 / 评论 / 关注 / 个人主页
│
├── branch-manage/                   # 店铺状态与签到
│   └── controller / domain / mapper / service     # AdminShop / Shop / Sign
│
├── branch-shop/                     # 店铺与店铺类型（默认包）
│   └── controller / domain / mapper / service
│
├── branch-second/                   # 优惠券与秒杀
│   ├── controller / domain / mapper / service
│   └── resources/                   # redis-seckill.lua / redis-unlock.lua
│
├── branch-ai/                       # AI 业务
│   ├── controller/                  # 对话 / 识图 / 会话，config/、graph/
│   ├── domain/                      # dto / entity / enums / vo
│   └── service/                     # memory / rag / session / tool / visual
│
├── start-main/                      # 主业务服务（8080）
│   ├── java/start/                  # config / monitor / statistics / websocket / FlowerApplication
│   └── resources/                   # application·mysql·redis yml、logback、static
│
├── start-ai/                        # AI 服务（8081）
│   ├── java/start/                  # config / AIApplication
│   └── resources/                   # ai.yml / session.yml / system-message.txt 等
│
├── start-generator/                 # 代码生成器
│
└── resource/                        # excel 报表 + image 商品图
```

---

## 前端vue

```
vue-flower/
├── index.html                       # HTML 入口
├── vite.config.js                   # 构建配置与开发代理
├── package.json
├── jsconfig.json
├── public/
│   └── favicon.ico
│
└── src/
    ├── main.js                      # 应用入口（注册 Pinia / ElementPlus / Router）
    ├── App.vue                      # 根组件
    ├── page.js                      # 路由定义（admin / user 两端 + 登录守卫）
    │
    ├── stores/                      # Pinia 状态管理
    │   ├── index.js
    │   └── modules/                 # admin / user / jwt
    │
    ├── api/                         # 接口封装（按端分包）
    │   ├── admin/                   # 管理端接口（admin / category / festival / festivalDetail / flower / flowerDetail / order / shop / statistics）
    │   ├── user/                    # 用户端接口（category / festival / flower / order / shop / user）
    │   ├── ai/                      # AI 接口（ai.js）
    │   └── file/                    # 文件 / Excel 接口
    │
    ├── utils/                       # 工具与请求封装
    │   ├── admin/request.js         # 管理端 Axios 实例与拦截器
    │   ├── user/request.js          # 用户端 Axios 实例与拦截器
    │   └── format.js
    │
    ├── layout/                      # 布局组件
    │   ├── admin.vue
    │   └── user.vue
    │
    ├── components/admin/            # 可复用管理端组件
    │   ├── AdminFormActions.vue
    │   ├── AdminFormPage.vue
    │   ├── AdminListPage.vue
    │   ├── AdminPagination.vue
    │   └── AdminTable.vue
    │
    ├── views/                       # 页面
    │   ├── admin/                   # 管理端页面
    │   │   ├── admin/               # 管理员（资料 / 头像 / 密码 / 员工编辑）
    │   │   ├── category/            # 分类（列表 / 新增）
    │   │   ├── festival/            # 节日礼盒（列表 / 新增 / 详情 / 规格）
    │   │   ├── flower/              # 鲜花（列表 / 新增 / 详情 / 规格）
    │   │   ├── login/               # 管理员登录页
    │   │   ├── order/               # 订单（详情 / 支付 / 退款）
    │   │   ├── shop/                # 店铺
    │   │   └── statistics/          # 统计图表（柱状 / 折线 / 扇形）
    │   ├── ai/                      # AI 助手页面
    │   └── user/                    # 用户端页面
    │       ├── category/
    │       ├── festival/
    │       ├── flower/
    │       ├── login/
    │       ├── order/
    │       ├── shop/
    │       └── shoppingCart/       # 购物车（预留目录）
    │
    └── assets/                      # 静态资源与全局样式（图片 / 登录素材 / 提示音 / 主题样式）
```

---

## 移动端uni-app

```
uni-flower/
├── index.html
├── vite.config.js
├── package.json
├── shims-uni.d.ts
├── src/                             # 源码（编译产物输出到 dist/）
│   ├── main.js
│   ├── App.vue
│   ├── manifest.json                # 应用配置
│   ├── pages.json                   # 页面与 tabBar 配置
│   ├── uni.scss / shime-uni.d.ts
│   ├── api/                         # 接口封装
│   │   ├── admin/                   # 管理端接口（admin / category / festival / festivalDetail / flower / flowerDetail / order / shop / statistics）
│   │   ├── ai/                      # AI 接口
│   │   └── file/                    # 文件 / Excel 接口
│   ├── layout/
│   │   └── TabBar.vue               # 底部导航
│   ├── pages/
│   │   ├── admin/                   # 管理端页面（home / goods / mine + admin / category / festival / flower / order / shop / statistics）
│   │   ├── index/                   # 首页
│   │   └── login/                   # 管理员登录页
│   ├── stores/                      # Pinia 状态管理（index / admin）
│   ├── styles/                      # 主题样式
│   ├── utils/                       # 请求封装与 JWT 工具
│   └── static/                      # 静态资源（菜单图标 / 登录素材）
└── dist/                            # 编译产物（mp-weixin 等）
```
