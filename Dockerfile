FROM mach378.cadc.dao.nrc.ca:5000/gradle:alpine

ENV SUPER_OPTS $JAVA_OPTS
ENV JAVA_OPTS "$SUPER_OPTS -Dca.nrc.cadc.reg.client.RegistryClient.host=jenkinsd.cadc.dao.nrc.ca -Dca.nrc.cadc.auth.BasicX509TrustManager.trust=true"

WORKDIR /usr/local/src/brite

COPY . .

RUN gradle clean build
CMD ["/usr/local/bin/gradle", "run"]

