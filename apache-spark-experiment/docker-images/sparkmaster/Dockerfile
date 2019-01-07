FROM loki2302/sparkbase:latest

MAINTAINER loki2302

EXPOSE 7077 4040 8080

ENV SPARK_MASTER_IP meaningless SPARK_MASTER_IP value here

CMD /opt/spark-1.3.1-bin-hadoop2.6/sbin/start-master.sh && tail -F /opt/spark-1.3.1-bin-hadoop2.6/logs/spark*
