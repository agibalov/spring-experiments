package me.loki2302;

import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.DefaultShellApplicationRunner;
import org.springframework.shell.jline.PromptProvider;

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
            Shell shell) {

        return new MyApplicationRunner(lineReader, promptProvider, shell);
    }

    /**
     * If there's a single command, executes this command and exits. Otherwise, runs interactively.
     */
    public static class MyApplicationRunner implements ApplicationRunner {
        private final LineReader lineReader;
        private final PromptProvider promptProvider;
        private final Shell shell;

        public MyApplicationRunner(LineReader lineReader, PromptProvider promptProvider, Shell shell) {
            this.lineReader = lineReader;
            this.promptProvider = promptProvider;
            this.shell = shell;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
            if(args.getSourceArgs().length > 0) {
                // TODO: this approach doesn't work for scenarios like 'say "hello world"'
                String command = Arrays.stream(args.getSourceArgs())
                        .collect(Collectors.joining(" "));
                shell.run(new SingleCommandInputProvider(command));
            } else {
                InputProvider inputProvider = new DefaultShellApplicationRunner.JLineInputProvider(
                        lineReader,
                        promptProvider);
                shell.run(inputProvider);
            }
        }

        public static class SingleCommandInputProvider implements InputProvider {
            private final String command;
            private boolean shouldExit = false;

            public SingleCommandInputProvider(String command) {
                this.command = command;
            }

            @Override
            public Input readInput() {
                if(!shouldExit) {
                    shouldExit = true;
                    return () -> command;
                }

                return null;
            }
        }
    }
}
