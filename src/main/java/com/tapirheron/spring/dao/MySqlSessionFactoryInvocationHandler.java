package com.tapirheron.spring.dao;

import com.tapirheron.spring.framework.ApplicationContext;
import com.tapirheron.spring.framework.Autowired;
import com.tapirheron.spring.framework.Componet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Data
@Componet
public class MySqlSessionFactoryInvocationHandler implements InvocationHandler {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MySqlSessionFactory mySqlSessionFactory;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
        if (method.getDeclaringClass() == Object.class) {
            if (proxy == null) {
                return null;
            }
            return method.invoke(applicationContext.getBean(proxy.getClass()), args);
        }


        return executeSql(args[0].toString(), method, method.getReturnType());
    }

    private <T> T executeSql(String sql, Method method, Class<T> returnType) throws Exception {
        log.info("正在执行sql\t{}", sql);
        try (PreparedStatement statement = mySqlSessionFactory.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                log.error("查询结果为空");
                return null;
            }
            if (isContainerType(returnType)) {
                return (T) getResults(Objects.requireNonNull(getElementClass(method)), resultSet);
            }
            return getResult(returnType, resultSet);
        } catch (SQLException e) {
            log.error("准备SQL语句时发生异常，SQL: {}, 错误信息: {}", sql, e.getMessage());
            throw e;
        }
    }
    /**
     * 获取容器内元素类型
     * @param method
     * @return 元素类型
     */
    private Class<?> getElementClass(Method method) {
        // 获取方法的返回类型
        Type returnType = method.getGenericReturnType();

        // 如果是参数化类型（如List<UserEntity>）
        if (returnType instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return null;
    }
    private boolean isContainerType(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz) ||
                clazz.isArray();
    }

    private <T> T createEntity(Class<T> returnType, ResultSet resultSet) throws Exception {
        T entity = returnType.getConstructor().newInstance();
        for (Field field : returnType.getDeclaredFields()) {
            Object column = null;
            String name;
            if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).isTableExist()) {
                name = field.getName();
            } else {
                name = field.getAnnotation(Column.class).columnName();
            }
            if (field.getType() == String.class) {
                column = resultSet.getString(name);
            } else if (field.getType() == int.class) {
                column = resultSet.getInt(name);
            } else if (field.getType() == float.class) {
                column = resultSet.getLong(name);
            }
            if (column == null) {
                continue;
            }
            field.setAccessible(true);
            field.set(entity, column);
        }
        return entity;
    }
    /**
     * 获取查询结果
     * @param resultSet
     * @return 查询的对象
     * @throws Exception
     */
    private <T> T getResult(Class<T> returnType, ResultSet resultSet) throws Exception {
        return createEntity(returnType, resultSet);
    }
    private <T> List<T> getResults(Class<T> returnType, ResultSet resultSet) throws Exception {
        List<T> results = new ArrayList<>();
        do {
            T entity = createEntity(returnType, resultSet);
            results.add(entity);
        } while (resultSet.next());
        return results;
    }




}
