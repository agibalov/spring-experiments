package me.loki2302.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoteController {
    @Autowired
    private NoteRepository noteRepository;

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public long getNoteCount() {
        return noteRepository.count();
    }

    @RequestMapping(value = "/notes", method = RequestMethod.POST)
    public void createNote(@RequestBody Note note) {
        note.id = null;
        noteRepository.save(note);
    }
}
