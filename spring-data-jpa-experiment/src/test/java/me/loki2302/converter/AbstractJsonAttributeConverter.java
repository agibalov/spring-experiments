package me.loki2302.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public abstract class AbstractJsonAttributeConverter<T> implements AttributeConverter<T, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<T> clazz;
    private final TypeReference<T> typeReference;

    public AbstractJsonAttributeConverter(Class<T> clazz) {
        this.clazz = clazz;
        this.typeReference = null;
    }

    public AbstractJsonAttributeConverter(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
        this.clazz = null;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if(attribute == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }

        try {
            if(clazz != null) {
                return OBJECT_MAPPER.readValue(dbData, clazz);
            } else if(typeReference != null) {
                return OBJECT_MAPPER.readValue(dbData, typeReference);
            } else {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
