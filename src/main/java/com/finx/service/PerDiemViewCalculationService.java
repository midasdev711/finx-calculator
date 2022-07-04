package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.views.PerDiemView;
import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PerDiemViewCalculationService {

    @Autowired
    private final LoanCalculationService loanCalculationService;

    public PerDiemView calculatePerDiemView(Deal deal) {
        if (isInitialCalculation(deal)) return standardPerDiem(deal);
        if (isChosenPayoutDateAfterFinalPayment(deal)) return standardPerDiem(deal);
        if (isChosenPayoutDateBeforeFirstPayment(deal)) return standardPerDiem(deal);
        if (isLease(deal)) return standardPerDiem(deal);


        LocalDate firstPaymentDate = deal.getDeferral().getFirstPaymentDate();
        LocalDate chosenLastPaymentDate = deal.getPerDiemView().getPayOutDate();

        Pair<LocalDate, Long> lastPaymentDateAndPeriodsPaid = getLastPaymentDateAndPeriodsPaid(firstPaymentDate, chosenLastPaymentDate, deal.getPaymentFrequency());

        double daysForFinancing = ((double) deal.getFinancingTerm() / 12) * 365.25;

        double perDiemDailyRate = (double) deal.getCostOfBorrowing() / daysForFinancing;

        double perDiemTotalRate = lastPaymentDateAndPeriodsPaid.getValue0().until(chosenLastPaymentDate, ChronoUnit.DAYS) * perDiemDailyRate;

        long financedAmount = deal.getTotalAmountFinanced();
        long paymentsPerYear = getPaymentsPerYear(deal.getPaymentFrequency());
        double effectiveInterestRate = (deal.getActualInterestRate() / 100) / paymentsPerYear;
        int effectiveAmortization = (int) Math.round(((double) deal.getAmortizationTerm() / 12) * paymentsPerYear);
        int effectiveFinancing = Math.toIntExact(lastPaymentDateAndPeriodsPaid.getValue1());

        long outstandingPrincipal = loanCalculationService.calculatePrincipleOutstanding(effectiveInterestRate, effectiveAmortization, financedAmount, effectiveFinancing);

        return PerDiemView.builder()
                .lastPaymentDate(lastPaymentDateAndPeriodsPaid.getValue0())
                .periodsPaid(lastPaymentDateAndPeriodsPaid.getValue1())
                .payOutDate(chosenLastPaymentDate)
                .perDiemRate(Math.round(perDiemTotalRate))
                .principal(outstandingPrincipal)
                .total(Math.round(outstandingPrincipal + perDiemTotalRate))
                .build();
    }


    private Pair<LocalDate, Long> getLastPaymentDateAndPeriodsPaid(LocalDate firstPaymentDate, LocalDate chosenLastPaymentDate, PaymentFrequency paymentFrequency) {
        LocalDate intermediate = firstPaymentDate;
        LocalDate previousDate = firstPaymentDate;
        long counter = 0;

        while (true) {
            switch (paymentFrequency) {
                case WEEKLY -> intermediate = intermediate.plusWeeks(1);
                case BI_WEEKLY -> intermediate = intermediate.plusWeeks(2);
                case MONTHLY -> intermediate = intermediate.plusMonths(1);
                case QUARTERLY -> intermediate = intermediate.plusMonths(3);
                case SEMI_YEARLY -> intermediate = intermediate.plusMonths(6);
            }

            if (intermediate.isAfter(chosenLastPaymentDate)) {
                return Pair.with(previousDate, counter);
            }

            previousDate = intermediate;
            counter++;
        }
    }

    private boolean isChosenPayoutDateAfterFinalPayment(Deal deal) {
        LocalDate chosenPayOutDate = deal.getPerDiemView().getPayOutDate();
        LocalDate finalPaymentDate = deal.getDeferral().getEndPaymentDate();

        return chosenPayOutDate.isAfter(finalPaymentDate.minusDays(1));
    }


    private boolean isChosenPayoutDateBeforeFirstPayment(Deal deal) {
        LocalDate chosenPayOutDate = deal.getPerDiemView().getPayOutDate();
        LocalDate firstPaymentDate = deal.getDeferral().getFirstPaymentDate();

        return chosenPayOutDate.isBefore(firstPaymentDate);
    }


    private PerDiemView standardPerDiem(Deal deal) {
        LocalDate endPaymentDate = deal.getDeferral().getEndPaymentDate();
        long paymentsPerYear = getPaymentsPerYear(deal.getPaymentFrequency());
        long periodsPaidWithLastPayment = Math.round(((double) deal.getFinancingTerm() / 12) * paymentsPerYear);

        return PerDiemView.builder()
                .lastPaymentDate(endPaymentDate)
                .periodsPaid(periodsPaidWithLastPayment)
                .payOutDate(endPaymentDate)
                .principal(deal.getPrincipleOutstanding())
                .total(deal.getPrincipleOutstanding())
                .build();
    }

    private boolean isInitialCalculation(Deal deal) {
        return deal.getPerDiemView() == null
                || deal.getPerDiemView().getPayOutDate() == null
                || deal.getPerDiemView().getPeriodsPaid() == 0
                || deal.getPerDiemView().getLastPaymentDate() == null;
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

    private boolean isLease(Deal deal) {
        return Objects.equals(deal.getDealType(), DealType.LEASE);
    }
}
