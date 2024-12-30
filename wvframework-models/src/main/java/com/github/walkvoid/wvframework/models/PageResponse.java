package com.github.walkvoid.wvframework.models;

import java.util.List;

/**
 * @author jiangjunqing
 * @date 2024/12/30
 * @description:
 * @version:
 */
public class PageResponse<R> implements Traceable {

    private String traceId;

    protected Long total ;

    protected Integer size;

    protected Long current;

    private List<R> data;

    private Boolean ignoreCount;

    private Tuple2<String, String> sorts;

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public List<R> getData() {
        return data;
    }

    public void setData(List<R> data) {
        this.data = data;
    }
}
