package io.agibalov.lifecyclecallbacks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext
public class LifecycleCallbacksTest {
    @Autowired
    private NoteRepository noteRepository;

    @Before
    public void resetLog() {
        NoteEntityListener.resetLog();
    }

    @Test
    public void canUseLifecycleCallbacksAtEntityListener() {
        Note note = new Note();
        note = noteRepository.save(note);
        assertEquals("onPrePersist,onPostPersist,", NoteEntityListener.getLog());

        note.content = "hello";
        NoteEntityListener.resetLog();
        note = noteRepository.save(note);
        assertEquals("onPostLoad,onPreUpdate,onPostUpdate,", NoteEntityListener.getLog());

        NoteEntityListener.resetLog();
        noteRepository.delete(note);
        assertEquals("onPostLoad,onPreRemove,onPostRemove,", NoteEntityListener.getLog());
    }

    @Test
    public void canUseLifecycleCallbacksAtEntity() {
        Note note = new Note();
        assertNull(note.content);

        note = noteRepository.save(note);
        assertEquals("", note.content);
    }
}
