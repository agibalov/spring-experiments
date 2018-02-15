package me.loki2302.servlet;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.validation.*;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataBinderTest {
    @Test
    public void shouldAllowMeToBindData() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("something", "omg");

        MyBean myBean = new MyBean();
        DataBinder dataBinder = new DataBinder(myBean);
        dataBinder.bind(propertyValues);

        assertEquals("omg", myBean.getSomething());
    }

    @Test
    public void shouldAllowMeToValidateDataUsingHibernateValidator() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("something", "");
        propertyValues.add("todos", Arrays.asList("a", "b"));

        MyBean myBean = new MyBean();
        DataBinder dataBinder = new DataBinder(myBean);
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setMessageInterpolator(new ParameterMessageInterpolator());
        localValidatorFactoryBean.afterPropertiesSet();
        dataBinder.setValidator(localValidatorFactoryBean);
        dataBinder.bind(propertyValues);
        dataBinder.validate();

        BindingResult bindingResult = dataBinder.getBindingResult();
        assertTrue(bindingResult.hasErrors());

        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream().collect(Collectors.toList());
        assertEquals(3, fieldErrors.size());

        FieldError todosErrorA = fieldErrors.stream().filter(e -> e.getRejectedValue().equals("a")).findFirst().get();
        assertEquals("length must be between 5 and 2147483647", todosErrorA.getDefaultMessage());
        assertEquals("todos[]", todosErrorA.getField());

        FieldError todosErrorB = fieldErrors.stream().filter(e -> e.getRejectedValue().equals("b")).findFirst().get();
        assertEquals("length must be between 5 and 2147483647", todosErrorB.getDefaultMessage());
        assertEquals("todos[]", todosErrorB.getField());

        FieldError somethingError = fieldErrors.stream().filter(e -> e.getField().equals("something")).findFirst().get();
        assertEquals("must not be empty", somethingError.getDefaultMessage());
        assertEquals("", somethingError.getRejectedValue());
    }

    @Test
    public void shouldAllowMeToValidateDataUsingSpringValidator() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("something", "");

        MyBean myBean = new MyBean();
        DataBinder dataBinder = new DataBinder(myBean);
        dataBinder.setValidator(new MyBeanValidator());
        dataBinder.bind(propertyValues);
        dataBinder.validate();

        BindingResult bindingResult = dataBinder.getBindingResult();
        assertTrue(bindingResult.hasErrors());

        FieldError fieldError = bindingResult.getFieldErrors().stream()
                .filter(e -> e.getField().equals("something"))
                .findAny()
                .get();
        assertEquals("field.required", fieldError.getCode());
        assertEquals("Why so empty?", fieldError.getDefaultMessage());
    }

    @Test
    public void shouldLetMeUseTheStringTrimmerEditorToTrimStrings() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("something", "   hey there  ");

        MyBean myBean = new MyBean();
        DataBinder dataBinder = new DataBinder(myBean);
        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        dataBinder.bind(propertyValues);
        assertEquals("hey there", myBean.getSomething());
    }

    @Test
    public void canUseIntToStringConverter() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("something", 1);

        MyBean myBean = new MyBean();
        DataBinder dataBinder = new DataBinder(myBean);
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(new IntToStringConverter());
        dataBinder.setConversionService(defaultConversionService);
        dataBinder.bind(propertyValues);
        assertEquals("one", myBean.getSomething());
    }

    @Data
    public static class MyBean {
        @NotEmpty
        private String something;
        private Set<@Length(min = 5) String> todos;
    }

    public static class MyBeanValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return MyBean.class.equals(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, "something", "field.required",
                    "Why so empty?");
        }
    }

    public static class IntToStringConverter implements Converter<Integer, String> {
        @Override
        public String convert(Integer source) {
            if(source == null) {
                return null;
            }

            if(source < 0 || source > 2) {
                throw new IllegalArgumentException();
            }

            return (new String[] { "zero", "one", "two" })[source];
        }
    }
}
