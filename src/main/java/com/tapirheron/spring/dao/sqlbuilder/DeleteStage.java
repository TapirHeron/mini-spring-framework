package com.tapirheron.spring.dao.sqlbuilder;

public interface DeleteStage {
    WhereStage deleteFrom(String table);
}
