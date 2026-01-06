#!/bin/bash
# =============================================================
# Script de Despliegue Automático a Minikube
# =============================================================
# Uso: ./deploy-minikube.sh
# =============================================================

set -e  # Detener si hay errores

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Iniciando despliegue a Minikube...${NC}"
echo ""

# Verificar que Minikube está corriendo
echo -e "${YELLOW}📋 Verificando Minikube...${NC}"
if ! minikube status | grep -q "Running"; then
    echo -e "${RED}❌ Minikube no está corriendo. Iniciándolo...${NC}"
    minikube start --cpus=4 --memory=8192 --driver=kvm2
fi
echo -e "${GREEN}✅ Minikube OK${NC}"
echo ""

# Ir al directorio de deployments
cd "$(dirname "$0")"
echo -e "${YELLOW}📁 Directorio: $(pwd)${NC}"
echo ""

# =============================================================
# PASO 1: ConfigMaps y Secrets
# =============================================================
echo -e "${YELLOW}🔐 Aplicando ConfigMaps y Secrets...${NC}"
kubectl apply -f postgres-config-map.yaml
kubectl apply -f postgres-secret.yaml
kubectl apply -f postgres-init-configmap.yaml
kubectl apply -f microservices-config-map.yaml
kubectl apply -f keycloak-secret.yaml
kubectl apply -f keycloak-realm-configmap.yaml
echo -e "${GREEN}✅ ConfigMaps y Secrets aplicados${NC}"
echo ""

# =============================================================
# PASO 2: PostgreSQL
# =============================================================
echo -e "${YELLOW}🐘 Desplegando PostgreSQL...${NC}"
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-service.yaml
echo "   Esperando a que PostgreSQL esté listo..."
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s
echo -e "${GREEN}✅ PostgreSQL listo${NC}"
echo ""

# =============================================================
# PASO 3: Keycloak
# =============================================================
echo -e "${YELLOW}🔑 Desplegando Keycloak...${NC}"
kubectl apply -f keycloak.yaml
echo -e "${GREEN}✅ Keycloak desplegado${NC}"
echo ""

# =============================================================
# PASO 4: Infraestructura (Eureka, Config, Gateway)
# =============================================================
echo -e "${YELLOW}🏗️ Desplegando infraestructura...${NC}"
kubectl apply -f eureka-service.yaml
kubectl apply -f eureka-service-service.yaml 2>/dev/null || true
echo "   Esperando a que Eureka inicie (30s)..."
sleep 30

kubectl apply -f config-service.yaml
echo "   Esperando a que Config Service inicie (20s)..."
sleep 20

kubectl apply -f gateway-service.yaml
echo -e "${GREEN}✅ Infraestructura desplegada${NC}"
echo ""

# =============================================================
# PASO 5: Microservicios de negocio (todos en paralelo)
# =============================================================
echo -e "${YELLOW}📦 Desplegando microservicios...${NC}"
kubectl apply -f inventory-service.yaml
kubectl apply -f clients-service.yaml
kubectl apply -f kardex-service.yaml
kubectl apply -f loans-service.yaml
kubectl apply -f rates-service.yaml
kubectl apply -f reports-service.yaml
kubectl apply -f users-service.yaml
echo -e "${GREEN}✅ Microservicios desplegados${NC}"
echo ""

# =============================================================
# PASO 6: Frontend
# =============================================================
echo -e "${YELLOW}🖥️ Desplegando Frontend...${NC}"
kubectl apply -f frontend-service.yaml
echo -e "${GREEN}✅ Frontend desplegado${NC}"
echo ""

# =============================================================
# PASO 6: Verificación final
# =============================================================
echo -e "${YELLOW}📊 Estado de los pods:${NC}"
echo ""
kubectl get pods
echo ""

echo -e "${GREEN}🎉 ¡Despliegue completado!${NC}"
echo ""
echo -e "${YELLOW}📌 Comandos útiles:${NC}"
echo "   kubectl get pods                    # Ver estado de pods"
echo "   kubectl logs -f deployment/X        # Ver logs de un servicio"
echo "   minikube service gateway-service --url  # Obtener URL del gateway"
echo "   minikube dashboard                  # Abrir dashboard gráfico"
echo ""
