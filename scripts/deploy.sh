#!/bin/bash

# StripeFlow Deployment Script
set -e

# Configuration
NAMESPACE="stripeflow"
REGISTRY="ghcr.io"
IMAGE_TAG="${1:-latest}"
ENVIRONMENT="${2:-staging}"

echo "🚀 Starting StripeFlow deployment..."
echo "Environment: $ENVIRONMENT"
echo "Image Tag: $IMAGE_TAG"
echo "Namespace: $NAMESPACE"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

# Check if helm is available
if ! command -v helm &> /dev/null; then
    echo "❌ helm is not installed or not in PATH"
    exit 1
fi

# Create namespace if it doesn't exist
echo "📦 Creating namespace..."
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Apply secrets
echo "🔐 Applying secrets..."
kubectl apply -f k8s/secrets.yaml

# Apply configmap
echo "⚙️ Applying configuration..."
kubectl apply -f k8s/configmap.yaml

# Deploy PostgreSQL
echo "🐘 Deploying PostgreSQL..."
helm upgrade --install postgres oci://registry-1.docker.io/bitnamicharts/postgresql \
    --namespace $NAMESPACE \
    --set auth.database=stripeflow \
    --set auth.username=stripeflow \
    --set auth.password=stripeflow \
    --set primary.persistence.size=20Gi \
    --set primary.resources.requests.memory=512Mi \
    --set primary.resources.requests.cpu=250m \
    --set primary.resources.limits.memory=1Gi \
    --set primary.resources.limits.cpu=500m

# Deploy Redis
echo "🔴 Deploying Redis..."
helm upgrade --install redis oci://registry-1.docker.io/bitnamicharts/redis \
    --namespace $NAMESPACE \
    --set auth.password=redis \
    --set master.persistence.size=10Gi \
    --set master.resources.requests.memory=256Mi \
    --set master.resources.requests.cpu=100m \
    --set master.resources.limits.memory=512Mi \
    --set master.resources.limits.cpu=250m

# Wait for databases to be ready
echo "⏳ Waiting for databases to be ready..."
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=postgresql -n $NAMESPACE --timeout=300s
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=redis -n $NAMESPACE --timeout=300s

# Deploy backend
echo "🔧 Deploying backend..."
kubectl apply -f k8s/backend-deployment.yaml
kubectl set image deployment/stripeflow-backend stripeflow-backend=$REGISTRY/stripeflow-backend:$IMAGE_TAG -n $NAMESPACE

# Deploy frontend
echo "🎨 Deploying frontend..."
kubectl apply -f k8s/frontend-deployment.yaml
kubectl set image deployment/stripeflow-frontend stripeflow-frontend=$REGISTRY/stripeflow-frontend:$IMAGE_TAG -n $NAMESPACE

# Apply services
echo "🌐 Applying services..."
kubectl apply -f k8s/services.yaml

# Deploy monitoring stack
echo "📊 Deploying monitoring stack..."

# Prometheus
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack \
    --namespace $NAMESPACE \
    --set prometheus.prometheusSpec.retention=30d \
    --set prometheus.prometheusSpec.storageSpec.volumeClaimTemplate.spec.resources.requests.storage=50Gi \
    --set grafana.adminPassword=admin \
    --set grafana.persistence.enabled=true \
    --set grafana.persistence.size=10Gi

# Wait for deployments to be ready
echo "⏳ Waiting for deployments to be ready..."
kubectl wait --for=condition=available deployment/stripeflow-backend -n $NAMESPACE --timeout=300s
kubectl wait --for=condition=available deployment/stripeflow-frontend -n $NAMESPACE --timeout=300s

# Apply ingress
echo "🌍 Applying ingress..."
kubectl apply -f k8s/ingress.yaml

# Run database migrations
echo "🗄️ Running database migrations..."
kubectl run stripeflow-migration --image=$REGISTRY/stripeflow-backend:$IMAGE_TAG \
    --namespace $NAMESPACE \
    --rm -i --restart=Never \
    --env="SPRING_PROFILES_ACTIVE=production" \
    --env="SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-postgresql:5432/stripeflow" \
    --env="SPRING_DATASOURCE_USERNAME=stripeflow" \
    --env="SPRING_DATASOURCE_PASSWORD=stripeflow" \
    --command -- java -jar app.jar --spring.main.web-application-type=none

# Health check
echo "🏥 Performing health check..."
sleep 30

# Check backend health
BACKEND_HEALTH=$(kubectl get pods -l app=stripeflow-backend -n $NAMESPACE -o jsonpath='{.items[0].status.containerStatuses[0].ready}')
if [ "$BACKEND_HEALTH" = "true" ]; then
    echo "✅ Backend is healthy"
else
    echo "❌ Backend health check failed"
    kubectl logs -l app=stripeflow-backend -n $NAMESPACE --tail=50
    exit 1
fi

# Check frontend health
FRONTEND_HEALTH=$(kubectl get pods -l app=stripeflow-frontend -n $NAMESPACE -o jsonpath='{.items[0].status.containerStatuses[0].ready}')
if [ "$FRONTEND_HEALTH" = "true" ]; then
    echo "✅ Frontend is healthy"
else
    echo "❌ Frontend health check failed"
    kubectl logs -l app=stripeflow-frontend -n $NAMESPACE --tail=50
    exit 1
fi

# Get service URLs
echo "🔗 Service URLs:"
kubectl get ingress -n $NAMESPACE

echo "🎉 Deployment completed successfully!"
echo "Backend: https://api.stripeflow.com"
echo "Frontend: https://app.stripeflow.com"
echo "Grafana: https://grafana.stripeflow.com"
echo "Prometheus: https://prometheus.stripeflow.com"


