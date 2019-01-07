FROM loki2302/sparkbase:latest
WORKDIR /opt/spark-1.3.1-bin-hadoop2.6
ENV SPARK_MASTER meaningless SPARK_MASTER value
ENV KAFKA_ZK_CONNECT meaningless default KAFKA_ZK_CONNECT value
ENV TOPIC meaningless default TOPIC value
ENV CONSOLE meaningless default CONSOLE value
CMD ./bin/spark-submit --master ${SPARK_MASTER} /spark-streaming-consumer-1.0-all.jar --kafka-zk-connect ${KAFKA_ZK_CONNECT} --topic ${TOPIC} --console ${CONSOLE}
