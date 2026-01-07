# =============================================================
# Dockerfile para PRODUCCIÓN - Loans Service
# Multi-stage build optimizado
# =============================================================

# Etapa 1: Build
FROM public.ecr.aws/docker/library/maven:3.9-amazoncorretto-21 AS build
WORKDIR /app

# Copiar pom.xml y descargar dependencias (caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime (imagen mínima)
FROM public.ecr.aws/amazoncorretto/amazoncorretto:21
WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 spring && adduser -u 1001 -G spring -D spring
USER spring:spring

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto del servicio
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar con configuración de producción
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
