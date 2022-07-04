package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.enums.PaymentFrequency;
import org.apache.poi.ss.formula.functions.Finance;
import org.springframework.stereotype.Service;

@Service
public class LoanCalculationService {

    public long calculateRegularPayment(Deal deal) {
        return calculateRegularPayment(deal.getTotalAmountFinanced(), deal.getActualInterestRate(),
                deal.getAmortizationTerm(), deal.getPaymentFrequency());
    }

    public long calculatePrincipleOutstanding(Deal deal) {
        long financedAmount = deal.getTotalAmountFinanced();
        long paymentsPerYear = getPaymentsPerYear(deal.getPaymentFrequency());
        double effectiveInterestRate = (deal.getActualInterestRate() / 100) / paymentsPerYear;
        int effectiveAmortization = (int) Math.round(((double) deal.getAmortizationTerm() / 12) * paymentsPerYear);
        int effectiveFinancing = (int) Math.round(((double) deal.getFinancingTerm() / 12) * paymentsPerYear);

        return calculatePrincipleOutstanding(effectiveInterestRate, effectiveAmortization, financedAmount, effectiveFinancing);
    }

    public long calculatePrincipleOutstanding(double effectiveInterestRate, int effectiveAmortization, long financedAmount, int effectiveFinancing) {
        return financedAmount - cumulativePrinciplePaymentsSinceLoanStart(effectiveInterestRate, effectiveAmortization, financedAmount, effectiveFinancing);
    }

    public long calculateTotalInterestPayments(Deal deal) {
        return calculateTotalInterestPayments(deal.getTotalAmountFinanced(), deal.getActualInterestRate(),
                deal.getFinancingTerm(), deal.getAmortizationTerm(), deal.getPaymentFrequency());
    }

    public long calculateRegularPayment(long totalAmountFinanced, double interestRate,
                                        long months, PaymentFrequency paymentFrequency) {
        long paymentsPerYear = getPaymentsPerYear(paymentFrequency);

        double interestRateForCalculation = (interestRate / 100) / paymentsPerYear;

        int periods = (int) Math.round(((double) months / 12) * paymentsPerYear);

        long paymentIfZeroInterest = Math.round((double) totalAmountFinanced / (double) periods);

        return interestRateForCalculation == 0 ? paymentIfZeroInterest : Math.abs(Math.round(Finance.pmt(interestRateForCalculation, periods, totalAmountFinanced)));
    }

    private long getPaymentsPerYear(PaymentFrequency paymentFrequency) {
        return switch (paymentFrequency) {
            case WEEKLY -> 52;
            case BI_WEEKLY -> 26;
            case MONTHLY -> 12;
            case QUARTERLY -> 4;
            case SEMI_YEARLY -> 2;
        };
    }

    public long calculateTotalInterestPayments(long totalAmountFinanced, double interestRate,
                                               long financingTerm, long amortizationTerm, PaymentFrequency paymentFrequency) {

        long paymentsPerYear = getPaymentsPerYear(paymentFrequency);

        long effectiveFinancingTerm = (long) (((double) financingTerm / 12) * paymentsPerYear);

        int effectiveAmortizationTerm = (int) (((double) amortizationTerm / 12) * paymentsPerYear);

        double ratePerPeriod = (interestRate / paymentsPerYear) / 100;

        return cumulativeInterestPaymentsSinceLoanStart(ratePerPeriod, effectiveAmortizationTerm, totalAmountFinanced, effectiveFinancingTerm);
    }

    public long calculateMonthlyPay(long totalAmountFinanced, double interestRate, long months) {
        return calculateRegularPayment(totalAmountFinanced, interestRate, months, PaymentFrequency.MONTHLY);
    }

    public long calculateMonthlyPay(Deal deal) {
        return calculateMonthlyPay(deal.getTotalAmountFinanced(), deal.getActualInterestRate(), deal.getAmortizationTerm());
    }

    public long cumulativeInterestPaymentsSinceLoanStart(double rate, int numberPaymentPeriods, long presentValue, long endPeriod) {
        if (rate == 0) {
            return 0;
        }

        double toReturn = 0;

        for (int i = 1; i <= endPeriod; i++) {
            toReturn += Finance.ipmt(rate, i, numberPaymentPeriods, presentValue);
        }

        return Math.abs(Math.round(toReturn));
    }

    public long cumulativePrinciplePaymentsSinceLoanStart(double rate, int numberPaymentPeriods, long presentValue, long endPeriod) {
        if (rate == 0) {
            return Math.round(((double) presentValue / (double) numberPaymentPeriods) * endPeriod);
        }

        double toReturn = 0;
        for (int i = 1; i <= endPeriod; i++) {
            toReturn += Finance.ppmt(rate, i, numberPaymentPeriods, presentValue);
        }

        return Math.abs(Math.round(toReturn));
    }

    public long calculateBuyDownValueFromProducts(Deal deal) {
        if (!isBuyDownValueToBeCalculated(deal)) return 0;

        long financedAmount = deal.getTotalAmountFinanced();
        long paymentsPerYear = getPaymentsPerYear(deal.getPaymentFrequency());
        double effectiveFinxInterestRate = (deal.getFinxInterestRateGenerated() / 100) / paymentsPerYear;
        double effectiveCustomInterestRate = (deal.getCustomInterestRate() / 100) / paymentsPerYear;
        int effectiveAmortization = (int) Math.round(((double) deal.getAmortizationTerm() / 12) * paymentsPerYear);
        int effectiveFinancing = (int) Math.round(((double) deal.getFinancingTerm() / 12) * paymentsPerYear);

        return cumulativeInterestPaymentsSinceLoanStart(effectiveFinxInterestRate, effectiveAmortization, financedAmount, effectiveFinancing)
                - cumulativeInterestPaymentsSinceLoanStart(effectiveCustomInterestRate, effectiveAmortization, financedAmount, effectiveFinancing);

    }

    private boolean isBuyDownValueToBeCalculated(Deal deal) {
        return deal.getCustomInterestRate() < deal.getFinxInterestRateGenerated();
    }


}
