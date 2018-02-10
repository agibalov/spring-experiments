package me.loki2302.servlet.dummysc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DummyServletContext implements ServletContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyServletContext.class);

    public final Map<String, String> initParameters = new HashMap<>();
    public final Map<String, Object> attributes = new HashMap<>();
    public final Map<String, Servlet> servlets = new HashMap<>();
    public final Map<String, Filter> filters = new HashMap<>();

    public String test() {
        DummyHttpServletRequest request = new DummyHttpServletRequest(
                "GET",
                "/",
                new HashMap<>(),
                "127.0.0.1",
                new HashMap<String, String>() {{
                    put("Accept", "*/*");
                }});

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new DummyServletOutputStream(baos);
        DummyHttpServletResponse response = new DummyHttpServletResponse(
                new HashMap<>(),
                servletOutputStream);

        // TODO: apply all matching filters
        // TODO: find the target servlet

        for(Servlet servlet : servlets.values()) {
            try {
                servlet.service(request, response);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String responseBody = new String(baos.toByteArray());

        LOGGER.info("RESPONSE");
        LOGGER.info("body: {}", responseBody);
        LOGGER.info("status: {}", response.getStatus());
        LOGGER.info("headers: {}", response.getHeaderNames().stream()
                .map(n -> String.format("%s=%s", n, response.getHeader(n)))
                .collect(Collectors.joining(",")));

        return responseBody;
    }

    @Override
    public String getContextPath() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServletContext getContext(String uripath) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getMajorVersion() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getMinorVersion() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getEffectiveMajorVersion() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getEffectiveMinorVersion() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getMimeType(String file) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Enumeration<String> getServletNames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void log(String msg) {
        LOGGER.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        LOGGER.info(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        LOGGER.info(message, throwable);
    }

    @Override
    public String getRealPath(String path) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getServerInfo() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Vector<>(initParameters.keySet()).elements();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        if(initParameters.containsKey(name)) {
            return false;
        }

        initParameters.put(name, value);
        return true;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<>(attributes.keySet()).elements();
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getServletContextName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        servlets.put(servletName, servlet);
        return new DummyServletRegistrationDynamic();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        filters.put(filterName, filter);
        return new DummyFilterRegistrationDynamic(filterName);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addListener(String className) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void declareRoles(String... roleNames) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getVirtualServerName() {
        throw new RuntimeException("Not implemented");
    }
}
