package com.manga.manga_web.exception;

public class HttpException extends RuntimeException {
    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}

