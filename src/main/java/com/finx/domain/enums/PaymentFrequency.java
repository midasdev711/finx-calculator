package com.finx.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentFrequency {
    WEEKLY(7),
    BI_WEEKLY(14),
    MONTHLY(30),
    QUARTERLY(90),
    SEMI_YEARLY(180);

    private final int standardDeferralTime;
}
