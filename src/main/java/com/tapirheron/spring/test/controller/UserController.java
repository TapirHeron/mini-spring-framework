package com.tapirheron.spring.test.controller;

import com.tapirheron.spring.dao.sqlbuilder.SQLQuery;
import com.tapirheron.spring.framework.ApplicationContext;
import com.tapirheron.spring.framework.Autowired;
import com.tapirheron.spring.framework.Componet;
import com.tapirheron.spring.mvc.Controller;
import com.tapirheron.spring.mvc.Param;
import com.tapirheron.spring.mvc.RequestMapping;
import com.tapirheron.spring.mvc.ResponseBody;
import com.tapirheron.spring.test.entity.UserEntity;
import com.tapirheron.spring.test.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@Componet
public class UserController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    ApplicationContext applicationContext;
    @RequestMapping("/getUser")
    @ResponseBody
    public UserEntity getUser(@Param("id") int id) {
        String sql = SQLQuery.selectBuilder()
                .select("*")
                .from("user")
                .where(SQLQuery.whereBuilder()
                        .where("id = " + id)
                        .build())
                .build();
        UserEntity o = userMapper.executeQuery(sql);
        System.out.println(o);
        return o;
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<UserEntity> getUsers(@Param("age") int age) {
        String sql = SQLQuery.selectBuilder()
                .select("*")
                .from("user")
                .where(SQLQuery.whereBuilder()
                        .where("age = " + age)
                        .build())
                .build();
        List<UserEntity> userEntities = userMapper.executeQueryList(sql);
        System.out.println(userEntities);
        return userEntities;
    }
    @RequestMapping("/add")
    @ResponseBody
    public UserEntity addUser(@Param("name") String name, @Param("age") int age, @Param("id") int id) {
        String sql = SQLQuery.insertBuilder()
                .insertInto("user")
                .assign(SQLQuery.assginmentBuilder()
                        .assign("name", name)
                        .assign("age", String.valueOf(age))
                        .assign("id", String.valueOf(id))
                        .build())
                .build();
        UserEntity userEntity = userMapper.executeQuery(sql);
        System.out.println(userEntity);
        return userEntity;
    }
}
