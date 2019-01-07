package me.loki2302;

import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.MalformedObjectNameException;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class AppTest {
    @Test
    public void dummy() throws MalformedObjectNameException, J4pException {
        J4pClient j4pClient = new J4pClient("http://localhost:8080/jolokia");
        J4pReadRequest j4pReadRequest = new J4pReadRequest(
                "java.lang:type=Memory",
                "HeapMemoryUsage");
        J4pReadResponse j4pReadResponse = j4pClient.execute(j4pReadRequest);

        Map<String, Long> values = j4pReadResponse.getValue();
        assertNotEquals(0, (long) values.get("used"));
    }
}
