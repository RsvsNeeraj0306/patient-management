FROM maven:3.8-openjdk-17 AS builder

WORKDIR /app

COPY billing-service/pom.xml ./

RUN mvn dependency:go-offline -B

COPY billing-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre AS runner

WORKDIR /app

COPY --from=builder /app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 5000

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]