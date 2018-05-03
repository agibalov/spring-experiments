package io.agibalov.polymorphic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PolymorphicEntitiesTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private GoodPersonRepository goodPersonRepository;

    @Autowired
    private BadPersonRepository badPersonRepository;

    @Test
    public void canUsePolymorphicEntities() {
        GoodPerson goodPerson = new GoodPerson();
        goodPerson.name = "loki2302";
        goodPerson.goodnessLevel = 10;
        goodPerson = personRepository.save(goodPerson);

        BadPerson badPerson = new BadPerson();
        badPerson.name = "Andrey";
        badPerson.badnessLevel = 9001;
        badPerson = personRepository.save(badPerson);

        List<Person> people = personRepository.findAll();
        assertEquals(2, people.size());
        assertPersonEquals(goodPerson, people.get(0));
        assertPersonEquals(badPerson, people.get(1));

        List<GoodPerson> goodPeople = goodPersonRepository.findAll();
        assertEquals(1, goodPeople.size());
        assertPersonEquals(goodPerson, goodPeople.get(0));

        List<BadPerson> badPeople = badPersonRepository.findAll();
        assertEquals(1, badPeople.size());
        assertPersonEquals(badPerson, badPeople.get(0));
    }

    private static void assertPersonEquals(Person expected, Person actual) {
        assertEquals(expected.id, actual.id);
        assertEquals(expected.name, actual.name);
        assertEquals(expected.getClass(), actual.getClass());

        if(expected instanceof GoodPerson) {
            GoodPerson expectedGoodPerson = (GoodPerson)expected;
            GoodPerson actualGoodPerson = (GoodPerson)actual;
            assertEquals(expectedGoodPerson.goodnessLevel, actualGoodPerson.goodnessLevel);
        } else if(expected instanceof BadPerson) {
            BadPerson expectedBadPerson = (BadPerson)expected;
            BadPerson actualBadPerson = (BadPerson)actual;
            assertEquals(expectedBadPerson.badnessLevel, actualBadPerson.badnessLevel);
        } else {
            fail("Unknown person type: " + expected.getClass());
        }
    }
}
