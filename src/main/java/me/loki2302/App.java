package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App implements CommandLineRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);

        // uncomment to make it shutdown once all CommandLineRunners are done
        // context.close();
    }

    @Autowired
    private TodoRepository todoRepository;

    @Override
    public void run(String... args) throws Exception {
        if(true) {
            Todo todo1 = new Todo();
            todo1.text = "todo1";
            todo1 = todoRepository.save(todo1);

            Todo todo2 = new Todo();
            todo2.text = "todo2";
            todo2 = todoRepository.save(todo2);

            Todo todo3 = new Todo();
            todo3.text = "todo3";
            todo3 = todoRepository.save(todo3);

            todo1.relatedTodos.add(todo2);
            todo1.relatedTodos.add(todo3);

            todo1 = todoRepository.save(todo1);
        }

        if(true) {
            Todo todo2 = todoRepository.findByText("todo2");
            LOGGER.info("Found todo2: id={} text={} related={}", todo2.id, todo2.text, todo2.relatedTodos.size());
        }
    }
}
