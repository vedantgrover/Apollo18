FROM maven:3.9-eclipse-temurin-26 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

FROM eclipse-temurin:26-jre-noble
RUN apt-get update && apt-get install -y --no-install-recommends fontconfig fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /build/target/Apollo18-*.jar app.jar
COPY src/main/resources/stock_data ./data/stock_data
RUN useradd -r apollo && chown -R apollo:apollo /app
USER apollo
ENV STOCK_DATA_DIR=/app/data/stock_data
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-Djava.awt.headless=true","-jar","app.jar"]
