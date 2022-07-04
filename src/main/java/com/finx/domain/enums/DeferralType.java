package com.finx.domain.enums;

public enum DeferralType {

    PAYS_BY_CHECK("Dealer/Mfr pays by cheque"),
    NO_DEFERRAL("No deferral applicable"),
    INCLUDED("Included in instalment payment");

    DeferralType(String description) {
    }
}
