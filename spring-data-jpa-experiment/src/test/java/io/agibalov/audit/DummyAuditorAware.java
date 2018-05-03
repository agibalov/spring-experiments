package io.agibalov.audit;

import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

@Service
public class DummyAuditorAware implements AuditorAware<String> {
    private String currentAuditor;

    public String getCurrentAuditor() {
        Assert.notNull(currentAuditor);
        return currentAuditor;
    }

    public void setCurrentAuditor(String currentAuditor) {
        this.currentAuditor = currentAuditor;
    }

}
