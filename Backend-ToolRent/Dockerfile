FROM maven:3.9-eclipse-temurin-17 AS builder
#direccion de trabajo dentro del contenedor
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src
#genera el jar
RUN mvn package -DskipTests

#ejecucion
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN mkdir -p /uploads/herramientas && chmod -R 755 /uploads


ARG JAR_FILE=target/ToolRent-Backend.jar
COPY --from=builder /app/${JAR_FILE} ToolRent-Backend.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","ToolRent-Backend.jar"]