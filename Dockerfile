FROM gradle:jdk21-alpine as build

ARG DB_USER=test
ARG DB_NAME=test
ARG DB_PASS=test
ARG SR_JWT_PRIVATE=test
ARG SR_JWT_PUBLIC=test
ARG SR_ACCESS_EXP_HOURS=1
ARG SR_ACCESS_EXP_MINS=15
ARG SR_REFRESH_EXP_DAYS=30

ENV DB_USER $DB_USER
ENV DB_NAME $DB_NAME
ENV DB_PASS $DB_PASS
ENV SR_JWT_PRIVATE $SR_JWT_PRIVATE
ENV SR_JWT_PUBLIC $SR_JWT_PUBLIC
ENV SR_ACCESS_EXP_HOURS $SR_ACCESS_EXP_HOURS
ENV SR_ACCESS_EXP_MINS $SR_ACCESS_EXP_MINS
ENV SR_REFRESH_EXP_DAYS $SR_REFRESH_EXP_DAYS

WORKDIR /tmp
COPY . /tmp

RUN gradle build

FROM eclipse-temurin:21 as production
COPY --from=build /tmp/build/libs/AuthService-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-Xms256M","-Xmx312M" ,"-jar","AuthService-0.0.1-SNAPSHOT.jar"]