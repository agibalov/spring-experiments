package io.agibalov.manytomanyvia;

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
public class ManyToManyViaTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Test
    public void canUseManyToManyVia() {
        Person personWhoParticipatesInAAndB = person();

        Project projectA = project();
        Person projectAParticipant1 = person();
        Person projectAParticipant2 = person();
        participation(projectAParticipant1, projectA, "developer");
        participation(projectAParticipant2, projectA, "qa");
        participation(personWhoParticipatesInAAndB, projectA, "manager");

        Project projectB = project();
        Person projectBParticipant1 = person();
        participation(projectBParticipant1, projectB, "developer");
        participation(personWhoParticipatesInAAndB, projectB, "janitor");

        // there are 3 participants in project A
        assertEquals(3, participationRepository.findByProject(projectA).size());

        // there are 2 participants in project B
        assertEquals(2, participationRepository.findByProject(projectB).size());

        // there is a person who participates in both projects A and B
        assertEquals(2, participationRepository.findByPerson(personWhoParticipatesInAAndB).size());

        // there are 2 developer participants
        assertEquals(2, participationRepository.findByRole("developer").size());

        // there is 1 janitor participant
        assertEquals(1, participationRepository.findByRole("janitor").size());
    }

    private Person person() {
        return personRepository.save(new Person());
    }

    private Project project() {
        return projectRepository.save(new Project());
    }

    private Participation participation(Person person, Project project, String role) {
        Participation participation = new Participation();
        participation.personId = person.id;
        participation.projectId = project.id;
        participation.role = role;
        return participationRepository.save(participation);
    }
}
