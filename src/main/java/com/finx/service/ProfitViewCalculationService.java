package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.views.ProfitView;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfitViewCalculationService {

    @Autowired
    private final ProductService productService;

    public Deal dealWithProfitViewCalculated(Deal deal) {
        long bankProfits = calculateBankProfits(deal);
        long dealerProfits = calculateDealerProfits(deal);
        long finxProfits = calculateFinxProfits(deal);
        long insuranceProfits = calculateInsuranceProfits(deal);

        long totalProfits = bankProfits + dealerProfits + finxProfits + insuranceProfits;

        return deal.withProfitView(
                ProfitView.builder()
                        .bank(bankProfits)
                        .dealership(dealerProfits)
                        .finx(finxProfits)
                        .insurance(insuranceProfits)
                        .total(totalProfits)
                        .build()
        );

    }

    public long calculateBankProfits(Deal deal) {
        return deal.getCostOfBorrowing()
                + deal.getRetailValueBuyDownPoints()
                + deal.getBuyDownAmount()
                - deal.getDealerReserve()
                + getPotentialProfitSurplus(deal)
                + deal.getAdminFee();
    }

    private long getPotentialProfitSurplus(Deal deal) {
        if (deal.getCustomInterestRate() != 0) {
            return 0;
        }

        long productProfits = deal.getChosenProducts()
                .stream()
                .map(productService::calculateProfit)
                .reduce(0L, Long::sum);

        long buyDownAmount = deal.getBuyDownAmount();

        long profitsRetainedForDealer = deal.getChosenProducts()
                .stream()
                .map(productService::calculateRetainedProfitForDealer)
                .reduce(0L, Long::sum);

        return productProfits - buyDownAmount - profitsRetainedForDealer;
    }

    public long calculateFinxProfits(Deal deal) {
        long finxProfitsFromProductMarkups = deal.getChosenProducts()
                .stream()
                .map(productService::getFinxProductMarkup)
                .reduce(0L, Long::sum);

        return deal.getFinxFee()
                + finxProfitsFromProductMarkups;
    }

    public long calculateDealerProfits(Deal deal) {
        long dealerProfitsFromProductMarkups = deal.getChosenProducts()
                .stream()
                .map(productService::getDealerProductMarkup)
                .reduce(0L, Long::sum);

        long productProfits = deal.getChosenProducts()
                .stream()
                .map(productService::calculateProfit)
                .reduce(0L, Long::sum);

        long buyDownAmount = deal.getBuyDownAmount();

        long potentialProfitSurplusForBank = getPotentialProfitSurplus(deal);

        long profitsFromProductsSold = productProfits - buyDownAmount - potentialProfitSurplusForBank;

        return dealerProfitsFromProductMarkups
                + profitsFromProductsSold
                + deal.getDealerReserve()
                + deal.getLicenseFee()
                + deal.getDealershipFee();
    }

    public long calculateInsuranceProfits(Deal deal) {
        return deal.getChosenProducts()
                .stream()
                .map(productService::getInsuranceProfit)
                .reduce(0L, Long::sum);
    }
}
