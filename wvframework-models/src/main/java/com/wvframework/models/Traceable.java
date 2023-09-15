package com.wvframework.models;

import java.io.Serializable;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/15
 * @desc 代表这是一个提供traceId的类：
 * 对于WebResponse，当发生错误时，可以使用traceId更加方便的追踪错误日志
 * 对于RpcResponse来说，发生错误时，可以将traceId提供给服务提供方，便于服务提供方更好的追踪错误
 */
public class Traceable implements Serializable {

    private String traceId;

    protected String getTraceId() {
        return traceId;
    }

    protected void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
