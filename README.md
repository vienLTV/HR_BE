# Cetus Backend (Cetus_BE)

Hệ thống Backend cho dự án **Cetus – Human Resource Management (HRM)**

---

## 📋 Tổng quan

Cetus Backend là RESTful API phục vụ hệ thống quản lý nhân sự, xây dựng bằng **Quarkus (Java 21)**, sử dụng **PostgreSQL**, **Liquibase** và **JWT Authentication** với phân quyền theo vai trò.

### Chức năng chính

- Quản lý nhân viên, phòng ban, team, chức danh
- Quản lý thông tin cá nhân, tài khoản ngân hàng, chứng chỉ
- Xác thực & phân quyền người dùng bằng JWT
- Audit lịch sử thay đổi dữ liệu (Hibernate Envers)

---

## 🧱 Công nghệ sử dụng

- **Framework**: Quarkus 3.x (Java 21)
- **ORM**: Hibernate ORM with Panache
- **Database**: PostgreSQL (Docker)
- **Migration**: Liquibase
- **Security**: SmallRye JWT (Bearer Token)
- **Audit**: Hibernate Envers
- **Build Tool**: Maven
- **API Docs**: OpenAPI / Swagger UI
- **DevOps**: Docker, Jenkins, GitLab CI/CD

---

## 🏗️ Kiến trúc Backend

```text
Client (Frontend - Next.js)
        |
        |  REST API + JWT
        v
Controller (REST Layer)
        |
        v
Service (Business Logic)
        |
        v
Repository (Panache ORM)
        |
        v
PostgreSQL Database
```

## 📂Cấu trúc thư mục

```text
cetus-core-master/
├── src/main/java/org/microboy
│   ├── config/            # Cấu hình ứng dụng (CORS, Beans)
│   ├── constants/         # Hằng số dùng chung
│   ├── dto/               # Data Transfer Objects
│   ├── entity/            # JPA Entities
│   ├── enums/             # Enum (Status, Role, ...)
│   ├── exception/         # Custom Exceptions
│   ├── repository/        # Panache Repositories
│   ├── rest/              # REST Controllers
│   ├── security/          # Authentication & Authorization (JWT)
│   └── service/           # Business Logic Services
│
├── src/main/resources
│   ├── application.properties   # Cấu hình DB, JWT, CORS
│   ├── privateKey.pem            # JWT private key
│   ├── publicKey.pem             # JWT public key
│   └── db/
│       └── changelog/            # Liquibase changelog
│
├── docker-compose.yml     # PostgreSQL Docker config
├── Jenkinsfile            # Jenkins CI/CD pipeline
├── deploy-dev.yml         # GitLab CI/CD
├── pom.xml                # Maven dependencies
└── README.md
```

## 🔐 Bảo mật & Phân quyền

### Xác thực

- Sử dụng JWT Bearer Token
- Token được sinh khi đăng nhập thành công
- Token được gửi qua header:

```text
Authorization: Bearer <token>
```

### Phân quyền (Roles)

- OWNER – Chủ tổ chức
- ADMIN – Quản trị viên
- MANAGER – Quản lý

### Cơ chế

- Backend verify JWT
- Kiểm tra role thông qua:
  - @RolesAllowed
  - Custom annotation @OwnerAdminManagerAllowed

## 🌐 API chính

| Method              | Endpoint          | Mô tả                   |
| ------------------- | ----------------- | ----------------------- |
| POST                | /auth/login       | Đăng nhập               |
| POST                | /sign-up          | Đăng ký tổ chức + owner |
| GET/POST/PUT/DELETE | /employees        | CRUD nhân viên          |
| GET/POST            | /job-titles       | Quản lý chức danh       |
| GET/POST            | /teams            | Quản lý team            |
| GET/POST            | /departments      | Quản lý phòng ban       |
| GET/POST            | /bank-accounts    | Tài khoản ngân hàng     |
| GET/POST            | /certificates     | Chứng chỉ               |
| GET                 | /employee-history | Lịch sử thay đổi        |

## 🗄️ Database & Migration

- Database: PostgreSQL
- Migration tự động bằng Liquibase
- Changelog chính:
  - masterChangeLog.xml
  - changeLog.xml
  - auditChangeLog.xml
- Liquibase sẽ tự tạo bảng khi ứng dụng khởi động.

## ⚙️ Yêu cầu môi trường

- Java JDK 21
- Docker Desktop
- Git
- ❌ Không cần cài PostgreSQL local

## ▶️ Cách chạy Backend (Local)

### 1. Khởi động PostgreSQL bằng Docker

Trong thư mục cetus-core-master, chạy:

```text
docker compose up -d
```

📌 Lệnh này sẽ:
-Khởi động container PostgreSQL

- Map cổng database: 5431
- Tạo database cetus_core (nếu chưa tồn tại)
- Lưu dữ liệu vào Docker volume (cetus_pg_data)

### 2️⃣ Chạy Backend bằng Maven (Quarkus Dev Mode)

Trong cùng thư mục cetus-core-master, chạy:

```text
./mvnw quarkus:dev
```

Quarkus sẽ:

- Kết nối tới PostgreSQL đang chạy
- Tự động migrate database bằng Liquibase
- Khởi động server ở chế độ dev (hot reload)

### 3️⃣ Truy cập ứng dụng
- Backend API:
👉 http://localhost:8080
- Swagger UI:
👉 http://localhost:8080/q/swagger-ui