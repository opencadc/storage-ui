FROM eclipse-temurin:11-alpine AS base

FROM base AS builder

COPY . /storage-ui
WORKDIR /storage-ui
RUN apk --no-cache add git \
    && git fetch origin main \
    && ./gradlew -i clean spotlessCheck build test --no-daemon

FROM images.opencadc.org/library/cadc-tomcat:1.3 AS production

RUN mkdir -p /usr/share/tomcat/config

COPY --from=builder /storage-ui/build/libs/storage.war /usr/share/tomcat/webapps/
