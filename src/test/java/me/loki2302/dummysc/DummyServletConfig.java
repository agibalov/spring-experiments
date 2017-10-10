package me.loki2302.dummysc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class DummyServletConfig implements ServletConfig {
    private final String servletName;
    private final DummyServletContext servletContext;

    public DummyServletConfig(
            String servletName,
            DummyServletContext servletContext) {

        this.servletName = servletName;
        this.servletContext = servletContext;
    }

    @Override
    public String getServletName() {
        return servletName;
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
