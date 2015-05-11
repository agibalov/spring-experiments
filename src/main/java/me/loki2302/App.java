package me.loki2302;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.receiver.Receiver;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;

public class App {
    public static void main(String[] args) {
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
