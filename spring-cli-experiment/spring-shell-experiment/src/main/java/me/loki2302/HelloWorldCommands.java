package me.loki2302;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import javax.validation.constraints.NotEmpty;

@ShellComponent
@ShellCommandGroup("Dummy commands")
public class HelloWorldCommands {
    @Autowired
    private CalculatorService calculatorService;

    @ShellMethodAvailability("say")
    public Availability canSay() {
        return Availability.available();
    }

    @ShellMethod(key = "say", value = "Say something")
    public String say(
            @ShellOption(value = "--what", help = "What exactly to say")
            @NotEmpty String what) {

        return "Saying: " + what;
    }

    @ShellMethod(key = "add", value = "Add two numbers")
    public String add(
            @ShellOption(value = "--a", help = "number A")
            @Range(min = 1, max = 100)
            int a,

            @ShellOption(value = "--b", help = "number B")
            @Range(min = 1, max = 300)
            int b) {

        int result = calculatorService.add(a, b);
        return String.format("Result is %d", result);
    }
}
