package com.tapirheron.spring.dao.sqlbuilder;

public interface SelectStage {
    FromStage select(String... columns);

}
