package com.tapirheron.spring.dao.sqlbuilder;

public final class DeleteBuilder extends AbstractBuilder.AbstractConditionalBuilder
        implements DeleteStage, WhereStage {

    @Override
    protected boolean isIllegal() {
        return table == null || where == null;
    }

    @Override
    public String build() {
        if (isIllegal()) {
            throw new IllegalArgumentException("Illegal Delete SQL Query");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM ")
                .append(table);
        return checkWhere(stringBuilder, where);
    }

    @Override
    public AbstractBuilder where(String where) {
        this.where = where;
        return this;
    }

    @Override
    public WhereStage deleteFrom(String table) {
        this.table = table;
        return this;
    }
}
