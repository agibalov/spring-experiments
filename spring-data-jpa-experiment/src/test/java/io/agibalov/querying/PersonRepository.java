package io.agibalov.querying;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository extends JpaRepository<Person, String>, JpaSpecificationExecutor<Person> {
}
