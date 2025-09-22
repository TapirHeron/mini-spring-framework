package com.tapirheron.spring.test.entity;

import com.tapirheron.spring.dao.Column;
import com.tapirheron.spring.dao.Table;
import lombok.Data;

@Table(tableName = "test")
@Data
public class UserEntity {
    @Column(columnName = "id")
    private int id;
    @Column(columnName = "name")
    private String name;
    @Column(columnName = "age")
    private int age;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
