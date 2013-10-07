package me.loki2302;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ctlok.springframework.web.servlet.view.rythm.RythmConfigurator;
import com.ctlok.springframework.web.servlet.view.rythm.RythmViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = MyConfiguration.class)
public class MyConfiguration {    
    @Bean
    public RythmConfigurator rythmConfigurator() {
        RythmConfigurator rythmConfigurator = new RythmConfigurator();
        rythmConfigurator.setMode("dev");
        rythmConfigurator.setTempDirectory("./");
        rythmConfigurator.setRootDirectory("/");
        rythmConfigurator.setImplicitPackages(Arrays.asList("me.loki2302.*"));
        
        return rythmConfigurator;
    }
    
    @Bean
    public RythmViewResolver rythmViewResolver(RythmConfigurator rythmConfigurator) {
        RythmViewResolver rythmViewResolver = new RythmViewResolver(rythmConfigurator);
        rythmViewResolver.setPrefix("/");
        rythmViewResolver.setSuffix(".html");
        return rythmViewResolver;
    }
}