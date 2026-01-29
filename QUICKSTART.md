# Quick Start Guide - Archie

Get Archie up and running in minutes!

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [5-Minute Setup](#5-minute-setup)
3. [First Diagram Upload](#first-diagram-upload)
4. [Generate Your First Project](#generate-your-first-project)
5. [Next Steps](#next-steps)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before you begin, ensure you have installed:

### Required
- **Java 21+** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
  ```bash
  java -version
  ```
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
  ```bash
  mvn -version
  ```
- **Git** - [Download](https://git-scm.com/)
  ```bash
  git --version
  ```

### Optional (for Frontend)
- **Node.js 18+** - [Download](https://nodejs.org/)
  ```bash
  node --version
  npm --version
  ```

### API Keys
- **Gemini API Key** (free) - Get it from [aistudio.google.com](https://aistudio.google.com/)
  - No credit card required
  - Free tier includes generous usage limits

---

## 5-Minute Setup

### Step 1: Clone the Repository
```bash
git clone https://github.com/Omar-Mega-Byte/archie.git
cd archie
```

### Step 2: Set Up Environment Variables
```bash
# Copy the example configuration
cp .env.example .env

# Edit .env and add your Gemini API key
# On Linux/macOS:
nano .env

# On Windows:
notepad .env
```

**Minimal .env configuration:**
```env
GEMINI_API_KEY=your_gemini_api_key_here
GEMINI_MODEL=gemini-2.0-flash
DATABASE_TYPE=h2
SERVER_PORT=8080
```

### Step 3: Build the Backend
```bash
# Download dependencies and compile
mvn clean install

# This may take 2-3 minutes on first run
```

### Step 4: Start the Application
```bash
# Run Spring Boot
mvn spring-boot:run
```

You should see output ending with:
```
Starting ArchieApplication
Started ArchieApplication in X.XXX seconds
```

### Step 5: Access the Application
Open your browser and navigate to:
```
http://localhost:8080
```

✅ **You're now running Archie!**

---

## First Diagram Upload

### Prepare Your Diagram

Before uploading, you need a diagram. You can:

1. **Draw on paper** - Sketch a simple database schema with tables and relationships
2. **Draw digitally** - Use Figma, Lucidchart, or even Paint
3. **Use a sample diagram** - Check the `Docs/` folder for examples

**Good example:** Draw a simple database with:
- A "User" table with columns: id, name, email
- A "Post" table with columns: id, title, userId
- A relationship line between User.id and Post.userId

### Upload via Web Interface

1. Take a photo/screenshot of your diagram
2. Go to `http://localhost:8080`
3. Click **"Upload Diagram"**
4. Select your image file
5. Click **"Upload"**

### Upload via cURL

```bash
# First, get an authentication token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Save the token from response
export TOKEN=your_token_here

# Upload diagram
curl -X POST http://localhost:8080/api/v1/diagrams/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@your_diagram.jpg"

# You'll get a response with diagramId:
# {"id": "diagram-123", "status": "uploaded"}
```

---

## Generate Your First Project

### Via Web Interface

1. After uploading, your diagram appears in the dashboard
2. Click the **"Generate Code"** button
3. Watch the code generation in real-time
4. Review the generated entities, repositories, and controllers
5. Click **"Download Project"** to get a ZIP file

### Via cURL

```bash
# Start code generation (streams response)
curl -X GET http://localhost:8080/api/v1/diagrams/diagram-123/generate \
  -H "Authorization: Bearer $TOKEN" \
  --stream

# This shows real-time code generation output
```

### Using the Generated Code

1. **Extract the downloaded ZIP**
   ```bash
   unzip generated-project.zip
   cd generated-project
   ```

2. **View the generated structure**
   ```
   src/
   ├── main/java/com/generated/
   │   ├── entity/
   │   │   ├── User.java          # Generated JPA Entity
   │   │   └── Post.java
   │   ├── repository/
   │   │   ├── UserRepository.java # Spring Data Repository
   │   │   └── PostRepository.java
   │   └── controller/
   │       ├── UserController.java # REST Controller
   │       └── PostController.java
   └── resources/
       └── schema.sql              # Database schema
   ```

3. **Build and run the generated project**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## Database Configuration

### Using H2 (Development - Default)

H2 is embedded and requires no setup:

```env
DATABASE_TYPE=h2
SERVER_PORT=8080
```

Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

### Switching to PostgreSQL (Production)

1. **Install PostgreSQL** - [Download](https://www.postgresql.org/download/)

2. **Create database**
   ```bash
   psql -U postgres
   CREATE DATABASE archiedb;
   CREATE USER archie WITH PASSWORD 'secure_password';
   GRANT ALL PRIVILEGES ON DATABASE archiedb TO archie;
   \q
   ```

3. **Update .env**
   ```env
   DATABASE_TYPE=postgres
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=archiedb
   DB_USERNAME=archie
   DB_PASSWORD=secure_password
   ```

4. **Restart the application**
   ```bash
   mvn spring-boot:run
   ```

---

## Environment Variables Reference

### Gemini AI Configuration (Required)
```env
GEMINI_API_KEY=your_api_key_here
GEMINI_MODEL=gemini-2.0-flash
GEMINI_TEMPERATURE=0.3           # 0.0-1.0 (lower = more deterministic)
GEMINI_MAX_TOKENS=4096           # Max response length
```

### Database Configuration
```env
DATABASE_TYPE=h2                 # or 'postgres'
DB_HOST=localhost                # PostgreSQL only
DB_PORT=5432                     # PostgreSQL only
DB_NAME=archiedb                 # PostgreSQL only
DB_USERNAME=archie               # PostgreSQL only
DB_PASSWORD=secure_password      # PostgreSQL only
```

### Application Configuration
```env
SERVER_PORT=8080                 # Server port
BASE_PACKAGE=com.generated       # Base package for generated code
UPLOAD_DIRECTORY=./archie-uploads # Where to store uploaded images
OUTPUT_DIRECTORY=./archie-projects # Where to store generated projects
LOGGING_LEVEL=INFO               # DEBUG, INFO, WARN, ERROR
```

---

## Running with Different Profiles

### Development (Default)
```bash
mvn spring-boot:run
```

### Development with Debug
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev --debug"
```

### Testing
```bash
mvn test
```

### Production
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## Building for Deployment

### Create Executable JAR
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/blueprint-to-boot-1.0.0-SNAPSHOT.jar
```

### Create Docker Image
```bash
# Build Docker image
docker build -t archie:latest .

# Run container
docker run -p 8080:8080 \
  -e GEMINI_API_KEY=your_key \
  -e DATABASE_TYPE=postgres \
  -e DB_HOST=postgres-container \
  archie:latest
```

### Docker Compose (Full Stack)
```bash
docker-compose up -d
```

---

## Common Tasks

### Verify Installation
```bash
# Check Java
java -version

# Check Maven
mvn -version

# Verify Git
git --version

# Test Gemini API Key
curl -X GET "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash?key=$GEMINI_API_KEY"
```

### View Application Logs
```bash
# Real-time logs
mvn spring-boot:run

# Save logs to file
mvn spring-boot:run > app.log 2>&1 &
tail -f app.log
```

### Reset Database
```bash
# For H2 (automatic on restart)
# Just stop and restart: mvn spring-boot:run

# For PostgreSQL (manual)
psql -U archie -d archiedb
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\q
```

### Access Application Endpoints

| Endpoint | Purpose |
|----------|---------|
| `http://localhost:8080` | Web interface |
| `http://localhost:8080/swagger-ui.html` | API documentation |
| `http://localhost:8080/h2-console` | H2 database console (dev only) |
| `http://localhost:8080/actuator` | Application health & metrics |

---

## Next Steps

1. **Explore the API** - Visit `http://localhost:8080/swagger-ui.html`

2. **Create More Diagrams** - Try different entity relationships:
   - One-to-Many relationships
   - Many-to-Many relationships
   - Complex inheritance hierarchies

3. **Customize Generated Code** - Modify the generated entities:
   - Add validation annotations
   - Add custom methods
   - Extend repositories with custom queries

4. **Deploy to Cloud** - See [README.md](./README.md) for deployment guides:
   - Google Cloud Run
   - AWS Elastic Beanstalk
   - Azure App Service

5. **Read Full Documentation** - Check out:
   - [README.md](./README.md) - Full project documentation
   - [Docs/MicroservicesArchitecture.md](./Docs/MicroservicesArchitecture.md) - Architecture details
   - [Docs/DevelopmentFlow.md](./Docs/DevelopmentFlow.md) - Development workflow
   - [SECURITY.md](./SECURITY.md) - Security guidelines

---

## Troubleshooting

### "Command not found: mvn"
**Solution:** Maven is not in your PATH
```bash
# Add to PATH (Linux/macOS)
export PATH=$PATH:/path/to/maven/bin

# Or install via package manager
brew install maven  # macOS
apt-get install maven  # Ubuntu
```

### "Gemini API Key Error"
**Problem:** `GEMINI_API_KEY not found or invalid`

**Solution:**
1. Verify key is in `.env` file
2. Ensure no quotes around the key value
3. Test the key:
   ```bash
   curl -X GET "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash?key=YOUR_KEY"
   ```
4. Get a new key from [aistudio.google.com](https://aistudio.google.com/)

### "Port 8080 Already in Use"
**Solution:** Use a different port
```bash
# Option 1: Change in .env
SERVER_PORT=8081

# Option 2: Change via command line
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Option 3: Kill process using port
# Linux/macOS:
lsof -ti:8080 | xargs kill -9

# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### "Database Connection Failed"
**Solution:**
1. Check database type in `.env`
   ```bash
   echo $DATABASE_TYPE
   ```
2. For PostgreSQL, verify credentials:
   ```bash
   psql -U archie -d archiedb
   ```
3. Check connection string in logs

### "Maven Build Failed"
**Solution:**
```bash
# Clean Maven cache
mvn clean

# Try building again with verbose output
mvn -X clean install

# Check Java version
java -version
```

### "Frontend Not Loading"
**Solution:**
```bash
# Rebuild frontend
cd frontend
npm install
npm run build
cd ..

# Restart backend
mvn spring-boot:run
```

---

## Getting Help

- **GitHub Issues**: [Report bugs](https://github.com/Omar-Mega-Byte/archie/issues)
- **GitHub Discussions**: [Ask questions](https://github.com/Omar-Mega-Byte/archie/discussions)
- **Email Support**: omar.tolis2004@gmail.com
- **Documentation**: Check [Docs/](./Docs/) folder

---

**Happy Coding with Archie!** 🚀
