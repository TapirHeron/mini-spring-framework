package com.tapirheron.spring.dao.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public final class WhereBuilder extends AbstractBuilder {

    private final List<String> wheres;
    public WhereBuilder(){
        this.wheres = new ArrayList<>();
    }
    public WhereBuilder where(String where) {
        this.wheres.add(where);
        return this;
    }
    @Override
    protected boolean isIllegal() {
        return wheres.isEmpty();
    }
    @Override
    public String build() {
        if (isIllegal()) {
            throw new IllegalStateException("Illegal arguments");
        }
        return String.join(" AND ", wheres);
    }

}
