FROM gradle:8.10.2-jdk21-alpine AS build

WORKDIR /app

COPY build.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || return 0

COPY src ./src

RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
