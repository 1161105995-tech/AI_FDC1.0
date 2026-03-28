# 智能档案管理系统

这是一个前后端分离的智能档案管理系统，支持档案创建、查询、归档、借阅、移交、入库上架、流程规则配置，以及 AI/OCR 辅助能力。

## 项目结构

- `backend/`：Spring Boot 后端，Java 17 + MyBatis-Plus
- `frontend/`：Vue 3 + TypeScript + Vite 前端
- `scripts/`：批量导入测试文件等辅助脚本
- `clear-validation-data.ps1`：清理验证数据和存储文件
- `generate-*.ps1` / `create-*.ps1` / `*.py`：生成测试文档、模拟数据的脚本

## 运行环境

建议在 Windows 环境下运行。当前仓库中的启动脚本和配置里包含了较多 Windows 路径。

### 必需组件

- JDK 17
- Maven 3.9+
- Node.js 18+，推荐 20+
- PostgreSQL 14+，默认库名为 `smart_archive`
- Redis 6+

### 可选组件

- Tesseract OCR：如果需要启用 PDF/OCR 识别，请安装并配置
- Neo4j：知识图谱能力默认关闭，非必需

## 配置说明

### 后端配置

后端配置文件：`backend/src/main/resources/application.yml`

默认配置如下：

- 后端端口：`8080`
- PostgreSQL：`jdbc:postgresql://localhost:5432/smart_archive`
- Redis：`localhost:6379`
- OCR：默认开启
- Tesseract 路径：`C:/Program Files/Tesseract-OCR/tesseract.exe`
- OCR 语言包目录：`D:/AI project/AI-search/ocr/tessdata`

如果你的电脑路径不同，请在启动前修改这些配置：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.data.redis.host`
- `spring.data.redis.port`
- `archive.ocr.tesseract-path`
- `archive.ocr.tessdata-dir`

### 前端配置

前端接口地址写在：`frontend/src/api/http.ts`

默认请求后端地址：

- `http://localhost:8080`

如果你修改了后端端口或部署地址，请同步修改这里的 `baseURL`。

## 快速启动

### 1. 准备数据库和缓存

先启动 PostgreSQL 和 Redis，并创建数据库：

```sql
CREATE DATABASE smart_archive;
```

如果你准备开启 OCR，还需要确认 Tesseract 已安装，并且 `tessdata` 目录可用。

### 2. 启动后端

进入 `backend/` 目录后执行：

```bash
mvn spring-boot:run
```

或者直接双击/运行：

- `backend/start-backend.cmd`

后端启动成功后，默认监听：

- `http://localhost:8080`

### 3. 启动前端

进入 `frontend/` 目录后执行：

```bash
npm install
npm run dev
```

或者直接运行：

- `frontend/start-frontend.cmd`

前端默认地址：

- `http://localhost:5173`

### 4. 打开系统

启动完成后，先打开前端地址，再通过页面访问各个模块。

## 常用脚本

### 数据清理

`clear-validation-data.ps1` 用于清理验证数据、归档文件和部分流程相关数据。它适合在重复测试前使用。

常见用法：

```powershell
.\clear-validation-data.ps1
```

### 批量导入测试文件

`scripts/run-bulk-import-test-files.ps1` 会调用 `scripts/bulk_import_archives.py` 批量导入测试文件，并把日志写到 `import-results/`。

常见用法：

```powershell
.\scripts\run-bulk-import-test-files.ps1
```

如果你希望前台执行，可以加 `-Foreground`。

### 测试文档生成

仓库根目录下还有一些生成测试文件的脚本，例如：

- `generate-documents.ps1`
- `generate-documents-en.ps1`
- `generate-formal-documents.ps1`
- `generate_final_documents.py`
- `generate_chinese_documents.py`
- `create-files.ps1`

这些脚本主要用于生成测试样本，不是系统运行所必需。

## 首次部署建议

1. 先确认 PostgreSQL、Redis、JDK、Node.js 都已安装。
2. 修改 `backend/src/main/resources/application.yml` 中的数据库、Redis 和 OCR 路径。
3. 修改 `frontend/src/api/http.ts` 中的后端地址。
4. 先启动后端，再启动前端。
5. 如果页面报接口错误，优先检查后端是否已启动，以及前端 `baseURL` 是否正确。

## 常见问题

### 1. 后端连不上数据库

- 确认 PostgreSQL 已启动
- 确认数据库 `smart_archive` 已创建
- 确认用户名和密码与 `application.yml` 一致

### 2. 前端请求失败

- 确认后端是否运行在 `8080`
- 确认 `frontend/src/api/http.ts` 的 `baseURL` 是否正确
- 检查浏览器控制台和后端日志

### 3. OCR 不生效

- 确认 Tesseract 已安装
- 确认 `archive.ocr.tesseract-path` 指向可执行文件
- 确认 `archive.ocr.tessdata-dir` 下有语言包

### 4. 数据库表没有自动创建

仓库中保留了数据库迁移 SQL 文件，路径在：

- `backend/src/main/resources/db/migration/`

如果你的本地环境没有自动执行迁移，请根据文件顺序手动导入，或者在项目中补充迁移工具后再启动。

## 说明

当前仓库的脚本和配置大多以本地开发为主，默认适合在同一台 Windows 机器上同时运行后端、前端、数据库和 Redis。若要发布到服务器，建议进一步把数据库地址、OCR 路径和前端接口地址改成环境变量或配置文件注入。
