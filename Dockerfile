FROM maven:3.8-jdk-11 AS builder

COPY . /dingtalk-robot
WORKDIR /dingtalk-robot
RUN mvn package

FROM openjdk:11

COPY entrypoint.sh /
COPY --from=builder /dingtalk-robot/target/dingtalk-robot-1.0-SNAPSHOT.jar /
EXPOSE  8080

ENTRYPOINT ["sh", "/entrypoint.sh"]