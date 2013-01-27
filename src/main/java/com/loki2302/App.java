package com.loki2302;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.loki2302.repositories.UserRepository;

public class App {
    public static void main(String[] args) {
    	ApplicationContext context = new ClassPathXmlApplicationContext("repository-context.xml");
        UserRepository userRepository = context.getBean(UserRepository.class);
        
        System.out.println(userRepository.count());
    }
}
