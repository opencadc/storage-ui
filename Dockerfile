# This is the Docker hub location for the User Storage User Interface (Project Beacon)
FROM opencadc/storage

# The JAVA_OPTS variable to pass to Tomcat.  Note the -Dca.nrc.cadc.reg.client.RegistryClient.host property.
ENV JAVA_OPTS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555 -Djava.security.egd=file:/dev/./urandom -Djsse.enableSNIExtension=false"
