package me.loki2302;

import org.springframework.stereotype.Component;

@Component("divSecurityDecider")
public class DivSecurityDecider {
    public boolean canDivideBy(int x) {
        return x != 13;
    }
}
