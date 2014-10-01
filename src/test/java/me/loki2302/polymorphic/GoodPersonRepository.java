package me.loki2302.polymorphic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodPersonRepository extends JpaRepository<GoodPerson, Long> {
}
