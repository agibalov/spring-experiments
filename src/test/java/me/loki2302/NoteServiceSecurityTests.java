package me.loki2302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = Config.class)
public class NoteServiceSecurityTests {
    @Autowired
    private NoteService noteService;

    @Before
    @After
    public void unsetAuthentication() {
        deauthenticate();
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void cantCreateNoteUnlessAuthenticated() {
        noteService.createNote("hello there");
    }

    @Test
    public void canCreateNoteIfAuthenticated() {
        authenticate("loki2302");
        Note note = noteService.createNote("hello there");
        assertEquals("loki2302", note.owner);
    }

    @Test(expected = AccessDeniedException.class)
    public void cantEditSomeoneElsesNote() {
        authenticate("loki2302");
        Note note = noteService.createNote("hello there");
        authenticate("hacker");
        noteService.updateNote(note, "pwned");
    }

    @Test
    public void canEditOwnNote() {
        authenticate("loki2302");
        Note note = noteService.createNote("hello there");
        noteService.updateNote(note, "bye");
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void cantEditNoteWithoutAuthentication() {
        authenticate("loki2302");
        Note note = noteService.createNote("hello there");
        deauthenticate();
        noteService.updateNote(note, "bye");
    }

    private static void authenticate(String principal) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                principal,
                "hello",
                AuthorityUtils.NO_AUTHORITIES);

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private static void deauthenticate() {
        SecurityContextHolder.clearContext();
    }
}
