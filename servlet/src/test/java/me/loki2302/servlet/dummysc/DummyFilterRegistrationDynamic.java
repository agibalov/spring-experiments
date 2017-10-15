package me.loki2302.servlet.dummysc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.*;
import java.util.stream.Collectors;

public class DummyFilterRegistrationDynamic implements FilterRegistration.Dynamic {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyFilterRegistrationDynamic.class);
    private final String name;

    public DummyFilterRegistrationDynamic(String name) {
        this.name = name;
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<String> getServletNameMappings() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        LOGGER.info("addMappingForUrlPatterns() {} {} {}",
                dispatcherTypes,
                isMatchAfter,
                Arrays.stream(urlPatterns).collect(Collectors.joining(",")));
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        LOGGER.info("setAsyncSupported() {}", isAsyncSupported);
    }

    @Override
    public String getName() {
        return name;
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
