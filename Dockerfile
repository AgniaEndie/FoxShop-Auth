FROM gradle:jdk21-alpine as build

WORKDIR /tmp
COPY . /tmp

RUN ls
RUN gradle build

ENTRYPOINT ["java", "-Xms256M","-Xmx312M" ,"-jar","/build/libs/CameraRegistryService-0.0.1-SNAPSHOT.jar"]
