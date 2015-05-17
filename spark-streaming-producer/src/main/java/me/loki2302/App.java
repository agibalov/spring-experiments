package me.loki2302;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String topicName = "the-topic";

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("client.id", "DemoProducer");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        Random r = new Random();
        while(true) {
            for (int i = 0; i < 10; ++i) {
                String key = String.format("key-%d", i + 1);
                String value = String.valueOf(r.nextInt(1000));
                producer.send(new ProducerRecord<String, String>(topicName, key, value)).get();
                Thread.sleep(5);
            }
            Thread.sleep(300);
        }
    }
}
