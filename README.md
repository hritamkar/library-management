# 📚 Library Management System

A full-stack multitenant library management application built with **SAP Cloud Application Programming Model (CAP)** for Java, designed for deployment on **SAP Business Technology Platform (BTP)**.

## 🎯 Overview

This application provides comprehensive library management capabilities with support for books, members, and loan tracking. It includes automatic fine calculation, email validation, duplicate prevention, and is designed for SaaS multitenancy deployment.

## ✨ Features

### Core Functionality
- **📖 Book Management**: Create, read, update, and delete books with automatic stock tracking
- **👥 Member Management**: Register and manage library members with email validation
- **📋 Loan Management**: Track book loans with automatic due date calculation (30 days)
- **💰 Fine Calculation**: Automatic fine calculation for overdue returns (₹10/day)
- **🔄 Return Processing**: Book return with fine calculation and stock updates

### Business Logic
- ✅ Automatic stock validation (prevents over-lending)
- ✅ Duplicate book prevention (updates stock instead of creating duplicates)
- ✅ Email validation for members
- ✅ Email verification for book returns (security feature)
- ✅ Prevents member deletion if they have active loans
- ✅ Automatic stock increment/decrement on loan/return

### Technical Features
- 🔐 OAuth2 authentication via XSUAA
- 🏢 Multitenancy ready with SaaS Registry integration
- 🌐 RESTful OData V4 API
- 📊 SAP HANA Cloud database support
- 🚀 Cloud Foundry deployment ready
- 🔄 Blue-green deployment support

## 🏗️ Architecture

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend Framework** | SAP CAP Java (Spring Boot 3.5.8) |
| **Database** | SAP HANA Cloud / H2 (local) |
| **Build Tool** | Maven 3.x |
| **Runtime** | Java 17 |
| **API Protocol** | OData V4 |
| **Authentication** | XSUAA (OAuth2) |
| **Routing** | Application Router |
| **Deployment** | Cloud Foundry (SAP BTP) |

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Node.js 18+** and npm ([Download](https://nodejs.org/))
- **SAP Cloud Foundry CLI** (for deployment) - [Install Guide](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)
- **Cloud MTA Build Tool** (for building) - `npm install -g mbt`

### 📥 Installation

1. **Clone the repository**
   ```bash
   git clone <your-repository-url>
   cd library-management
   ```

2. **Install root dependencies**
   ```bash
   npm install
   ```

3. **Install service dependencies and build**
   ```bash
   cd srv
   mvn clean install -DskipTests
   cd ..
   ```

After clean install, go to srv/src/gen and mark the java folder as "Generated Sources Root" in your IDE.

## 💻 Running Locally

### Run with Maven (Recommended for Development)

1. **Navigate to service directory**
   ```bash
   cd srv
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**
   - 🌐 Application: http://localhost:8080
   - 📊 OData Service: http://localhost:8080/odata/v4/LibraryService
   - 📖 Service Metadata: http://localhost:8080/odata/v4/LibraryService/$metadata


## 🔨 Building for Production

### Build MTA Archive

The Multi-Target Application (MTA) archive bundles all components for deployment:

```bash
# Build the MTA archive
mbt build

# Output: mta_archives/library-management_1.0.0.mtar
```

This command:
- ✅ Builds the Java service (`srv`)
- ✅ Packages the database module (`db`)
- ✅ Packages the application router (`approuter`)
- ✅ Creates a deployable `.mtar` file

### Build Components Separately (Optional)

**Build Java Service:**
```bash
cd srv
mvn clean package -DskipTests
```

**Build Database Module:**
```bash
cd db
npm install
```

## ☁️ Deployment to SAP BTP

### Prerequisites for Deployment

1. **SAP BTP Account** (Trial or Production)
2. **Cloud Foundry Space** with appropriate entitlements
3. **Required Services:**
   - SAP HANA Cloud (or hdi-shared for trial)
   - XSUAA
   - SaaS Registry (for multitenancy)

### Step-by-Step Deployment

#### 1. Login to Cloud Foundry

```bash
cf login -a <api-endpoint>
```

#### 2. Target Your Org and Space

```bash
cf target -o <your-org> -s <your-space>
```

#### 3. Deploy the MTA Archive

```bash
cf deploy mta_archives/library-management_1.0.0.mtar
```

**Deployment process includes:**
- ✅ Creating/updating services (library-db, library-uaa, library-registry)
- ✅ Deploying database schema (library-management-db-deployer)
- ✅ Deploying Java service (library-management-srv)
- ✅ Deploying application router (library-management-approuter)

**Expected deployment time:** 8-12 minutes

#### 4. Verify Deployment

```bash
# Check applications
cf apps

# Check services
cf services

# View application logs
cf logs library-management-srv --recent
```

### Accessing Your Deployed Application

After successful deployment:

**Application URLs:**
- Service: `https://<org>-<space>-library-management-srv.cfapps.<region>.hana.ondemand.com`
- Approuter: `https://<org>-<space>-library-management-approuter.cfapps.<region>.hana.ondemand.com`

## 📡 API Documentation

### OData V4 Endpoints

**Base URL:** `https://<your-app-url>/odata/v4/LibraryService`

#### Books

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/Books` | List all books |
| POST | `/Books` | Create a new book |
| GET | `/Books({ID})` | Get book by ID |
| PATCH | `/Books({ID})` | Update book |
| DELETE | `/Books({ID})` | Delete book |

**Example Book Entity:**
```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "stock": 5
}
```

#### Members

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/Members` | List all members |
| POST | `/Members` | Register a member |
| GET | `/Members({ID})` | Get member by ID |
| PATCH | `/Members({ID})` | Update member |
| DELETE | `/Members({ID})` | Delete member |

**Example Member Entity:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### Loans

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/Loans` | List all loans |
| POST | `/Loans` | Create a loan |
| GET | `/Loans({ID})` | Get loan by ID |

**Example Loan Entity:**
```json
{
  "bookId": "book-uuid",
  "memberId": "member-uuid",
  "loanDate": "2026-01-11",
  "dueDate": "2026-02-10",
  "returnDate": null
}
```

**Return Book:**
```bash
POST /odata/v4/LibraryService/returnBook
Content-Type: application/json

{
  "loanId": "loan-uuid",
  "email": "john.doe@example.com"
}

Response: { "value": 50 }  // Fine amount
```

### Subscription API (Multitenancy)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/mt/v1.0/subscriptions/dependencies` | Get subscription dependencies |
| PUT | `/mt/v1.0/subscriptions/tenants/{tenantId}` | Subscribe a tenant |
| DELETE | `/mt/v1.0/subscriptions/tenants/{tenantId}` | Unsubscribe a tenant |

## 🔐 Security & Authentication

### Local Development
- Mock authentication enabled by default
- No login required for testing

### Production (BTP)
- OAuth2 authentication via XSUAA
- Role-based access control
- Two main roles:
  - **Admin**: Full access to all operations
  - **MTCallback**: Subscription management

### Configuring Users (Production)

1. Go to BTP Cockpit
2. Navigate to your subaccount → Security → Role Collections
3. Assign users to role collections

## 🧪 Sample Usage Scenarios

### Scenario 1: Adding Books and Members

```bash
# Add a book
curl -X POST http://localhost:8080/odata/v4/LibraryService/Books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Pragmatic Programmer",
    "author": "David Thomas",
    "stock": 3
  }'

# Register a member
curl -X POST http://localhost:8080/odata/v4/LibraryService/Members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com"
  }'
```

### Scenario 2: Creating a Loan

```bash
curl -X POST http://localhost:8080/odata/v4/LibraryService/Loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": "<book-id>",
    "memberId": "<member-id>",
    "loanDate": "2026-01-11",
    "dueDate": "2026-02-10"
  }'
```

### Scenario 3: Returning a Book (On Time)

```bash
curl -X POST http://localhost:8080/odata/v4/LibraryService/returnBook \
  -H "Content-Type: application/json" \
  -d '{
    "loanId": "<loan-id>",
    "email": "jane.smith@example.com"
  }'

# Response: { "fine": 0 }  // No fine
```

### Scenario 4: Returning a Book (Overdue)

If the book is returned 5 days late:

```bash
# Response: { "fine": 50 }  // ₹10 × 5 days = ₹50
```

## 🔧 Configuration

### application.yaml

Key configurations:

```yaml
cds:
  multitenancy:
    enabled: true          # Enable multitenancy
    tenantId: request      # Extract tenant from request
  
  security:
    authentication-strategy: never  # Disable auth for subscription endpoints
```

### xs-security.json

Defines OAuth2 security:
- Scopes: `Admin`, `Callback`, `mtcallback`
- Role templates
- Grant authorities for SaaS Registry

### mta.yaml

Multi-Target Application descriptor:
- Defines modules (db, srv, approuter)
- Configures services (HANA, XSUAA, SaaS Registry)
- Sets up routes and dependencies

## ⚠️ Known Limitations

### Trial Account Limitations

**What Works:**
- ✅ All CRUD operations
- ✅ Business logic (loans, returns, fines)
- ✅ Subscription API endpoints (return HTTP 200)
- ✅ Single-tenant deployment

**What Doesn't Work:**
- ❌ Multitenancy with tenant-specific HDI containers
- ❌ Tenant database isolation
- ❌ Full SaaS subscription flow

**Why:** Trial accounts don't have access to HANA Cloud's HDI container provisioning for tenants.

**Solution:** For production multitenancy, deploy to a production BTP account with HANA Cloud service.

## 🐛 Troubleshooting

### Build Issues

**Maven build fails:**
```bash
# Clean and rebuild
cd srv
mvn clean install -U
```

**MTA build fails:**
```bash
# Clean build artifacts
rm -rf mta_archives/ node_modules/ srv/target/
npm install
mbt build
```

### Deployment Issues

**Service creation fails:**
```bash
# Check service status
cf services

# View service creation logs
cf service library-db
```

**Application won't start:**
```bash
# View application logs
cf logs library-management-srv --recent

# Check application details
cf app library-management-srv
```

### Runtime Issues

**Database connection errors:**
- Verify HANA service is running
- Check service binding: `cf env library-management-srv`

**Authentication errors:**
- Verify XSUAA service configuration
- Check xs-security.json is correctly deployed

## 🧹 Maintenance

### Update Dependencies

```bash
# Update Node.js dependencies
npm update

# Update Maven dependencies
cd srv
mvn versions:display-dependency-updates
```

### Clean Build Artifacts

```bash
# Remove all build artifacts
rm -rf mta_archives/ node_modules/ srv/target/ db/gen/

# Reinstall and rebuild
npm install
mbt build
```

### Redeploy

```bash
# Rebuild and redeploy
mbt build
cf deploy mta_archives/library-management_1.0.0.mtar
```

## 📊 Database Schema

### Entity Relationships

```
Members (1) ──< (N) Loans (N) >── (1) Books
```

### Key Fields

**Books:**
- ID (UUID, Primary Key)
- title (String)
- author (String)
- stock (Integer)
- Managed fields (createdAt, createdBy, modifiedAt, modifiedBy)

**Members:**
- ID (UUID, Primary Key)
- name (String)
- email (String, unique)
- Managed fields

**Loans:**
- ID (UUID, Primary Key)
- bookId (UUID, Foreign Key)
- memberId (UUID, Foreign Key)
- loanDate (Date)
- dueDate (Date, auto-calculated as loanDate + 30 days)
- returnDate (Date, nullable)
- fine (Integer, default 0)
- Managed fields

## 🙏 Acknowledgments

- [SAP Cloud Application Programming Model](https://cap.cloud.sap/)
- [SAP Business Technology Platform](https://www.sap.com/products/technology-platform.html)
- Spring Boot Framework
---

**Version:** 1.0.0  
**Last Updated:** January 11, 2026  
**Status:** ✅ Production Ready (Single-tenant) | ⚠️ Multitenancy requires HANA Cloud
