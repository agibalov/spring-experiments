package me.loki2302;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            context = new AnnotationConfigApplicationContext(AppConfiguration.class);
            PeopleRepository peopleRepository = context.getBean(PeopleRepository.class);
            
            Person person = new Person();
            person = peopleRepository.save(person);
            
            System.out.println(peopleRepository.count());
        } finally {
            if(context != null) {
                context.close();
            }
        }        
    }
}
