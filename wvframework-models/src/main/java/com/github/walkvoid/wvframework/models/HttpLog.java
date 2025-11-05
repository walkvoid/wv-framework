package com.github.walkvoid.wvframework.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author walkvoid
 * @date 2025/11/5
 * @description:
 * @version:
 */
public class HttpLog implements Serializable {
    private static final long serialVersionUID = 2560361290813208441L;

    private Long id;

    private String traceId;

    private String businessNo;

    private String uri;

    private String request;

    private String originRequest;

    private String header;

    private String response;

    private String originResponse;

    private Integer code;

    private String errorMsg;

    private LocalDateTime requestTime;

    private String requestClient;

    private String responseServer;





}
