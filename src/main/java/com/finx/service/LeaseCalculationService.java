package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.enums.PaymentFrequency;
import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class LeaseCalculationService {

    @Autowired
    private final TaxCalculationService taxCalculationService;

    private static final double MONEYFACTOR = 2400D;

    public long calculateTotalInterestPaymentsForLease(Deal deal) {
        return Math.round(getPairDepricationFeeFinanceFee(deal).getValue1() * deal.getFinancingTerm());
    }

    public long calculateRegularPaymentForLease(Deal deal) {
        long monthlyPay = calculateMonthlyPayForLease(deal);
        if (Objects.equals(deal.getPaymentFrequency(), PaymentFrequency.MONTHLY)) return monthlyPay;

        long paymentsPerYear = getPaymentsPerYear(deal.getPaymentFrequency());

        return Math.round((double) monthlyPay * 12 / paymentsPerYear);
    }

    public long calculateMonthlyPayForLease(Deal deal) {
        double leaseFees = getLeaseFees(deal);

        double leaseFeesWithoutLien = getLeaseFees(deal.withTotalAmountFinanced(deal.getTotalAmountFinanced()-deal.getLienAmount()));

        double taxRate = (taxCalculationService.determineGstTaxRate(deal) + taxCalculationService.determinePstTaxRate(deal) + taxCalculationService.determineLuxuryTaxRate(deal)) / 100;

        double taxPayment = leaseFeesWithoutLien * taxRate;

        return Math.round(leaseFees + taxPayment);
    }

    public long calculatePstTaxValueForLease(Deal deal) {
        double leaseFees = getLeaseFees(deal.withTotalAmountFinanced(deal.getTotalAmountFinanced()-deal.getLienAmount()));
        double taxRate = taxCalculationService.determinePstTaxRate(deal) / 100;
        return Math.round(leaseFees * taxRate * deal.getFinancingTerm());
    }

    public long calculateGstTaxValueForLease(Deal deal) {
        double leaseFees = getLeaseFees(deal.withTotalAmountFinanced(deal.getTotalAmountFinanced()-deal.getLienAmount()));
        double taxRate = taxCalculationService.determineGstTaxRate(deal) / 100;
        return Math.round(leaseFees * taxRate * deal.getFinancingTerm());
    }

    public long calculateLuxuryTaxValueForLease(Deal deal) {
        double leaseFees = getLeaseFees(deal.withTotalAmountFinanced(deal.getTotalAmountFinanced()-deal.getLienAmount()));
        double taxRate = taxCalculationService.determineLuxuryTaxRate(deal) / 100;
        return Math.round(leaseFees * taxRate * deal.getFinancingTerm());
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

    private double getLeaseFees(Deal deal) {
        return getPairDepricationFeeFinanceFee(deal).getValue0() + getPairDepricationFeeFinanceFee(deal).getValue1();
    }

    private Pair<Double, Double> getPairDepricationFeeFinanceFee(Deal deal) {
        long valueForLease = deal.getTotalAmountFinanced();
        long residual = deal.getResidual();
        double moneyFactor = deal.getActualInterestRate() / MONEYFACTOR;

        double depricationFee = (double) (valueForLease - residual) / deal.getFinancingTerm();
        double financeFee = (valueForLease + residual) * moneyFactor;

        return Pair.with(depricationFee, financeFee);
    }
}
