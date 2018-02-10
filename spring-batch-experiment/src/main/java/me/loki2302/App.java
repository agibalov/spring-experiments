package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job myJob;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("*** ONE ***");
        if(true) {
            JobExecution jobExecution = jobLauncher.run(
                    myJob,
                    new JobParametersBuilder().toJobParameters());
            BatchStatus batchStatus = jobExecution.getStatus();
            LOGGER.info("status = {}", batchStatus);
        }

        LOGGER.info("*** TWO ***");
        if(true) {
            JobExecution jobExecution = jobLauncher.run(
                    myJob,
                    new JobParametersBuilder().toJobParameters());
            BatchStatus batchStatus = jobExecution.getStatus();
            LOGGER.info("status = {}", batchStatus);
        }
    }

    @Configuration
    public static class MyJobConfiguration {
        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job myJob() {
            return jobBuilderFactory.get("myJob")
                    .start(myStepOne())
                    .next(myStepTwo())
                    .listener(myJobExecutionListener())
                    .build();
        }

        @Bean
        public MyJobExecutionListener myJobExecutionListener() {
            return new MyJobExecutionListener();
        }

        @Bean
        public Step myStepOne() {
            return stepBuilderFactory.get("myStepOne")
                    .<String, String>chunk(1)
                    .reader(myItemReader())
                    .listener(myItemReadListener())
                    .processor(myItemProcessor())
                    .writer(myItemWriter())
                    .allowStartIfComplete(true) // otherwise it won't rerun
                    .build();
        }

        @Bean
        public Step myStepTwo() {
            return stepBuilderFactory.get("myStepTwo")
                    .tasklet(new Tasklet() {
                        private final Logger LOGGER = LoggerFactory.getLogger(Tasklet.class);

                        @Override
                        public RepeatStatus execute(
                                StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {

                            LOGGER.info("I am tasklet!");

                            return RepeatStatus.FINISHED;
                        }
                    })
                    .allowStartIfComplete(true) // otherwise it won't rerun
                    .build();
        }

        @Bean
        public ItemReader<String> myItemReader() {
            List<String> items = Arrays.asList("one", "two", "three");
            return new ListItemReader<>(items);
        }

        @Bean
        public MyItemProcessor myItemProcessor() {
            return new MyItemProcessor();
        }

        @Bean
        public MyItemReadListener myItemReadListener() {
            return new MyItemReadListener();
        }

        @Bean
        public ItemWriter<String> myItemWriter() {
            return new ItemWriter<String>() {
                private final Logger LOGGER = LoggerFactory.getLogger(ItemWriter.class);

                @Override
                public void write(List<? extends String> items) throws Exception {
                    LOGGER.info("Writer {}", items);
                }
            };
        }

        public static class MyItemProcessor implements ItemProcessor<String, String> {
            private final static Logger LOGGER = LoggerFactory.getLogger(MyItemProcessor.class);

            @Override
            public String process(String item) throws Exception {
                LOGGER.info("Processing {}", item);
                return item + "!";
            }
        }

        public static class MyItemReadListener implements ItemReadListener<String> {
            private final Logger LOGGER = LoggerFactory.getLogger(MyItemReadListener.class);

            @Override
            public void beforeRead() {
                LOGGER.info("About to read item");
            }

            @Override
            public void afterRead(String item) {
                LOGGER.info("Read {}", item);
            }

            @Override
            public void onReadError(Exception ex) {
                LOGGER.error("Failed to read item");
            }
        }

        public static class MyJobExecutionListener implements JobExecutionListener {
            private final static Logger LOGGER = LoggerFactory.getLogger(MyJobExecutionListener.class);

            @Override
            public void beforeJob(JobExecution jobExecution) {
                LOGGER.info("Before job: {}", jobExecution);
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                LOGGER.info("After job: {}", jobExecution);
            }
        }
    }
}
