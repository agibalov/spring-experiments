package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CalculatorService.class);

    public int add(int a, int b) {
        int result = a + b;
        LOGGER.info("Adding {} and {}, result is {}", a, b, result);
        return result;
    }
}
