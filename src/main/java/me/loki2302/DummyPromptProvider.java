package me.loki2302;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Integer.MIN_VALUE)
public class DummyPromptProvider implements PromptProvider {
    @Override
    public String getPrompt() {
        return ":-)";
    }

    @Override
    public String getProviderName() {
        return DummyPromptProvider.class.getName();
    }
}
