package me.loki2302;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class NoteService {
    @PreAuthorize("isAuthenticated()")
    public Note createNote(String text) {
        Note note = new Note();
        note.text = text;

        // TODO: I don't like it
        note.owner = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return note;
    }

    // TODO: I don't like it as well
    @PreAuthorize("isAuthenticated() and #note.owner.equals(principal)")
    public Note updateNote(Note note, String text) {
        note.text = text;
        return note;
    }
}
