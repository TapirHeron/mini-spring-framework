package com.tapirheron.spring.dao.sqlbuilder;



public class SQLQuery {
    private SQLQuery(){}


    public static SelectBuilder selectBuilder() {
        return new SelectBuilder();
    }
    public static InsertBuilder insertBuilder() {
        return new InsertBuilder();
    }

    public static UpdateBuilder updateBuilder() {
        return new UpdateBuilder();
    }

    public static DeleteBuilder deleteBuilder() {
        return new DeleteBuilder();
    }
    public static WhereBuilder whereBuilder() {
        return new WhereBuilder();
    }
    public static SetBuilder setBuilder() {
        return new SetBuilder();
    }

    public static AssginmentBuilder assginmentBuilder() {
        return new AssginmentBuilder();
    }



    public static class Query{

        public static final String INSERT_INTO = "INSERT INTO %s (%s) VALUES (%s)";
    }
}
