package me.loki2302.httplogging.app;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultDto addNumbers(@RequestBody TwoNumbersDto twoNumbersDto) {
        ResultDto resultDto = new ResultDto();
        resultDto.a = twoNumbersDto.a;
        resultDto.b = twoNumbersDto.b;
        resultDto.result = twoNumbersDto.a + twoNumbersDto.b;
        return resultDto;
    }
}
