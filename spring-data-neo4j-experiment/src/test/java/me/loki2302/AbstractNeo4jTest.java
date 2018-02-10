package me.loki2302;

import me.loki2302.dummy.Adder;
import me.loki2302.dummy.Calculator;
import me.loki2302.dummy.Negator;
import me.loki2302.dummy.Subtractor;
import me.loki2302.entities.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.util.IterableUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.Assert.assertThat;

public abstract class AbstractNeo4jTest {
    @Autowired
    private CodeReader codeReader;

    @Autowired
    private ClassNodeRepository classNodeRepository;

    @Autowired
    private MethodNodeRepository methodNodeRepository;

    @Autowired
    private Neo4jOperations neo4jOperations;

    @Test
    public void canGetProjectedResultUsingRepository() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        List<ClassNodeIdAndName> classNodeIdsAndNames = classNodeRepository.getAllIdsAndNames();
        assertThat(classNodeIdsAndNames, hasOnly(
                c -> c.name.equals(Adder.class.getName()),
                c -> c.name.equals(Negator.class.getName()),
                c -> c.name.equals(Subtractor.class.getName()),
                c -> c.name.equals(Calculator.class.getName())
        ));
    }

    @Ignore("http://stackoverflow.com/questions/39967609/should-i-expect-neo4joperationsqueryforobjects-to-work-with-queryresult-pojos")
    @Test
    public void canGetProjectedResultUsingNeo4jOperationsAsQueryResult() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        Iterable<ClassNodeIdAndName> classNodeIdsAndNamesIterable = neo4jOperations.queryForObjects(
                ClassNodeIdAndName.class,
                "MATCH (c:Class) RETURN ID(c) AS id, c.name AS name",
                new HashMap<>());
        List<ClassNodeIdAndName> classNodeIdsAndNames = IterableUtils.toList(classNodeIdsAndNamesIterable);
        assertThat(classNodeIdsAndNames, hasOnly(
                c -> c.name.equals(Adder.class.getName()),
                c -> c.name.equals(Negator.class.getName()),
                c -> c.name.equals(Subtractor.class.getName()),
                c -> c.name.equals(Calculator.class.getName())
        ));
    }

    @Test
    public void canGetProjectedResultUsingNeo4jOperationsAsResult() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        Result result = neo4jOperations.query(
                "MATCH (c:Class) RETURN ID(c) AS id, c.name AS name",
                new HashMap<>());
        List<Map<String, Object>> maps = IterableUtils.toList(result);
        assertThat(maps, hasOnly(
                m -> m.get("name").equals(Adder.class.getName()),
                m -> m.get("name").equals(Negator.class.getName()),
                m -> m.get("name").equals(Subtractor.class.getName()),
                m -> m.get("name").equals(Calculator.class.getName())
        ));
    }

    @Test
    public void canGetClassesBasedOnTheirDependencies() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));

        List<ClassNode> classesThatDependOnOtherClasses = classNodeRepository.findAllClassesThatDependOnOtherClasses();
        assertThat(classesThatDependOnOtherClasses, hasOnly(
                c -> c.name.equals(Subtractor.class.getName()),
                c -> c.name.equals(Calculator.class.getName())
        ));

        List<ClassNode> classesThatOtherClassesDependOn = classNodeRepository.findAllClassesThatOtherClassesDependOn();
        assertThat(classesThatOtherClassesDependOn, hasOnly(
                c -> c.name.equals(Subtractor.class.getName()),
                c -> c.name.equals(Adder.class.getName()),
                c -> c.name.equals(Negator.class.getName())
        ));
    }

    @Test
    public void canGetSingleClassDependencies() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        List<ClassNode> dependenciesOfCalculator = classNodeRepository.findAllDependencyClasses(Calculator.class.getName());
        assertThat(dependenciesOfCalculator, hasOnly(
                c -> c.name.equals(Adder.class.getName()),
                c -> c.name.equals(Subtractor.class.getName())
        ));
    }

    @Test
    public void canGetSingleClassDependents() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        List<ClassNode> dependentsOfCalculator = classNodeRepository.findAllDependentClasses(Adder.class.getName());
        assertThat(dependentsOfCalculator, hasOnly(
                c -> c.name.equals(Calculator.class.getName()),
                c -> c.name.equals(Subtractor.class.getName())
        ));
    }

    @Test
    public void canGetSingleClassMethods() {
        codeReader.readCode(new File("src/main/java/me/loki2302/dummy"));
        List<MethodNode> methods = methodNodeRepository.findByClass(Calculator.class.getName());
        assertThat(methods, hasOnly(
                m -> m.name.equals(Calculator.class.getName() + "#add"),
                m -> m.name.equals(Calculator.class.getName() + "#subtract")
        ));
    }

    public static <T> Matcher<List<T>> hasOnly(Predicate<T>... predicates) {
        return new TypeSafeMatcher<List<T>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("There should be %d elements and they should match predicates", predicates.length));
            }

            @Override
            protected void describeMismatchSafely(List<T> item, Description mismatchDescription) {
                MatchResult matchResult = match(predicates, item);
                mismatchDescription.appendText(matchResult.error);
            }

            @Override
            protected boolean matchesSafely(List<T> item) {
                MatchResult matchResult = match(predicates, item);
                return matchResult.ok;
            }

            private MatchResult match(Predicate<T>[] predicates, List<T> items) {
                if(predicates.length != items.size()) {
                    return new MatchResult(false, String.format("There were %d elements instead of %d", items.size(), predicates.length));
                }

                for(int i = 0; i < predicates.length; ++i) {
                    Predicate<T> predicate = predicates[i];

                    boolean match = items.stream().anyMatch(predicate);
                    if(!match) {
                        String error = String.format("No elements matched predicate #%d. Elements were:\n", i + 1);
                        for(T it : items) {
                            error += String.format(" * %s\n", it);
                        }
                        return new MatchResult(false, error);
                    }
                }

                return new MatchResult(true, null);
            }

            class MatchResult {
                public final boolean ok;
                public final String error;

                MatchResult(boolean ok, String error) {
                    this.ok = ok;
                    this.error = error;
                }
            }
        };
    }
}
