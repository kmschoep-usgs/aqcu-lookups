FROM maven:3.6.0-jdk-8-alpine AS build

#Pass build args into env vars
ARG CI
ENV CI=$CI

ARG SONAR_HOST_URL
ENV SONAR_HOST_URL=$SONAR_HOST_URL

ARG SONAR_LOGIN
ENV SONAR_LOGIN=$SONAR_LOGIN

COPY pom.xml /build/pom.xml

#copy the host's maven repository cache into the container
COPY .m2/repository /root/.m2/repository


WORKDIR /build

RUN if getent ahosts "sslhelp.doi.net" > /dev/null 2>&1; then \
		wget 'http://sslhelp.doi.net/docs/DOIRootCA2.cer' && \
		keytool -import -trustcacerts -file DOIRootCA2.cer -alias DOIRootCA2.cer -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit; \
	fi

#download all maven dependencies (this will only re-run if the pom has changed)
#suppress INFO-level logs about dependency downloads to permit the build to succed within Travis' log length limits
RUN mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn dependency:go-offline

COPY dependency-check-suppression.xml /build
COPY .git /build
COPY src /build/src
ARG BUILD_COMMAND="mvn -B clean verify"
RUN ${BUILD_COMMAND}

FROM usgswma/wma-spring-boot-base:8-jre-slim-0.0.4

ENV serverPort=7503
ENV aquariusServiceEndpoint=http://aquarius.test.gov
ENV aquariusServiceUser=changeme
ENV oauthResourceId=resource-id
ENV oauthResourceTokenKeyUri=https://example.gov/oauth/token_key
ENV HEALTHY_RESPONSE_CONTAINS='{"status":"UP"}'
ENV HEALTH_CHECK_ENDPOINT=actuator/health

COPY --chown=1000:1000 --from=build /build/target/*.jar app.jar
