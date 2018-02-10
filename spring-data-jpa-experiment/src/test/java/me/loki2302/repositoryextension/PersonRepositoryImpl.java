package me.loki2302.repositoryextension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// Important: name should be XXXRepositoryImpl
public class PersonRepositoryImpl implements PersonRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Person customFindOne(long id) {
        return entityManager.find(Person.class, id);
    }
}
