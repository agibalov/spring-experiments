package me.loki2302;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.springframework.web.WebApplicationInitializer;

public class App {
    public static void main(String[] args) throws Exception {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setBaseResource(Resource.newClassPathResource("META-INF/webapp"));        
        webAppContext.setConfigurations(new Configuration[] {
                new AnnotationConfiguration() {
                    @Override
                    public void preConfigure(WebAppContext context) throws Exception {
                        MultiMap<String> map = new MultiMap<String>();
                        map.add(WebApplicationInitializer.class.getName(), MyWebApplicationInitializer.class.getName());
                        context.setAttribute(CLASS_INHERITANCE_MAP, map);
                        _classInheritanceHandler = new ClassInheritanceHandler(map);
                    }
                }
        });
        
        Server server = new Server(8080);
        server.setHandler(webAppContext);
        
        server.start();
        server.join();
    }
}
