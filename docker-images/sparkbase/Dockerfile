FROM ubuntu:14.10

MAINTAINER loki2302

ENV SPARK_VERSION 1.3.1

RUN apt-get update && apt-get install -y curl wget openjdk-7-jdk
ADD download-spark.sh /tmp/download-spark.sh
RUN /tmp/download-spark.sh
RUN tar xf /tmp/spark-${SPARK_VERSION}.tgz -C /opt

CMD ls /opt
