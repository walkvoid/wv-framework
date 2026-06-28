package com.github.walkvoid.wvframework.models;

import java.io.Serializable;

/**
 * @author jiangjunqing
 * @date 2023/9/18
 * @desc 分页请求体， P（parameter）：请求参数
 */
public class PageRequest<P> implements Serializable {
    private static final long serialVersionUID = 7309392790875964006L;

    private long current;

    private int size;

    private P parameter;

    private int first;

    /**
     * 默认查询第一页（从0开始），pageSize为10
     * @param parameter
     * @return
     * @param <P>
     */
    public static <P> PageRequest<P> of(P parameter) {
        return new PageRequest<P>(0, 10, parameter, 0);
    }

    /**
     * 最常用的静态构造方法
     * @param current
     * @param pageSize
     * @param parameter
     * @return
     * @param <P>
     */
    public static <P> PageRequest<P> of(long current, int pageSize, P parameter) {
        return new PageRequest<P>(current, pageSize, parameter, 0);
    }

    /**
     * 全参数的静态构造方法
     * @param current
     * @param pageSize
     * @param parameter
     * @param firstPage
     * @return
     * @param <P>
     */
    public static <P> PageRequest<P> of(long current, int pageSize, P parameter, int firstPage) {
        return new PageRequest<P>(current, pageSize, parameter, firstPage);
    }

    /**
     * 全参构造方法
     * @param current
     * @param size
     * @param parameter
     * @param first
     */
    protected PageRequest(long current, int size, P parameter, int first) {
        if (first != 0 && first != 1) {
            throw new IllegalArgumentException("firstPage just can be 0 or 1");
        }
        this.current = current;
        this.size = size;
        this.parameter = parameter;
        this.first = first;
    }

    /**
     * 空参构造方法
     */
    public PageRequest() {
    }


    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public P getParameter() {
        return parameter;
    }

    public void setParameter(P parameter) {
        this.parameter = parameter;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    /**
     * 解析有效页大小，默认 10
     */
    public int resolvePageSize() {
        return size > 0 ? size : 10;
    }

    /**
     * 转换为 MyBatis-Plus 页码（从 1 开始）
     */
    public long toMpPageNum() {
        if (first == 0) {
            return current + 1;
        }
        return current > 0 ? current : 1;
    }

    /**
     * 将 MyBatis-Plus 页码转换为响应当前页（与 firstPage 约定一致）
     */
    public long toResponseCurrent(long mpCurrent) {
        return first == 0 ? mpCurrent - 1 : mpCurrent;
    }
}
