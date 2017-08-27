package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldCommands implements CommandMarker {
    @Autowired
    private CalculatorService calculatorService;

    @CliAvailabilityIndicator({"say hello", "say"})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = "say hello", help = "Say 'Hello!'")
    public String sayHello() {
        return "Hello!";
    }

    @CliCommand(value = "say", help = "Say something")
    public String say(
            @CliOption(key = "what", mandatory = true, help = "What exactly to say")
            String what) {

        return "Saying: " + what;
    }

    @CliCommand(value = "add", help = "Add two numbers")
    public String add(
            @CliOption(key = "a", mandatory = true, help = "number A")
            int a,

            @CliOption(key = "b", mandatory = true, help = "number B")
            int b) {

        int result = calculatorService.add(a, b);
        return String.format("Result is %d", result);
    }
}
