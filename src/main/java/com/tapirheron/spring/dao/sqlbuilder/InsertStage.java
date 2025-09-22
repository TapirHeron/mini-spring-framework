package com.tapirheron.spring.dao.sqlbuilder;

public interface InsertStage {
    AssignmentStage insertInto(String table);
}
