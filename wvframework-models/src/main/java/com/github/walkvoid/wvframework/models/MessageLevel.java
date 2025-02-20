package com.github.walkvoid.wvframework.models;

    /**
     * 给前端用户的提示消息等级
     */
public enum MessageLevel {

    /**
     * 响应成功，前端用户不需要看到任何提示信息
     */
    SILENT,

    /**
     * 响应成功，前端用户需要看到这个提示信息，以便知道下一步将做什么，常用用户异步接口返回
     */
    INFO,

    /**
     * 响应成功，前端用户需要看到这个提醒信息，提醒用户如果需要进一步体验完整的服务，需要进行更多的操作，常见于补充资料等场景
     */
    WARNING,

    /**
     * 响应失败，前端用户需要看到这个错误消息，以便知道他们遇到了异常情况，以及需要如何处理，适用于一切错误场景
     */
    ERROR,
}