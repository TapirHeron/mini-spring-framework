package com.tapirheron.spring.dao.sqlbuilder;

public class Main {
    public static void main(String[] args) {
        System.out.println(
                SQLQuery.selectBuilder()
                        .select("id", "name", "email")
                        .from("users")
                        .where(SQLQuery.whereBuilder()
                                .where("id = 1")
                                .where("name = 'John'")
                                .where("email = 'john@example.com'")
                                .build())
                        .build());
        System.out.println(SQLQuery.updateBuilder()
                .update("users")
                .set(SQLQuery.setBuilder()
                        .set("name", "John Doe")
                        .set("email", "john@example.com")
                        .build())
                .where(SQLQuery.whereBuilder()
                        .where("id = 1")
                        .where("name = 'John'")
                        .build())
                .build());
        System.out.println(SQLQuery.deleteBuilder()
                .deleteFrom("users")
                .where(SQLQuery.whereBuilder()
                        .where("id = 1")
                        .where("name = 'John'")
                        .build())
                .build());
        System.out.println(SQLQuery.insertBuilder()
                .insertInto("users")
                .assign(SQLQuery.assginmentBuilder()
                        .assign("name", "John Doe")
                        .assign("email", "john@example.com")
                        .assign("password", "password")
                        .build())
                .build());
    }
}