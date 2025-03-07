FROM maven:3.9-eclipse-temurin-21-alpine as builder
WORKDIR /opt/app
COPY pom.xml ./
COPY ./src ./src
RUN mvn clean install

FROM openjdk:21
WORKDIR /opt/app/
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java","-jar","/opt/app/*.jar"]