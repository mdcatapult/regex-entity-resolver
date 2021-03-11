FROM openjdk:14
COPY target/scala-2.13/webservice.jar /webservice.jar
ENTRYPOINT java $JAVA_OPTS -jar /webservice.jar --config /srv/common.conf