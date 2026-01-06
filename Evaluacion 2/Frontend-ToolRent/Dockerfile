# =============================================================
# Dockerfile para PRODUCCIÓN - Frontend React/Vite
# Multi-stage build optimizado con Nginx
# =============================================================

# Etapa 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Copiar package.json y instalar dependencias
COPY package*.json ./
RUN npm ci --only=production=false

# Copiar código fuente y construir
COPY . .
RUN npm run build

# Etapa 2: Servir con Nginx
FROM nginx:alpine

# Copiar la configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar los archivos construidos
COPY --from=build /app/dist /usr/share/nginx/html

# Puerto de Nginx
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Ejecutar Nginx en foreground
CMD ["nginx", "-g", "daemon off;"]