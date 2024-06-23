FROM gradle:jdk21-alpine as build

ARG DB_USER=test
ARG DB_NAME=test
ARG DB_PASS=test

ENV DB_USER $DB_USER
ENV DB_NAME $DB_NAME
ENV DB_PASS $DB_PASS

WORKDIR /tmp
COPY . /tmp

RUN gradle build

ENTRYPOINT ["java", "-Xms256M","-Xmx312M" ,"-jar","/tmp/build/libs/AuthService-0.0.1-SNAPSHOT.jar"]
#FROM eclipse-temurin:21 as production
#COPY --from=build /tmp/build/libs/CameraRegistryService-0.0.1-SNAPSHOT.jar .
