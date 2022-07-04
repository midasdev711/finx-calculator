package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.Deferral;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.DeferralType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DeferralCalculationServiceTest {

    private final DeferralCalculationService deferralCalculationService = new DeferralCalculationService();

    @Test
    void dealWithDeferralCalculated() {
        Deal deal = Deal.builder()
                .car(Car.builder().build())
                .deferral(
                        Deferral.builder()
                                .deferralType(DeferralType.INCLUDED)
                                .timeForFirstPayment(30)
                                .firstPaymentDate(LocalDate.now().plusDays(31))
                                .endPaymentDate(LocalDate.now().plusDays(30).plusMonths(24))
                                .deferredInterestCost(0)
                                .build()
                )
                .dealType(DealType.DEALERSHIP)
                .chosenProgram("BMO Fixed Rate")
                .province(Province.BRITISH_COLUMBIA)
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .actualInterestRate(7.99)
                .amortizationTerm(48)
                .financingTerm(24)
                .totalAmountFinanced(2817088)
                .build();

        Deal result = deferralCalculationService.dealWithDeferralCalculated(deal);

        assertThat(result.getMonthlyPayment())
                .isEqualTo(28);

    }
}