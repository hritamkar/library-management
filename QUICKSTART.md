# 🚀 Quick Start Guide

## For First-Time Users

### 1️⃣ Initial Setup (One-time)

```bash
# Clone the repository
git clone <your-repo-url>
cd library-management

# Install dependencies
npm install

# Build the service
cd srv
mvn clean install
cd ..
```

### 2️⃣ Run Locally

```bash
cd srv
mvn spring-boot:run
```

**Access:** http://localhost:8080

### 3️⃣ Test the API

```bash
# Create a book
curl -X POST http://localhost:8080/odata/v4/LibraryService/Books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book","author":"Test Author","stock":5}'

# List books
curl http://localhost:8080/odata/v4/LibraryService/Books
```

### 4️⃣ Deploy to BTP

```bash
# Build
mbt build

# Login to Cloud Foundry
cf login -a https://api.cf.<region>.hana.ondemand.com

# Deploy
cf deploy mta_archives/library-management_1.0.0.mtar
```

## Common Commands

```bash
# Clean build artifacts
mvn clean
rm -rf mta_archives/ node_modules/

# Rebuild everything
npm install && mbt build

# View deployment logs
cf logs library-management-srv --recent

# Check deployed apps
cf apps
```

## Need Help?

See the main [README.md](README.md) for detailed documentation.

