package me.loki2302.app2;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = App2.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class App2Test {
    private final static long TEST_FILE_SIZE = 1024 * 1024 * 1024;

    @Autowired
    private App2.UploadController uploadController;

    @Test
    public void cantDoAStreamingUploadWithRestTemplate() {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", new DummyResource(new DummyInputStream(TEST_FILE_SIZE), "hello.dat"));

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false); // this results in 'getBody not supported'

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        try {
            restTemplate.exchange(
                    "http://localhost:8080/upload",
                    HttpMethod.POST,
                    new HttpEntity<Object>(parts),
                    Void.class);
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    // this test takes looong
    @Test
    public void canDoAStreamingUploadWithApacheHttpClient() throws IOException {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody(
                "file",
                new DummyInputStream(TEST_FILE_SIZE),
                ContentType.APPLICATION_OCTET_STREAM,
                "hello.dat");
        org.apache.http.HttpEntity entity = multipartEntityBuilder.build();

        HttpPost httpPost = new HttpPost("http://localhost:8080/upload");
        httpPost.setEntity(entity);

        CloseableHttpClient httpClient = HttpClients.createMinimal();
        httpClient.execute(httpPost);

        assertEquals(TEST_FILE_SIZE, uploadController.uploadedFileSize);
    }

    public static class DummyInputStream extends InputStream {
        private final long size;
        private long position = 0;

        public DummyInputStream(long size) {
            this.size = size;
        }

        @Override
        public int read() throws IOException {
            if(position < size) {
                ++position;
                return 1;
            }

            return -1;
        }
    }

    public static class DummyResource implements Resource {
        private final InputStream inputStream;
        private final String filename;

        public DummyResource(InputStream inputStream, String filename) {
            this.inputStream = inputStream;
            this.filename = filename;
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public URL getURL() throws IOException {
            return null;
        }

        @Override
        public URI getURI() throws IOException {
            return null;
        }

        @Override
        public File getFile() throws IOException {
            return null;
        }

        @Override
        public long contentLength() throws IOException {
            return 0;
        }

        @Override
        public long lastModified() throws IOException {
            return 0;
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            return null;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return inputStream;
        }
    }
}
