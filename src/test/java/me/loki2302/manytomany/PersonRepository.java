package me.loki2302.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByProjectsIn(List<Project> projects);
}
