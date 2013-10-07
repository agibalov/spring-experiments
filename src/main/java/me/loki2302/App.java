package me.loki2302;

import me.loki2302.entities.User;
import me.loki2302.repositories.UserRepository;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            context = new AnnotationConfigApplicationContext(HsqldbConfiguration.class);
            UserRepository userRepository = context.getBean(UserRepository.class);
            
            User u = new User();
            u.setUserName("loki2302");
            u.setPassword("qwerty123");
            u = userRepository.save(u);
            
            System.out.println(userRepository.count());
        } finally {
            if(context != null) {
                context.close();
            }
        }        
    }
}
