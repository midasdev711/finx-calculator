package com.finx.web.exceptions;

public class NoSuchProgramException extends RuntimeException {

    public NoSuchProgramException() {
        super("There is no such program available");
    }
}
