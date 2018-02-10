package me.loki2302.manytomanyvia;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;

@Entity
@IdClass(ParticipationId.class)
public class Participation {
    @Id
    public Long personId;

    @Id
    public Long projectId;

    @ManyToOne
    @JoinColumn(name = "personId", updatable = false, insertable = false)
    public Person person;

    @ManyToOne
    @JoinColumn(name = "projectId", updatable = false, insertable = false)
    public Project project;

    public String role;
}
