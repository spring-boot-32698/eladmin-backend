# ---- build ----
FROM maven:3.8-eclipse-temurin-8 AS build
WORKDIR /src
COPY pom.xml .
COPY eladmin-common/pom.xml eladmin-common/pom.xml
COPY eladmin-logging/pom.xml eladmin-logging/pom.xml
COPY eladmin-tools/pom.xml eladmin-tools/pom.xml
COPY eladmin-generator/pom.xml eladmin-generator/pom.xml
COPY eladmin-system/pom.xml eladmin-system/pom.xml
RUN mvn -q -B dependency:go-offline -DskipTests || true
COPY . .
RUN mvn -B -q clean package -DskipTests

# ---- run ----
FROM eclipse-temurin:8-jre
# fontconfig + fonts are required by the captcha generator
RUN apt-get update && apt-get install -y --no-install-recommends fontconfig fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /src/eladmin-system/target/eladmin-system-*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
