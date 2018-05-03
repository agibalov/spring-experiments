package io.agibalov.polymorphic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BadPersonRepository extends JpaRepository<BadPerson, Long> {
}
