# Wiki

## 一、后端（spring-flower）

```
spring-flower/
├── pom.xml                          # 父 POM，统一管理依赖版本与子模块
│
├── common/                          # 公共基础模块
│   ├── pom.xml
│   └── src/main/java/common/
│       ├── constant/                # 全局常量（错误码、JWT、Redis 前缀）
│       ├── exception/               # 异常体系（基类 + 各业务异常 + 文件异常）
│       ├── result/                  # 统一返回体（Result / PageResult / ScrollResult）
│       ├── system/                  # 系统枚举
│       └── util/                    # 工具类
│
├── model/                           # 数据模型模块
│   ├── pom.xml
│   └── src/main/java/model/
│       ├── bo/                      # 业务对象
│       ├── constant/                # 业务常量（角色、店铺、状态）
│       ├── dto/                     # 数据传输对象（入参）
│       ├── entity/                  # 数据库实体
│       ├── enums/                   # 业务枚举（订单/支付/配送状态）
│       ├── excel/                   # Excel 映射模型
│       ├── system/                  # 系统 VO（字典等）
│       ├── vo/                      # 视图对象（出参，含 statistics 统计 VO）
│       └── wrapper/                 # 包装对象（LogicData 等）
│
├── framework/                       # 设施层模块
│   ├── pom.xml
│   └── src/main/
│       ├── java/framework/
│       │   ├── aop/                 # AOP 操作日志（注解 + 切面）
│       │   ├── config/              # 配置类（Druid、MyBatis、Security、缓存、Caffeine、Web、Jackson 等）
│       │   ├── exceptionhandle/     # 全局异常处理
│       │   ├── filter/              # 过滤器（JWT 刷新、信息、Druid 等）
│       │   ├── interceptor/         # 拦截器（敏感词）
│       │   ├── mybatis/             # MyBatis 自动填充
│       │   ├── properties/          # 配置属性（JWT、阿里云 OSS）
│       │   ├── redis/               # Redis 指标导出
│       │   ├── security/            # 安全上下文
│       │   ├── util/                # JWT、OSS 工具
│       │   ├── wechat/              # 微信支付封装
│       │   └── zhifubao/            # 支付宝支付封装（config / service / DTO）
│       └── resources/
│           ├── application.yml
│           ├── mysql.yml
│           ├── redis.yml
│           └── redis集群配置
│
├── service/                         # 业务层模块
│   ├── pom.xml
│   └── src/main/java/
│       ├── mapper/                  # MyBatis-Plus Mapper 接口
│       └── service/                 # Service 接口 + impl 实现（含权限认证 provider）
│
├── branch-main/                     # 主业务启动模块（端口 8080）
│   ├── pom.xml
│   └── src/main/
│       ├── java/start/
│       │   ├── admin/               # 后台管理 Controller（统计、商品、分类、节日、订单、店铺、员工）
│       │   ├── employee/            # 员工端 Controller
│       │   ├── file/                # 文件 / 字典 / Excel 报表 Controller
│       │   ├── monitor/             # Druid / Redis 监控 Controller
│       │   ├── user/                # C 端用户 Controller（分类、鲜花、节日、订单、购物车、地址、店铺）
│       │   ├── wallet/              # 支付宝支付 / 授权登录 Controller
│       │   ├── websocket/           # WebSocket 配置、服务端、定时任务
│       │   └── FlowerApplication.java
│       └── resources/
│           ├── application-dev.yml
│           ├── logback-spring.xml
│           ├── mysql-dev.yml
│           ├── redis-dev.yml
│           └── static/              # 静态页面与资源（merchant.html、websocket.html、提示音）
│
├── branch-ai/                       # AI 扩展服务启动模块（端口 8081）
│   ├── pom.xml
│   └── src/main/
│       ├── java/
│       │   ├── comom/enums/         # AI 事件 / 消息类型枚举
│       │   ├── framework/properties/# 会话配置属性
│       │   ├── service/
│       │   │   ├── memory/          # 对话记忆（mysql / redis 仓库 + 工具）
│       │   │   ├── rag/             # RAG 对话与向量距离计算
│       │   │   ├── session/         # 会话服务
│       │   │   ├── tool/            # AI 工具（业务查询工具）
│       │   │   └── visual/          # 图片识别服务
│       │   └── start/
│       │       ├── controller/      # 对话、识图、会话 Controller
│       │       ├── graph/           # StateGraph 工作流节点定义
│       │       ├── load/            # ChatClient / Prompt / Spring AI 配置
│       │       ├── vo/              # AI 出参 VO
│       │       └── AIApplication.java
│       └── resources/
│           ├── ai.yml               # Spring AI / 大模型 / 向量库配置
│           ├── application-dev.yml
│           ├── mysql-dev.yml
│           ├── redis-dev.yml
│           ├── session.yml          # 会话记忆配置
│           └── system-message.txt   # 系统提示词
│
├── branch-generator/                # 代码生成器模块
│   ├── pom.xml
│   └── src/main/java/start/         # Generator、启动类、JDBC 配置
│
└── branch-resource/                 # 资源文件
    ├── excel/                       # 报表 Excel
    └── image/                       # 商品图片资源
```

---

## 二、前端（vue-flower）

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
    │
    ├── router/
    │   └── index.js                 # 三端路由 + 登录守卫
    │
    ├── stores/                      # Pinia 状态管理
    │   ├── index.js
    │   └── modules/                 # admin / emp / user / jwt
    │
    ├── api/                         # 接口封装（按端分包）
    │   ├── admin/                   # 管理端接口
    │   ├── employee/                # 员工端接口
    │   ├── user/                    # 用户端接口（含 ai.js）
    │   └── file/                    # 文件 / Excel 接口
    │
    ├── utils/                       # 工具与请求封装
    │   ├── admin/request.js         # 管理端 Axios 实例与拦截器
    │   ├── user/request.js          # 用户端 Axios 实例与拦截器
    │   └── format.js
    │
    ├── layout/                      # 三端布局组件
    │   ├── admin.vue
    │   ├── user.vue
    │   └── emp.vue
    │
    ├── components/admin/            # 可复用管理端组件
    │   ├── AdminFormActions.vue
    │   ├── AdminFormPage.vue
    │   ├── AdminListPage.vue
    │   ├── AdminPagination.vue
    │   └── AdminTable.vue
    │
    ├── views/                       # 页面（按端分包）
    │   ├── admin/                   # 管理端页面
    │   │   ├── category/            # 分类（列表 / 新增）
    │   │   ├── employee/            # 员工（列表 / 新增 / 资料 / 头像 / 密码）
    │   │   ├── festival/            # 节日礼盒（列表 / 新增 / 详情 / 规格）
    │   │   ├── flower/              # 鲜花（列表 / 新增 / 详情 / 规格）
    │   │   ├── login/               # 登录页
    │   │   ├── order/               # 订单（支付 / 退款 / 详情）
    │   │   ├── shop/                # 店铺
    │   │   └── statistics/          # 统计图表（柱状 / 折线 / 扇形）
    │   ├── emp/                     # 店员端页面
    │   │   ├── category/
    │   │   └── login/
    │   └── user/                    # 用户端页面
    │       ├── ai/                  # AI 助手
    │       ├── category/
    │       ├── festival/
    │       ├── flower/
    │       ├── login/
    │       ├── order/
    │       └── shop/
    │
    └── assets/                      # 静态资源与全局样式

```
