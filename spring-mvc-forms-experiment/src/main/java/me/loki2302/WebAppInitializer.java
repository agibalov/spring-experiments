package me.loki2302;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebAppConfiguration.class);

        ServletRegistration.Dynamic dispatcherServletRegistration = servletContext.addServlet(
                "dispatcher", 
                new DispatcherServlet(context));
        
        dispatcherServletRegistration.setLoadOnStartup(1);
        dispatcherServletRegistration.addMapping("/");
    }
}