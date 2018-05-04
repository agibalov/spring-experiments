package io.agibalov.querying;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class PagingAndSortingTest {
    @Autowired
    private PersonRepository personRepository;

    @Before
    public void loadData() {
        personRepository.save(Person.builder().id("1").name("Fake Spock").age(11).build());
        personRepository.save(Person.builder().id("2").name("Spock").age(22).build());
        personRepository.save(Person.builder().id("3").name("James T. Kirk").age(33).build());
        personRepository.save(Person.builder().id("4").name("Scotty").age(44).build());
        personRepository.save(Person.builder().id("5").name("Uhura").age(55).build());
    }

    @Test
    public void canGetAscendingPersons() {
        List<Person> descendingPersons = personRepository.findAll(
                new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        assertEquals(
                Arrays.asList("1", "2", "3", "4", "5"),
                descendingPersons.stream().map(p -> p.getId()).collect(Collectors.toList()));
    }

    @Test
    public void canGetDescendingPersons() {
        List<Person> descendingPersons = personRepository.findAll(
                new Sort(new Sort.Order(Sort.Direction.DESC, "id")));
        assertEquals(
                Arrays.asList("5", "4", "3", "2", "1"),
                descendingPersons.stream().map(p -> p.getId()).collect(Collectors.toList()));
    }

    @Test
    public void canPaginate() {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

        Page<Person> page1 = personRepository.findAll(new PageRequest(0, 2, sort));
        assertEquals(5, page1.getTotalElements());
        assertEquals(3, page1.getTotalPages());
        assertEquals(
                Arrays.asList("5", "4"),
                page1.getContent().stream().map(p -> p.getId()).collect(Collectors.toList()));

        Page<Person> page2 = personRepository.findAll(new PageRequest(1, 2, sort));
        assertEquals(5, page2.getTotalElements());
        assertEquals(3, page2.getTotalPages());
        assertEquals(
                Arrays.asList("3", "2"),
                page2.getContent().stream().map(p -> p.getId()).collect(Collectors.toList()));

        Page<Person> page3 = personRepository.findAll(new PageRequest(2, 2, sort));
        assertEquals(5, page3.getTotalElements());
        assertEquals(3, page3.getTotalPages());
        assertEquals(
                Arrays.asList("1"),
                page3.getContent().stream().map(p -> p.getId()).collect(Collectors.toList()));
    }

    @SpringBootApplication
    @EnableJpaRepositories
    @EntityScan
    public static class Config {
    }
}
