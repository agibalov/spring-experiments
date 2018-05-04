package io.agibalov.metadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext
public class MetadataTest {
    @Autowired
    private EntityManager entityManager;

    @Test
    public void canGetMetadata() {
        Metamodel metamodel = entityManager.getMetamodel();
        Set<EntityType<?>> entityTypesSet = metamodel.getEntities();
        assertEquals(1, entityTypesSet.size());

        EntityType<?>[] entityTypes = entityTypesSet.toArray(new EntityType<?>[entityTypesSet.size()]);
        EntityType<?> singleEntityType = entityTypes[0];

        assertEquals("Person", singleEntityType.getName());
        assertEquals(Person.class, singleEntityType.getJavaType());

        Set<? extends Attribute<?, ?>> attributesSet = singleEntityType.getDeclaredAttributes();
        assertEquals(3, attributesSet.size());

        Map<String, Attribute<?, ?>> attributeMap = new HashMap<String, Attribute<?, ?>>();
        for(Attribute<?, ?> attribute : attributesSet) {
            attributeMap.put(attribute.getName(), attribute);
        }

        Attribute<?, ?> idAttribute = attributeMap.get("id");
        assertEquals(Long.class, idAttribute.getJavaType());
        assertTrue(idAttribute instanceof SingularAttribute);
        assertTrue(((SingularAttribute<?, ?>) idAttribute).isId());

        Attribute<?, ?> nameAttribute = attributeMap.get("name");
        assertEquals(String.class, nameAttribute.getJavaType());
        assertTrue(nameAttribute instanceof SingularAttribute);
        assertFalse(((SingularAttribute<?, ?>) nameAttribute).isId());

        Attribute<?, ?> ageAttribute = attributeMap.get("age");
        assertEquals(int.class, ageAttribute.getJavaType());
        assertTrue(ageAttribute instanceof SingularAttribute);
        assertFalse(((SingularAttribute<?, ?>)ageAttribute).isId());

        SingularAttribute<?, Long> explicitIdAttribute = singleEntityType.getId(Long.class);
        assertSame(idAttribute, explicitIdAttribute);
    }

    @Test
    public void canUseSpringToGetMetadata() {
        JpaEntityInformation<Person, ?> jpaEntityInformation =
                JpaEntityInformationSupport.getEntityInformation(Person.class, entityManager);
        SingularAttribute<? super Person, ?> idAttribute = jpaEntityInformation.getIdAttribute();
        assertEquals(Long.class, idAttribute.getJavaType());
        assertEquals("id", idAttribute.getName());
    }
}
