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
    minikube start --cpus=8 --memory=7500 --driver=kvm2
fi
echo -e "${GREEN}✅ Minikube OK${NC}"
echo ""

# Obtener IP de Minikube
MINIKUBE_IP=$(minikube ip)
echo -e "${YELLOW}📌 IP de Minikube: ${MINIKUBE_IP}${NC}"
echo ""

# Ir al directorio de deployments
cd "$(dirname "$0")"
echo -e "${YELLOW}📁 Directorio: $(pwd)${NC}"
echo ""

# =============================================================
# PASO 0: Actualizar IPs dinámicamente en los archivos YAML
# =============================================================
echo -e "${YELLOW}🔧 Actualizando IPs en archivos de configuración...${NC}"

# Actualizar keycloak.yaml con la IP correcta
sed -i "s|value: \"[0-9.]*:[0-9]*\"|value: \"${MINIKUBE_IP}:31000\"|g" keycloak.yaml

# Actualizar microservices-config-map.yaml con las IPs correctas
sed -i "s|KEYCLOAK_EXTERNAL_URL:.*|KEYCLOAK_EXTERNAL_URL: \"http://${MINIKUBE_IP}:31000\"|g" microservices-config-map.yaml
sed -i "s|KEYCLOAK_ISSUER_URI:.*|KEYCLOAK_ISSUER_URI: \"http://${MINIKUBE_IP}:31000/realms/tingeso\"|g" microservices-config-map.yaml
sed -i "s|KEYCLOAK_JWK_SET_URI:.*|KEYCLOAK_JWK_SET_URI: \"http://${MINIKUBE_IP}:31000/realms/tingeso/protocol/openid-connect/certs\"|g" microservices-config-map.yaml

echo -e "${GREEN}✅ IPs actualizadas para ${MINIKUBE_IP}${NC}"
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
echo "   Esperando a que Keycloak esté listo (puede tardar ~60s)..."
kubectl wait --for=condition=ready pod -l app=keycloak --timeout=180s || {
    echo -e "${RED}⚠️ Keycloak tardó más de lo esperado. Verificando logs...${NC}"
    kubectl logs -l app=keycloak --tail=20
}
echo -e "${GREEN}✅ Keycloak desplegado${NC}"

# Verificar que Keycloak responde
echo -e "${YELLOW}🔍 Verificando que Keycloak responde...${NC}"
KEYCLOAK_READY=false
for i in {1..30}; do
    if curl -s --connect-timeout 2 "http://${MINIKUBE_IP}:31000/realms/tingeso" > /dev/null 2>&1; then
        KEYCLOAK_READY=true
        break
    fi
    echo "   Intento $i/30 - Esperando a Keycloak..."
    sleep 2
done

if [ "$KEYCLOAK_READY" = true ]; then
    echo -e "${GREEN}✅ Keycloak está respondiendo correctamente${NC}"
else
    echo -e "${YELLOW}⚠️ Keycloak aún no responde. Continúa el despliegue pero verifica después.${NC}"
fi
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
echo -e "${YELLOW}📌 URLs de acceso:${NC}"
echo "   Frontend:        http://${MINIKUBE_IP}:30080"
echo "   Gateway API:     http://${MINIKUBE_IP}:30000"
echo "   Keycloak Admin:  http://${MINIKUBE_IP}:31000/admin"
echo "   Keycloak Realm:  http://${MINIKUBE_IP}:31000/realms/tingeso"
echo ""
echo -e "${YELLOW}📌 Comandos útiles:${NC}"
echo "   kubectl get pods                    # Ver estado de pods"
echo "   kubectl logs -f deployment/X        # Ver logs de un servicio"
echo "   minikube service gateway-service --url  # Obtener URL del gateway"
echo "   minikube dashboard                  # Abrir dashboard gráfico"
echo ""
