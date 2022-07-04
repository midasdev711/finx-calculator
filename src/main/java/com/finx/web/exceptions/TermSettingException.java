package com.finx.web.exceptions;

public class TermSettingException extends RuntimeException {

    public TermSettingException() { super("The Financing Term cannot be greater than the Amortization Term");}
}
