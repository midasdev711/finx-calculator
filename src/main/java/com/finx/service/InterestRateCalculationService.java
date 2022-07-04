package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.products.Product;
import com.finx.domain.products.ProductChoice;
import com.finx.domain.products.ProductVariation;
import com.finx.domain.programs.InterestRate;
import com.finx.domain.programs.Program;
import com.finx.domain.programs.Tier;
import com.finx.web.exceptions.InsufficientCreditScoreException;
import com.finx.web.exceptions.NoSuchProductVariationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InterestRateCalculationService {

    @Autowired
    private final ProgramService programService;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final LoanCalculationService loanCalculationService;

    public double calculateFinxInterestRateGenerated(Deal deal) {
        if (deal.getInterestRateAvailable() == 0) {
            return 0;
        }

        if (deal.getInterestRateAvailable() - deal.getLoyaltyInterestRateDeduction() <= 0) {
            return 0;
        }

        double finxInterestRateBeforeDeduction = getInterestRatesForChosenProgramOrThrow(deal)
                .stream()
                .sorted(Comparator.reverseOrder())
                .filter(aDouble -> aDouble <= deal.getInterestRateAvailable())
                .skip(deal.getChosenBuyDownPoint())
                .findFirst()
                .orElse(0D);

        return Math.max(finxInterestRateBeforeDeduction - deal.getLoyaltyInterestRateDeduction(), 0);
    }

    public double calculateCustomInterestRate(Deal deal) {
        if (deal.isManualCustomInterestRate()) return deal.getCustomInterestRate();
        if (isNoProductChosenOrCustomInterestDeactivated(deal)) return deal.getFinxInterestRateGenerated();


        long availableFundsForBuyingDownInterestRate = deal.getChosenProducts()
                .stream()
                .map(this::calculateFundsForBuyingDownInterestRate)
                .reduce(0L, Long::sum);

        long interestPaymentsAtFinxrate = loanCalculationService.calculateTotalInterestPayments(
                deal.getTotalAmountFinanced(),
                deal.getFinxInterestRateGenerated(),
                deal.getFinancingTerm(),
                deal.getAmortizationTerm(),
                deal.getPaymentFrequency()
        );

        double previousInterestRate = 0;

        double toReturn = 0;

        for (int i = 1; i < 10_000; i++) {
            double currentInterestRate = (double) i / 100;

            long interestPaymentAtCurrentRate = calculateTotalInterestPaymentForRate(currentInterestRate, deal);

            long currentBuyDownAmount = interestPaymentsAtFinxrate - interestPaymentAtCurrentRate;

            if (currentBuyDownAmount < availableFundsForBuyingDownInterestRate) {
                toReturn = previousInterestRate;
                break;
            } else {
                previousInterestRate = currentInterestRate;
            }

        }

        return toReturn;
    }

    private long calculateTotalInterestPaymentForRate(double rate, Deal deal) {
        return loanCalculationService.calculateTotalInterestPayments(
                deal.getTotalAmountFinanced(),
                rate,
                deal.getFinancingTerm(),
                deal.getAmortizationTerm(),
                deal.getPaymentFrequency()
        );
    }

    private long calculateFundsForBuyingDownInterestRate(ProductChoice productChoice) {
        Product chosenProduct = productService.findProductByNameOrThrow(productChoice.getName());
        ProductVariation chosenProductVariation = chosenProduct.getProductVariations()
                .stream()
                .filter(productVariation -> isProductChoiceMatchingProductVariation(productChoice, productVariation))
                .findFirst()
                .orElseThrow(NoSuchProductVariationException::new);

        double ratioForBuyingDownInterestRate = (1 - (double) chosenProduct.getProfitShareDealershipPercentage() / 100);
        long totalProfit = chosenProductVariation.getRetailPrice()
                - chosenProduct.getDealerMarkup()
                - chosenProduct.getFinxMarkup()
                - chosenProductVariation.getDealerCostForInsurance();

        return Math.round(totalProfit * ratioForBuyingDownInterestRate);
    }

    private boolean isProductChoiceMatchingProductVariation(ProductChoice productChoice, ProductVariation productVariation) {
        return Objects.equals(productChoice.getKm(), productVariation.getKm())
                && Objects.equals(productChoice.getTerm(), productVariation.getTerm())
                && Objects.equals(productChoice.getRetailPrice(), productVariation.getRetailPrice());
    }

    private boolean isNoProductChosenOrCustomInterestDeactivated(Deal deal) {
        return deal.getChosenProducts() == null
                || deal.getChosenProducts().isEmpty()
                || !deal.isBuyDownActivated();
    }

    public double calculateActualInterestRate(Deal deal) {
        return deal.getCustomInterestRate();
    }

    public List<Double> getInterestRatesForChosenProgramOrThrow(Deal deal) {
        Program chosenProgram = programService.findProgramOrThrow(deal.getChosenProgram());
        Tier relevantTier = programService.getRelevantTierFromProgramOrThrow(deal, chosenProgram);

        if (!isCreditScoreInProgramRange(deal, chosenProgram)) throw new InsufficientCreditScoreException();

        return relevantTier.getInterestRates()
                .stream()
                .map(InterestRate::getRate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private boolean isCreditScoreInProgramRange(Deal deal, Program program) {
        return deal.getClientCreditScore() >= program.getLowerCreditScoreBoundary()
                && deal.getClientCreditScore() <= program.getUpperCreditScoreBoundary();
    }

}
