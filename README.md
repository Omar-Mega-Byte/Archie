# Archie - Blueprint to Boot

> Transform hand-drawn diagrams into production-ready Spring Boot projects with AI-powered code generation

[![Java 21](https://img.shields.io/badge/Java-21-007396?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![Maven 3.8+](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=flat&logo=apache-maven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Overview

Archie is an AI-powered Spring Boot application that accelerates development by automatically generating boilerplate code from architectural diagrams. Simply photograph a hand-drawn database schema or system architecture diagram, upload it to Archie, and watch as the application generates:

- **JPA Entities** with proper annotations
- **Spring Data Repositories** for data access
- **REST Controllers** with CRUD operations
- **SQL Schema** files for database initialization

Perfect for rapid prototyping, reducing time-to-first-implementation, and eliminating tedious boilerplate code.

## Key Features

### 🎯 Multimodal Input Processing
- Upload photographs of hand-drawn diagrams
- Automatic entity and relationship detection
- Support for complex database schemas and system architectures

### 🤖 AI-Powered Analysis
- **Gemini 3 Integration** for advanced visual understanding
- Intelligent reasoning about entity relationships and cardinality
- Real-time streaming code generation for instant feedback
- Context-aware code generation using architectural intent

### ⚡ Rapid Code Generation
- Generate complete Spring Boot project structures in seconds
- Production-ready code following Spring Boot best practices
- Automatic relationship mapping (One-to-Many, Many-to-One, etc.)
- Downloadable project artifacts

### 🔐 Enterprise-Ready
- User authentication and project management
- Project history and design tracking
- Secure credential management
- Support for multiple database backends (H2, PostgreSQL)

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.3.6
- **Language**: Java 21
- **Build Tool**: Maven 3.8+
- **AI Integration**: Gemini 3 API via Spring AI
- **Security**: Spring Security + JWT
- **Data Access**: Spring Data JPA
- **Reactive**: Spring WebFlux (for streaming responses)

### Frontend
- **Framework**: React/Vue.js
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **Package Manager**: npm/yarn

### Database
- **Development**: H2 (embedded)
- **Production**: PostgreSQL
- **Migrations**: Liquibase/Flyway ready

### DevOps & Infrastructure
- **Containerization**: Docker-ready
- **Cloud Deployment**: Supports GCP, AWS, Azure
- **CI/CD**: GitHub Actions

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (React/Vue)                      │
│              Image Upload & Project Management               │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                  Spring Boot Backend                         │
├─────────────────────────────────────────────────────────────┤
│ ┌──────────────────┐    ┌──────────────────┐               │
│ │  Web Controllers │    │  Auth Service    │               │
│ └────────┬─────────┘    └──────────────────┘               │
│          │                                                   │
│ ┌────────▼──────────────────────────────────────────────┐  │
│ │         Image Analysis & Code Generation             │  │
│ │  ┌─────────────────────────────────────────────────┐ │  │
│ │  │      Gemini 3 AI Integration                    │ │  │
│ │  │  (Visual Analysis + Code Generation)           │ │  │
│ │  └─────────────────────────────────────────────────┘ │  │
│ └───────────────┬──────────────────────────────────────┘  │
│                 │                                           │
│ ┌───────────────▼──────────────────────────────────────┐  │
│ │      Code Generation Engine                         │  │
│ │  • JavaPoet Integration                            │  │
│ │  • Spring Annotations & Configuration              │  │
│ │  • Project Artifact Generation                     │  │
│ └───────────────┬──────────────────────────────────────┘  │
│                 │                                           │
│ ┌───────────────▼──────────────────────────────────────┐  │
│ │      Data Persistence Layer                         │  │
│ │  • Project History & Design Tracking               │  │
│ │  • User Management                                 │  │
│ └───────────────────────────────────────────────────────┘  │
└──────────────────┬───────────────────────────────────────┘
                   │
       ┌───────────┴──────────────┬────────────────┐
       ▼                          ▼                ▼
   ┌────────┐              ┌──────────┐      ┌────────────┐
   │  H2 DB │              │PostgreSQL│      │   Google   │
   │(Dev)   │              │(Prod)    │      │   Gemini   │
   └────────┘              └──────────┘      └────────────┘
```

## Quick Start

### Prerequisites
- **Java 21** or later
- **Maven 3.8** or later
- **Node.js 18+** (for frontend)
- **Git**
- **Gemini API Key** (free from [aistudio.google.com](https://aistudio.google.com/))

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Omar-Mega-Byte/archie.git
cd archie
```

2. **Set up environment variables**
```bash
cp .env.example .env
# Edit .env and add your Gemini API key
```

3. **Build the backend**
```bash
mvn clean install
```

4. **Build the frontend**
```bash
cd frontend
npm install
npm run build
cd ..
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Detailed Setup & Usage

For comprehensive setup instructions, environment configuration, and usage examples, see [QUICKSTART.md](./QUICKSTART.md).

## Project Structure

```
archie/
├── src/
│   ├── main/
│   │   ├── java/com/archie/
│   │   │   ├── ArchieApplication.java
│   │   │   ├── ai/                    # AI/Gemini integration
│   │   │   ├── auth/                  # Authentication & Security
│   │   │   ├── codegen/               # Code generation engine
│   │   │   ├── image/                 # Image processing
│   │   │   ├── project/               # Project management
│   │   │   └── web/                   # REST controllers
│   │   └── resources/
│   │       └── application.yml        # Spring configuration
│   └── test/                          # Unit & integration tests
├── frontend/
│   ├── src/
│   │   ├── components/                # React components
│   │   ├── pages/                     # Page views
│   │   ├── services/                  # API communication
│   │   └── App.jsx
│   └── package.json
├── Docs/                              # Documentation
│   ├── ProjectSRS.md                  # Requirements specification
│   ├── MicroservicesArchitecture.md   # Architecture details
│   └── DevelopmentFlow.md             # Development workflow
├── pom.xml                            # Maven configuration
├── .env.example                       # Environment template
└── README.md                          # This file
```

## Configuration

### Environment Variables

Create a `.env` file from `.env.example`:

```bash
# Essential Configuration
GEMINI_API_KEY=your_api_key_here
GEMINI_MODEL=gemini-2.0-flash

# Database
DATABASE_TYPE=h2              # or 'postgres'
DB_HOST=localhost
DB_PORT=5432
DB_NAME=archiedb
DB_USERNAME=archie
DB_PASSWORD=secure_password

# Application
SERVER_PORT=8080
BASE_PACKAGE=com.generated

# Logging
LOGGING_LEVEL=INFO
```

### Spring Profiles

```bash
# Development
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Testing
mvn test -Dspring-boot.run.arguments="--spring.profiles.active=test"

# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## API Documentation

### Endpoints

#### Upload & Generate
- **POST** `/api/v1/diagrams/upload` - Upload diagram image
- **GET** `/api/v1/diagrams/{id}` - Retrieve diagram details
- **GET** `/api/v1/diagrams/{id}/generate` - Start code generation (streams response)

#### Project Management
- **GET** `/api/v1/projects` - List user's projects
- **POST** `/api/v1/projects` - Create new project
- **GET** `/api/v1/projects/{id}` - Get project details
- **POST** `/api/v1/projects/{id}/download` - Download project artifact

#### Authentication
- **POST** `/api/v1/auth/register` - Register new user
- **POST** `/api/v1/auth/login` - User login
- **POST** `/api/v1/auth/logout` - User logout
- **POST** `/api/v1/auth/refresh` - Refresh JWT token

For full API documentation, visit `/swagger-ui.html` when the application is running.

## Usage Example

### 1. Create & Upload Diagram
```bash
curl -X POST http://localhost:8080/api/v1/diagrams/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@diagram.jpg"
```

### 2. Generate Code
```bash
curl -X GET http://localhost:8080/api/v1/diagrams/{diagramId}/generate \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --stream
```

### 3. Download Project
```bash
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/download \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o generated-project.zip
```

## Building from Source

### Build Backend
```bash
# Full build with tests
mvn clean verify

# Skip tests (faster)
mvn clean install -DskipTests

# Build JAR
mvn clean package
```

### Build Frontend
```bash
cd frontend

# Development
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

### Docker Build
```bash
docker build -t archie:latest .
docker run -p 8080:8080 \
  -e GEMINI_API_KEY=your_key \
  archie:latest
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=GeminiAnalysisServiceTest
```

### Test Coverage
```bash
mvn clean verify jacoco:report
open target/site/jacoco/index.html
```

## Deployment

### Cloud Deployment

#### Google Cloud Run
```bash
gcloud run deploy archie \
  --source . \
  --platform managed \
  --region us-central1 \
  --set-env-vars GEMINI_API_KEY=your_key
```

#### AWS Elastic Beanstalk
```bash
eb init
eb create archie-env
eb deploy
```

#### Azure App Service
```bash
az webapp up --name archie --runtime "java|21"
```

## Security

### Security Best Practices
- **API Keys**: Never commit `.env` file; use environment variables
- **JWT Tokens**: Implement token refresh and expiration
- **CORS**: Configure appropriate CORS policies
- **Input Validation**: All inputs are validated server-side
- **Dependency Scanning**: Regular security audits

See [SECURITY.md](./SECURITY.md) for comprehensive security guidelines.

## Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Setup
```bash
git clone https://github.com/Omar-Mega-Byte/archie.git
cd archie
mvn clean install
cd frontend && npm install
```

### Code Style
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use 4-space indentation
- Add JSDoc comments for public APIs

## Roadmap

### v1.0 (Current)
- ✅ Basic diagram upload and analysis
- ✅ JPA entity generation
- ✅ REST controller scaffolding
- ✅ User authentication

### v1.1 (Planned)
- 🔄 Support for microservices architecture
- 🔄 Advanced relationship handling
- 🔄 Business logic suggestions
- 🔄 API documentation generation

### v2.0 (Future)
- 📋 Support for non-Spring frameworks
- 📋 Integration with CI/CD pipelines
- 📋 Collaborative diagram design
- 📋 Advanced code optimization

## Troubleshooting

### Common Issues

**Q: "Gemini API key not found"**
- A: Ensure `.env` file exists in project root with `GEMINI_API_KEY` set
- Check that environment variable is properly loaded: `echo $GEMINI_API_KEY`

**Q: "Database connection failed"**
- A: Verify DATABASE_TYPE is set correctly (h2 or postgres)
- For PostgreSQL, ensure DB_HOST, DB_PORT, DB_NAME, etc. are configured

**Q: "Frontend not loading"**
- A: Ensure frontend is built: `cd frontend && npm run build`
- Check that Spring Boot serves static files from `src/main/resources/static/`

**Q: "Port 8080 already in use"**
- A: Change port: `SERVER_PORT=8081 mvn spring-boot:run`

For more troubleshooting, see [Docs/DevelopmentFlow.md](./Docs/DevelopmentFlow.md)

## Performance Optimization

### Caching
- Spring Cache abstraction for frequently accessed data
- Redis integration ready for production scaling

### Image Processing
- Efficient image compression before Gemini analysis
- Batch processing for multiple diagram uploads

### Code Generation
- Streaming responses for real-time user feedback
- Async processing for large projects

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact & Support

- **Issues**: [GitHub Issues](https://github.com/Omar-Mega-Byte/archie/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Omar-Mega-Byte/archie/discussions)
- **Email**: omar.tolis2004@gmail.com
    
## Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot) framework
- AI powered by [Google Gemini 3](https://ai.google.dev/)
- Code generation via [JavaPoet](https://github.com/square/javapoet)
- Developed during [Gemini 3 Hackathon](https://gemini-hackathon.dev)

---

**Made with ❤️ by Omar Elrfaay**
