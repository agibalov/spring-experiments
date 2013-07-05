package me.loki2302;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MyWebApplicationInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        Settings settings = null;
        ClassPathXmlApplicationContext configurationContext = null;
        try {
            configurationContext = new ClassPathXmlApplicationContext("/settings.xml");
            settings = configurationContext.getBean(Settings.class);            
        } finally {
            if(configurationContext != null) {
                configurationContext.close();
            }
        }
        
        System.out.println("****************************************************");
        System.out.printf("settings: %s\n", settings);
        System.out.println("****************************************************");        
        
        List<Class<?>> configurations = new ArrayList<Class<?>>();
        configurations.add(MyConfiguration.class);
        
        if(settings.getUseDummyNotificationService()) {
            configurations.add(DummyNotificationServiceConfiguration.class);
        } else {
            configurations.add(RealNotificationServiceConfiguration.class);
        }
        
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(configurations.toArray(new Class<?>[configurations.size()]));
                       
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "dispatcher", 
                new DispatcherServlet(context));
        
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}