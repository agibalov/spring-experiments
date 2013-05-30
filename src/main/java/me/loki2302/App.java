package me.loki2302;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            context = new AnnotationConfigApplicationContext(AppConfiguration.class);
            Neo4jTemplate neo4jTemplate = context.getBean(Neo4jTemplate.class);
            PeopleRepository peopleRepository = context.getBean(PeopleRepository.class);
            
            Neo4jHelper.cleanDb(neo4jTemplate);
            
            Person person1 = new Person();
            person1.setName("loki2302");
            person1 = peopleRepository.save(person1);
            
            Person person2 = new Person();
            person2.setName("loki2302_2");
            person2 = peopleRepository.save(person2);
            
            System.out.println(peopleRepository.count());
            for(Person p : peopleRepository.findAll()) {
                System.out.println(p);
            }
        } finally {
            if(context != null) {
                context.close();
            }
        }        
    }
}
