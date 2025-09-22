package com.tapirheron.spring.dao.sqlbuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssginmentBuilder extends AbstractBuilder {
    private final Map<String, String> assignmentsMap;

    public AssginmentBuilder() {
        this.assignmentsMap = new HashMap<>();
    }

    public AssginmentBuilder assign(String column, String value) {
        this.assignmentsMap.put(column, value);
        return this;
    }

    @Override
    protected boolean isIllegal() {
      return assignmentsMap.values()
      .stream()
      .anyMatch(Objects::isNull);
    }

    @Override
    public String build() {
        if (isIllegal()) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        StringBuilder stringBuilder = new StringBuilder()
                .append("(");
        for (String column : assignmentsMap.keySet()) {
            stringBuilder.append(column)
                    .append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")")
                .append(" VALUES (");
        for (String value : assignmentsMap.values()) {
            stringBuilder.append(value)
                    .append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.append(")").toString();
    }
}
