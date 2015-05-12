package me.loki2302;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.receiver.Receiver;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("helloworld");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        JavaRDD<Integer> data = jsc.parallelize(Arrays.asList(11, 22, 33, 44, 55, 66), 2);

        final Map<Long, Integer> activityMap = new HashMap<>();

        int sum = data.reduce(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                long threadId = Thread.currentThread().getId();
                System.out.printf("[thread=%d]: %d + %d => %d\n",
                        threadId, v1, v2, v1 + v2);
                return v1 + v2;
            }
        });

        jsc.stop();

        System.out.println(sum);
    }

    private static void kafkaConsumptionSparkStreamingHelloWorld() {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("helloworld");
        JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, Durations.seconds(1));

        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        topicMap.put("the-topic", 1);
        JavaPairReceiverInputDStream<String, String> kafkaStream =
                KafkaUtils.createStream(jsc, "localhost", "group1", topicMap);
        JavaDStream<Integer> values = kafkaStream.map(new Function<Tuple2<String, String>, Integer>() {
            @Override
            public Integer call(Tuple2<String, String> v1) throws Exception {
                return Integer.parseInt(v1._2());
            }
        });
        JavaDStream<Integer> sum = values.reduce(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        sum.foreachRDD(new Function<JavaRDD<Integer>, Void>() {
            @Override
            public Void call(JavaRDD<Integer> v1) throws Exception {
                List<Integer> sumOptional = v1.collect();
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

    private static void customReceiverSparkStreamingHelloWorld() {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("helloworld");
        JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, Durations.seconds(1));
        JavaReceiverInputDStream<String> lines = jsc.receiverStream(new DummyReceiver());

        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(String s) throws Exception {
                return Arrays.asList(s.split(" "));
            }
        });
        JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        });
        JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        wordCounts.foreachRDD(new Function2<JavaPairRDD<String, Integer>, Time, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Integer> v1, Time v2) throws Exception {
                System.out.println("**********************************");
                System.out.printf("time=%s\n", v2);
                Map<String, Integer> m = v1.collectAsMap();
                for(String k : m.keySet()) {
                    System.out.printf("'%s' -> %d\n", k, m.get(k));
                }
                System.out.println("**********************************");

                return null;
            }
        });

        jsc.start();
        jsc.awaitTermination();
    }

    public static class DummyReceiver extends Receiver<String> {
        public DummyReceiver() {
            super(StorageLevel.MEMORY_ONLY());
        }

        @Override
        public void onStart() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean invert = false;
                    while(!isStopped()) {
                        if(!invert) {
                            store("hello hello world");
                        } else {
                            store("hello world world");
                        }
                        invert = !invert;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onStop() {
            // intentionally blank:
            // the worker thread's Runnable monitors isStopped()
        }

        @Override
        public StorageLevel storageLevel() {
            return StorageLevel.MEMORY_ONLY(); // why do I need to duplicate it?
        }
    }
}
