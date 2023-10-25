FROM amazoncorretto:17-alpine-jdk as base
RUN apk add maven
COPY . .

FROM base as build
ARG COMMON_UTILS_NEXUS_REPOSITORY
ARG CUSTOM_DATA_NEXUS_REPOSITORY
ARG NEXUS_USER
ARG NEXUS_PASSWORD
RUN mvn install --settings settings.xml -Dnexus-custom-data-library.repository=${CUSTOM_DATA_NEXUS_REPOSITORY} -Dnexus-common-utils-library.repository=${COMMON_UTILS_NEXUS_REPOSITORY} -Denv.NEXUS_USER=${NEXUS_USER} -Denv.NEXUS_PASSWORD=${NEXUS_PASSWORD} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -Dmaven.resolver.transport=wagon
RUN mvn package -Dmaven.skipTests

FROM amazoncorretto:17-alpine-jdk as production
COPY --from=build ./target/transactions-service-*.jar ./app.jar
CMD ["java","-jar","./app.jar"]