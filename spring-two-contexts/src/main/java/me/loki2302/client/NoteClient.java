package me.loki2302.client;

import me.loki2302.server.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NoteClient {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClientNoteRepository clientNoteRepository;

    public long getNoteCount() {
        return restTemplate.getForObject("http://localhost:8080/notes", Long.class);
    }

    public void createNote(String text) {
        Note note = new Note();
        note.text = "hello there";
        restTemplate.postForObject("http://localhost:8080/notes", note, Void.class);
    }

    public long getClientNoteCount() {
        return clientNoteRepository.count();
    }

    public void createClientNote(String text) {
        ClientNote note = new ClientNote();
        note.text = text;
        clientNoteRepository.save(note);
    }
}
