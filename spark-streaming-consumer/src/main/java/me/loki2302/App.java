package me.loki2302;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        String zookeeperQuorum = "zookeeper.weave.local:2181";
        String kafkaGroup = "group1";
        String kafkaTopic = "the-topic-3";

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
                if(sumOptional.isEmpty()) {
                    System.out.println("no data");
                } else {
                    System.out.printf("sum is %d\n", sumOptional.get(0));
                }

                return null;
            }
        });

        jsc.start();
        jsc.awaitTermination();
    }
}
