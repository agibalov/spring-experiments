package io.agibalov.converter;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

public class SetOfStringAsJsonAttributeConverter extends AbstractJsonAttributeConverter<Set<String>> {
    public SetOfStringAsJsonAttributeConverter() {
        super(new TypeReference<Set<String>>() {});
    }
}
