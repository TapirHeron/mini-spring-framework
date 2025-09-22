package com.tapirheron.spring.dao;

import com.tapirheron.spring.dao.sqlbuilder.AssginmentBuilder;
import com.tapirheron.spring.dao.sqlbuilder.SQLQuery;
import com.tapirheron.spring.dao.sqlbuilder.SetBuilder;
import com.tapirheron.spring.test.entity.UserEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface BaseMapper<T> {

    UserEntity executeQuery(String sqlQuery);

    List<UserEntity> executeQueryList(String sqlQuery);
    /**
     * 获取字段map
     * @param t
     * @return 表名
     */
    private String getSqlTableName(T t) {
        String tableName;
        if (t.getClass().isAnnotationPresent(Table.class) &&
                !t.getClass().getAnnotation(Table.class).tableName().isEmpty()) {
            tableName = t.getClass().getAnnotation(Table.class).tableName();
        } else {
            tableName = convertClassNameToSqlName(t.getClass().getSimpleName());
        }
        return tableName;
    }
    private Map<String, Object> getSqlFieldValue(T t) {
        Map<String, Object> fieldValueMap = new HashMap<>();
        for (Field field : t.getClass().getDeclaredFields()) {
            // 明确标注在表中不存在的字段
            if (field.isAnnotationPresent(Column.class) &&
                    !field.getAnnotation(Column.class).isTableExist()) {
                continue;
            }
            String columnName;
            field.setAccessible(true);
            // 获取字段名，默认用Column注解的columnName属性，如果为空，则转化java字段名为sql字段名
            if (field.isAnnotationPresent(Column.class) &&
                    !field.getAnnotation(Column.class).columnName().isEmpty()) {
                columnName = field.getAnnotation(Column.class).columnName();
            } else {
                columnName = convertClassNameToSqlName(field.getName());
            }
            try {
                fieldValueMap.put(columnName, field.get(t));
            } catch (IllegalAccessException ignore) {}
        }
        return fieldValueMap;
    }
    default boolean save(T t) {
        String tableName = getSqlTableName(t);
        Map<String, Object> fieldValueMap = getSqlFieldValue(t);
        AssginmentBuilder assginmentBuilder = SQLQuery.assginmentBuilder();
        fieldValueMap.forEach((columnName, value) -> assginmentBuilder.assign(columnName, value.toString()));
        Object o = executeQuery(SQLQuery.insertBuilder().
                insertInto(tableName)
                .assign(assginmentBuilder.build())
                .build());
        return o != null;
    }

    default boolean updateById(T t) {
        String tableName = getSqlTableName(t);
        Map<String, Object> fieldValueMap = getSqlFieldValue(t);
        String primaryKey = getPrimaryKey(t);
        if (primaryKey == null) {
            return false;
        }
        Object primaryKeyValue = fieldValueMap.get(primaryKey);
        SetBuilder setBuilder = SQLQuery.setBuilder();
        fieldValueMap.forEach((columnName, value) -> setBuilder.set(columnName, value.toString()));
        Object o = executeQuery(SQLQuery.updateBuilder()
                .update(tableName)
                .set(setBuilder.build())
                .where(SQLQuery.whereBuilder()
                        .where(primaryKey + "=" + primaryKeyValue.toString())
                        .build())
                .build());
        return o != null;
    }
    default boolean deleteById(T t) {
        String tableName = getSqlTableName(t);
        String primaryKey = getPrimaryKey(t);
        if (primaryKey == null) {
            return false;
        }
        Map<String, Object> fieldValueMap = getSqlFieldValue(t);
        Object primaryKeyValue = fieldValueMap.get(primaryKey);
        Object o = executeQuery(SQLQuery.deleteBuilder()
                .deleteFrom(tableName)
                .where(SQLQuery.whereBuilder()
                        .where(primaryKey + "=" + primaryKeyValue.toString())
                        .build())
                .build());
        return o != null;
    }

    default T selectById(T t) {
        System.out.println("================\n" + t);
        String tableName = getSqlTableName(t);
        String primaryKey = getPrimaryKey(t);
        if (primaryKey == null) {
            return null;
        }
        Map<String, Object> fieldValueMap = getSqlFieldValue(t);
        Object primaryKeyValue = fieldValueMap.get(primaryKey);
        Object o = executeQuery(SQLQuery.selectBuilder()
                .select("*")
                .from(tableName)
                .where(SQLQuery.whereBuilder()
                        .where(primaryKey + "=" + primaryKeyValue.toString())
                        .build())
                .build());
        return (T) o;
    }

    /**
     * 获取主键字段名
     * @param t
     * @return
     */
    private String getPrimaryKey(T t) {
        Field primaryKeyField = null;
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                primaryKeyField = field;
            }
        }
        if (primaryKeyField == null) {
            return null;
        }
        if (primaryKeyField.isAnnotationPresent(Column.class) && 
                !primaryKeyField.getAnnotation(Column.class).columnName().isEmpty()) {
            return primaryKeyField.getAnnotation(Column.class).columnName();
        }
        return convertClassNameToSqlName(primaryKeyField.getName());
    }

    private String convertClassNameToSqlName(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(className.charAt(0)));

        for (int i = 1; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
