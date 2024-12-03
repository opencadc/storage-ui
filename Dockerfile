FROM images.opencadc.org/library/cadc-tomcat:1

COPY build/libs/storage.war /usr/share/tomcat/webapps/
