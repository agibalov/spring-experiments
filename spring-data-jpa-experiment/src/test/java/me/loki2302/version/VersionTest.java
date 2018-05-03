package me.loki2302.version;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VersionTest {
    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void versionGetsSetTo0WhenEntityFirstSaved() {
        Note note = new Note();
        assertNull(note.version);

        note = noteRepository.save(note);
        assertEquals(0L, (long)note.version);
    }

    @Test
    public void versionIsNotUpdatedOnSaveIfThereWereNoChangesToEntity() {
        Note note = new Note();
        note = noteRepository.save(note);
        assertEquals(0L, (long)note.version);

        note = noteRepository.save(note);
        assertEquals(0L, (long)note.version);
    }

    @Test
    public void versionIsUpdatedOnSaveIfThereWereChangesToEntity() {
        Note note = new Note();
        note = noteRepository.save(note);
        assertEquals(0L, (long)note.version);

        note.content = "hello";
        note = noteRepository.save(note);
        assertEquals(1L, (long)note.version);
    }

    @Test
    public void canGetObjectOptimisticLockingFailureExceptionWhenUpdatingOlderVersion() {
        Note note = new Note();
        note = noteRepository.save(note);

        note.content = "hello";
        note = noteRepository.save(note);

        note.content = "hi there";
        --note.version;

        try {
            noteRepository.save(note);
            fail();
        } catch(ObjectOptimisticLockingFailureException e) {
            assertEquals(1L, (long)(Long)e.getIdentifier());
            assertNull(e.getPersistentClass()); // wtf it is null?
            assertEquals(Note.class.getName(), e.getPersistentClassName());
        }
    }
}
