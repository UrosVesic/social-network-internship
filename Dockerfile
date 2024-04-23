FROM openjdk:17-jdk
COPY target/social-network-0.0.1-SNAPSHOT.jar /service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/service.jar"]