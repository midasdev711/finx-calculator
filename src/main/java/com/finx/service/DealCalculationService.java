package com.finx.service;

import com.finx.domain.BuyDownPoint;
import com.finx.domain.Deal;
import com.finx.domain.enums.DealType;
import com.finx.domain.products.ProductChoice;
import com.finx.domain.programs.Program;
import com.finx.web.exceptions.TermSettingException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.formula.functions.Finance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class DealCalculationService {

    //region Fields
    @Autowired
    private final ProgramService programService;

    @Autowired
    private final PpsaService ppsaService;

    @Autowired
    private final LoanCalculationService loanCalculationService;

    @Autowired
    private final TaxCalculationService taxCalculationService;

    @Autowired
    private final InterestRateCalculationService interestRateCalculationService;

    @Autowired
    private final DeferralCalculationService deferralCalculationService;

    @Autowired
    private final BuyDownPointCalculationService buyDownPointCalculationService;

    @Autowired
    private final DealerReserveCalculationService dealerReserveCalculationService;

    @Autowired
    private final ProfitViewCalculationService profitViewCalculationService;

    @Autowired
    private final PerDiemViewCalculationService perDiemViewCalculationService;

    @Autowired
    private final LeaseCalculationService leaseCalculationService;
    //endregion

    public Deal dealWithAllFieldsCalculated(Deal deal) {
        Deal toReturn = deal;

        if (isLease(deal)) {
            toReturn.setAmortizationTerm(deal.getFinancingTerm());
        }

        //Fields that only need summing up
        toReturn = dealWithRetailValueProductsCalculated(toReturn);
        toReturn = dealWithAdminFinxPPSAFeesCalculated(toReturn);
        toReturn = dealWithBuyDownPointRetailValueCalculatedOptionallyChangeSelectedBuyDownPoint(toReturn);

        toReturn = dealWithTaxesCalculated(toReturn);

        toReturn = dealWithTotalAmountFinancedCalculated(toReturn);

        toReturn = dealWithListOfInterestRatesAppended(toReturn);
        toReturn = dealWithAvailableInterestDetermined(toReturn);
        toReturn = dealWithBuyDownPointsAppended(toReturn);
        toReturn = dealWithCountAvailableBuyDownPointsCalculated(toReturn);

        toReturn = dealWithFinxCustomActualInterestRateCalculated(toReturn);
        toReturn = dealWithLoanOrLeaseInformationCalculatedOrThrow(toReturn);
        toReturn = dealWithBuyDownFromProductsCalculated(toReturn);
        toReturn = dealWithBreakEvenPointCalculated(toReturn);

        toReturn = dealWithDeferralCalculated(toReturn);

        toReturn = dealWithDealerReserveCalculated(toReturn);
        toReturn = dealWithCustomerInterestSavingCalculated(toReturn);

        toReturn = dealWithProfitViewCalculated(toReturn);
        toReturn = dealWithPerDiemViewCalculated(toReturn);

        return toReturn;
    }

    private Deal dealWithPerDiemViewCalculated(Deal deal) {
        return deal
                .withPerDiemView(perDiemViewCalculationService.calculatePerDiemView(deal));
    }

    private Deal dealWithBuyDownFromProductsCalculated(Deal deal) {
        return deal.withBuyDownAmount(loanCalculationService.calculateBuyDownValueFromProducts(deal));
    }

    private Deal dealWithProfitViewCalculated(Deal deal) {
        return profitViewCalculationService.dealWithProfitViewCalculated(deal);
    }

    private Deal dealWithCustomerInterestSavingCalculated(Deal deal) {
        if (deal.getChosenBuyDownPoint() == 0 || deal.getRetailValueProducts() == 0) {
            deal.withCustomerInterestSaving(0);
        }

        //This is calculating the interest saving assuming a monthly  payment frequency
        double highestAvailableInterestRate = (deal.getInterestRates().get(0) / 100) / 12;
        double actualInterestRate = (deal.getActualInterestRate() / 100) / 12;

        long interestPaymentsAtHighRate = loanCalculationService.cumulativeInterestPaymentsSinceLoanStart(highestAvailableInterestRate, deal.getAmortizationTerm(), deal.getTotalAmountFinanced(), deal.getFinancingTerm());
        long interestPaymentsAtActualRate = loanCalculationService.cumulativeInterestPaymentsSinceLoanStart(actualInterestRate, deal.getAmortizationTerm(), deal.getTotalAmountFinanced(), deal.getFinancingTerm());

        return deal.withCustomerInterestSaving(interestPaymentsAtHighRate - interestPaymentsAtActualRate);
    }

    private Deal dealWithCountAvailableBuyDownPointsCalculated(Deal deal) {
        return deal.withBuyDownPointsAvailable(
                buyDownPointCalculationService.calculateCountBuyDownPoints(deal)
        );
    }

    private Deal dealWithBuyDownPointsAppended(Deal deal) {
        return deal.withBuyDownPoints(
                buyDownPointCalculationService.getBuyDownPointsForDeal(deal)
        );
    }

    private Deal dealWithBreakEvenPointCalculated(Deal deal) {
        if (deal.getChosenBuyDownPoint() == 0) {
            return deal.withBreakEvenPoint(0);
        }

        BuyDownPoint currentBuyDownPoint = deal.getBuyDownPoints()
                .stream()
                .sorted(Comparator.reverseOrder())
                .skip(deal.getChosenBuyDownPoint() - 1)
                .findFirst()
                .orElseThrow();

        double buyDownTotalPrice = currentBuyDownPoint.getTotalPayableValue();
        double rateForAvailableInterest = (deal.getInterestRateAvailable() / 100) / 12;
        double rateForFinxInterest = (deal.getFinxInterestRateGenerated() / 100) / 12;
        int periods = deal.getAmortizationTerm();
        double financedValue = deal.getTotalAmountFinanced();

        double paymentAtAvailableRate = Finance.pmt(rateForAvailableInterest, periods, financedValue) * -1;
        double paymentAtFinxRate = Finance.pmt(rateForFinxInterest, periods, financedValue) * -1;

        long result = Math.round(buyDownTotalPrice / (paymentAtAvailableRate - paymentAtFinxRate));

        return deal.withBreakEvenPoint(result);
    }

    private Deal dealWithBuyDownPointRetailValueCalculatedOptionallyChangeSelectedBuyDownPoint(Deal deal) {
        Deal zeroValueBaseDeal = deal.withRetailValueBuyDownPoints(0);
        int chosenBuyDownPoint = deal.getChosenBuyDownPoint();
        int maxAvailableBuyDownPoints = 0;

        if (chosenBuyDownPoint == 0) {
            return zeroValueBaseDeal;
        }

        //build up all financials for the calculation
        Deal currentDeal = dealWithTaxesCalculated(zeroValueBaseDeal);
        currentDeal = dealWithTotalAmountFinancedCalculated(currentDeal);
        currentDeal = dealWithListOfInterestRatesAppended(currentDeal);
        currentDeal = dealWithAvailableInterestDetermined(currentDeal);

        long currentNetValue = 0;

        //six iterations are enough to make the BuyDownPoint settle to a stable value
        for (int i = 0; i < 6; i++) {
            Set<BuyDownPoint> currentSet = buyDownPointCalculationService.getBuyDownPointsForDeal(currentDeal);
            maxAvailableBuyDownPoints = currentSet.size();

            BuyDownPoint currentBuyDownPoint = currentSet
                    .stream()
                    .sorted(Comparator.reverseOrder())
                    .skip(Math.max(Math.min(chosenBuyDownPoint, maxAvailableBuyDownPoints) - 1, 0))
                    .findFirst()
                    .orElseGet(this::getZeroValueBuyDownPoint);

            long currentTotalValue = currentBuyDownPoint.getTotalPayableValue();
            currentNetValue = currentBuyDownPoint.getRetailPrice();

            currentDeal = dealWithTotalAmountFinancedCalculated(currentDeal.withRetailValueBuyDownPoints(currentTotalValue));
        }

        return deal
                .withRetailValueBuyDownPoints(currentNetValue)
                .withChosenBuyDownPoint(Math.min(chosenBuyDownPoint, maxAvailableBuyDownPoints));

    }

    private BuyDownPoint getZeroValueBuyDownPoint() {
        return BuyDownPoint.builder()
                .interestRate(0)
                .retailPrice(0)
                .gstTax(0)
                .pstTax(0)
                .luxuryTax(0)
                .build();
    }

    private Deal dealWithAvailableInterestDetermined(Deal deal) {
        Double defaultRate = deal.getInterestRates().get(0);
        if (deal.getInterestRateAvailable() == 0 || isUnavailableRateChosen(deal)) {
            return deal.withInterestRateAvailable(
                    defaultRate
            );
        }

        return deal;
    }

    private boolean isUnavailableRateChosen(Deal deal) {
        return deal.getInterestRates()
                .stream()
                .noneMatch(rate -> deal.getInterestRateAvailable() == rate);
    }

    private Deal dealWithListOfInterestRatesAppended(Deal deal) {
        return deal.withInterestRates(interestRateCalculationService.getInterestRatesForChosenProgramOrThrow(deal));
    }

    private Deal dealWithDeferralCalculated(Deal deal) {
        return deferralCalculationService.dealWithDeferralCalculated(deal);
    }

    public Deal dealWithFinxCustomActualInterestRateCalculated(Deal deal) {
        Deal toReturn = deal.withFinxInterestRateGenerated(interestRateCalculationService.calculateFinxInterestRateGenerated(deal));
        toReturn = toReturn.withCustomInterestRate(interestRateCalculationService.calculateCustomInterestRate(toReturn));
        toReturn = toReturn.withActualInterestRate(interestRateCalculationService.calculateActualInterestRate(toReturn));
        return toReturn;
    }

    private Deal dealWithTaxesCalculated(Deal deal) {
        return deal
                .withPstTax(taxCalculationService.calculatePstTaxValue(deal))
                .withGstTax(taxCalculationService.calculateGstTaxValue(deal))
                .withLuxuryTax(taxCalculationService.calculateLuxuryTaxValue(deal));
    }

    public Deal dealWithTotalAmountFinancedCalculated(Deal deal) {
        long totalAmountFinanced = deal.getVehiclePrice()
                - deal.getRebateBeforeTaxes()
                - deal.getTradeInPrice()
                + deal.getLienAmount()
                + deal.getRetailValueBuyDownPoints()
                - deal.getCashDown()
                + deal.getRetailValueProducts()
                - deal.getRebateAfterTaxes();

        if (!isLease(deal)) {
            totalAmountFinanced = totalAmountFinanced
                    + deal.getPstTax()
                    + deal.getGstTax()
                    + deal.getLuxuryTax()
                    + deal.getAdminFee()
                    + deal.getFinxFee()
                    + deal.getPpsaFee()
                    + deal.getDealershipFee()
                    + deal.getLicenseFee();
        }

        return deal.withTotalAmountFinanced(totalAmountFinanced);
    }

    public Deal dealWithDealerReserveCalculated(Deal deal) {
        return deal.withDealerReserve(dealerReserveCalculationService.calculateDealerReserve(deal));
    }

    public Deal dealWithLoanOrLeaseInformationCalculatedOrThrow(Deal deal) {
        if (deal.getFinancingTerm() > deal.getAmortizationTerm()) throw new TermSettingException();

        if (isLease(deal)) {
            return deal
                    .withCostOfBorrowing(leaseCalculationService.calculateTotalInterestPaymentsForLease(deal))
                    .withPrincipleOutstanding(0)
                    .withPaymentPerFrequency(leaseCalculationService.calculateRegularPaymentForLease(deal))
                    .withMonthlyPayment(leaseCalculationService.calculateMonthlyPayForLease(deal))
                    .withPstTax(leaseCalculationService.calculatePstTaxValueForLease(deal))
                    .withGstTax(leaseCalculationService.calculateGstTaxValueForLease(deal))
                    .withLuxuryTax(leaseCalculationService.calculateLuxuryTaxValueForLease(deal));
        } else {
            return deal
                    .withCostOfBorrowing(loanCalculationService.calculateTotalInterestPayments(deal))
                    .withPrincipleOutstanding(loanCalculationService.calculatePrincipleOutstanding(deal))
                    .withPaymentPerFrequency(loanCalculationService.calculateRegularPayment(deal))
                    .withMonthlyPayment(loanCalculationService.calculateMonthlyPay(deal));
        }

    }

    private boolean isLease(Deal deal) {
        return Objects.equals(deal.getDealType(), DealType.LEASE);
    }

    public Deal dealWithRetailValueProductsCalculated(Deal deal) {
        if (deal.getChosenProducts() == null) {
            return deal
                    .withChosenProducts(Collections.emptySet())
                    .withRetailValueProducts(0);
        }
        return deal.withRetailValueProducts(
                deal.getChosenProducts()
                        .stream()
                        .map(ProductChoice::getRetailPrice)
                        .reduce(0L, Long::sum)
        );
    }

    public Deal dealWithAdminFinxPPSAFeesCalculated(Deal deal) {
        Program chosenProgram = programService.findProgramOrThrow(deal.getChosenProgram());
        return deal
                .withPpsaFee(ppsaService.findPpsaFee(deal))
                .withAdminFee(chosenProgram.getAdminFee())
                .withFinxFee(chosenProgram.getFinxFee());
    }

}
