package me.loki2302;

import org.apache.commons.cli.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.ConfigException;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Options options = new Options();
        options.addOption(OptionBuilder
                .isRequired()
                .withLongOpt("kafka")
                .hasArg()
                .withArgName("KAFKA_HOST_AND_PORT")
                .withDescription("Kafka host and port, like kafka.weave.local:9092")
                .create("k"));
        options.addOption(OptionBuilder
                .isRequired()
                .withLongOpt("topic")
                .hasArg()
                .withArgName("KAFKA_TOPIC")
                .withDescription("Kafka topic name, like the-topic-3")
                .create("t"));

        if(args.length == 0) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("producer", options);
            return;
        }

        CommandLineParser commandLineParser = new BasicParser();
        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String kafkaHostAndPort = commandLine.getOptionValue("kafka");
        String kafkaTopic = commandLine.getOptionValue("topic");

        System.out.printf("kafka: %s\n", kafkaHostAndPort);
        System.out.printf("topic: %s\n", kafkaTopic);

        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafkaHostAndPort);
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
                producer.send(new ProducerRecord<String, String>(kafkaTopic, key, value)).get();
                System.out.printf("Sent (%s, %s) to %s\n", key, value, kafkaTopic);
                Thread.sleep(5);
            }
            System.out.println("Pause");
            Thread.sleep(300);
        }
    }
}
