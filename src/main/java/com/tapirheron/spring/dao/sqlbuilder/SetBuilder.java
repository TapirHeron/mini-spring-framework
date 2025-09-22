package com.tapirheron.spring.dao.sqlbuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SetBuilder extends AbstractBuilder {
    private final Map<String, String> setConditions;

    public SetBuilder() {
        this.setConditions = new HashMap<>();
    }

    public SetBuilder set(String column, String value) {
        this.setConditions.put(column, value);
        return this;
    }
    @Override
    protected boolean isIllegal() {
        return setConditions.isEmpty() ||
                setConditions.values()
                .stream()
                .anyMatch(Objects::isNull);
    }
    @Override
    public String build() {
         return this.setConditions
                 .entrySet()
                 .stream()
                 .map(entry -> entry.getKey() + "=" + entry.getValue())
                 .collect(Collectors.joining(", "));
    }
}
