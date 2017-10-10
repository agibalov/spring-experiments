package me.loki2302.dummysc;

import org.springframework.http.HttpHeaders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class DummyHttpServletResponse implements HttpServletResponse {
    private int sc;
    private String characterEncoding;
    private final Map<String, String> headers;
    private final ServletOutputStream servletOutputStream;

    public DummyHttpServletResponse(
            Map<String, String> headers,
            ServletOutputStream servletOutputStream) {

        this.headers = headers;
        this.servletOutputStream = servletOutputStream;
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String encodeUrl(String url) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void sendError(int sc) throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addDateHeader(String name, long date) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        setHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addIntHeader(String name, int value) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setStatus(int sc) {
        this.sc = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.sc = sc;
    }

    @Override
    public int getStatus() {
        return sc;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return Collections.singleton(getHeader(name));
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setContentLengthLong(long len) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setContentType(String type) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setBufferSize(int size) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int getBufferSize() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void resetBuffer() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isCommitted() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void reset() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setLocale(Locale loc) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Locale getLocale() {
        throw new RuntimeException("Not implemented");
    }
}
