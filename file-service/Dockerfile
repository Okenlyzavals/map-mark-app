FROM maven:3.9.7-eclipse-temurin-22-alpine AS build
COPY src /home/file-serivce/src
COPY pom.xml /home/file-serivce
RUN mvn -f /home/file-serivce/pom.xml clean package

FROM eclipse-temurin:22-jdk-alpine
LABEL org.opencontainers.image.authors="baranouski"
COPY --from=build /home/file-serivce/target/file-service-1.0.0.jar file-service-1.0.0.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","file-service-1.0.0.jar"]