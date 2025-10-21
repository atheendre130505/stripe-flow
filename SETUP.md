# StripeFlow Setup Guide

This guide will help you set up StripeFlow locally and deploy it to GitHub Pages.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ and npm
- Java 17 and Maven
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/atheendre130505/stripe-flow.git
cd stripe-flow
```

### 2. Local Development Setup

#### Option A: Docker Compose (Recommended)
```bash
# Copy environment file
cp env.local .env

# Start all services
./scripts/local-deploy.sh
```

#### Option B: Manual Setup
```bash
# Backend
cd backend
mvn clean install
mvn spring-boot:run

# Frontend (in another terminal)
cd frontend
npm install
npm run dev
```

### 3. Access the Application
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## 🌐 GitHub Pages Deployment

### 1. Enable GitHub Pages
1. Go to your repository settings
2. Navigate to "Pages" section
3. Select "GitHub Actions" as source
4. The workflow will automatically deploy on push to main

### 2. Configure Environment Variables
Add these secrets to your repository:
- `API_URL`: Your backend API URL (e.g., `https://api.stripeflow.com`)

### 3. Custom Domain (Optional)
1. Add a `CNAME` file to the `frontend/public` directory
2. Set your custom domain in GitHub Pages settings

## 🗄️ Database Setup

### Local Database (Docker)
The local setup automatically creates:
- **PostgreSQL**: Database for application data
- **Redis**: Cache and session storage

### Manual Database Setup
```bash
# PostgreSQL
createdb stripeflow
psql stripeflow < backend/src/main/resources/db/migration/V1__Create_initial_tables.sql

# Redis
redis-server
```

## 🔧 Configuration

### Environment Variables
Copy `env.local` to `.env` and configure:

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=stripeflow
DB_USER=stripeflow
DB_PASSWORD=stripeflow

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=redis

# Stripe (Test Keys)
STRIPE_SECRET_KEY=sk_test_your_stripe_test_secret_key
STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_test_publishable_key

# Frontend
VITE_API_URL=http://localhost:8080/api
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_test_publishable_key
```

### Stripe Configuration
1. Create a Stripe account at https://stripe.com
2. Get your test API keys from the Stripe dashboard
3. Update the environment variables with your keys

## 🧪 Testing

### Run Tests
```bash
# All tests
make test

# Backend tests
make test-backend

# Frontend tests
make test-frontend

# Performance tests
./scripts/performance-test.sh
```

### Test Coverage
```bash
# Backend coverage
cd backend && mvn clean test jacoco:report

# Frontend coverage
cd frontend && npm test -- --coverage
```

## 🚀 Deployment Options

### 1. GitHub Pages (Frontend Only)
- Automatic deployment on push to main
- Free hosting for static sites
- Custom domain support

### 2. Docker Compose (Full Stack)
```bash
# Production deployment
docker-compose -f docker-compose.prod.yml up -d
```

### 3. Kubernetes (Production)
```bash
# Deploy to Kubernetes
./scripts/deploy.sh latest production
```

### 4. Cloud Platforms
- **AWS**: EKS + RDS + ElastiCache
- **Azure**: AKS + Azure Database + Redis
- **GCP**: GKE + Cloud SQL + Memorystore

## 🔒 Security

### API Keys
- Never commit real API keys to the repository
- Use environment variables for sensitive data
- Rotate keys regularly

### Database Security
- Use strong passwords
- Enable SSL connections
- Regular backups

### HTTPS
- Use SSL certificates for production
- Configure CORS properly
- Enable security headers

## 📊 Monitoring

### Local Monitoring
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:9090 (if enabled)

### Production Monitoring
- **Prometheus**: Metrics collection
- **Grafana**: Visualization dashboards
- **AlertManager**: Alert notifications

## 🐛 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose -f docker-compose.local.yml ps postgres

# Check logs
docker-compose -f docker-compose.local.yml logs postgres
```

#### 2. Redis Connection Issues
```bash
# Check if Redis is running
docker-compose -f docker-compose.local.yml ps redis

# Test Redis connection
docker-compose -f docker-compose.local.yml exec redis redis-cli ping
```

#### 3. Frontend Build Issues
```bash
# Clear node modules and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
```

#### 4. Backend Build Issues
```bash
# Clear Maven cache and rebuild
cd backend
mvn clean install -U
```

### Performance Issues
```bash
# Check resource usage
docker stats

# Monitor logs
docker-compose -f docker-compose.local.yml logs -f
```

## 📚 Documentation

- [API Documentation](docs/api/README.md)
- [Architecture Guide](docs/architecture/README.md)
- [Performance Guide](docs/performance/README.md)
- [Deployment Guide](docs/deployment/README.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/atheendre130505/stripe-flow/issues)
- **Discussions**: [GitHub Discussions](https://github.com/atheendre130505/stripe-flow/discussions)
- **Documentation**: [Wiki](https://github.com/atheendre130505/stripe-flow/wiki)

---

**Happy coding! 🚀**
