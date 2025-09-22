package com.tapirheron.spring.test.mapper;

import com.tapirheron.spring.dao.BaseMapper;
import com.tapirheron.spring.dao.Mapper;
import com.tapirheron.spring.test.entity.UserEntity;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    boolean addUser(UserEntity userEntity);
}
