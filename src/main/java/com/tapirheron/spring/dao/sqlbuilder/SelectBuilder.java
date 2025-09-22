package com.tapirheron.spring.dao.sqlbuilder;

public final class SelectBuilder extends AbstractBuilder.AbstractConditionalBuilder
        implements SelectStage, FromStage, WhereStage {

    private String[] columns;

    public FromStage select(String... columns) {
        this.columns = columns;
        return this;
    }
    @Override
    public WhereStage from(String table) {
        this.table = table;
        return this;
    }


    @Override
    public AbstractBuilder where(String where) {
        this.where =  where;
        return this;
    }

    @Override
    protected boolean isIllegal() {
        return table == null || columns == null;
    }

    @Override
    public String build() {
        if (isIllegal()) {
            throw new IllegalArgumentException("Illegal argument");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ")
                .append(String.join(", ", columns))
                .append(" FROM ")
                .append(table);
        return checkWhere(stringBuilder, where);
    }


}
