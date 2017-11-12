FROM java:8
COPY ./target/slow-kafka-consumer-1.0.0-SNAPSHOT-jar-with-dependencies.jar /tmp/slow-kafka-consumer-1.0.0-SNAPSHOT-jar-with-dependencies.jar
ENV BOOTSTRAP_SERVERS kafka:9092
ENV TOPIC input-topic
ENV GROUP_ID slow-consumer
CMD [ "bash", "-c", "java -jar /tmp/slow-kafka-consumer-1.0.0-SNAPSHOT-jar-with-dependencies.jar --bootstrap-servers ${BOOTSTRAP_SERVERS} --topics ${TOPIC} --group-id ${GROUP_ID}"]