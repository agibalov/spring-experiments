package me.loki2302.dummysc;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class DummyFilterConfig implements FilterConfig {
    private final String filterName;
    private final DummyServletContext servletContext;

    public DummyFilterConfig(String filterName, DummyServletContext servletContext) {
        this.filterName = filterName;
        this.servletContext = servletContext;
    }

    @Override
    public String getFilterName() {
        return filterName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Vector<>(Collections.EMPTY_SET).elements();
    }
}
