package me.loki2302;

import org.apache.commons.cli.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(OptionBuilder
                .isRequired()
                .withLongOpt("kafka-zk-connect")
                .hasArg()
                .withArgName("KAFKA_ZK_CONNECT_HOST_AND_PORT")
                .withDescription("Kafka ZooKeeper connect host and port, zookeeper.weave.lan:2181")
                .create("k"));
        options.addOption(OptionBuilder
                .isRequired()
                .withLongOpt("topic")
                .hasArg()
                .withArgName("KAFKA_TOPIC")
                .withDescription("Kafka topic name, like the-topic-3")
                .create("t"));
        options.addOption(OptionBuilder
                .isRequired()
                .withLongOpt("console")
                .hasArg()
                .withArgName("CONSOLE")
                .withDescription("Console URL, like http://console.weave.lan:8080/")
                .create("t"));

        if(args.length == 0) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("consumer", options);
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

        final String zookeeperQuorum = commandLine.getOptionValue("kafka-zk-connect");
        final String kafkaTopic = commandLine.getOptionValue("topic");
        final String consoleUrl = commandLine.getOptionValue("console");

        System.out.printf("zookeeperQuorum: %s\n", zookeeperQuorum);
        System.out.printf("topic: %s\n", kafkaTopic);
        System.out.printf("console: %s\n", consoleUrl);

        final String kafkaGroup = "group1";

        //SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("KafkaConsumer");
        SparkConf sparkConf = new SparkConf().setAppName("KafkaConsumer");
        JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, Durations.seconds(5));

        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        topicMap.put(kafkaTopic, 1);
        JavaPairReceiverInputDStream<String, String> kafkaStream =
                KafkaUtils.createStream(jsc, zookeeperQuorum, kafkaGroup, topicMap);
        JavaDStream<Integer> values = kafkaStream.map(new Function<Tuple2<String, String>, Integer>() {
            @Override
            public Integer call(Tuple2<String, String> v1) throws Exception {
                System.out.printf("MAP: %s\n", v1._2());
                return Integer.parseInt(v1._2());
            }
        });
        JavaDStream<Integer> sum = values.reduce(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                System.out.printf("REDUCE: %d + %d\n", v1, v2);
                return v1 + v2;
            }
        });
        sum.foreachRDD(new Function<JavaRDD<Integer>, Void>() {
            @Override
            public Void call(JavaRDD<Integer> v1) throws Exception {
                List<Integer> sumOptional = v1.collect();
                System.out.printf("FOREACH: size=%d\n", sumOptional.size());

                Integer sumOrNull = sumOptional.isEmpty() ? null : sumOptional.get(0);
                if(sumOrNull == null) {
                    System.out.println("no data");
                } else {
                    System.out.printf("sum is %d\n", sumOrNull);
                }

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(new MappingJackson2HttpMessageConverter()));
                UpdateDTO updateDTO = new UpdateDTO();
                updateDTO.sum = sumOrNull;
                restTemplate.put(consoleUrl, updateDTO);

                return null;
            }
        });

        jsc.start();
        jsc.awaitTermination();
    }

    public static class UpdateDTO {
        public Integer sum;
    }
}
