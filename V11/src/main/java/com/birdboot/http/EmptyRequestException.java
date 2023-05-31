package com.birdboot.http;

/**
 * 空请求异常
 * 当HttpServletRequest解析请求时,如果客户端发送了空请求时会抛出该异常
 */
public class EmptyRequestException extends Exception {
    public EmptyRequestException() {
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRequestException(Throwable cause) {
        super(cause);
    }

    public EmptyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
