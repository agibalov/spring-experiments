package io.agibalov.audit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuditTest {
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private DummyAuditorAware auditorAware;

    @Autowired
    private DummyDateTimeProvider dateTimeProvider;

    @Test
    public void allAuditFieldsAreSetOnFirstSave() {
        auditorAware.setCurrentAuditor("loki2302");
        dateTimeProvider.setNow(new DateTime(DateTimeZone.UTC)
                .withYear(2014)
                .withMonthOfYear(11)
                .withDayOfMonth(26)
                .withHourOfDay(12)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .toGregorianCalendar());

        Note note = new Note();
        note = noteRepository.save(note);

        assertEquals("loki2302", note.createdBy);

        DateTime createdAtDateTime = new DateTime(note.createdAt, DateTimeZone.UTC);
        assertEquals(2014, createdAtDateTime.getYear());
        assertEquals(11, createdAtDateTime.getMonthOfYear());
        assertEquals(26, createdAtDateTime.getDayOfMonth());
        assertEquals(12, createdAtDateTime.getHourOfDay());
        assertEquals(0, createdAtDateTime.getMinuteOfHour());
        assertEquals(0, createdAtDateTime.getSecondOfMinute());

        assertEquals("loki2302", note.modifiedBy);

        DateTime lastModifiedAtDateTime = new DateTime(note.modifiedAt, DateTimeZone.UTC);
        assertEquals(2014, lastModifiedAtDateTime.getYear());
        assertEquals(11, lastModifiedAtDateTime.getMonthOfYear());
        assertEquals(26, lastModifiedAtDateTime.getDayOfMonth());
        assertEquals(12, lastModifiedAtDateTime.getHourOfDay());
        assertEquals(0, lastModifiedAtDateTime.getMinuteOfHour());
        assertEquals(0, lastModifiedAtDateTime.getSecondOfMinute());
    }

    @Test
    public void modificationAuditFieldsAreUpdateWhenEntityIsUpdated() {
        auditorAware.setCurrentAuditor("loki2302");
        dateTimeProvider.setNow(new DateTime(DateTimeZone.UTC)
                .withYear(2014)
                .withMonthOfYear(11)
                .withDayOfMonth(26)
                .withHourOfDay(12)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .toGregorianCalendar());

        Note note = new Note();
        note = noteRepository.save(note);

        auditorAware.setCurrentAuditor("Andrey");
        dateTimeProvider.setNow(new DateTime(DateTimeZone.UTC)
                .withYear(2014)
                .withMonthOfYear(11)
                .withDayOfMonth(26)
                .withHourOfDay(12)
                .withMinuteOfHour(1)
                .withSecondOfMinute(0)
                .toGregorianCalendar());

        note.content = "hello";
        note = noteRepository.save(note);

        assertEquals("loki2302", note.createdBy);

        DateTime createdAtDateTime = new DateTime(note.createdAt, DateTimeZone.UTC);
        assertEquals(2014, createdAtDateTime.getYear());
        assertEquals(11, createdAtDateTime.getMonthOfYear());
        assertEquals(26, createdAtDateTime.getDayOfMonth());
        assertEquals(12, createdAtDateTime.getHourOfDay());
        assertEquals(0, createdAtDateTime.getMinuteOfHour());
        assertEquals(0, createdAtDateTime.getSecondOfMinute());

        assertEquals("Andrey", note.modifiedBy);

        DateTime lastModifiedAtDateTime = new DateTime(note.modifiedAt, DateTimeZone.UTC);
        assertEquals(2014, lastModifiedAtDateTime.getYear());
        assertEquals(11, lastModifiedAtDateTime.getMonthOfYear());
        assertEquals(26, lastModifiedAtDateTime.getDayOfMonth());
        assertEquals(12, lastModifiedAtDateTime.getHourOfDay());
        assertEquals(1, lastModifiedAtDateTime.getMinuteOfHour());
        assertEquals(0, lastModifiedAtDateTime.getSecondOfMinute());
    }
}
