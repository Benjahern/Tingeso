# Guía Esencial de Kubernetes para Microservicios

## 📚 Conceptos Fundamentales

### 1. Arquitectura de Kubernetes

```
┌─────────────────────────────────────────────────────────────┐
│                     KUBERNETES CLUSTER                       │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐                                        │
│  │  CONTROL PLANE  │  ← Cerebro del cluster                 │
│  │  - API Server   │  ← Recibe todos los comandos           │
│  │  - Scheduler    │  ← Decide dónde correr pods            │
│  │  - etcd         │  ← Base de datos del estado            │
│  └─────────────────┘                                        │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   NODE 1     │  │   NODE 2     │  │   NODE 3     │       │
│  │  ┌────────┐  │  │  ┌────────┐  │  │  ┌────────┐  │       │
│  │  │  Pod   │  │  │  │  Pod   │  │  │  │  Pod   │  │       │
│  │  └────────┘  │  │  └────────┘  │  │  └────────┘  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧱 Recursos Principales (Lo que DEBES saber)

### Pod
**¿Qué es?** La unidad más pequeña. Un contenedor (o varios) corriendo juntos.

**Cuándo usarlo:** Casi nunca directamente. Usa Deployments.

**Concepto clave:** Los pods son efímeros - pueden morir y recrearse con otra IP.

---

### Deployment
**¿Qué es?** Gestiona la creación y actualización de Pods.

**Cuándo usarlo:** SIEMPRE para aplicaciones stateless (APIs, frontends).

**Lo que hace por ti:**
- Mantiene X réplicas corriendo
- Rolling updates sin downtime
- Rollback si algo falla

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mi-servicio
spec:
  replicas: 3              # ← Cuántas copias quiero
  selector:
    matchLabels:
      app: mi-servicio     # ← Cómo identifico mis pods
  template:
    spec:
      containers:
      - name: mi-servicio
        image: mi-imagen:v1
        resources:         # ← SIEMPRE define límites
          limits:
            memory: "512Mi"
            cpu: "500m"
```

---

### Service
**¿Qué es?** Un "DNS interno" que da una IP estable a tus pods.

**Por qué es necesario:** Los pods cambian de IP al recrearse. El Service da una IP fija.

#### Tipos de Service:

| Tipo | Uso | Accesible desde |
|------|-----|-----------------|
| **ClusterIP** | Comunicación interna | Solo dentro del cluster |
| **NodePort** | Exponer servicio | Puerto en cada nodo (30000-32767) |
| **LoadBalancer** | Producción en cloud | IP pública externa |

---

#### ClusterIP (Por defecto)

**¿Qué es?** Una IP virtual interna que solo existe dentro del cluster.

```
┌─────────────────── CLUSTER ───────────────────┐
│                                                │
│   Pod A ──────▶ ClusterIP ──────▶ Pod B       │
│                 (10.96.0.1)                    │
│                                                │
│   ✅ Pod A puede acceder                       │
│   ❌ Tu laptop NO puede acceder               │
└────────────────────────────────────────────────┘
```

**Cuándo usarlo:**
- Servicios que solo hablan entre ellos (clients-service → users-service)
- Bases de datos (no quieres exponerlas al exterior)
- Cualquier servicio que NO necesite acceso externo

```yaml
spec:
  type: ClusterIP  # ← O simplemente no poner type (es el default)
  ports:
  - port: 8080
```

---

#### NodePort

**¿Qué es?** Abre un puerto (30000-32767) en TODOS los nodos del cluster.

```
                    ┌─────────────────────────────────┐
Tu laptop ─────────▶│  Node IP:30000                  │
                    │       │                         │
                    │       ▼                         │
                    │   ┌───────┐                     │
                    │   │  Pod  │                     │
                    │   └───────┘                     │
                    └─────────────────────────────────┘
```

**Cuándo usarlo:**
- Desarrollo local con Minikube
- Cuando no tienes LoadBalancer (on-premise)
- Para exponer el Gateway al exterior

```yaml
spec:
  type: NodePort
  ports:
  - port: 8080        # Puerto interno del servicio
    targetPort: 8080  # Puerto del contenedor
    nodePort: 30000   # Puerto externo (30000-32767)
```

**Cómo acceder:**
```bash
# En Minikube
minikube service gateway-service --url

# O directamente
http://<IP-del-nodo>:30000
```

---

#### LoadBalancer

**¿Qué es?** Pide al cloud provider una IP pública con balanceo de carga.

```
                         ┌─────────────────────────────────┐
Internet ───▶ IP Pública │  Load Balancer (AWS/GCP/Azure)  │
              (34.x.x.x) │       │                         │
                         │       ▼                         │
                         │   ┌───────┐  ┌───────┐          │
                         │   │ Pod 1 │  │ Pod 2 │          │
                         │   └───────┘  └───────┘          │
                         └─────────────────────────────────┘
```

**Cuándo usarlo:**
- Producción en la nube (AWS, GCP, Azure)
- Cuando necesitas una IP pública estable

**NO funciona en:**
- Minikube (a menos que uses `minikube tunnel`)
- Clusters on-premise sin MetalLB

```yaml
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
```

---

#### Resumen Visual

```
┌──────────────────────────────────────────────────────────────┐
│                                                              │
│  INTERNET                                                    │
│      │                                                       │
│      ▼                                                       │
│  ┌────────────────┐                                          │
│  │ LoadBalancer   │  ← Solo en cloud (AWS/GCP/Azure)         │
│  │ (IP pública)   │                                          │
│  └───────┬────────┘                                          │
│          │                                                   │
│          ▼                                                   │
│  ┌────────────────┐                                          │
│  │   NodePort     │  ← Acceso desde tu laptop (dev)          │
│  │ (puerto 30xxx) │                                          │
│  └───────┬────────┘                                          │
│          │                                                   │
│          ▼                                                   │
│  ┌────────────────┐                                          │
│  │   ClusterIP    │  ← Solo dentro del cluster               │
│  │ (IP interna)   │                                          │
│  └───────┬────────┘                                          │
│          │                                                   │
│          ▼                                                   │
│      ┌───────┐                                               │
│      │  Pod  │                                               │
│      └───────┘                                               │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mi-servicio
spec:
  selector:
    app: mi-servicio    # ← Conecta con pods que tengan este label
  ports:
  - port: 80            # ← Puerto del servicio
    targetPort: 8080    # ← Puerto del contenedor
  type: ClusterIP       # ← Solo accesible internamente
```

**Cómo se comunican los servicios:**
```
http://nombre-servicio:puerto
http://clients-service:8080
```

---

### ConfigMap
**¿Qué es?** Almacena configuración no sensible.

**Cuándo usarlo:** Variables de entorno, archivos de config, URLs.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  DATABASE_HOST: "postgres"
  LOG_LEVEL: "info"
```

**Cómo usarla en un Deployment:**
```yaml
env:
- name: DATABASE_HOST
  valueFrom:
    configMapKeyRef:
      name: app-config
      key: DATABASE_HOST
```

---

### Secret
**¿Qué es?** Como ConfigMap pero para datos sensibles (passwords, tokens).

**Importante:** Los valores van en Base64 (NO es encriptación, solo encoding).

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
type: Opaque
data:
  username: YWRtaW4=      # echo -n "admin" | base64
  password: cGFzc3dvcmQ=  # echo -n "password" | base64
```

---

## 🏥 Health Checks (Crítico para producción)

### Liveness Probe
**Pregunta:** "¿Estás vivo?"

**Si falla:** Kubernetes MATA el pod y lo reinicia.

### Readiness Probe  
**Pregunta:** "¿Puedes recibir tráfico?"

**Si falla:** Kubernetes deja de enviarle tráfico (pero no lo mata).

```yaml
containers:
- name: mi-app
  livenessProbe:
    httpGet:
      path: /actuator/health
      port: 8080
    initialDelaySeconds: 30  # ← Espera antes de empezar a chequear
    periodSeconds: 10        # ← Cada cuánto chequea
  readinessProbe:
    httpGet:
      path: /actuator/health/readiness
      port: 8080
    initialDelaySeconds: 5
    periodSeconds: 5
```

---

## 🔧 Comandos kubectl Esenciales

```bash
# Ver recursos
kubectl get pods                    # Lista pods
kubectl get services               # Lista servicios
kubectl get all                    # Lista todo

# Ver detalles
kubectl describe pod <nombre>      # Info detallada
kubectl logs <pod>                 # Ver logs
kubectl logs -f <pod>              # Logs en tiempo real

# Debugging
kubectl exec -it <pod> -- /bin/sh  # Entrar al contenedor
kubectl port-forward <pod> 8080:80 # Túnel local

# Aplicar cambios
kubectl apply -f archivo.yaml      # Crear/actualizar
kubectl delete -f archivo.yaml    # Eliminar

# Diagnóstico
kubectl get events                 # Ver eventos del cluster
kubectl top pods                   # Ver uso de recursos
```

---

## 🎯 Patrones Comunes en Microservicios

### 1. Comunicación entre servicios
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Gateway   │───▶│   Service   │───▶│   Service   │
│  (NodePort) │    │  (ClusterIP)│    │  (ClusterIP)│
└─────────────┘    └─────────────┘    └─────────────┘
       ↑
    Usuario
```

### 2. Base de datos compartida
```yaml
# La DB es un Service ClusterIP
# Todos los microservicios se conectan a:
# jdbc:postgresql://postgres-service:5432/database
```

### 3. Orden de deployment
```
1. ConfigMaps y Secrets  ← Primero la configuración
2. Base de datos         ← Luego la persistencia
3. Config Server         ← Si usas Spring Cloud Config
4. Eureka/Service Mesh   ← Descubrimiento de servicios
5. Gateway               ← Punto de entrada
6. Microservicios        ← Finalmente la lógica de negocio
```

---

## ⚠️ Errores Comunes y Cómo Arreglarlos

| Error | Causa | Solución |
|-------|-------|----------|
| `ImagePullBackOff` | No encuentra la imagen | Verifica nombre de imagen y registry |
| `CrashLoopBackOff` | App crashea constantemente | Revisa logs: `kubectl logs <pod>` |
| `Pending` | No hay recursos disponibles | Reduce requests o agrega nodos |
| `OOMKilled` | Se quedó sin memoria | Aumenta `limits.memory` |

---

## 📝 Checklist antes de Deploy

- [ ] ¿Definí `resources.limits` para CPU y memoria?
- [ ] ¿Configuré health checks (liveness/readiness)?
- [ ] ¿Los secrets están en Secrets, no en ConfigMaps?
- [ ] ¿El Service tiene el selector correcto?
- [ ] ¿Las variables de entorno apuntan a nombres de Service, no IPs?

---

## 🔗 Flujo Típico de Deploy

```bash
# 1. Aplicar ConfigMaps y Secrets
kubectl apply -f configmaps/
kubectl apply -f secrets/

# 2. Aplicar Deployments y Services
kubectl apply -f deployments/

# 3. Verificar estado
kubectl get pods -w  # Watch en tiempo real

# 4. Si algo falla
kubectl describe pod <nombre-del-pod>
kubectl logs <nombre-del-pod>
```

---

## 💡 Tip Final

> **No memorices YAML.** Entiende QUÉ hace cada recurso y POR QUÉ lo necesitas.
> La IA puede generar el código, pero tú necesitas saber si está bien y debuggear cuando falla.
