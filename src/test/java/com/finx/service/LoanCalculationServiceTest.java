package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

class LoanCalculationServiceTest {

    private LoanCalculationService loanCalculationService = new LoanCalculationService();

    @Test
    void weeklyPaymentCalculation() {
        double expected = loanCalculationService.calculateRegularPayment(4671769, 1.17, 54, PaymentFrequency.WEEKLY);

        assertThat(expected).isEqualTo(20497);
    }

    @Test
    void monthlyPaymentCalculation() {
        double expected = loanCalculationService.calculateRegularPayment(6540079, 0.5, 42, PaymentFrequency.QUARTERLY);

        assertThat(expected).isEqualTo(471540);
    }

    @Test
    void calculateRegularPayments() {
        double expected = loanCalculationService.calculateRegularPayment(3964912,3.84, 6*12, PaymentFrequency.BI_WEEKLY);

        assertThat(expected).isEqualTo(28475);
    }

    @Test
    void calculateTotalInterestPayments() {
        double expected = loanCalculationService.calculateTotalInterestPayments(4671445, 5.29, 18, 96, PaymentFrequency.MONTHLY);

        assertThat(expected).isEqualTo(343612);
    }

    @Test
    void calculateRegularPayment() {
        long expected = loanCalculationService.calculateRegularPayment(100, 0, 48, PaymentFrequency.MONTHLY);

        assertThat(expected).isEqualTo(2);
    }

    @Test
    void cumulativePrinciplePaymentsSinceLoanStart() {
        double rate = 0.00602500;
        int periods = 48;
        long value = 2832953;
        long endPeriod = 24;

        long result = loanCalculationService.cumulativePrinciplePaymentsSinceLoanStart(rate, periods, value, endPeriod);
        assertThat(result)
                .isEqualTo(1314549);
    }

    @Test
    void calculatePrincipleOutstandingOnDeal() {
        Deal deal = Deal.builder()
                .car(Car.builder().build())
                .dealType(DealType.DEALERSHIP)
                .chosenProgram("BMO Fixed Rate")
                .province(Province.BRITISH_COLUMBIA)
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .actualInterestRate(7.99)
                .amortizationTerm(48)
                .financingTerm(24)
                .totalAmountFinanced(2817088)
                .build();

    long result = loanCalculationService.calculatePrincipleOutstanding(deal);

    assertThat(result)
            .isEqualTo(1520477);

    }

    @Test
    void calculatePrincipleOutstandingOnDealWithZeroInterest() {
        Deal deal = Deal.builder()
                .car(Car.builder().build())
                .dealType(DealType.DEALERSHIP)
                .chosenProgram("BMO Fixed Rate")
                .province(Province.BRITISH_COLUMBIA)
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .actualInterestRate(0)
                .amortizationTerm(48)
                .financingTerm(24)
                .totalAmountFinanced(2817088)
                .build();

    long result = loanCalculationService.calculatePrincipleOutstanding(deal);

    assertThat(result)
            .isEqualTo(1408544);

    }


}