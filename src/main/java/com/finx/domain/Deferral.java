package com.finx.domain;

import com.finx.domain.enums.DeferralType;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@With
public class Deferral {

    private DeferralType deferralType;

    private long timeForFirstPayment;

    private LocalDate firstPaymentDate;

    private LocalDate endPaymentDate;

    private long deferredInterestCost;
}
