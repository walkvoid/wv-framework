package com.github.walkvoid.wvframework.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.annotation.PostConstruct;

/**
 * WV Framework DAO 自动配置 — 注册 BaseEnumCompositeTypeHandler 为 MyBatis 默认枚举处理器。
 *
 * <p>配置项：
 * <ul>
 *   <li>{@code wvframework.dao.enabled=true} — 是否开启（默认 true）</li>
 * </ul>
 *
 * <p>无需额外配置，枚举类实现 {@link BaseEnum} 即可自动生效。
 *
 * @author walkvoid
 */
@AutoConfiguration
@ConditionalOnClass({BaseEnum.class, SqlSessionFactory.class})
@ConditionalOnProperty(prefix = "wvframework.dao", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(SqlSessionFactory.class)
public class WvframeworkDaoAutoConfiguration {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 将 BaseEnumCompositeTypeHandler 注册为 MyBatis 默认枚举处理器。
     * 之后每次 MyBatis 遇到枚举类型时，Composite 会运行时判断：
     * implements BaseEnum → BaseEnumTypeHandler，否则 → EnumTypeHandler。
     */
    @PostConstruct
    public void registerDefaultEnumTypeHandler() {
        sqlSessionFactory.getConfiguration()
                .getTypeHandlerRegistry()
                .setDefaultEnumTypeHandler(BaseEnumCompositeTypeHandler.class);
    }
}
