package com.finx.web.exceptions;

public class NoSuchProductException extends RuntimeException {

    public NoSuchProductException() {
        super("There is no product by this name");
    }

}
