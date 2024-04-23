FROM openjdk:17-jdk
COPY build/libs/aws-ecs-0.0.1-SNAPSHOT.jar /service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/service.jar"]