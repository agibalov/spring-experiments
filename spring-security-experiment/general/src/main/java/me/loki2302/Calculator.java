package me.loki2302;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Calculator {
    @PreAuthorize("#x < 10")
    public int add(int x, int y) {
        return x + y;
    }

    @PreAuthorize("@divSecurityDecider.canDivideBy(#y)")
    public int div(int x, int y) {
        return x / y;
    }

    @PostAuthorize("returnObject != 5")
    public int sub(int x, int y) {
        return x - y;
    }

    @PreFilter("filterObject % 2 == 0")
    public int sum(List<Integer> values) {
        int sum = 0;
        for(int x : values) {
            sum += x;
        }
        return sum;
    }

    @PostFilter("filterObject % 3 != 0")
    public List<Integer> doublify(List<Integer> values) {
        List<Integer> result = new ArrayList<>();
        for(int x : values) {
            result.add(x * 2);
        }
        return result;
    }
}
