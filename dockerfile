FROM gradle:jdk17 AS build
WORKDIR /home/gradle/src
COPY . /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-jdk-alpine
WORKDIR /application
COPY --from=build /home/gradle/src/build/libs/*.jar /application/spring-application.jar
ENTRYPOINT ["java", "-jar", "spring-application.jar"]