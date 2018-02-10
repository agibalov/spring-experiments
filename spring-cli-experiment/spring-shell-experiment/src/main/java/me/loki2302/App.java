package me.loki2302;

import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.DefaultShellApplicationRunner;
import org.springframework.shell.jline.FileInputProvider;
import org.springframework.shell.jline.PromptProvider;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @Autowired
    public ApplicationRunner applicationRunner(
            LineReader lineReader,
            PromptProvider promptProvider,
            Parser parser,
            Shell shell) {

        return new MyApplicationRunner(lineReader, promptProvider, parser, shell);
    }

    /**
     * If there's a single command, executes this command and exits. Otherwise, runs interactively.
     */
    public static class MyApplicationRunner implements ApplicationRunner {
        private final LineReader lineReader;
        private final PromptProvider promptProvider;
        private final Parser parser;
        private final Shell shell;

        public MyApplicationRunner(
                LineReader lineReader,
                PromptProvider promptProvider,
                Parser parser,
                Shell shell) {

            this.lineReader = lineReader;
            this.promptProvider = promptProvider;
            this.parser = parser;
            this.shell = shell;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
            boolean hasCommandLineArgs = args.getSourceArgs().length > 0;
            if(hasCommandLineArgs) {
                String command = Arrays.stream(args.getSourceArgs())
                        .map(s -> s.contains(" ") ? "\"" + s + "\"" : s)
                        .collect(Collectors.joining(" "));

                try(Reader reader = new StringReader(command);
                    FileInputProvider inputProvider = new FileInputProvider(reader, parser)) {

                    shell.run(inputProvider);
                }

                return;
            }

            InputProvider inputProvider = new DefaultShellApplicationRunner.JLineInputProvider(
                    lineReader,
                    promptProvider);
            shell.run(inputProvider);
        }
    }
}
