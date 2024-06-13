FROM maven:3.9.7-eclipse-temurin-22-alpine AS build
COPY src /home/user-serivce/src
COPY pom.xml /home/user-serivce
RUN mvn -f /home/user-serivce/pom.xml clean package

FROM eclipse-temurin:22-jdk-alpine
LABEL org.opencontainers.image.authors="baranouski"
COPY --from=build /home/user-serivce/target/user-service-1.0.0.jar user-service-1.0.0.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","user-service-1.0.0.jar"]