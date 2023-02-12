package com.ase.download.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class DownloadFileNotFoundException extends RuntimeException {
    public DownloadFileNotFoundException(String message) {
        super(message);
    }

    public DownloadFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}