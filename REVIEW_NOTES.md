# Spring Boot 学习笔记（Demo 1-4 复习）

> 学习路径：REST 基础 → 持久化 → 校验异常 → 查询与分页
> 每个 Demo 都是独立可运行的 Spring Boot 3.5.4 + Java 21 项目，前端由 Spring Boot 托管静态资源，前后端同端口打通。
> 所有代码已推送到 GitHub：https://github.com/ZhangLv504/SpringBootDemoStydu

---

## 目录

- [Demo 1：REST 基础 + 三层架构 + 前后端打通](#demo-1rest-基础--三层架构--前后端打通)
- [Demo 2：Spring Data JPA + H2 数据库](#demo-2spring-data-jpa--h2-数据库)
- [Demo 3：参数校验 + 全局异常处理](#demo-3参数校验--全局异常处理)
- [Demo 4：JPA 自定义查询与分页](#demo-4jpa-自定义查询与分页)
- [速查表：三层架构与关键注解](#速查表三层架构与关键注解)

---

## Demo 1：REST 基础 + 三层架构 + 前后端打通

### 基础信息

| 项目 | 内容 |
|---|---|
| 目录 | `demo1-rest-basics` |
| 场景 | 图书商店：新增图书、查询全部、按 id 查询 |
| 技术栈 | Spring Boot 3.5.4、Java 21、Maven、内存存储（`Map` + `AtomicLong`） |
| 分层 | `controller` / `service` / `dto` / `model` |
| 接口 | `GET /api/books`、`GET /api/books/{id}`、`POST /api/books` |
| 前端 | `src/main/resources/static/`（`index.html` + `css/style.css` + `js/app.js`） |
| 运行 | `cd demo1-rest-basics && mvn spring-boot:run`，浏览器打开 `http://localhost:8080` |

### 关键类

- `BookController`：只做 HTTP 交互，返回 `201`/`200`/`404`
- `BookService`：业务逻辑，持有 `Map<Long, Book>` + `AtomicLong` 生成 id
- `BookRequest`（DTO）：接收请求体（record）
- `Book`（model）：普通 POJO，无数据库

### 前后端如何打通（重要）

- 前端文件放在 `src/main/resources/static/`，**Spring Boot 自动把它当静态资源对外提供**
- 前端 `fetch('/api/books')` 用**相对路径**，浏览器加载页面和请求 API 来自**同一个源**（同一 host:port），所以**无跨域（CORS）问题**
- 请求 `/` 返回 `index.html`，请求 `/api/books` 走 Controller，由同一个内嵌 Tomcat 处理

### 复习题与答案

**1. 构造器注入 vs `@Autowired` 字段注入？为什么官方推荐构造器注入？**

- Spring 通过**反射**调用 Controller 唯一带 `BookService` 参数的构造器，自动把容器里的 `BookService` Bean 传进来（Spring 4.3+ 单一构造器连 `@Autowired` 都可以省）
- `@Autowired` 字段注入效果不同：字段**不能声明 `final`**（final 只能通过构造器初始化），依赖是通过**反射**在运行时塞进去的
- 构造器注入的优点：依赖**不可变（final）**、**可测试**（可以直接 `new BookController(service)` 传 mock，不必启动 Spring 容器）、依赖关系明确、防止遗漏注入（漏了构造直接编译失败）

**2. `@RestController` vs `@Controller`？只写 `@Controller` 还能返回 JSON 吗？**

- 不能。`@RestController` = `@Controller` + `@ResponseBody`
- `@ResponseBody` 告诉 Spring：方法返回值**直接序列化写入 HTTP 响应体**
- 只写 `@Controller`，返回值会被 `ViewResolver` 当成**视图名**去找模板（如 Thymeleaf），返回的是 HTML 视图而不是 JSON

**3. `@Service` 换成 `@Component` 行吗？三个注解什么关系？**

- 行。`@Controller`、`@Service`、`@Repository` **内部都包含 `@Component`**（元注解），只是用来区分逻辑角色
- 额外区别：`@Repository` 还会开启**持久层异常转译**（把 JPA/Hibernate 异常转成 Spring 的 `DataAccessException`）

**4. POST 请求体为什么用 DTO 而不是直接用 `Book`？**

- 直接传 `Book` **也能 JSON 化**（不是"做不到"），真正的理由是：
  1. **解耦**：持久化实体不该当 API 契约暴露，改表结构时前端不用跟着改
  2. **安全**：避免泄露实体里多余的字段（如密码、内部状态）
  3. **稳定**：输入在边界被显式约束（配合校验注解，见 Demo 3）
- 核心：**解耦 + 安全 + 稳定**

**5. 新建返回 201、查不到返回 404，为什么比全返回 200 规范？**

- HTTP 状态码是**机器可读的语义**，客户端、缓存、SDK、监控都依赖它
- 前端可以根据状态码走不同分支（成功/重定向/客户端错误/服务端错误），便于快速定位问题

**6. 前端放 `static/` 后为什么和后端共用 8080 端口？前后端分离的跨域怎么解决？**

- 前端文件被 Spring Boot 作为静态资源返回给浏览器，浏览器加载页面时它本来就来自后端 host+端口（8080），`fetch` 相对路径自然请求同一个后端——**同源，所以没有 CORS**
- "跨域" = 浏览器里两个 URL 的**协议/域名/端口任一不同**
- 前后端分离时（Vue 在 5173，后端在 8080）就跨域了，解决方式：
  1. 后端加 CORS 头：`@CrossOrigin` 或全局 `CorsConfiguration`
  2. 反向代理：Nginx / Vite dev proxy 把 `/api` 转发到后端，让浏览器只看到一个源
- 注意：**CORS 是浏览器的安全策略**，不是网络层面的限制；用 curl 发请求根本不涉及跨域

**7. `fetch('/api/books')` 相对路径为什么能命中后端？写死 `http://localhost:8080/api/books` 有什么坏处？**

- 相对路径自动基于**当前页面 origin** 解析，页面从 8080 加载，就请求 8080 的 `/api/books`
- 写死 `localhost:8080` 的坏处：换端口、换域名、上生产环境都要改代码；写死了 `localhost` 部署到服务器就直接失效，还可能引入跨域问题

---

## Demo 2：Spring Data JPA + H2 数据库

### 基础信息

| 项目 | 内容 |
|---|---|
| 目录 | `demo2-jpa` |
| 场景 | 把 Demo 1 的内存 `Map` 换成真正的数据库持久化，**接口契约不变**（前端无感知） |
| 技术栈 | Spring Data JPA（底层 Hibernate）、H2 内存数据库 |
| 新增依赖 | `spring-boot-starter-data-jpa`、`com.h2database:h2` |
| 分层 | `controller` / `service` / `dto` / `model` / **`repository`**（新增） |
| 接口 | 与 Demo 1 完全一致：`GET /api/books`、`GET /api/books/{id}`、`POST /api/books` |
| 运行 | `cd demo2-jpa && mvn spring-boot:run` |

### 关键类

- `Book`（model）：加了 `@Entity`、`@Table(name="books")`、`@Id`、`@GeneratedValue(IDENTITY)`，从 POJO 变成 **JPA 实体**
- `BookRepository`：**空接口**继承 `JpaRepository<Book, Long>`，一行代码不用写，Spring 自动生成增删改查
- `BookService`：删掉手写 `Map`/`AtomicLong`，改为调用 repository
- `application.yml`：H2 数据源、`ddl-auto: update`、H2 控制台

### 数据库配置说明（application.yml）

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:bookdb    # H2 内存库，应用一停数据就没了
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: update          # 根据实体自动建表/更新表
    show-sql: true              # 控制台打印 SQL
  h2:
    console:
      enabled: true             # 网页控制台在 /h2-console
```

### 启动日志里两行关键输出

```
Found 1 JPA repository interface.        ← Spring 自动为 BookRepository 生成代理实现
Hibernate: create table books (... identity, primary key (id))   ← 自动建表
```

### 复习题与答案

**1. 空的 `JpaRepository` 接口，为什么 `findAll()`/`save()`/`findById()` 就能用？**

- Spring Data 启动时扫描到 `BookRepository` 接口，在**运行时动态生成代理实现**（动态代理 + 反射）
- 它按"接口 + 方法名"自动翻译成 SQL，这就是为什么**接口不用写实现**

**2. 实体 vs 表？不写 `@Table(name="books")` 表名会是什么？`@Id`、`@GeneratedValue` 是什么？**

- 不写 `@Table`，默认表名 = 实体类名（Spring Boot 命名策略下 `Book` → `book`）
- `@Id` 必须有：标识该字段是**主键**，对应数据库表的主键列
- `@GeneratedValue(IDENTITY)`：主键由**数据库自增**生成（H2/MySQL 的 `AUTO_INCREMENT`）

**3. JPA 为什么强制要求无参构造器？只写有参构造器会怎样？**

- JPA 规范要求实体必须有无参构造器：从数据库查出一行时，Hibernate 要先**用反射造出一个空对象**，再把列值填进字段
- 只写有参构造器（像 Demo 1 的 Book），加载实体时会**直接报错**（无法实例化）

**4. `ddl-auto: update` 是什么？生产环境为什么不能用？**

- `update`：Hibernate 每次启动时对比实体和表结构，**自动改表**
- 生产禁用原因：**无法删列**、schema 变化不可控、容易误删数据、多人协作演进困难
- 生产应该用 **Flyway** 之类的迁移工具：把建表/改表写成带版本号的 SQL 脚本（如 `V1__create_books.sql`），顺序执行、可追踪、可回滚

**5. Demo1→Demo2 持久化从 Map 换成数据库，Controller/前端完全没改，证明什么？**

- 证明了**依赖倒置**和**分层解耦**的价值
- 稳定边界是 **Service 接口 + Repository 抽象**（依赖抽象，不依赖具体实现）
- 底层（Map / H2 / MySQL）可替换，上层（Controller、前端）不动

**6. H2 控制台怎么用？**

1. 打开 `http://localhost:8080/h2-console`
2. JDBC URL 填 `jdbc:h2:mem:bookdb`（就是 `application.yml` 里 `url:` 那行）
3. 用户名 `sa`，密码留空，点 Connect
4. 左侧展开 `BOOKS` 表 → 执行 `SELECT * FROM BOOKS` 查看数据

**7. 为什么不直接学 MySQL 而先用 H2？两者关键差异？**

| 维度 | H2 内存库 | MySQL |
|---|---|---|
| 存储 | 数据在 JVM/进程内 | 数据落盘到文件 |
| 生命周期 | 应用一停数据就没了 | 跨重启持久 |
| 并发 | 单进程访问 | 多进程/多机并发访问 |
| 适用 | 学习、单元测试、快速原型 | 生产标准 |

- 好消息：**JPA/Spring Data 这套 API 对 H2 和 MySQL 完全一样**，换库只改 `application.yml` 里的 driver/url 那几行

---

## Demo 3：参数校验 + 全局异常处理

### 基础信息

| 项目 | 内容 |
|---|---|
| 目录 | `demo3-validation` |
| 场景 | 拦截非法输入（书名空、价格为负等），统一、结构化地返回错误 |
| 技术栈 | `spring-boot-starter-validation`（Bean Validation）、`@RestControllerAdvice` |
| 新增 | `exception` 包：`GlobalExceptionHandler`、`BookNotFoundException`；`dto/ErrorResponse` |
| 接口 | 与 Demo 1/2 一致，但错误响应从"裸奔"变成统一 JSON |

### 关键类

- `BookRequest`（DTO）：加 `@NotBlank`、`@NotNull`、`@Positive` 校验注解
- `BookController`：入参加 `@Valid`，`getById` 不再自己拼 404（交给 Service 抛异常）
- `BookService.findById`：找不到时抛 `BookNotFoundException`（不再上抛 `Optional`）
- `GlobalExceptionHandler`：`@RestControllerAdvice` + `@ExceptionHandler`，统一 400/404/500
- `ErrorResponse`：统一错误体 `{status, message, errors[]}`

### 行为验证（实测结果）

```
POST /api/books  {"title":"Spring 源码",...}   → 201 + 新增的书
POST /api/books  {"title":"","author":"张三",...} → 400 + {"status":400,"message":"参数校验失败","errors":["title: 书名不能为空"]}
POST /api/books  {"price":0}                    → 400 + {"errors":["price: 价格必须大于 0"]}
GET  /api/books/999                             → 404 + {"status":404,"message":"图书不存在: id=999","errors":[]}
```

### 复习题与答案

**1. 校验发生在哪一层？忘加 `@Valid` 会怎样？**

- 校验在 **Controller 边界**触发：`@Valid` 让 Spring 在反序列化 `BookRequest` 后自动执行校验注解，失败抛 `MethodArgumentNotValidException`
- **忘加 `@Valid`，注解完全不生效**——校验注解只是声明，必须由 `@Valid` 触发才有意义

**2. `@RestControllerAdvice` 是什么？和 `@RestController` 什么关系？**

- `@RestControllerAdvice` = 对**所有 `@RestController` 生效的全局增强**（切面）
- 里面用 `@ExceptionHandler(某异常)` 声明"这个方法专门处理哪类异常"
- 作用：把 Controller 里的 try/catch 全部抽掉——Controller 只负责正常流程，异常统一兜底

**3. 为什么用自定义异常 `BookNotFoundException`，而不是 Controller 里 `if (book == null) return 404`？**

- 关键：**Service 层不该依赖 HTTP 语义**（不该知道 404 是什么）
- Service 只表达业务事实："这个 id 的图书不存在"，抛业务异常
- HTTP 状态码是**展示层（Advice）的事**，由它统一映射
- 好处：业务逻辑可复用（比如其他调用方也捕获这个异常）、职责清晰

**4. HTTP 语义（400/404/500）应该由谁决定？三层责任边界？**

- **由 Advice（全局异常处理器）决定**
- `Controller`：接收请求、解析参数、转发给 Service，不写业务判断
- `Service`：业务逻辑，抛业务异常（`BookNotFoundException` 等），**不知道 HTTP**
- `Advice`：捕获异常，映射成状态码 + 统一错误体

**5. DTO 里嵌套子对象（如 `OrderRequest` 里有个 `Address`），怎么校验 `Address.city`？**

- 在子对象字段上加 **`@Valid`** 实现级联校验：`@Valid private Address address;`
- `@Valid` 会"走进"这个字段，递归校验里面对象的注解
- （注意：`@Validated` 也能用于方法级参数校验等场景）

**6. 为什么"后端给结构化错误、前端只负责展示"比"后端拼好一句中文返回"更好？**

- **多端复用**：Web、App、小程序可以各自按需渲染，后端不用为每种客户端写不同文案
- **国际化 i18n**：前端拿到 `errors` 的字段名+错误码，可以自己翻译成不同语言
- **语义化**：`{field: "title", message: "书名不能为空"}` 便于排查和自动化处理，而拼好的一句话无法被程序区分是哪里的错

---

## Demo 4：JPA 自定义查询与分页

### 基础信息

| 项目 | 内容 |
|---|---|
| 目录 | `demo4-query` |
| 场景 | 图书搜索与分页：按书名模糊搜、按作者查、多字段搜、分页 |
| 技术栈 | Spring Data JPA 的三种查询能力（方法名派生 / `@Query` / `Pageable`） |
| 新增 | `config/DataSeeder`（启动种子数据 8 本书）、`@Query`、`Pageable`，前端加了搜索框 |
| 接口 | `GET /api/books?keyword=`、`GET /api/books/by-author?author=`、`GET /api/books/search?kw=`、`GET /api/books/paged?page=&size=` |

### 三把钥匙（核心知识点）

**第 1 把：方法名派生查询** —— 方法名本身就是 SQL 说明书
```java
List<Book> findByTitleContainingIgnoreCase(String keyword);
// → select * from books where lower(title) like concat('%', lower(?), '%')
```
- 拆解：`findBy`(查) + `Title`(字段) + `Containing`(包含=LIKE %kw%) + `IgnoreCase`(忽略大小写)
- 常见组合：`And`/`Or`/`GreaterThan`/`LessThan`/`OrderBy`/`Top10` 等

**第 2 把：`@Query` 手写 JPQL** —— 复杂/多字段查询用注解写
```java
@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE ... OR LOWER(b.author) LIKE ...")
List<Book> search(@Param("kw") String kw);
```
- `:kw` 是命名参数，`@Param` 绑定
- JPQL 面向**实体和字段**（`b.title`）而非数据库表列

**第 3 把：`Pageable` 分页** —— 不用自己拼 LIMIT/OFFSET
```java
Page<Book> findAll(Pageable pageable);   // 由 JpaRepository 提供
```
- Controller 用 `PageRequest.of(page, size)` 构造
- 返回 `Page`，自带 `content`(本页)、`totalElements`(总条数)、`totalPages`(总页数)
- 实测：`page=0&size=3` → 本页 3、总 8、共 3 页

### 种子数据

- `DataSeeder` 实现 `CommandLineRunner`：Spring 容器就绪后自动执行
- 用 `repository.count() > 0` 判断表为空才插入，避免重复种子

### 复习题与答案

**1. `findByTitleContainingIgnoreCase` 到底怎么翻译成 SQL？方法名有哪些"语法"？**

- 分为三段：`findBy` + 字段名 `Title` + 修饰词 `Containing`、`IgnoreCase`
- 每个部分都是有意义的约定：`Containing`→`LIKE %kw%`、`IgnoreCase`→`lower()`
- 更多修饰词：`And`/`Or`/`GreaterThan`/`LessThanEqual`/`Between`/`OrderByXxxDesc`/`Top3`
- 方法名写错/字段名对不上，启动时 Spring Data 会**直接甩异常**提示你（不是在运行时才挂）——这是个贴心特性

**2. 派生查询和 `@Query` 各自适合什么场景？**

- **派生查询**：简单单条件（按字段查/模糊/排序），方法名清晰可读
- **`@Query`**：多字段关联（标题 OR 作者）、复杂条件、需要写原生 SQL（`nativeQuery=true`）时
- 原则：`@Query` 里的 JPQL 写明文 SQL，维护成本高，能派生就用派生，复杂才手写

**3. 为什么返回 `Page<Book>` 而不是 `List<Book>`？`Page` 对象里有什么？**

- 分页不仅要"这一页的数据"，还需要"总共多少条/共几页"供前端渲染分页条
- `Page` 自带：`content`(本页数据)、`totalElements`(总条数)、`totalPages`(总页数)、`number`(当前页码)、`size`(页大小)
- 前端拿到 `content` 渲染列表、拿 `totalPages` 做页码分页控件

**4. `page` 和 `size` 从哪来？`PageRequest.of(page, size)` 的 `page` 从 0 还是 1 开始？**

- 从 URL 查询参数来：`/api/books/paged?page=0&size=3`
- `PageRequest.of` 的 **page 从 0 开始**（第 1 页是 page=0）——这是 Spring Data 的约定，容易和"第 1 页=1"的习惯混淆

**5. 为什么用 `CommandLineRunner` 做种子数据？怎么避免每次启动都重复插入？**

- `CommandLineRunner` 在 Spring 容器初始化完成后、应用真正对外服务前执行一次，适合初始化种子数据、预热缓存等
- 用 `repository.count() > 0` 判断：表非空就跳过，保证只插一次

**6. 前端搜索是怎么"打通"的？**

- 搜索框输入后 `fetch('/api/books?keyword=' + encodeURIComponent(kw))`
- 后端 `list(@RequestParam(required=false) String keyword)`：没传就查全部，传了就按标题模糊搜
- `encodeURIComponent` 编码中文关键词，避免 URL 里直接塞汉字导致乱码

---

## 速查表：三层架构与关键注解

| 层/注解 | 职责 | 说明 |
|---|---|---|
| `@Controller` | 接收 HTTP 请求 | 返回视图（配合模板），或加 `@ResponseBody` 返回数据 |
| `@RestController` | 接收 HTTP 请求并返回 JSON | = `@Controller` + `@ResponseBody`，前后端分离常用 |
| `@Service` | 业务逻辑 | 继承自 `@Component` |
| `@Repository` | 数据访问 | 继承自 `@Component`，额外开启持久层异常转译 |
| `@Component` | 通用 Bean | 三者共同的元注解 |
| `@Entity` | JPA 实体 | 对应数据库表 |
| `@Id` + `@GeneratedValue` | 主键自增 | 交给数据库管理 |
| `JpaRepository<T, ID>` | 数据访问接口 | 继承即得 CRUD，Spring 自动生成实现 |
| `@Valid` | 触发校验 | 配合 DTO 上的 `@NotBlank`/`@Positive` 等 |
| `@RestControllerAdvice` | 全局异常处理 | 配合 `@ExceptionHandler` 统一错误响应 |

### 复习记忆口诀

- **三层**：Controller（接客）→ Service（做事）→ Repository（存取），各管各的
- **DTO 守边界**：进（请求体校验）出（不泄露实体），实体不裸奔
- **异常分层**：Service 抛业务异常，Advice 定 HTTP 状态码
- **换库不换码**：JPA API 统一，H2→MySQL 只改配置
