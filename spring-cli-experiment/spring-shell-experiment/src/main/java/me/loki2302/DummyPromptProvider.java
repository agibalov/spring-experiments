package me.loki2302;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class DummyPromptProvider implements PromptProvider {
    @Override
    public AttributedString getPrompt() {
        return new AttributedString(":-)", AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
    }
}
