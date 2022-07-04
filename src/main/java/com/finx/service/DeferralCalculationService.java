package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.Deferral;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.DeferralType;
import com.finx.domain.enums.PaymentFrequency;
import org.apache.poi.ss.formula.functions.Finance;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class DeferralCalculationService {

    private static final int MONTHS_IN_A_YEAR = 12;
    private static final double DAYS_IN_A_YEAR = 365.0;

    public Deal dealWithDeferralCalculated(Deal deal) {
        if (isInitialDeferral(deal)) return deal.withDeferral(standardDeferral(deal));
        if (isFirstPaymentBeforeStandardDeferralTime(deal)) return deal.withDeferral(standardDeferral(deal));
        if (hasPaymentFrequencyChangedToLowerFrequency(deal)) return deal.withDeferral(standardDeferral(deal));
        if (isLease(deal)) return deal.withDeferral(standardDeferral(deal));

        double interestRatePaymentFirstYear = (deal.getActualInterestRate() / 100) * deal.getTotalAmountFinanced();
        double interestRatePaymentOneDay = interestRatePaymentFirstYear / DAYS_IN_A_YEAR;

        //REMARK: Deferral uses monthly values and does not calculate with interest rates for weekly, bi-weekly etc.
        int standardDeferralTime = deal.getPaymentFrequency().getStandardDeferralTime();
        LocalDate defaultFirstPaymentDate = LocalDate.now().plusDays(standardDeferralTime);
        LocalDate selectedFirstPaymentDate = deal.getDeferral().getFirstPaymentDate();
        LocalDate calculatedEndPaymentDate = calculateEndPaymentDate(selectedFirstPaymentDate, deal);
        long timeForFirstPayment = LocalDate.now().until(selectedFirstPaymentDate, ChronoUnit.DAYS);

        long daysForDeferral = defaultFirstPaymentDate.until(selectedFirstPaymentDate, ChronoUnit.DAYS);
        double deferralToFinance = daysForDeferral * interestRatePaymentOneDay;

        DeferralType deferralTypeToBeSelected = determineDeferralType(deal);;

        double rateForDeferralCalculation = (deal.getActualInterestRate() / 100) / MONTHS_IN_A_YEAR;
        double monthlyAddedDeferralCost = Math.abs(Finance.pmt(rateForDeferralCalculation, deal.getFinancingTerm(), deferralToFinance));

        long addToCostOfBorrowing = isIncluded(deferralTypeToBeSelected) ? Math.round(monthlyAddedDeferralCost * deal.getFinancingTerm()) : 0;
        long addToMonthlyPayment = isIncluded(deferralTypeToBeSelected) ? Math.round(monthlyAddedDeferralCost) : 0;
        long addToPaymentPerFrequency = isIncluded(deferralTypeToBeSelected) ? Math.round(monthlyAddedDeferralCost * MONTHS_IN_A_YEAR / getPaymentsPerYear(deal.getPaymentFrequency())) : 0;

        //if no cost then no deferral
        deferralTypeToBeSelected = deferralToFinance == 0 ? DeferralType.NO_DEFERRAL : deferralTypeToBeSelected;

        return deal
                .withCostOfBorrowing(deal.getCostOfBorrowing() + addToCostOfBorrowing)
                .withMonthlyPayment(deal.getMonthlyPayment() + addToMonthlyPayment)
                .withPaymentPerFrequency(deal.getPaymentPerFrequency() + addToPaymentPerFrequency)
                .withDeferral(
                        Deferral.builder()
                                .deferralType(deferralTypeToBeSelected)
                                .timeForFirstPayment(timeForFirstPayment)
                                .firstPaymentDate(selectedFirstPaymentDate)
                                .endPaymentDate(calculatedEndPaymentDate)
                                .deferredInterestCost(Math.round(deferralToFinance))
                                .build()
                );
    }

    private boolean isLease(Deal deal) {
        return Objects.equals(deal.getDealType(), DealType.LEASE);
    }

    private LocalDate calculateEndPaymentDate(LocalDate selectedFirstPaymentDate, Deal deal) {
        final LocalDate baseFinalDate = selectedFirstPaymentDate.plusMonths(deal.getFinancingTerm());
        return switch (deal.getPaymentFrequency()) {
            case WEEKLY -> baseFinalDate.minusWeeks(1);
            case BI_WEEKLY -> baseFinalDate.minusWeeks(2);
            case MONTHLY -> baseFinalDate.minusMonths(1);
            case QUARTERLY -> baseFinalDate.minusMonths(3);
            case SEMI_YEARLY -> baseFinalDate.minusMonths(6);
        };
    }

    private boolean hasPaymentFrequencyChangedToLowerFrequency(Deal deal) {
        return deal.getDeferral().getTimeForFirstPayment() > deal.getPaymentFrequency().getStandardDeferralTime()
                && Objects.equals(deal.getDeferral().getDeferralType(), DeferralType.NO_DEFERRAL);
    }

    private DeferralType determineDeferralType(Deal deal) {
        return isNoDeferralChosen(deal) ? DeferralType.INCLUDED : deal.getDeferral().getDeferralType();
    }

    private boolean isNoDeferralChosen(Deal deal) {
        return Objects.equals(deal.getDeferral().getDeferralType(), DeferralType.NO_DEFERRAL);
    }

    private boolean isFirstPaymentBeforeStandardDeferralTime(Deal deal) {
        Deferral deferral = deal.getDeferral();
        int standardDeferralTime = deal.getPaymentFrequency().getStandardDeferralTime();
        return deferral.getFirstPaymentDate().isBefore(LocalDate.now().plusDays(standardDeferralTime));
    }

    private boolean isIncluded(DeferralType deferralType) {
        return Objects.equals(deferralType, DeferralType.INCLUDED);
    }

    private Deferral standardDeferral(Deal deal) {
        int standardDeferralTime = deal.getPaymentFrequency().getStandardDeferralTime();
        LocalDate firstPaymentDate = LocalDate.now().plusDays(standardDeferralTime);
        return Deferral.builder()
                .deferralType(DeferralType.NO_DEFERRAL)
                .deferredInterestCost(0)
                .endPaymentDate(firstPaymentDate.plusMonths(deal.getFinancingTerm() - 1))
                .firstPaymentDate(firstPaymentDate)
                .timeForFirstPayment(standardDeferralTime)
                .build();
    }

    private boolean isInitialDeferral(Deal deal) {
        return deal.getDeferral() == null
                || deal.getDeferral().getEndPaymentDate() == null
                || deal.getDeferral().getFirstPaymentDate() == null
                || deal.getDeferral().getDeferralType() == null;
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
}
