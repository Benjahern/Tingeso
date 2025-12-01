# Etapa 1: Builder
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .

# OPTIMIZACIÓN 1: Usar caché de BuildKit para .m2
# Esto evita descargar dependencias desde cero si cambias el pom.xml o si el go-offline falla
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src

# OPTIMIZACIÓN 2: Compilar en paralelo (-T 1C) y saltar tests
# Usamos la caché de Maven también aquí para acelerar el empaquetado
RUN --mount=type=cache,target=/root/.m2 mvn -T 1C clean package -DskipTests

# Etapa 2: Runtime (Ejecución)
# OPTIMIZACIÓN 3: Usar Alpine en lugar de Jammy (Imagen mucho más liviana = subida más rápida)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalamos dependencias mínimas si son necesarias (Alpine a veces requiere fuentes, etc.)
# Para archivos temporales o subidas, Alpine maneja permisos estándar.
RUN mkdir -p /uploads/herramientas && chmod -R 755 /uploads

# Copia usando comodín (*) para no depender del nombre exacto/versión del jar
COPY --from=builder /app/target/*.jar ToolRent-Backend.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","ToolRent-Backend.jar"]
