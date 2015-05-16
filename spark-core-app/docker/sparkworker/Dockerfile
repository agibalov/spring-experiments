FROM loki2302/sparkbase:latest

MAINTAINER loki2302

EXPOSE 8081

CMD /opt/spark-1.3.1-bin-hadoop2.6/bin/spark-class org.apache.spark.deploy.worker.Worker spark://${SPARK_MASTER} --cores 1 --webui-port ${WEBUI_PORT}


