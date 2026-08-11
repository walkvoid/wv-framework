package com.github.walkvoid.wvframework.models;

import java.io.Serializable;

/**
 * 分页请求体，P（param）：请求参数
 *
 * @author jiangjunqing
 * @date 2023/9/18
 */
public class PageRequest<P> implements Serializable {
    private static final long serialVersionUID = 7309392790875964006L;

    /** 当前页，前端传1-based，内部存0-based */
    private long current;

    private int size;

    private P param;

    /**
     * 默认查询第一页（从0开始），pageSize为10
     */
    public static <P> PageRequest<P> of(P param) {
        return new PageRequest<P>(1, 10, param);
    }

    /**
     * 最常用的静态构造方法，current 为前端1-based页码，内部自动转为0-based
     */
    public static <P> PageRequest<P> of(long current, int pageSize, P param) {
        return new PageRequest<P>(current, pageSize, param);
    }

    /**
     * 全参构造方法，current 为前端1-based页码，内部自动转为0-based
     */
    protected PageRequest(long current, int size, P param) {
        this.current = Math.max(0, current - 1);
        this.size = size;
        this.param = param;
    }

    /**
     * 空参构造方法
     */
    public PageRequest() {
    }

    public long getCurrent() {
        return current;
    }

    /** 前端传1-based，setter内自动转为0-based */
    public void setCurrent(long current) {
        this.current = Math.max(0, current - 1);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public P getParam() {
        return param;
    }

    public void setParam(P param) {
        this.param = param;
    }

    /**
     * 解析有效页大小，默认 10
     */
    public int resolvePageSize() {
        return size > 0 ? size : 10;
    }
}
