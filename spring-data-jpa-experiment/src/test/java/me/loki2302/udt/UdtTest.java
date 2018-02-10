package me.loki2302.udt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UdtTest {
    @Autowired
    private DataRepository dataRepository;

    @Test
    public void canUseIntegerAsVarcharUserType() {
        Data data = new Data();
        data.someInteger = 123;
        dataRepository.save(data);

        Data data2 = dataRepository.findAll().get(0);
        assertEquals(123, (int)data2.someInteger);
    }

    @Test
    public void canUseDtoAsJsonUserType() {
        Data data = new Data();
        data.someUselessDto = new SomeUselessDto();
        data.someUselessDto.number = 222;
        data.someUselessDto.text = "hello there";
        dataRepository.save(data);

        Data data2 = dataRepository.findAll().get(0);
        assertEquals(222, data2.someUselessDto.number);
        assertEquals("hello there", data2.someUselessDto.text);
    }
}
