# Wiki

## sql

```
database\sql.txt                     #20张表
```

## 后端spring

```
spring-flower/
├── pom.xml                          # 父 POM，统一管理依赖版本与子模块
│
├── framework/                       # 设施层模块
│   ├── pom.xml
│   └── src/main/java/
│       ├── common/                  # 通用常量与异常
│       │   ├── RoleConstant / ShopConstant / StatusConstant
│       │   ├── constant/            # ErrorConstant / JwtConstant / RedisPrefixConstant
│       │   ├── exception/           # 基类 BaseException + 各业务异常，file/ 文件异常
│       │   └── system/              # 系统枚举与字典 VO（DictionaryVO / NumberEnum / ObjectEnum / OptionEnum）
│       ├── result/                  # 统一返回体（Result / PageResult / ScrollResult）
│       └── framework/
│           ├── aop/                 # 操作日志注解 + 切面（含 oparation/ 操作枚举）
│           ├── bo/                  # 业务对象（UserBO）
│           ├── exceptionhandle/     # 全局异常处理（GlobalExceptionHandler / ExternalExceptionHandle / PartExceptionHandler）
│           ├── interceptor/         # 拦截器（SensitiveWordInterceptor 敏感词）
│           ├── mybatis/             # MyBatis-Plus 自动填充（AutoMetaObjectHandler）、DruidFilter
│           ├── properties/          # 配置属性（JwtProperties / AliOssProperties）
│           ├── redis/               # Redis 指标导出（RedisExporter）
│           ├── security/            # 安全上下文（Admin / User 认证 Token 与 Provider、LoginUserDetails、SecurityContextParam）
│           ├── thread/              # 线程池配置（ThreadPoolConfig）
│           ├── util/                # JWT、阿里云 OSS 工具
│           ├── wechat/              # 微信支付封装
│           └── zhifubao/            # 支付宝支付封装（config / service / DTO）
│
├── branch-system/                   # 系统支撑业务模块（com.branch.*）
│   ├── pom.xml
│   └── src/main/java/com/branch/
│       ├── config/                  # OSS 配置（OssConfig）
│       ├── controller/              # 字典 / 文件 / OSS 文件 Controller
│       │   └── shop/                # 店铺 Controller（管理端 AdminShopController + 用户端 ShopController）
│       └── domain/
│           ├── dto/                 # 系统 DTO
│           └── vo/                  # 店铺 VO（ShopVO）
│
├── branch-flower/                   # 鲜花商品业务模块（com.branch.*）
│   ├── pom.xml
│   └── src/main/java/com/branch/
│       ├── controller/
│       │   ├── admin/               # 管理端：鲜花 / 分类 / 节日 / 规格 Controller
│       │   └── user/                # 用户端：鲜花 / 分类 / 节日 / 规格 Controller
│       ├── domain/
│       │   ├── bo/                  # 业务对象（FlowerBO，供 AI 工具调用）
│       │   ├── dto/                 # 鲜花 / 分类 / 节日 / 规格 DTO（含分页 DTO）
│       │   ├── entity/              # 实体：Flower / FlowerCategory / FlowerDetail / Festival / FestivalDetail
│       │   ├── vo/                  # 视图对象：FlowerVO / FlowerCategoryVO / FlowerDetailVO / FestivalVO / FestivalDetailVO
│       │   └── wrapper/             # 逻辑包装（LogicData）
│       ├── mapper/                  # 鲜花 / 分类 / 节日 / 规格 Mapper
│       └── service/                 # Service 接口 + impl 实现
│
├── branch-pay/                      # 订单与支付业务模块（com.branch.*）
│   ├── pom.xml
│   └── src/main/java/com/branch/
│       ├── controller/
│       │   ├── admin/               # 管理端订单 Controller
│       │   └── user/                # 用户端订单 Controller
│       ├── domain/
│       │   ├── dto/                 # 订单 DTO（FlowerOrderDTO / FlowerOrderDetailDTO / FlowerOrderPageDTO）
│       │   ├── entity/              # 实体：FlowerOrder / FlowerOrderDetail / FlowerOrderPay
│       │   ├── enums/               # 订单状态枚举：OrderStatusEnum / PayStatusEnum / DeliveryStatusEnum
│       │   └── vo/                  # 订单 VO 与统计 VO（FlowerOrderVO / StatisticsVO / OrderStatisticsVO / TodayStatisticsVO / TopStatisticsVO）
│       ├── mapper/                  # 订单 / 订单明细 / 订单支付 Mapper
│       ├── service/                 # Service 接口 + impl 实现
│       └── wallet/                  # 支付宝支付 / 授权登录 Controller
│
├── branch-emp/                      # 员工、角色与权限业务模块（com.branch.*）
│   ├── pom.xml
│   └── src/main/java/com/branch/
│       ├── controller/              # 管理员 / 角色 / 权限 / Excel / 用户 Controller
│       ├── domain/
│       │   ├── dto/                 # 员工 / 用户 / 登录 / 密码 / 角色权限 DTO（含 page/ 分页 DTO）
│       │   ├── entity/              # 实体：Employee / EmployeeRole / Role / RolePermission / Permission / User
│       │   ├── excel/               # Excel 导出模型（UserExcel）
│       │   └── vo/                  # EmployeeVO / RoleVO / PermissionVO / RolePermissionVO
│       ├── filter/                  # Admin / User 刷新过滤器、InformationRequestFilter
│       ├── mapper/                  # 员工 / 员工角色 / 角色 / 角色权限 / 权限 / 用户 Mapper
│       └── service/                 # Service 接口 + impl 实现，含 login/（LoginAdminService / LoginUserService）
│
├── branch-user/                     # C 端用户业务模块（com.branch.*）
│   ├── pom.xml
│   └── src/main/java/com/branch/
│       ├── controller/              # 收货地址 / 购物车 Controller
│       ├── domain/
│       │   ├── dto/                 # 用户 DTO（UserAddressDTO / UserShoppingDTO）
│       │   ├── entity/              # 实体：UserAddress / UserShopping
│       │   └── vo/                  # 购物车视图对象（UserShoppingVO）
│       ├── mapper/                  # 地址 / 购物车 Mapper
│       └── service/                 # Service 接口 + impl 实现
│
├── branch-ai/                       # AI 业务模块（com.branch.*）
│   ├── pom.xml
│   └── src/main/java/com/branch/
│       ├── controller/              # 对话 / 识图 / 会话 Controller，含 graph/ 工作流节点、load/ 配置加载
│       ├── domain/
│       │   ├── bo/                  # AI 业务对象
│       │   ├── dto/                 # AI 入参 DTO（ChatDTO）
│       │   ├── entity/              # 实体：ChatRecord / Session
│       │   ├── enums/               # 聊天事件 / 消息类型枚举
│       │   └── vo/                  # AI 出参 VO（MessageVO / SessionVO / SessionTitleVO / ChatEventVO）
│       ├── mapper/                  # 会话 / 聊天记录 Mapper
│       ├── properties/              # 会话配置属性
│       └── service/
│           ├── memory/              # 对话记忆工具（mysql/、redis/ 两种实现）
│           ├── rag/                 # RAG 对话与向量距离计算
│           ├── session/             # 会话服务
│           ├── tool/                # AI 业务查询工具
│           └── visual/              # 图片识别服务
│
├── start-main/                      # 主业务启动模块（FlowerApplication，端口 8080）
│   ├── pom.xml
│   └── src/main/
│       ├── java/start/
│       │   ├── config/              # 配置类（Druid / MyBatis / Security / SpringCache / Caffeine / Web / Rest / Jackson / 敏感词）
│       │   ├── monitor/             # Druid / Redis 监控 Controller
│       │   ├── statistics/          # 统计 Controller、Service 与统计 VO
│       │   ├── websocket/           # WebSocket 配置、服务端、定时任务（OrderTask）
│       │   └── FlowerApplication.java
│       └── resources/
│           ├── application.yml / application-dev.yml
│           ├── mysql.yml / mysql-dev.yml
│           ├── redis.yml / redis-dev.yml / redis哨兵配置.yml
│           ├── logback-spring.xml
│           └── static/              # 静态页面与资源（merchant.html、user.html、提示音）
│
├── start-ai/                        # AI 服务启动模块（AIApplication，端口 8081）
│   ├── pom.xml
│   └── src/main/
│       ├── java/start/
│       │   ├── config/              # 配置类
│       │   └── AIApplication.java
│       └── resources/
│           ├── ai.yml               # Spring AI / 大模型 / 向量库配置
│           ├── application.yml / application-dev.yml
│           ├── mysql.yml / mysql-dev.yml
│           ├── redis.yml / redis-dev.yml
│           ├── session.yml          # 会话记忆配置
│           └── system-message.txt   # 系统提示词
│
├── start-generator/                 # 代码生成器模块
│   ├── pom.xml
│   └── src/main/
│       ├── java/start/              # Generator、GeneratorApplication、JDBCProperties
│       └── resources/               # application.yml / application-dev.yml / mysql.yml / mysql-dev.yml
│
└── branch-resource/                 # 资源文件
    ├── excel/                       # 报表 Excel（report.xlsx）
    └── image/                       # 商品图片资源
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
