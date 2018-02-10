package me.loki2302;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Component
    public static class MyCommandLineRunner implements CommandLineRunner {
        private final static Logger LOGGER = LoggerFactory.getLogger(MyCommandLineRunner.class);

        @Autowired
        private CommonArguments commonArguments;

        @Autowired
        private ApplicationContext applicationContext;

        @Override
        public void run(String... args) throws Exception {
            JCommander.Builder jCommanderBuilder = JCommander.newBuilder()
                    .addObject(commonArguments);
            applicationContext.getBeansWithAnnotation(Parameters.class)
                    .forEach((k, v) -> jCommanderBuilder.addCommand(v));
            JCommander jCommander = jCommanderBuilder
                    .programName("tool")
                    .build();

            boolean hasError = false;
            try {
                jCommander.parse(args);
            } catch (ParameterException e) {
                System.out.printf("%s\n", e.getMessage());
                hasError = true;
            }

            String command = jCommander.getParsedCommand();
            if(command == null) {
                jCommander.usage();
                return;
            } else if(hasError) {
                jCommander.usage(command);
                return;
            }

            Map<String, Runnable> commandHandlers = applicationContext.getBeansOfType(Runnable.class);

            Optional<Runnable> commandHandlerOptional = commandHandlers.values().stream()
                    .filter(h -> Arrays.binarySearch(h.getClass().getAnnotation(Parameters.class).commandNames(), command) >= 0)
                    .findFirst();
            if(!commandHandlerOptional.isPresent()) {
                System.out.printf("There is no handler for command %s\n", command);
                return;
            }

            Runnable commandHandler = commandHandlerOptional.get();
            commandHandler.run();
        }
    }

    @Component
    public static class CommonArguments {
        @Parameter(names = "--verbose", description = "Lots of console output")
        public boolean verbose;
    }

    @Component
    @Parameters(commandNames = "deploy", commandDescription = "Deploy the stuff")
    public static class DeployArguments implements Runnable {
        private final static Logger LOGGER = LoggerFactory.getLogger(DeployArguments.class);

        @Parameter(names = "--what", description = "What needs to be deployed", required = true)
        public String what;

        @Autowired
        private CommonArguments commonArguments;

        @Autowired
        private DeploymentService deploymentService;

        @Override
        public void run() {
            LOGGER.info("Deploying {}", what);
            LOGGER.info("Verbose: {}", commonArguments.verbose);
            deploymentService.deploy();
        }
    }

    @Component
    @Parameters(commandNames = "undeploy", commandDescription = "Undeploy the stuff")
    public static class UndeployArguments implements Runnable {
        private final static Logger LOGGER = LoggerFactory.getLogger(UndeployArguments.class);

        @Parameter(names = "--what", description = "What needs to be undeployed", required = true)
        public String what;

        @Autowired
        private CommonArguments commonArguments;

        @Autowired
        private DeploymentService deploymentService;

        @Override
        public void run() {
            LOGGER.info("Undeploying {}", what);
            LOGGER.info("Verbose: {}", commonArguments.verbose);
            deploymentService.undeploy();
        }
    }

    @Component
    public static class DeploymentService {
        private final static Logger LOGGER = LoggerFactory.getLogger(DeploymentService.class);

        public void deploy() {
            LOGGER.info("Deploying...");
        }

        public void undeploy() {
            LOGGER.info("Undeploying...");
        }
    }

    @Component
    @Parameters(commandNames = "ping", commandDescription = "Ping hosts using the standard 'ping' utility.")
    public static class PingArguments implements Runnable {
        private final static Logger LOGGER = LoggerFactory.getLogger(PingArguments.class);

        @Parameter(names = "--host", description = "The host to ping", required = true)
        public String host;

        @Parameter(names = "--repetitions", description = "The number of times to ping it", required = false)
        public int repetitions = 5;

        @Override
        public void run() {
            int code;
            try {
                code = new ProcessExecutor()
                        .command("ping", host, "-c", String.valueOf(repetitions))
                        .readOutput(true)
                        .redirectOutputAlsoTo(Slf4jStream.of(LOGGER).asInfo())
                        .execute().getExitValue();
            } catch(Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            LOGGER.info("ping exited with code {}", code);
        }
    }
}
