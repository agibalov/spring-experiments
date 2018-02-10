package me.loki2302.servlet.dummysc;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public class DummyHttpServletRequest implements HttpServletRequest {
    private final String method;
    private final String requestUri;
    private final Map<String, Object> attributes;
    private final String remoteAddr;

    public DummyHttpServletRequest(
            String method,
            String requestUri,
            Map<String, Object> attributes,
            String remoteAddr, Map<String, String> headers) {

        this.method = method;
        this.requestUri = requestUri;
        this.attributes = attributes;
        this.remoteAddr = remoteAddr;
        this.headers = headers;
    }

    @Override
    public String getAuthType() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Cookie[] getCookies() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public long getDateHeader(String name) {
        throw new RuntimeException("Not implemented");
    }

    private final Map<String, String> headers;

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return new Vector<>(Collections.singleton(getHeader(name))).elements();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Vector<>(headers.keySet()).elements();
    }

    @Override
    public int getIntHeader(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getPathTranslated() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getContextPath() {
        return "/";
    }

    @Override
    public String getQueryString() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getRemoteUser() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getRequestURI() {
        return requestUri;
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getServletPath() {
        return "/";
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String changeSessionId() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void logout() throws ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new RuntimeException("Not implemented");
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
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getContentLength() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public long getContentLengthLong() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getContentType() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getParameter(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String[] getParameterValues(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getProtocol() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getScheme() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getServerName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getServerPort() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Locale getLocale() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isSecure() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getRealPath(String path) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getRemotePort() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getLocalName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getLocalAddr() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getLocalPort() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServletContext getServletContext() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new RuntimeException("Not implemented");
    }
}
