package me.loki2302;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = Config.class)
public class SecurityTests {
    @Autowired
    private Calculator calculator;

    @Before
    public void setUpAuthentication() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                "hello",
                "hello",
                AuthorityUtils.NO_AUTHORITIES);

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    public void canUseAddWhenLeftOperandIsLessThanOrEqualTo10() {
        assertEquals(11, calculator.add(9, 2));
    }

    @Test(expected = AccessDeniedException.class)
    public void cantUseAddWhenLeftOperandIsGreaterThan10() {
        calculator.add(11, 2);
    }

    @Test
    public void canUseDivWhenRightOperandIsNot13() {
        assertEquals(5, calculator.div(11, 2));
    }

    @Test(expected = AccessDeniedException.class)
    public void cantUseDivWhenRightOperandIs13() {
        calculator.div(11, 13);
    }

    @Test
    public void canUseSubIfResultIsNot5() {
        assertEquals(-1, calculator.sub(1, 2));
    }

    @Test(expected = AccessDeniedException.class)
    public void cantUseSubIfResultIs5() {
        calculator.sub(10, 5);
    }

    @Test
    public void canSumUpNumbersWhenTheyAreAllEven() {
        int result = calculator.sum(new ArrayList<Integer>(Arrays.asList(2, 6, 4)));
        assertEquals(12, result);
    }

    @Test
    public void sumIgnoresOddNumbers() {
        int result = calculator.sum(new ArrayList<Integer>(Arrays.asList(2, 6, 4, 3)));
        assertEquals(12, result);
    }

    @Test
    public void canDoubleAllNumbersIfResultIsNotDivisibleBy3() {
        List<Integer> result = calculator.doublify(Arrays.asList(1, 2, 5));
        assertEquals(3, result.size());
        assertEquals(2, (int)result.get(0));
        assertEquals(4, (int)result.get(1));
        assertEquals(10, (int)result.get(2));
    }

    @Test
    public void doubleSkipsTheResultsWhichAreDivisibleBy3() {
        List<Integer> result = calculator.doublify(Arrays.asList(1, 2, 6, 5));
        assertEquals(3, result.size());
        assertEquals(2, (int)result.get(0));
        assertEquals(4, (int)result.get(1));
        assertEquals(10, (int)result.get(2));
    }
}
