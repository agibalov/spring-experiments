package me.loki2302.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(
            "select person from Person person " +
            "join person.projects personProjects " +
            "where personProjects in :projects " +
            "group by person.id " +
            "having count(person.id) = :projectsCount") // how do I get count from :projects?
    List<Person> findByProjects(
            @Param("projects") List<Project> projects,
            @Param("projectsCount") long projectsCount);
}
