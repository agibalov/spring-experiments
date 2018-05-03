package io.agibalov.manytomanyvia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    List<Participation> findByPerson(Person person);
    List<Participation> findByProject(Project project);
    List<Participation> findByRole(String role);
}
