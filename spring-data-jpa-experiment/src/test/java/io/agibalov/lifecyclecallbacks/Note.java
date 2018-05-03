package io.agibalov.lifecyclecallbacks;

import javax.persistence.*;

@Entity
@EntityListeners(NoteEntityListener.class)
public class Note {
    @Id
    @GeneratedValue
    public Long id;

    public String content;

    @PrePersist
    public void setContentToEmptyStringIfItIsNull() {
        if(content == null) {
            content = "";
        }
    }
}
