package com.finx.web.exceptions;

public class NoSuchProductVariationException extends RuntimeException {

    public NoSuchProductVariationException() {
        super("There is no product variation that matches the provided term, km or retail value");
    }

}
