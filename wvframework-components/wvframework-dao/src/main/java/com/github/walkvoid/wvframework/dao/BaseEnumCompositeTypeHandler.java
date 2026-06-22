package com.github.walkvoid.wvframework.dao;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 懒加载枚举 Composite 路由 Handler — 注册为 MyBatis 默认枚举处理器，运行时动态选择委托：
 * <ol>
 *   <li>枚举实现了 {@link BaseEnum} → 委托 {@link BaseEnumTypeHandler}</li>
 *   <li>其他枚举 → 委托 MyBatis 原生 {@link EnumTypeHandler}</li>
 * </ol>
 *
 * <p>使用者只需引入 {@code wvframework-dao-starter}，无需任何配置。枚举类实现 {@link BaseEnum} 即可自动生效。
 *
 * @author walkvoid
 */
public class BaseEnumCompositeTypeHandler<E extends Enum<E>> implements TypeHandler<E> {

    private final TypeHandler<E> delegate;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BaseEnumCompositeTypeHandler(Class<E> type) {
        if (BaseEnum.class.isAssignableFrom(type)) {
            this.delegate = new BaseEnumTypeHandler(type);
        } else {
            this.delegate = new EnumTypeHandler(type);
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        delegate.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getResult(ResultSet rs, String columnName) throws SQLException {
        return delegate.getResult(rs, columnName);
    }

    @Override
    public E getResult(ResultSet rs, int columnIndex) throws SQLException {
        return delegate.getResult(rs, columnIndex);
    }

    @Override
    public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delegate.getResult(cs, columnIndex);
    }
}
