# StripeFlow Development Makefile

.PHONY: help install build test test-coverage clean dev docker-build docker-up docker-down

# Default target
help:
	@echo "StripeFlow Development Commands"
	@echo "==============================="
	@echo ""
	@echo "Installation:"
	@echo "  install          Install all dependencies"
	@echo "  install-backend  Install backend dependencies"
	@echo "  install-frontend Install frontend dependencies"
	@echo ""
	@echo "Development:"
	@echo "  dev              Start development environment"
	@echo "  dev-backend      Start backend only"
	@echo "  dev-frontend     Start frontend only"
	@echo ""
	@echo "Testing:"
	@echo "  test             Run all tests"
	@echo "  test-backend     Run backend tests"
	@echo "  test-frontend    Run frontend tests"
	@echo "  test-coverage    Run tests with coverage"
	@echo ""
	@echo "Docker:"
	@echo "  docker-build     Build Docker images"
	@echo "  docker-up        Start with Docker Compose"
	@echo "  docker-down      Stop Docker Compose"
	@echo ""
	@echo "Utilities:"
	@echo "  build            Build all projects"
	@echo "  clean            Clean all build artifacts"
	@echo "  lint             Run linting"
	@echo "  format           Format code"

# Installation
install: install-backend install-frontend

install-backend:
	@echo "Installing backend dependencies..."
	cd backend && mvn clean install -DskipTests

install-frontend:
	@echo "Installing frontend dependencies..."
	cd frontend && npm install

# Development
dev: docker-up

dev-backend:
	@echo "Starting backend development server..."
	cd backend && mvn spring-boot:run

dev-frontend:
	@echo "Starting frontend development server..."
	cd frontend && npm run dev

# Testing
test: test-backend test-frontend

test-backend:
	@echo "Running backend tests..."
	cd backend && mvn test

test-frontend:
	@echo "Running frontend tests..."
	cd frontend && npm run test:ci

test-coverage: test-backend-coverage test-frontend-coverage

test-backend-coverage:
	@echo "Running backend tests with coverage..."
	cd backend && mvn clean test jacoco:report
	@echo "Backend coverage report generated at backend/target/site/jacoco/index.html"

test-frontend-coverage:
	@echo "Running frontend tests with coverage..."
	cd frontend && npm run test:coverage
	@echo "Frontend coverage report generated at frontend/coverage/lcov-report/index.html"

# Docker
docker-build:
	@echo "Building Docker images..."
	docker-compose build

docker-up:
	@echo "Starting services with Docker Compose..."
	docker-compose up -d
	@echo "Services started. Backend: http://localhost:8080, Frontend: http://localhost:3000"

docker-down:
	@echo "Stopping Docker Compose services..."
	docker-compose down

docker-logs:
	@echo "Showing Docker Compose logs..."
	docker-compose logs -f

# Build
build: build-backend build-frontend

build-backend:
	@echo "Building backend..."
	cd backend && mvn clean package -DskipTests

build-frontend:
	@echo "Building frontend..."
	cd frontend && npm run build

# Utilities
clean:
	@echo "Cleaning build artifacts..."
	cd backend && mvn clean
	cd frontend && rm -rf node_modules dist coverage
	@echo "Clean complete"

lint: lint-backend lint-frontend

lint-backend:
	@echo "Linting backend code..."
	cd backend && mvn checkstyle:check

lint-frontend:
	@echo "Linting frontend code..."
	cd frontend && npm run lint

format: format-backend format-frontend

format-backend:
	@echo "Formatting backend code..."
	cd backend && mvn spotless:apply

format-frontend:
	@echo "Formatting frontend code..."
	cd frontend && npm run format

# Database
db-migrate:
	@echo "Running database migrations..."
	cd backend && mvn flyway:migrate

db-reset:
	@echo "Resetting database..."
	cd backend && mvn flyway:clean flyway:migrate

# Production
prod-build:
	@echo "Building for production..."
	docker-compose -f docker-compose.prod.yml build

prod-up:
	@echo "Starting production environment..."
	docker-compose -f docker-compose.prod.yml up -d

# Health checks
health:
	@echo "Checking service health..."
	@curl -f http://localhost:8080/actuator/health || echo "Backend not healthy"
	@curl -f http://localhost:3000 || echo "Frontend not healthy"

# Quick start
quickstart: install docker-up
	@echo "StripeFlow is starting up..."
	@echo "Backend: http://localhost:8080"
	@echo "Frontend: http://localhost:3000"
	@echo "API Docs: http://localhost:8080/swagger-ui.html"