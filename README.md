# Cetus Backend - Human Resource Management System

> Backend API cho hệ thống Quản lý Nhân sự Cetus được xây dựng với Quarkus, PostgreSQL và JWT Authentication

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.11.1-blue.svg)](https://quarkus.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 Tổng quan

**Cetus Backend** là hệ thống RESTful API phục vụ ứng dụng Quản lý Nhân sự (HRM - Human Resource Management), cung cấp đầy đủ các tính năng quản lý tổ chức, nhân viên, chấm công, nghỉ phép, lương bổng và phân quyền người dùng.

### Tính năng chính

#### 🏢 Quản lý Tổ chức & Phân quyền

- Đăng ký và quản lý tổ chức (Organization)
- Quản lý tài khoản người dùng với 4 cấp độ vai trò: OWNER, ADMIN, MANAGER, USER
- Xác thực và phân quyền bằng JWT (JSON Web Token) với mã hóa RS256
- Audit trail tự động ghi lại mọi thay đổi dữ liệu (Hibernate Envers)

#### 👥 Quản lý Nhân viên

- CRUD hồ sơ nhân viên (employee profile)
- Quản lý thông tin cá nhân, liên hệ khẩn cấp
- Quản lý chứng chỉ (certificates) và tài khoản ngân hàng
- Upload và quản lý ảnh đại diện (avatar)
- Lịch sử thay đổi nhân viên với audit log chi tiết

#### 🏗️ Quản lý Cơ cấu Tổ chức

- Quản lý phòng ban (Department) với cấu trúc phân cấp
- Quản lý nhóm/team và thành viên
- Quản lý chức danh (Job Title) và phân công

#### ⏰ Chấm công & Nghỉ phép

- Ghi nhận chấm công hàng ngày (check-in/check-out)
- Quản lý yêu cầu nghỉ phép với quy trình phê duyệt
- Báo cáo và thống kê chấm công theo nhân viên/team

#### 💰 Quản lý Lương

- Tính lương theo kỳ (tháng/năm)
- Quản lý lương cơ bản, thưởng, khấu trừ
- Theo dõi trạng thái chi trả
- Báo cáo lương theo nhân viên, team

---

## 🛠️ Công nghệ Sử dụng

### Core Framework & Language

- **Java 21** - Ngôn ngữ lập trình
- **Quarkus 3.11.1** - Framework backend hiệu năng cao, hỗ trợ native compilation
- **Maven** - Build tool và quản lý dependencies

### Database & ORM

- **PostgreSQL 16** - Hệ quản trị cơ sở dữ liệu quan hệ
- **Hibernate ORM with Panache** - Object-Relational Mapping
- **Liquibase** - Database migration và version control
- **Hibernate Envers** - Audit logging tự động

### Security & Authentication

- **SmallRye JWT** - JWT authentication và authorization
- **RS256 Algorithm** - Mã hóa JWT với public/private key
- **RBAC** - Role-Based Access Control

### API Documentation

- **OpenAPI 3.0** - Chuẩn mô tả API
- **Swagger UI** - Giao diện tương tác với API

### DevOps & Deployment

- **Docker & Docker Compose** - Container hóa ứng dụng
- **Jenkins** - CI/CD pipeline
- **GitLab CI/CD** - Alternative CI/CD option

### Development Tools

- **Lombok** - Giảm boilerplate code
- **JUnit 5** - Unit testing
- **REST Assured** - API integration testing

---

## 🏗️ Kiến trúc Hệ thống

### Mô hình 3 tầng (Three-tier Architecture)

```
┌─────────────────────────────────────────────────────────┐
│              Frontend (Next.js 14)                      │
│         http://localhost:3000                           │
└─────────────────────────┬───────────────────────────────┘
                          │
                          │ HTTP/REST + JWT Bearer Token
                          │
┌─────────────────────────▼───────────────────────────────┐
│                 REST API Layer                          │
│              (JAX-RS Resources)                         │
│  - CORS Filter                                          │
│  - JWT Authentication Filter                            │
│  - Exception Handlers                                   │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│               Service Layer                             │
│          (Business Logic)                               │
│  - Validation                                           │
│  - Authorization (Role check)                           │
│  - Transaction Management                               │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│            Repository Layer                             │
│         (Panache Repository)                            │
│  - CRUD Operations                                      │
│  - Custom Queries                                       │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│           PostgreSQL Database                           │
│         jdbc:postgresql://localhost:5431/cetus_core     │
│  - Tables                                               │
│  - Audit Tables (*_aud)                                 │
│  - Indexes & Constraints                                │
└─────────────────────────────────────────────────────────┘
```

### Luồng xử lý Request

```
Client Request
    ↓
CORS Filter → JWT Validation → Role Check
    ↓
REST Controller (Endpoint)
    ↓
Service (Business Logic + Validation)
    ↓
Repository (Data Access via Panache)
    ↓
PostgreSQL Database
    ↓
Envers Audit (Tự động ghi log)
    ↓
Response (JSON) → Client
```

---

## 📂 Cấu trúc Thư mục

```
cetus-core-master/
│
├── src/main/
│   ├── java/org/microboy/
│   │   ├── config/              # Cấu hình ứng dụng
│   │   │   ├── CorsFilter.java
│   │   │   └── BeanConfig.java
│   │   │
│   │   ├── constants/           # Hằng số toàn hệ thống
│   │   │   ├── ErrorMessages.java
│   │   │   └── ApiPaths.java
│   │   │
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── request/         # Request DTOs
│   │   │   └── response/        # Response DTOs
│   │   │
│   │   ├── entity/              # JPA Entities
│   │   │   ├── EmployeeCore.java
│   │   │   ├── Organization.java
│   │   │   ├── User.java
│   │   │   ├── Department.java
│   │   │   ├── Team.java
│   │   │   ├── Attendance.java
│   │   │   ├── LeaveRequest.java
│   │   │   ├── Salary.java
│   │   │   └── ...
│   │   │
│   │   ├── enums/               # Enumerations
│   │   │   ├── EmployeeStatus.java
│   │   │   ├── Role.java
│   │   │   └── AttendanceStatus.java
│   │   │
│   │   ├── exception/           # Custom Exceptions
│   │   │   ├── BusinessException.java
│   │   │   ├── NotFoundException.java
│   │   │   └── UnauthorizedException.java
│   │   │
│   │   ├── repository/          # Panache Repositories
│   │   │   ├── EmployeeRepository.java
│   │   │   ├── UserRepository.java
│   │   │   └── ...
│   │   │
│   │   ├── rest/                # REST Controllers (JAX-RS)
│   │   │   ├── AuthResource.java
│   │   │   ├── EmployeeResource.java
│   │   │   ├── DepartmentResource.java
│   │   │   ├── AttendanceResource.java
│   │   │   ├── LeaveResource.java
│   │   │   └── SalaryResource.java
│   │   │
│   │   ├── security/            # Security & JWT
│   │   │   ├── JwtService.java
│   │   │   ├── PasswordEncoder.java
│   │   │   └── RoleValidator.java
│   │   │
│   │   └── service/             # Business Logic Services
│   │       ├── EmployeeService.java
│   │       ├── AuthService.java
│   │       ├── AttendanceService.java
│   │       └── ...
│   │
│   └── resources/
│       ├── application.properties  # Cấu hình chính
│       ├── privateKey.pem          # JWT Private Key (RS256)
│       ├── publicKey.pem           # JWT Public Key (RS256)
│       │
│       └── db/                     # Liquibase Changelogs
│           ├── masterChangeLog.xml
│           ├── changeLog.xml              # Schema chính
│           ├── auditChangeLog.xml         # Audit tables
│           ├── leaveRequestChangeLog.xml  # Nghỉ phép
│           ├── salaryChangeLog.xml        # Lương
│           └── employeeHistoryAuditChangeLog.xml
│
├── src/test/                    # Unit & Integration Tests
│   └── java/org/microboy/
│
├── src/main/docker/             # Dockerfiles
│   ├── Dockerfile.jvm           # JVM mode
│   ├── Dockerfile.native        # Native mode (GraalVM)
│   └── Dockerfile.legacy-jar
│
├── docker-compose.yml           # PostgreSQL container (Dev)
├── docker-compose.prod.yml      # Production compose
├── pom.xml                      # Maven dependencies
├── Jenkinsfile                  # CI/CD pipeline
├── deploy-dev.yml               # GitLab CI config
└── README.md
```

---

## 🗄️ Mô hình Cơ sở Dữ liệu

### Các bảng chính

#### 🏢 Organization & Users

- `organization` - Tổ chức
- `users` - Tài khoản đăng nhập
- `users_roles` - Vai trò người dùng (OWNER/ADMIN/MANAGER/USER)

#### 👥 Employee Management

- `employee_core` - Thông tin nhân viên chính
- `employee_profile` - Ảnh đại diện và metadata
- `employee_history` - Lịch sử thay đổi trường dữ liệu
- `certificate` - Chứng chỉ
- `bank_account` - Tài khoản ngân hàng
- `emergency_contact` - Liên hệ khẩn cấp

#### 🏗️ Organization Structure

- `department` - Phòng ban (hỗ trợ phân cấp)
- `team` - Nhóm/Team
- `team_member` - Thành viên team
- `job_title` - Chức danh
- `employee_job_title` - Ánh xạ nhân viên - chức danh

#### ⏰ Attendance & Leave

- `attendance` - Chấm công (unique: org + employee + date)
- `leave_requests` - Yêu cầu nghỉ phép

#### 💰 Payroll

- `salary` - Bảng lương theo kỳ

#### 📊 Audit & History

- `*_aud` tables - Các bảng audit (Envers)
- `cetus_core_revinfo` - Thông tin revision

### Quan hệ chính

```
organization (1) ──────────(N) employee_core
organization (1) ──────────(N) users
employee_core (1) ─────────(1) employee_profile
employee_core (1) ─────────(1) users
employee_core (1) ─────────(N) certificate
employee_core (1) ─────────(N) bank_account
employee_core (1) ─────────(N) attendance
employee_core (1) ─────────(N) leave_requests
employee_core (1) ─────────(N) salary
department (1) ────────────(N) team
team (N) ──────────────────(N) employee_core (qua team_member)
job_title (N) ─────────────(N) employee_core (qua employee_job_title)
```

### Database Migration (Liquibase)

Liquibase tự động chạy khi khởi động ứng dụng:

- Tạo schema nếu chưa tồn tại
- Áp dụng các changesets mới
- Hỗ trợ rollback và versioning

**Changelogs:**

- `masterChangeLog.xml` - Master file, include tất cả sub-changelogs
- `changeLog.xml` - Schema chính (organization, employee, department...)
- `auditChangeLog.xml` - Audit tables (\*\_aud)
- `employeeHistoryAuditChangeLog.xml` - Employee audit refactoring
- `leaveRequestChangeLog.xml` - Leave management
- `salaryChangeLog.xml` - Payroll system

---

## 🔐 Bảo mật & Phân quyền

### JWT Authentication (RS256)

**Cấu trúc JWT:**

```json
{
  "iss": "cetus",
  "sub": "user@example.com",
  "groups": ["OWNER", "ADMIN"],
  "exp": 1642345678,
  "organizationId": "uuid-here"
}
```

**Quy trình xác thực:**

1. Client gửi request với header: `Authorization: Bearer <JWT>`
2. Backend verify signature bằng `publicKey.pem`
3. Kiểm tra issuer = `cetus`
4. Kiểm tra expiration (mặc định: 3600s = 1h)
5. Trích xuất role từ claim `groups`

### Role-Based Access Control (RBAC)

| Role        | Quyền hạn                                                                                                      |
| ----------- | -------------------------------------------------------------------------------------------------------------- |
| **OWNER**   | - Toàn quyền quản lý tổ chức<br>- Tạo/xóa tài khoản<br>- CRUD mọi module<br>- Không tự xóa tài khoản owner     |
| **ADMIN**   | - Tương tự OWNER<br>- CRUD nhân viên, phòng ban, team<br>- Duyệt nghỉ phép, quản lý lương<br>- Không xóa owner |
| **MANAGER** | - Xem nhân viên (read-only)<br>- Quản lý team<br>- Duyệt nghỉ phép<br>- Xem báo cáo chấm công/lương            |
| **USER**    | - Xem/sửa hồ sơ cá nhân<br>- Chấm công cá nhân<br>- Gửi yêu cầu nghỉ phép<br>- Xem lương cá nhân               |

### Kiểm tra phân quyền trong Code

**Annotation-based:**

```java
@GET
@Path("/employees")
@RolesAllowed({"OWNER", "ADMIN", "MANAGER"})
public Response getEmployees() { ... }
```

**Programmatic check:**

```java
if (!securityIdentity.hasRole("OWNER")) {
    throw new UnauthorizedException("Only OWNER can perform this action");
}
```

### CORS Configuration

Cho phép Frontend truy cập từ:

- `http://localhost:3000` (Development)
- `http://cetus.site` (Production)

Headers được phép:

- `accept`, `authorization`, `content-type`, `x-requested-with`

Methods được phép:

- `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`

### Security Best Practices

✅ **Đã áp dụng:**

- JWT với RS256 (không lưu secret trong code)
- Password hashing (BCrypt/PBKDF2)
- CORS whitelist
- Input validation
- SQL injection prevention (ORM)
- Audit logging mọi thay đổi

⚠️ **Khuyến nghị Production:**

- Externalize private key ra ngoài resources
- Sử dụng HTTPS/TLS
- Rate limiting cho API
- Implement refresh token mechanism
- Regular security audits

---

## 🌐 API Documentation

### Swagger UI

Truy cập tài liệu API tương tác tại:

```
http://localhost:8080/q/swagger-ui
```

### Các nhóm API chính

#### 1. Authentication & Authorization

```
POST   /auth/login          # Đăng nhập, trả về JWT
POST   /sign-up              # Đăng ký tổ chức mới (tạo OWNER)
GET    /sign-up/current      # Lấy thông tin owner hiện tại
```

#### 2. User Management

```
POST   /users                # Tạo tài khoản USER (OWNER/ADMIN only)
GET    /users                # Danh sách users
GET    /users/{email}        # Chi tiết user
PUT    /users/{email}        # Cập nhật user
DELETE /users/{email}        # Xóa user
```

#### 3. Employee Management

```
GET    /employees            # Danh sách nhân viên
POST   /employees            # Tạo nhân viên mới
GET    /employees/{id}       # Chi tiết nhân viên
PUT    /employees/{id}       # Cập nhật nhân viên
DELETE /employees/{id}       # Xóa nhân viên
GET    /employee-history     # Lịch sử thay đổi
```

#### 4. Organization Structure

```
# Departments
GET    /departments
POST   /departments
GET    /departments/{id}
PUT    /departments/{id}
DELETE /departments/{id}

# Teams
GET    /teams
POST   /teams
GET    /teams/{id}
PUT    /teams/{id}
DELETE /teams/{id}

# Job Titles
GET    /job-titles
POST   /job-titles
GET    /job-titles/{id}
PUT    /job-titles/{id}
DELETE /job-titles/{id}
```

#### 5. Employee Documents

```
GET    /certificates         # Danh sách chứng chỉ
POST   /certificates         # Thêm chứng chỉ
GET    /bank-accounts        # Danh sách tài khoản ngân hàng
POST   /bank-accounts        # Thêm tài khoản ngân hàng
```

#### 6. Attendance

```
GET    /attendance           # Danh sách chấm công
POST   /attendance           # Ghi nhận chấm công
GET    /attendance/{id}      # Chi tiết
PUT    /attendance/{id}      # Cập nhật
DELETE /attendance/{id}      # Xóa
```

#### 7. Leave Management

```
GET    /leave-requests       # Danh sách yêu cầu nghỉ
POST   /leave-requests       # Tạo yêu cầu nghỉ
GET    /leave-requests/{id}  # Chi tiết
PUT    /leave-requests/{id}  # Duyệt/Từ chối
DELETE /leave-requests/{id}  # Xóa
```

#### 8. Payroll

```
GET    /salary               # Danh sách bảng lương
POST   /salary               # Tạo bảng lương
GET    /salary/{id}          # Chi tiết
PUT    /salary/{id}          # Cập nhật
DELETE /salary/{id}          # Xóa
```

### Ví dụ Request/Response

**Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@company.com",
    "password": "password123"
  }'
```

**Response:**

```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "role": "OWNER"
}
```

**Create Employee (với JWT):**

```bash
curl -X POST http://localhost:8080/employees \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "companyEmail": "john.doe@company.com",
    "jobTitleId": "uuid-here",
    "teamId": "uuid-here"
  }'
```

---

## ⚙️ Cài đặt & Chạy Ứng dụng

### Yêu cầu Môi trường

| Công cụ    | Phiên bản   | Bắt buộc |
| ---------- | ----------- | -------- |
| Java JDK   | 21+         | ✅       |
| Maven      | 3.8+        | ✅       |
| Docker     | 20.10+      | ✅       |
| Git        | 2.x         | ✅       |
| PostgreSQL | 16 (Docker) | ✅       |

❌ **Không cần cài PostgreSQL local** - Sử dụng Docker Compose

### Hướng dẫn Cài đặt (Development)

#### Bước 1: Clone Repository

```bash
git clone <repository-url>
cd cetus-core-master
```

#### Bước 2: Khởi động PostgreSQL

```bash
docker compose up -d
```

**Lệnh này sẽ:**

- Khởi động container PostgreSQL phiên bản 16
- Map port `5431:5432` (tránh conflict với Postgres local)
- Tạo database `cetus_core`
- Tạo user `postgres/postgres`
- Lưu dữ liệu vào volume `cetus_pg_data`

**Kiểm tra container:**

```bash
docker ps
docker logs <container-id>
```

**Dừng container:**

```bash
docker compose down        # Dừng nhưng giữ data
docker compose down -v     # Dừng và xóa volume (mất data)
```

#### Bước 3: Chạy Backend (Dev Mode)

```bash
./mvnw quarkus:dev
```

**Hoặc trên Windows:**

```cmd
mvnw.cmd quarkus:dev
```

**Dev Mode features:**

- ✅ Hot reload tự động khi sửa code
- ✅ Live coding (không cần restart)
- ✅ Dev UI tại http://localhost:8080/q/dev/
- ✅ Continuous testing

**Quarkus sẽ:**

1. Kết nối tới PostgreSQL (localhost:5431)
2. Chạy Liquibase migration (tự động tạo/update schema)
3. Khởi động server tại `http://localhost:8080`
4. Load JWT keys từ resources
5. Enable CORS cho localhost:3000

#### Bước 4: Truy cập Ứng dụng

**Backend API:**

```
http://localhost:8080
```

**Swagger UI (API Docs):**

```
http://localhost:8080/q/swagger-ui
```

**Dev UI (Quarkus Dashboard):**

```
http://localhost:8080/q/dev/
```

**Health Check:**

```bash
curl http://localhost:8080/q/health
```

### Chạy Tests

```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify

# Với coverage
./mvnw test jacoco:report
```

### Build Production JAR

```bash
./mvnw clean package -DskipTests

# Output: target/quarkus-app/
```

### Build Native Executable (GraalVM)

```bash
./mvnw package -Pnative -DskipTests

# Yêu cầu: GraalVM 21+
# Output: target/cetus-core-1.0.0-SNAPSHOT-runner
```

---

## 🚀 Deployment (Production)

### Docker Build

#### JVM Mode (Khuyến nghị)

```bash
docker build -f src/main/docker/Dockerfile.jvm -t cetus-backend:latest .
```

#### Native Mode (Tối ưu hiệu năng)

```bash
docker build -f src/main/docker/Dockerfile.native -t cetus-backend:native .
```

### Docker Compose Production

```bash
docker-compose -f docker-compose.prod.yml up -d
```

**Cấu hình Production:**

```yaml
# docker-compose.prod.yml
version: "3.8"
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: cetus_core
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - cetus-network

  backend:
    image: cetus-backend:latest
    environment:
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:postgresql://postgres:5432/cetus_core
      QUARKUS_DATASOURCE_USERNAME: ${DB_USER}
      QUARKUS_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      QUARKUS_HTTP_CORS_ORIGINS: https://cetus.site
      MP_JWT_VERIFY_PUBLICKEY_LOCATION: /config/publicKey.pem
      SMALLRYE_JWT_SIGN_KEY_LOCATION: /config/privateKey.pem
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    networks:
      - cetus-network
    volumes:
      - ./keys:/config

networks:
  cetus-network:
    driver: bridge

volumes:
  postgres_data:
```

### CI/CD Pipeline

#### Jenkins (Jenkinsfile)

```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -f src/main/docker/Dockerfile.jvm -t cetus-backend:${BUILD_NUMBER} .'
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker-compose -f docker-compose.prod.yml up -d'
            }
        }
    }
}
```

#### GitLab CI (deploy-dev.yml)

```yaml
stages:
  - build
  - test
  - deploy

build:
  stage: build
  script:
    - ./mvnw clean package -DskipTests
  artifacts:
    paths:
      - target/

test:
  stage: test
  script:
    - ./mvnw test

deploy:
  stage: deploy
  script:
    - docker build -t cetus-backend:latest .
    - docker-compose up -d
  only:
    - main
```

### Environment Variables (Production)

Tạo file `.env`:

```bash
# Database
DB_USER=postgres_prod
DB_PASSWORD=secure_password_here
DB_NAME=cetus_core
DB_HOST=postgres
DB_PORT=5432

# JWT
JWT_ISSUER=cetus
JWT_DURATION=3600

# CORS
ALLOWED_ORIGINS=https://cetus.site,https://www.cetus.site

# Logging
QUARKUS_LOG_LEVEL=INFO
```

**Load trong Docker:**

```bash
docker run --env-file .env cetus-backend:latest
```

---

## 🔧 Cấu hình Chi tiết

### application.properties

```properties
# ============================================
# DATABASE CONFIGURATION
# ============================================
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5431/cetus_core
quarkus.datasource.jdbc.transactions=enabled
quarkus.datasource.jdbc.additional-jdbc-properties.autosave=always

# ============================================
# LIQUIBASE CONFIGURATION
# ============================================
quarkus.liquibase.migrate-at-start=true
quarkus.liquibase.change-log=db/masterChangeLog.xml
quarkus.liquibase.database-change-log-lock-table-name=DATABASECHANGELOGLOCK
quarkus.liquibase.database-change-log-table-name=DATABASECHANGELOG

# ============================================
# HIBERNATE ENVERS (AUDIT)
# ============================================
hibernate.envers.revision_entity_class=org.microboy.entity.AuditRevisionEntity
quarkus.hibernate-envers.store-data-at-delete=true

# ============================================
# JWT CONFIGURATION
# ============================================
mp.jwt.verify.publickey.location=/publicKey.pem
mp.jwt.verify.issuer=cetus
smallrye.jwt.sign.key.location=/privateKey.pem
quarkus.smallrye-jwt.enabled=true
com.microboy.cetus.jwt.duration=3600

# ============================================
# CORS CONFIGURATION
# ============================================
quarkus.http.cors=true
quarkus.http.cors.origins=http://cetus.site,http://localhost:3000
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
quarkus.http.cors.methods=GET,POST,PUT,DELETE,PATCH,OPTIONS

# ============================================
# PASSWORD ENCRYPTION
# ============================================
com.microboy.cetus.password.secret=mysecret
com.microboy.cetus.password.iteration=33
com.microboy.cetus.password.keylength=256

# ============================================
# LOGGING
# ============================================
quarkus.log.level=DEBUG
quarkus.log.category."org.microboy".level=DEBUG
quarkus.log.category."io.quarkus.hibernate".level=DEBUG
quarkus.log.category."org.hibernate".level=DEBUG
quarkus.log.category."org.jboss.resteasy".level=DEBUG
```

---

## 📊 Monitoring & Logging

### Health Checks

```bash
# Liveness
curl http://localhost:8080/q/health/live

# Readiness
curl http://localhost:8080/q/health/ready

# Full health
curl http://localhost:8080/q/health
```

### Metrics

```bash
# Prometheus metrics
curl http://localhost:8080/q/metrics

# Application metrics
curl http://localhost:8080/q/metrics/application
```

### Log Levels

Dev: `DEBUG` (chi tiết)  
Prod: `INFO` (cân bằng)

---

## 🧪 Testing

### Test Structure

```
src/test/java/org/microboy/
├── rest/              # API integration tests
│   ├── EmployeeResourceTest.java
│   └── AuthResourceTest.java
├── service/           # Unit tests
│   ├── EmployeeServiceTest.java
│   └── AuthServiceTest.java
└── repository/        # Repository tests
    └── EmployeeRepositoryTest.java
```

---

## 📝 License

MIT License - Xem file [LICENSE](LICENSE)

---

## 👥 Contributors

- **Team Cetus Development**
- Backend Developer: [Your Name]
- Frontend Developer: [Your Name]

---

## 📧 Contact & Support

- **Email:** support@cetus.site
- **Documentation:** [Wiki](wiki-link)
- **Issues:** [GitHub Issues](issues-link)

---

**Made with ❤️ by Cetus Team**
