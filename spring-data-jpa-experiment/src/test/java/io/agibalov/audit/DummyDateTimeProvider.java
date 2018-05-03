package io.agibalov.audit;

import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

@Service("dateTimeProvider")
public class DummyDateTimeProvider implements DateTimeProvider {
    private Calendar calendar;

    public Calendar getNow() {
        Assert.notNull(calendar);
        return calendar;
    }

    public void setNow(Calendar calendar) {
        this.calendar = calendar;
    }
}
