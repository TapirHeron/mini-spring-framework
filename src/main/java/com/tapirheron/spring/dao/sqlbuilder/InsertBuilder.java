package com.tapirheron.spring.dao.sqlbuilder;

public final class InsertBuilder extends AbstractBuilder
        implements InsertStage, AssignmentStage {

    private String assignment;

    @Override
    protected boolean isIllegal() {
        return assignment == null || table == null;
    }

    @Override
    public String build() {
        if (isIllegal()) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        return new StringBuilder()
                .append("INSERT INTO ")
                .append(table)
                .append(assignment)
                .toString();
    }

    @Override
    public AbstractBuilder assign(String assignment) {
        this.assignment = assignment;
        return this;
    }

    @Override
    public AssignmentStage insertInto(String table) {
        this.table = table;
        return this;
    }
}
