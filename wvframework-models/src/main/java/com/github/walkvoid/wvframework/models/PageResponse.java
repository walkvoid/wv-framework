package com.github.walkvoid.wvframework.models;

import java.util.List;

/**
 * @author jiangjunqing
 * @date 2024/12/30
 * @description:
 * @version:
 */
class PageResponse<R> {

    /**
     * total data count
     */
    protected Long total ;

    /**
     * page size
     */
    protected Integer size;

    /**
     * current page index
     */
    protected Long current;

    /**
     * data of one page
     */
    private List<R> data;

    protected PageResponse(Long total, Integer size, Long current, List<R> data) {
        this.total = total;
        this.size = size;
        this.current = current;
        this.data = data;
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
