package me.loki2302.decltransactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public void createPersonAndThenThrow() {
        Person person = new Person();
        person.name = "loki2302";
        personRepository.save(person);

        throw new OopsException();
    }
}
