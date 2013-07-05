package me.loki2302;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    public int addNumbers(int a, int b) {
        return a + b;
    }
}