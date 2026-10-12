# Multi-stage Dockerfile for Java 8+ / 17 Runtime
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN javac -d bin src/main/java/com/juancovili/fintech/model/*.java \
              src/main/java/com/juancovili/fintech/service/*.java \
              src/main/java/com/juancovili/fintech/parser/*.java \
              src/main/java/com/juancovili/fintech/repository/*.java \
              src/main/java/com/juancovili/fintech/controller/*.java \
              src/main/java/com/juancovili/fintech/Main.java

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/bin ./bin
COPY --from=builder /app/samples ./samples
COPY --from=builder /app/sql ./sql

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "bin", "com.juancovili.fintech.Main"]
