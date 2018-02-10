package me.loki2302;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.loki2302.rythm.FancyDateFormatTag;
import me.loki2302.rythm.FancyTimeFormatTag;
import me.loki2302.rythm.MarkdownTag;

import org.rythmengine.template.ITemplate;
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
        rythmConfigurator.setImplicitPackages(Arrays.asList("me.loki2302.service.*"));
        
        List<ITemplate> tags = new ArrayList<ITemplate>();
        tags.add(new MarkdownTag());
        tags.add(new FancyDateFormatTag());
        tags.add(new FancyTimeFormatTag());
        rythmConfigurator.setTags(tags);
        
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
