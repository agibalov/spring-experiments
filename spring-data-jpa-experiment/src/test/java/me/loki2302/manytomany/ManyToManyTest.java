package me.loki2302.manytomany;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class ManyToManyTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void canUseManyToMany() {
        Project projectOne = project();
        Project projectTwo = project();
        Project projectThree = project();

        Person personOne = addProjects(person(), projectOne);
        Person personTwo = addProjects(person(), projectTwo);
        Person personThree = addProjects(person(), projectThree);
        Person personFour = addProjects(person(), projectOne, projectTwo);
        Person personFive = addProjects(person(), projectOne, projectTwo, projectThree);

        assertEquals(3, projectRepository.findAll().size());
        assertEquals(5, personRepository.findAll().size());


        assertEquals(3, personRepository.findByProjects(Arrays.asList(projectOne), 1).size());
        assertEquals(3, personRepository.findByProjects(Arrays.asList(projectTwo), 1).size());
        assertEquals(2, personRepository.findByProjects(Arrays.asList(projectThree), 1).size());
        assertEquals(2, personRepository.findByProjects(Arrays.asList(projectOne, projectTwo), 2).size());
        assertEquals(1, personRepository.findByProjects(Arrays.asList(projectOne, projectTwo, projectThree), 3).size());

        personOne = personRepository.findOne(personOne.id);
        assertEquals(1, personOne.projects.size());

        personTwo = personRepository.findOne(personTwo.id);
        assertEquals(1, personTwo.projects.size());

        personThree = personRepository.findOne(personThree.id);
        assertEquals(1, personThree.projects.size());

        personFour = personRepository.findOne(personFour.id);
        assertEquals(2, personFour.projects.size());

        personFive = personRepository.findOne(personFive.id);
        assertEquals(3, personFive.projects.size());
    }

    private Person person() {
        return personRepository.save(new Person());
    }

    private Project project() {
        return projectRepository.save(new Project());
    }

    private Person addProjects(Person person, Project... projects) {
        person.projects.addAll(Arrays.asList(projects));
        return personRepository.save(person);
    }
}
