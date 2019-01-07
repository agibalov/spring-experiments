package me.loki2302;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName("AddNumbers");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        JavaRDD<Integer> data = jsc.parallelize(Arrays.asList(11, 22, 33, 44, 55, 66), 2);

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
}
