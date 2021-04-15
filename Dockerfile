FROM tomcat:9-jdk11-slim

COPY LocalAuthority.properties /root/config/
COPY build/libs/*.war webapps/

