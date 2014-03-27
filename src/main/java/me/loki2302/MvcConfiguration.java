package me.loki2302;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ctlok.springframework.web.servlet.view.rythm.RythmConfigurator;
import com.ctlok.springframework.web.servlet.view.rythm.RythmViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = MvcConfiguration.class)
public class MvcConfiguration extends WebMvcConfigurerAdapter {
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
    
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        //multipartResolver.setMaxUploadSize(1024 * 1024);        
        return multipartResolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new DownloadableFileMessageConverter());
    }
}