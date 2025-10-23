#!/bin/bash

# StripeFlow Local Deployment Script
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 StripeFlow Local Deployment${NC}"
echo "=================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed. Please install Docker Compose.${NC}"
    exit 1
fi

echo -e "${YELLOW}📋 Setting up local environment...${NC}"

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo -e "${YELLOW}📝 Creating .env file from template...${NC}"
    cp env.local .env
    echo -e "${GREEN}✅ Created .env file. Please update with your actual values.${NC}"
fi

# Build and start services
echo -e "${YELLOW}🔨 Building and starting services...${NC}"
docker-compose -f docker-compose.local.yml down --remove-orphans
docker-compose -f docker-compose.local.yml build --no-cache
docker-compose -f docker-compose.local.yml up -d

# Wait for services to be healthy
echo -e "${YELLOW}⏳ Waiting for services to be healthy...${NC}"

# Wait for PostgreSQL
echo -e "${YELLOW}🐘 Waiting for PostgreSQL...${NC}"
timeout=60
while ! docker-compose -f docker-compose.local.yml exec postgres pg_isready -U stripeflow -d stripeflow > /dev/null 2>&1; do
    sleep 2
    timeout=$((timeout - 2))
    if [ $timeout -le 0 ]; then
        echo -e "${RED}❌ PostgreSQL failed to start within 60 seconds${NC}"
        exit 1
    fi
done
echo -e "${GREEN}✅ PostgreSQL is ready${NC}"

# Wait for Redis
echo -e "${YELLOW}🔴 Waiting for Redis...${NC}"
timeout=60
while ! docker-compose -f docker-compose.local.yml exec redis redis-cli ping > /dev/null 2>&1; do
    sleep 2
    timeout=$((timeout - 2))
    if [ $timeout -le 0 ]; then
        echo -e "${RED}❌ Redis failed to start within 60 seconds${NC}"
        exit 1
    fi
done
echo -e "${GREEN}✅ Redis is ready${NC}"

# Wait for Backend
echo -e "${YELLOW}🔧 Waiting for Backend API...${NC}"
timeout=120
while ! curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; do
    sleep 5
    timeout=$((timeout - 5))
    if [ $timeout -le 0 ]; then
        echo -e "${RED}❌ Backend API failed to start within 120 seconds${NC}"
        echo -e "${YELLOW}📋 Backend logs:${NC}"
        docker-compose -f docker-compose.local.yml logs backend
        exit 1
    fi
done
echo -e "${GREEN}✅ Backend API is ready${NC}"

# Wait for Frontend
echo -e "${YELLOW}🎨 Waiting for Frontend...${NC}"
timeout=60
while ! curl -f http://localhost:3000/health > /dev/null 2>&1; do
    sleep 3
    timeout=$((timeout - 3))
    if [ $timeout -le 0 ]; then
        echo -e "${RED}❌ Frontend failed to start within 60 seconds${NC}"
        echo -e "${YELLOW}📋 Frontend logs:${NC}"
        docker-compose -f docker-compose.local.yml logs frontend
        exit 1
    fi
done
echo -e "${GREEN}✅ Frontend is ready${NC}"

# Run database migrations
echo -e "${YELLOW}🗄️ Running database migrations...${NC}"
docker-compose -f docker-compose.local.yml exec backend java -jar app.jar --spring.main.web-application-type=none --spring.profiles.active=docker

# Test API endpoints
echo -e "${YELLOW}🧪 Testing API endpoints...${NC}"

# Test health endpoint
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Health check passed${NC}"
else
    echo -e "${RED}❌ Health check failed${NC}"
fi

# Test API documentation
if curl -f http://localhost:8080/swagger-ui.html > /dev/null 2>&1; then
    echo -e "${GREEN}✅ API documentation is accessible${NC}"
else
    echo -e "${YELLOW}⚠️ API documentation not accessible${NC}"
fi

# Display service URLs
echo ""
echo -e "${GREEN}🎉 StripeFlow is now running locally!${NC}"
echo "=================================="
echo -e "${BLUE}📱 Frontend:${NC} http://localhost:3000"
echo -e "${BLUE}🔧 Backend API:${NC} http://localhost:8080"
echo -e "${BLUE}📚 API Documentation:${NC} http://localhost:8080/swagger-ui.html"
echo -e "${BLUE}🏥 Health Check:${NC} http://localhost:8080/actuator/health"
echo ""
echo -e "${YELLOW}📋 Service Status:${NC}"
docker-compose -f docker-compose.local.yml ps

echo ""
echo -e "${YELLOW}🔧 Useful Commands:${NC}"
echo "  View logs: docker-compose -f docker-compose.local.yml logs -f"
echo "  Stop services: docker-compose -f docker-compose.local.yml down"
echo "  Restart services: docker-compose -f docker-compose.local.yml restart"
echo "  Rebuild services: docker-compose -f docker-compose.local.yml up --build"

echo ""
echo -e "${GREEN}✅ Local deployment completed successfully!${NC}"


