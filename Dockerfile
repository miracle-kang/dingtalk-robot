FROM openjdk:11

COPY entrypoint.sh /
COPY target/dingtalk-robot-1.0-SNAPSHOT.jar /
EXPOSE  8080

ENTRYPOINT ["sh", "/entrypoint.sh"]