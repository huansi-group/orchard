# DataAgent

DataAgent 是一个前后端分离的**数据智能体工作台**示例项目：前端提供登录注册、运营概览与智能体管理界面，后端提供基于 Spring Security + JWT 的无状态认证与智能体管理 API。

项目开箱即用：后端使用 H2 内存数据库并自动初始化演示数据，前端通过 Vite 代理访问后端，无需额外配置数据库即可本地运行。

## 功能特性

- **账号体系**：登录、注册、获取当前用户；JWT（Bearer）无状态认证，令牌默认 24 小时有效
- **角色权限**：`ADMIN` 可创建、编辑、删除智能体；`USER` 仅可查看
- **运营概览**：智能体总数、运行状态、数据源分布与最近更新
- **智能体管理**：列表、关键词搜索、状态筛选、新建、编辑、删除
- **安全基线**：BCrypt 密码散列、统一 401/403 JSON 响应、参数校验、CORS 白名单
- **界面体验**：Vue 3 + Element Plus 中文界面，响应式布局，支持移动端

## 技术栈

| 端 | 技术 |
| --- | --- |
| 前端 | Vue 3.5、TypeScript、Vite 6、Element Plus 2、Pinia、Vue Router 4、Axios |
| 后端 | Java 21、Spring Boot 3.5.x、Spring Security、Spring Data JPA、JJWT、H2、Maven |

## 目录结构

```text
.
├── backend/                  # Spring Boot 后端
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/huansi/dataagent/
│       │   ├── auth/         # 登录、注册、当前用户
│       │   ├── agent/        # 智能体领域与 CRUD API
│       │   ├── dashboard/    # 概览统计
│       │   ├── security/     # JWT 生成/校验、认证过滤器
│       │   ├── config/       # Security、CORS、演示数据初始化
│       │   ├── user/         # 用户与角色
│       │   └── common/       # 统一响应与异常处理
│       ├── main/resources/application.yml
│       └── test/             # 认证与智能体 API 集成测试
├── frontend/                 # Vue 3 前端
│   ├── src/
│   │   ├── api/              # Axios 请求封装
│   │   ├── components/       # 登录/注册通用外壳
│   │   ├── layouts/          # 工作台布局
│   │   ├── router/           # 路由与登录守卫
│   │   ├── stores/           # Pinia 认证状态
│   │   ├── views/            # 登录、注册、概览、智能体
│   │   └── utils/
│   └── vite.config.ts        # 开发代理 /api -> localhost:8080
├── docs/screenshots/         # 界面截图
├── README.md
└── LICENSE
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- Node.js 20.19+（推荐 22 LTS）与 npm 10+

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动在 <http://localhost:8080>。H2 控制台：<http://localhost:8080/h2-console>（JDBC URL `jdbc:h2:mem:dataagent`，用户名 `sa`，密码为空）。

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 <http://localhost:5173>。开发服务器已将 `/api` 代理到 `http://localhost:8080`，前端无需配置后端地址。

### 3. 使用演示账号登录

| 用户名 | 密码 | 角色 | 权限 |
| --- | --- | --- | --- |
| `admin` | `admin123` | 管理员 | 查看 + 新建/编辑/删除智能体 |
| `analyst` | `analyst123` | 普通用户 | 仅查看 |

也可以在登录页点击“一键填入”，或注册新账号（新账号默认角色为 `USER`）。

## 界面预览

| 登录 | 注册 |
| --- | --- |
| ![登录页](docs/screenshots/login.png) | ![注册页](docs/screenshots/register.png) |

| 概览 | 智能体管理 |
| --- | --- |
| ![概览页](docs/screenshots/dashboard.png) | ![智能体管理](docs/screenshots/agents.png) |

| 新建智能体 | 移动端概览 |
| --- | --- |
| ![新建智能体](docs/screenshots/agent-dialog.png) | ![移动端概览](docs/screenshots/dashboard-mobile.png) |

## 配置说明

后端配置位于 `backend/src/main/resources/application.yml`，支持通过环境变量覆盖：

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | 后端端口 |
| `JWT_SECRET` | 开发环境默认密钥 | JWT 签名密钥，**至少 32 个字符**，生产环境必须覆盖 |
| `JWT_EXPIRATION` | `24h` | 令牌有效期，支持 `30m`、`24h` 等 Duration 格式 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | 允许的前端来源，逗号分隔 |

示例：

```bash
JWT_SECRET="please-replace-with-a-random-64-char-secret-key-2026" \
CORS_ALLOWED_ORIGINS="https://dataagent.example.com" \
mvn spring-boot:run
```

> **部署提示**：前端与后端同域部署时，浏览器对 POST 请求仍会携带 `Origin` 头，请将 `CORS_ALLOWED_ORIGINS` 设置为实际访问域名（例如 `https://dataagent.example.com`）。

> **安全提示**：`JWT_SECRET` 与演示账号仅用于本地体验。部署到生产环境前，请替换随机密钥、关闭或移除 H2 控制台，并改用 MySQL/PostgreSQL 等持久化数据库。

## API 一览

所有业务接口以 `/api` 为前缀，除登录、注册外均需在请求头携带 `Authorization: Bearer <token>`。

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | 公开 | 登录并返回 JWT |
| `POST` | `/api/auth/register` | 公开 | 注册并返回 JWT |
| `GET` | `/api/auth/me` | 已登录 | 当前用户信息 |
| `GET` | `/api/dashboard/summary` | 已登录 | 概览统计 |
| `GET` | `/api/agents` | 已登录 | 智能体列表，支持 `keyword`、`status` 参数 |
| `POST` | `/api/agents` | 管理员 | 创建智能体 |
| `PUT` | `/api/agents/{id}` | 管理员 | 更新智能体 |
| `DELETE` | `/api/agents/{id}` | 管理员 | 删除智能体 |

统一响应结构：

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "displayName": "系统管理员",
      "email": "admin@dataagent.local",
      "role": "ADMIN"
    }
  }
}
```

登录示例：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 构建与测试

### 后端

```bash
cd backend
mvn test        # 运行认证与智能体 API 集成测试
mvn package     # 构建可执行 jar：target/dataagent-backend-0.1.0-SNAPSHOT.jar
java -jar target/dataagent-backend-0.1.0-SNAPSHOT.jar
```

### 前端

```bash
cd frontend
npm run typecheck   # vue-tsc 类型检查
npm run build       # 类型检查并构建到 dist/
npm run preview     # 本地预览构建产物
```

前端构建产物为纯静态文件，部署时需将 `/api` 反向代理到后端服务。

## 许可证

[MIT](LICENSE)
