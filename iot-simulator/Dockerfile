# Dockerfile multi-stage del simulador IoT (Java puro).
# Se construye con el contexto en la raíz del repo: docker compose build iot-simulator

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
COPY iot-simulator/src iot-simulator/src
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -B -pl iot-simulator -am package -DskipTests

# ---------- Etapa 2: imagen final ----------
# Solo usa java.base, java.net.http y java.logging, que vienen en el JRE.
FROM eclipse-temurin:21-jre
ENV JAVA_TOOL_OPTIONS="-Xmx64m -XX:+UseSerialGC"
WORKDIR /app
COPY --from=build /app/iot-simulator/target/iot-simulator.jar app.jar
ENTRYPOINT ["java", "-Djava.util.logging.SimpleFormatter.format=%1$tF %1$tT %4$s %3$s - %5$s%6$s%n", "-jar", "app.jar"]
