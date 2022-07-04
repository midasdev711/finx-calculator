package com.finx.web.exceptions;

public class NoTierAvailableException extends RuntimeException {

    public NoTierAvailableException() {
        super("There is no available financing tier for the given deal configuration");
    }

}
