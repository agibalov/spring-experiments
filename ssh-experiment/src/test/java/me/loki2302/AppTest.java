package me.loki2302;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppTest.class);

    @Test
    public void dummy() throws IOException {
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier((hostname, port, key) -> true);
        sshClient.connect("localhost", 2000);
        try {
            sshClient.authPassword("user1", "password1");
            try(Session session = sshClient.startSession()) {
                Session.Command helloCommand = session.exec("hello");
                String response = IOUtils.readFully(helloCommand.getInputStream()).toString();
                LOGGER.info("Server says: {}", response);
                assertTrue(response.startsWith("Hello! Time is"));
            }
        } finally {
            sshClient.disconnect();
        }
    }
}
