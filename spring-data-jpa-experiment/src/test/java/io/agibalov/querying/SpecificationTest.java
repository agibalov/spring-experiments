package io.agibalov.querying;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class SpecificationTest {
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
    public void canGetPersonsByAgeCriterion() {
        List<Person> persons = personRepository.findAll(new Specification<Person>() {
            @Override
            public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("age"), 22),
                        cb.lessThanOrEqualTo(root.get("age"), 44));
            }
        });
        assertEquals(
                new HashSet<>(Arrays.asList("2", "3", "4")),
                persons.stream().map(p -> p.getId()).collect(Collectors.toSet()));
    }

    @Test
    public void canGetPersonsByNameCriterion() {
        List<Person> persons = personRepository.findAll(new Specification<Person>() {
            @Override
            public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(root.get("name"), "%" + "Spock" + "%");
            }
        });
        assertEquals(
                new HashSet<>(Arrays.asList("1", "2")),
                persons.stream().map(p -> p.getId()).collect(Collectors.toSet()));
    }

    @SpringBootApplication
    @EnableJpaRepositories
    @EntityScan
    public static class Config {
    }
}
