package com.finx.web.exceptions;

public class InsufficientCreditScoreException extends RuntimeException{

    public InsufficientCreditScoreException () {
        super("The given Credit Score does not allow for the provided program");
    }
}
