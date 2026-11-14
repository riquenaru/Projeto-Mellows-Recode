FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src
COPY frontend frontend
COPY Img Img
WORKDIR /workspace/backend
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/backend/target/mellows-api-1.0.0-SNAPSHOT.jar app.jar
RUN groupadd --system mellows && useradd --system --gid mellows --home-dir /app --shell /usr/sbin/nologin mellows
USER mellows
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
