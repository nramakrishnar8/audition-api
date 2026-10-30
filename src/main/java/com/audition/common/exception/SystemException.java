package com.audition.common.exception;

import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = -5876728854007114881L;

    private final Integer statusCode;
    private final String title;
    private final String detail;

    public SystemException(final String detail, final String title, final Integer errorCode) {
        super(detail);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }

    public SystemException(final String detail, final String title, final Integer errorCode,
            final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }
}
