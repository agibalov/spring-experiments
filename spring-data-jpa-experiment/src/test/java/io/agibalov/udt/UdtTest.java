package io.agibalov.udt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext
public class UdtTest {
    @Autowired
    private DataRepository dataRepository;

    @Test
    public void canUseIntegerAsVarcharUserType() {
        Data data = new Data();
        data.id = 1L;
        data.someInteger = 123;
        dataRepository.save(data);

        Data data2 = dataRepository.findOne(1L);
        assertEquals(123, (int)data2.someInteger);
    }

    @Test
    public void canUseDtoAsJsonUserType() {
        Data data = new Data();
        data.id = 1L;
        data.someUselessDto = new SomeUselessDto();
        data.someUselessDto.number = 222;
        data.someUselessDto.text = "hello there";
        dataRepository.save(data);

        Data data2 = dataRepository.findOne(1L);
        assertEquals(222, data2.someUselessDto.number);
        assertEquals("hello there", data2.someUselessDto.text);
    }
}
