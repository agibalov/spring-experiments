package io.agibalov.udt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

public class DTOAsJSONUserType implements UserType, ParameterizedType {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private Class<?> clazz;

    @Override
    public void setParameterValues(Properties parameters) {
        String className = parameters.getProperty("className");
        if(className == null) {
            throw new HibernateException("className is required");
        }

        try {
            clazz = ReflectHelper.classForName(className);
        } catch (ClassNotFoundException e) {
            throw new HibernateException("class not found", e);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class returnedClass() {
        return clazz;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String jsonString = rs.getString(names[0]);
        if(jsonString == null) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new HibernateException("Can't read JSON from result set", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if(value == null) {
            st.setNull(index, Types.VARCHAR);
            return;
        }

        try {
            String jsonString = objectMapper.writeValueAsString(value);
            st.setString(index, jsonString);
        } catch (JsonProcessingException e) {
            throw new HibernateException("Can't convert object to JSON", e);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if(value == null) {
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(value);
            Object obj =  objectMapper.readValue(json, clazz);
            return obj;
        } catch (JsonProcessingException e) {
            throw new HibernateException("Failed to deep copy object", e);
        } catch (IOException e) {
            throw new HibernateException("Failed to deep copy object", e);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new HibernateException("Can't convert object to JSON", e);
        }
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
