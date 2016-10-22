package me.loki2302.httplogging.logging;

import org.apache.http.*;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingHttpRequestResponseInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggingHttpRequestResponseInterceptor.class);

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        RequestLine requestLine = request.getRequestLine();
        LOGGER.info("Request to: {} {}",
                requestLine.getMethod(),
                requestLine.getUri());

        Header[] headers = request.getAllHeaders();
        for(Header header : headers) {
            LOGGER.info("Header: {}", header);
        }

        if(request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest)request;
            HttpEntity wrappedEntity = new BufferedHttpEntity(httpEntityEnclosingRequest.getEntity());
            httpEntityEnclosingRequest.setEntity(wrappedEntity);

            String requestString = EntityUtils.toString(wrappedEntity);
            LOGGER.info("Request body: {}", requestString);
        }
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine();

        LOGGER.info("Response: {} {}",
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase());

        HttpEntity wrappedEntity = new BufferedHttpEntity(response.getEntity());
        response.setEntity(wrappedEntity);

        String contentString = EntityUtils.toString(wrappedEntity);
        LOGGER.info("Response body: {}", contentString);
    }
}
