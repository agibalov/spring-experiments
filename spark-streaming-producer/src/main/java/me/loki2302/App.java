package me.loki2302;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.ConfigException;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String topicName = "the-topic-3";

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "kafka.weave.local:9092");
        properties.put("client.id", "DemoProducer");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = null;
        for(int i = 1; i <= 10; ++i) {
            try {
                System.out.printf("Connecting to kafka, attempt %d\n", i);
                producer = new KafkaProducer<String, String>(properties);
                System.out.println("Connected successfully");
                break;
            } catch (ConfigException e) {
                System.out.printf("Attempt %d failed: %s\n", i, e.getMessage());
                Thread.sleep(5000);
                continue;
            }
        }

        if(producer == null)
        {
            System.out.println("Failed to connect to kafka");
            return;
        }

        Random r = new Random();
        while(true) {
            for (int i = 0; i < 10; ++i) {
                String key = String.format("key-%d", i + 1);
                String value = String.valueOf(r.nextInt(1000));
                producer.send(new ProducerRecord<String, String>(topicName, key, value)).get();
                System.out.printf("Sent (%s, %s) to %s\n", key, value, topicName);
                Thread.sleep(5);
            }
            System.out.println("Pause");
            Thread.sleep(300);
        }
    }
}
