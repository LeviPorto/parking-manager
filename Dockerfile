# ===== STAGE 1: build =====
FROM gradle:8.7-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle clean bootJar --no-daemon

# ===== STAGE 2: runtime =====
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 3003

ENTRYPOINT ["java", "-jar", "app.jar"]