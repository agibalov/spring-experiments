package me.loki2302;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import org.apache.jasper.servlet.JspServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MyWebApplicationInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerJspServlet(servletContext);
        registerWebAppServlet(servletContext);        
    }
    
    private static void registerWebAppServlet(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(MyConfiguration.class);               
        HttpServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "dispatcher", 
                dispatcherServlet);
        
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
    
    private static void registerJspServlet(ServletContext servletContext) {
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "jsp",
                new JspServlet());
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("*.jsp");
        System.out.println(dispatcher);
    }
}