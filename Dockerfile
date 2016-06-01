FROM mach378.cadc.dao.nrc.ca:5000/tomcat:alpine

ENV JAVA_OPTS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555 -Dca.nrc.cadc.reg.client.RegistryClient.host=jenkinsd.cadc.dao.nrc.ca -Dca.nrc.cadc.auth.BasicX509TrustManager.trust=true"

COPY beacon.war webapps/
COPY cadcproxy.pem /root/.ssl/cadcproxy.pem
