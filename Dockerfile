FROM openjdk:21
WORKDIR /opt/app/
EXPOSE 8080
COPY target/*.jar /opt/app/*.jar
ENTRYPOINT ["java","-jar","/opt/app/*.jar"]