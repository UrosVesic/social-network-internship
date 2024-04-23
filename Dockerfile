# AS <NAME> to name this stage as maven
FROM maven:3.6.3 AS maven
LABEL MAINTAINER="veljko.bozic@levi9.com"

WORKDIR /usr/app

ADD ./src /usr/app/
ADD ./pom.xml /usr/app/
# Compile and package the application to an executable JAR
RUN mvn clean package -DskipTests

# For Java 
FROM openjdk:18.0.2

EXPOSE 8080

WORKDIR /opt/app

ARG JAR_FILE=target/social-network*.jar

ADD ${JAR_FILE} social-network-image.jar

# Copy the social-network-image.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /usr/app/${JAR_FILE} /opt/app/

ENTRYPOINT ["java","-jar","social-network-image.jar"]