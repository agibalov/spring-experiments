package me.loki2302.udt;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class IntegerAsVarcharUserType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[] {Types.VARCHAR };
    }

    @Override
    public Class returnedClass() {
        System.out.printf("returnedClass()\n");
        return Integer.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        System.out.printf("equals(%s, %s)\n", x, y);
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        System.out.printf("hashCode(%s)\n", x);
        return Objects.hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Object obj = null;

        if(!rs.wasNull()) {
            String readString = rs.getString(names[0]);
            System.out.printf("nullSafeGet(): got string '%s'\n", readString);

            obj = Integer.valueOf(readString);
            System.out.printf("nullSafeGet(): read as %d\n", obj);
        }

        System.out.printf("nullSafeGet()=%s\n", obj);

        return obj;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        System.out.printf("nullSafeSet(%s)\n", value);
        if(value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, value.toString());
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return Integer.valueOf(value.toString());
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Integer)deepCopy(value);
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
