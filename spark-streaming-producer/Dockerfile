FROM aglover/java8-pier
ADD spark-streaming-producer-1.0.tar /
WORKDIR /spark-streaming-producer-1.0/bin
ENV KAFKA meaningless default KAFKA value
ENV TOPIC meaningless default TOPIC value
CMD ./spark-streaming-producer --kafka ${KAFKA} --topic ${TOPIC}
