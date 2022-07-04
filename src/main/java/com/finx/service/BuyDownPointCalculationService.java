package com.finx.service;

import com.finx.domain.BuyDownPoint;
import com.finx.domain.Deal;
import com.finx.domain.programs.InterestRate;
import com.finx.domain.programs.Program;
import com.finx.domain.programs.Tier;
import com.finx.web.exceptions.NoTierAvailableException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BuyDownPointCalculationService {

    @Autowired
    private final ProgramService programService;

    @Autowired
    private final DealValidationService dealValidationService;

    @Autowired
    private final TaxCalculationService taxCalculationService;

    @Autowired
    private final DealerReserveCalculationService dealerReserveCalculationService;

    public Set<BuyDownPoint> getBuyDownPointsForDeal(Deal deal) {
        if (!dealValidationService.isValidProgram(deal)) {
            return Collections.emptySet();
        }

        Program chosenProgram = programService.findProgramOrThrow(deal.getChosenProgram());
        Tier chosenTier = programService.getRelevantTierFromProgramOrThrow(deal, chosenProgram);

        return chosenTier.getInterestRates()
                .stream()
                .sorted(Comparator.reverseOrder())
                .filter(rate -> rate.getRate() < deal.getInterestRateAvailable())
                .map(interestRate -> buyDownPointWithRetailValueCalculated(deal, interestRate, chosenTier))
                .collect(Collectors.toSet());
    }

    private BuyDownPoint buyDownPointWithRetailValueCalculated(Deal deal, InterestRate interestRate, Tier chosenTier) {
        InterestRate highestInterestRateInTierForChosenAvailableInterest = chosenTier.getInterestRates()
                .stream()
                .sorted(Comparator.reverseOrder())
                .filter(rate -> rate.getRate() <= deal.getInterestRateAvailable())
                .findFirst()
                .orElseThrow(NoTierAvailableException::new);

        long dealerReserve = dealerReserveCalculationService.calculateDealerReserveFromTierWithCertainInterestRate(deal, chosenTier, interestRate);
        long dealerReserveHighestInterestRate = dealerReserveCalculationService.calculateDealerReserveFromTierWithCertainInterestRate(deal, chosenTier, highestInterestRateInTierForChosenAvailableInterest);

        double pstRate = taxCalculationService.determinePstTaxRate(deal);
        double gstRate = taxCalculationService.determineGstTaxRate(deal);
        double luxuryRate = taxCalculationService.determineLuxuryTaxRate(deal);

        long retailValueBuyDownPoint = dealerReserveHighestInterestRate - dealerReserve;

        return BuyDownPoint.builder()
                .interestRate(interestRate.getRate())
                .retailPrice(retailValueBuyDownPoint)
                .pstTax((long) (retailValueBuyDownPoint * pstRate / 100))
                .gstTax((long) (retailValueBuyDownPoint * gstRate / 100))
                .luxuryTax((long) (retailValueBuyDownPoint * luxuryRate / 100))
                .build();
    }

    public int calculateCountBuyDownPoints(Deal deal) {
        Program chosenProgram = programService.findProgramOrThrow(deal.getChosenProgram());
        Tier chosenTier = programService.getRelevantTierFromProgramOrThrow(deal, chosenProgram);
        return (int) chosenTier.getInterestRates()
                .stream()
                .sorted()
                .takeWhile(interestRate -> interestRate.getRate() != deal.getInterestRateAvailable())
                .count();
    }
}
