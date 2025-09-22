package com.tapirheron.spring.dao.sqlbuilder;

public final class UpdateBuilder extends AbstractBuilder.AbstractConditionalBuilder
        implements UpdateStage, SetStage, WhereStage {


    private String setExpression;

    @Override
    public AbstractBuilder where(String where) {
        this.where =  where;
        return this;
    }
    @Override
    protected boolean isIllegal() {
        return table == null || setExpression == null;
    }
    @Override
    public String build() {
        if (isIllegal()) {
            throw new IllegalArgumentException("Table or setExpression is null");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE ")
                .append(table)
                .append(" SET ")
                .append(setExpression);
        return checkWhere(stringBuilder, where);
    }

    @Override
    public WhereStage set(String setExpression) {
        this.setExpression = setExpression;
        return this;
    }

    @Override
    public SetStage update(String table) {
        this.table = table;
        return this;
    }
}
