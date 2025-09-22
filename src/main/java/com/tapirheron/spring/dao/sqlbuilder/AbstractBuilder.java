package com.tapirheron.spring.dao.sqlbuilder;

public abstract class AbstractBuilder {
    protected String table;

    protected boolean isIllegal() {
        return false;
    }
    public abstract String build();


    static abstract class AbstractConditionalBuilder extends AbstractBuilder {
        protected String where;

        protected String checkWhere(StringBuilder stringBuilder, String where) {
            if (where == null) {
                return stringBuilder.toString();
            }
            return stringBuilder.append(" WHERE ")
                    .append(where)
                    .toString();
        }
    }
}


