package me.loki2302.servlet;

import lombok.Data;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;

import static org.junit.Assert.assertEquals;

public class BeanWrapperTest {
    @Test
    public void canUseBeanWrapper() {
        MyBean myBean = new MyBean();
        BeanWrapper wrapper = new BeanWrapperImpl(myBean);

        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        assertEquals(2, propertyDescriptors.length);
        assertEquals("class", propertyDescriptors[0].getName());
        assertEquals("something", propertyDescriptors[1].getName());
        assertEquals(String.class, propertyDescriptors[1].getPropertyType());

        wrapper.setPropertyValue("something", "hey there");
        assertEquals("hey there", myBean.getSomething());
        assertEquals("hey there", wrapper.getPropertyValue("something"));
    }

    @Data
    public static class MyBean {
        private String something;
    }
}
