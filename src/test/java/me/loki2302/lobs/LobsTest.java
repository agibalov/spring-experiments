package me.loki2302.lobs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class LobsTest {
    @Autowired
    private DataRepository dataRepository;

    @Test
    public void dummy() {
        TheBLOB theBLOB = new TheBLOB();
        theBLOB.message = "hello blob";

        Data data = new Data();
        data.clob = "hello clob";
        data.blob = theBLOB;
        dataRepository.save(data);

        data = dataRepository.findOne(1L);
        assertEquals("hello clob", data.clob);
        assertNotNull(data.blob);
        assertEquals("hello blob", data.blob.message);
    }
}
