FROM gradle:jdk21-alpine as build

ARG DB_USER
ENV env_user $DB_USER
ARG DB_NAME
ENV env_name $name
ARG DB_PASS
ENV env_pass $DB_PASS
WORKDIR /tmp
COPY . /tmp

RUN gradle build
ENTRYPOINT ["java", "-Xms256M","-Xmx312M" ,"-jar","/build/libs/CameraRegistryService-0.0.1-SNAPSHOT.jar"]
#FROM eclipse-temurin:21 as production
#COPY --from=build /tmp/build/libs/CameraRegistryService-0.0.1-SNAPSHOT.jar .
