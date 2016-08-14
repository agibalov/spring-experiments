package me.loki2302;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Integer.MIN_VALUE)
public class DummyBannerProvider implements BannerProvider {
    @Override
    public String getBanner() {
        return String.format("***\nThe Shell\n***\n");
    }

    @Override
    public String getVersion() {
        return String.format("1.0");
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome!";
    }

    @Override
    public String getProviderName() {
        return DummyBannerProvider.class.getName();
    }
}
