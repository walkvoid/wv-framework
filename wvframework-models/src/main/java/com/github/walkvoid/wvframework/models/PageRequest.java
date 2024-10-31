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

    private int pageSize;

    private P parameter;

    private int firstPage;

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
     * @param pageSize
     * @param parameter
     * @param firstPage
     */
    protected PageRequest(long current, int pageSize, P parameter, int firstPage) {
        if (firstPage != 0 && firstPage != 1) {
            throw new IllegalArgumentException("firstPage just can be 0 or 1");
        }
        this.current = current;
        this.pageSize = pageSize;
        this.parameter = parameter;
        this.firstPage = firstPage;
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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public P getParameter() {
        return parameter;
    }

    public void setParameter(P parameter) {
        this.parameter = parameter;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }
}
