# Dockerfile multi-stage de metricas-service.
# Se construye con el contexto en la raíz del repo: docker compose build metricas-service

# ---------- Etapa 1: compilar con JDK + Maven Wrapper ----------
FROM eclipse-temurin:21-jdk AS build
ENV MAVEN_OPTS="-Xmx512m"
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY core-service/pom.xml core-service/
COPY metricas-service/pom.xml metricas-service/
COPY notificaciones-service/pom.xml notificaciones-service/
COPY iot-simulator/pom.xml iot-simulator/
COPY metricas-service/src metricas-service/src
# sed: por si el archivo mvnw llegó con fin de línea de Windows (CRLF)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -B -pl metricas-service -am package -DskipTests

# ---------- Etapa 2: imagen final, solo JRE + el jar ----------
FROM eclipse-temurin:21-jre
# Memoria acotada y arranque más rápido (la PC de desarrollo tiene 6 GB de RAM)
ENV JAVA_TOOL_OPTIONS="-Xmx256m -XX:+UseSerialGC -XX:TieredStopAtLevel=1"
WORKDIR /app
COPY --from=build /app/metricas-service/target/metricas-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
