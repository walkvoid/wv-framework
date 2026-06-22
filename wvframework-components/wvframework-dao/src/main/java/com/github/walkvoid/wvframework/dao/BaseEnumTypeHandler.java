package com.github.walkvoid.wvframework.dao;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用枚举 TypeHandler — 实现 {@link BaseEnum} 的枚举自动与数据库列映射。
 *
 * <p>key 类型通过 {@link BaseEnum#getKey()} 返回的 {@link Serializable} 自动适配，
 * JDBC 的 setObject/getObject 负责类型转换。支持 Integer、Long、String、Short 等。
 *
 * @param <E> 枚举类型
 * @param <K> key 的 Serializable 类型
 * @author walkvoid
 */
public class BaseEnumTypeHandler<E extends Enum<E> & BaseEnum<K>, K extends Serializable>
        extends BaseTypeHandler<E> {

    private final Class<E> type;

    public BaseEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
            throws SQLException {
        K key = parameter.getKey();
        if (jdbcType == null) {
            ps.setObject(i, key);
        } else {
            ps.setObject(i, key, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        K key = getKey(rs.getObject(columnName));
        return rs.wasNull() ? null : valueOf(key);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        K key = getKey(rs.getObject(columnIndex));
        return rs.wasNull() ? null : valueOf(key);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        K key = getKey(cs.getObject(columnIndex));
        return cs.wasNull() ? null : valueOf(key);
    }

    @SuppressWarnings("unchecked")
    private K getKey(Object value) {
        return (K) value;
    }

    private E valueOf(K key) {
        for (E e : type.getEnumConstants()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "Cannot convert " + key + " to " + type.getSimpleName());
    }
}
