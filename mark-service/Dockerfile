FROM maven:3.9.7-eclipse-temurin-22-alpine AS build
COPY src /home/mark-serivce/src
COPY pom.xml /home/mark-serivce
RUN mvn -f /home/mark-serivce/pom.xml clean package

FROM eclipse-temurin:22-jdk-alpine
LABEL org.opencontainers.image.authors="baranouski"
COPY --from=build /home/mark-serivce/target/mark-service-1.0.0.jar mark-service-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","mark-service-1.0.0.jar"]