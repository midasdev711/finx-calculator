package com.finx.transfer;

import com.finx.domain.enums.DeferralType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DeferralDto {

    private DeferralType deferralType;

    private long timeForFirstPayment;

    private LocalDate firstPaymentDate;

    private LocalDate endPaymentDate;

    private double deferredInterestCost;

}
