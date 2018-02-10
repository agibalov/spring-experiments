package me.loki2302.servlet.dummysc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import java.util.*;
import java.util.stream.Collectors;

public class DummyServletRegistrationDynamic implements ServletRegistration.Dynamic {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyServletRegistrationDynamic.class);

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        LOGGER.info("setLoadOnStartup() {}", loadOnStartup);
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        LOGGER.info("setMultipartConfig() {}", multipartConfig);
    }

    @Override
    public void setRunAsRole(String roleName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        LOGGER.info("setAsyncSupported() {}", isAsyncSupported);
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        LOGGER.info("addMapping() {}", Arrays.stream(urlPatterns).collect(Collectors.joining(",")));
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getMappings() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getRunAsRole() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getClassName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getInitParameter(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, String> getInitParameters() {
        throw new RuntimeException("Not implemented");
    }
}
