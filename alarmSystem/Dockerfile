FROM openjdk:8-jre

MAINTAINER wanghan

RUN apt-get update && apt-get install -y \
    wget \
    curl \
    coreutils \
	vim \
	net-tools

#COPY target /app

EXPOSE 8087

WORKDIR /app

ENTRYPOINT ["java","-jar","alarm-system.jar"]
