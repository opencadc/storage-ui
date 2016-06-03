FROM canfar/tomcat

ARG BEACON_OPTS=""

ENV INIT_JAVA_OPTS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555 -Djava.security.egd=file:/dev/./urandom -Djsse.enableSNIExtension=false -Dca.nrc.cadc.auth.BasicX509TrustManager.trust=true"
ENV JAVA_OPTS "${INIT_JAVA_OPTS} ${BEACON_OPTS}"

COPY beacon.war webapps/
