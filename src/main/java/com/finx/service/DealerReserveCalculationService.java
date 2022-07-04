package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.programs.InterestRate;
import com.finx.domain.programs.Tier;
import com.finx.web.exceptions.NoTierAvailableException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DealerReserveCalculationService {

    @Autowired
    private final ProgramService programService;

    public long calculateDealerReserve(Deal deal) {
        var program = programService.findProgramOrThrow(deal.getChosenProgram());
        var selectedTierFromProgram = programService.getRelevantTierFromProgramOrThrow(deal, program);
        return calculateDealerReserveFromTier(deal, selectedTierFromProgram);
    }

    public long calculateDealerReserveFromTierWithCertainInterestRate(Deal deal, Tier tier, InterestRate interestRate) {
        Deal dealWithInterestRateSetup = deal
                .withFinxInterestRateGenerated(interestRate.getRate())
                .withLoyaltyInterestRateDeduction(0);
        return calculateDealerReserveFromTier(dealWithInterestRateSetup, tier);
    }

    private long calculateDealerReserveFromTier(Deal deal, Tier tier) {
        double interestRateForLookup = getInterestRate(deal);

        InterestRate chosenRate = getChosenRateOrThrow(tier, interestRateForLookup);

        if (isFixedCommission(chosenRate)) {
            return chosenRate.getDollarDealerReserve();
        }

        if (isVariableCommission(chosenRate)) {
            return Math.round((chosenRate.getPercentageDealerReserve() / 100) * deal.getTotalAmountFinanced());
        }

        return calculateCappedCommission(chosenRate, deal);
    }

    private InterestRate getChosenRateOrThrow(Tier tier, double interestRateForLookup) {
        return tier.getInterestRates()
                .stream()
                .filter(rate -> rate.getRate() == interestRateForLookup)
                .findFirst()
                .orElseThrow(NoTierAvailableException::new);
    }

    private long calculateCappedCommission(InterestRate chosenRate, Deal deal) {
        long variableCommission = Math.round((chosenRate.getPercentageDealerReserve() / 100) * deal.getTotalAmountFinanced());
        long fixedCommission = chosenRate.getDollarDealerReserve();
        return Math.min(variableCommission, fixedCommission);
    }

    private boolean isVariableCommission(InterestRate chosenRate) {
        return chosenRate.getDollarDealerReserve() == 0 && chosenRate.getPercentageDealerReserve() != 0;
    }

    private boolean isFixedCommission(InterestRate chosenRate) {
        return chosenRate.getDollarDealerReserve() != 0 && chosenRate.getPercentageDealerReserve() == 0;
    }

    private double getInterestRate(Deal deal) {
        return deal.getFinxInterestRateGenerated() + deal.getLoyaltyInterestRateDeduction();
    }
}
