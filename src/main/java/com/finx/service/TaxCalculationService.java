package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.Province;
import com.finx.domain.products.ProductChoice;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class TaxCalculationService {

    public long calculatePstTaxValue(Deal deal) {
        long allTaxableValues = getAllTaxableValues(deal);
        double taxRate = determinePstTaxRate(deal) / 100;

        return (long) (taxRate * allTaxableValues);
    }

    public long calculateLuxuryTaxValue(Deal deal) {
        long allTaxableValues = getAllTaxableValues(deal);
        double taxRate = determineLuxuryTaxRate(deal) / 100;

        return (long) (taxRate * allTaxableValues);
    }

    public long calculateGstTaxValue(Deal deal) {
        long allTaxableValues = getAllTaxableValues(deal);
        double taxRate = determineGstTaxRate(deal) / 100;

        boolean isLuxury = calculateLuxuryTaxValue(deal) > 0;

        return (long) (taxRate * (allTaxableValues + getDoubleTaxableValuesIfLuxury(deal, isLuxury)));
    }

    private long getDoubleTaxableValuesIfLuxury(Deal deal, boolean isLuxury) {
        return isLuxury ? calculateLuxuryTaxValue(deal) + calculatePstTaxValue(deal) : 0;
    }

    private long getAllTaxableValues(Deal deal) {
        long vehicleTaxableValue = deal.getVehiclePrice() - deal.getRebateBeforeTaxes() - deal.getTradeInPrice();
        long productsTaxableValue = Optional.ofNullable(deal.getChosenProducts())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(ProductChoice::getRetailPrice)
                .reduce(0L, Long::sum);
        long buyDownPointTaxableValue = deal.getRetailValueBuyDownPoints();

        long feesTaxableValue = deal.getDealershipFee() + deal.getLicenseFee();

        return vehicleTaxableValue + productsTaxableValue + buyDownPointTaxableValue + feesTaxableValue;
    }


    public double determinePstTaxRate(Deal deal) {
        if (!deal.isPstActivated()) return 0;

        return determinePstTaxRate(deal.getProvince(), deal.getVehiclePrice(), deal.getDealType());
    }

    private double determinePstTaxRate(Province province, long vehiclePrice, DealType dealType) {
        double baseTaxRate = switch (province) {
            case ALBERTA, YUKON, NUNAVUT, NORTHWEST_TERRITORIES -> 0;
            case BRITISH_COLUMBIA, MANITOBA -> 7;
            case NEW_BRUNSWICK, PRINCE_EDWARD_ISLAND, NOVA_SCOTIA, NEWFOUNDLAND_AND_LABRADOR -> 10;
            case ONTARIO -> 8;
            case QUEBEC -> 9.975;
            case SASKATCHEWAN -> 6;
        };

        if (isInBritishColumbia(province) && isPrivateDeal(dealType)) {
            baseTaxRate = baseTaxRate + calculateAdditionalTax(vehiclePrice, dealType);
        }

        return baseTaxRate;
    }

    public double determineGstTaxRate(Deal deal) {
        if (!deal.isGstActivated()) return 0;

        return switch (deal.getDealType()) {
            case PRIVATE -> 0;
            case DEALERSHIP, LEASE -> 5;
        };
    }

    public double determineLuxuryTaxRate(Deal deal) {
        if (!deal.isGstActivated()) return 0;

        return determineLuxuryTaxRate(deal.getProvince(), deal.getVehiclePrice(), deal.getDealType());
    }

    private double determineLuxuryTaxRate(Province province, long vehiclePrice, DealType dealType) {
        if (isPrivateDeal(dealType) || !isInBritishColumbia(province)) {
            return 0;
        }

        return calculateAdditionalTax(vehiclePrice, dealType);
    }

    private boolean isInBritishColumbia(Province province) {
        return province.equals(Province.BRITISH_COLUMBIA);
    }

    private boolean isPrivateDeal(DealType dealType) {
        return dealType.equals(DealType.PRIVATE);
    }


    private double calculateAdditionalTax(long vehiclePrice, DealType dealType) {
        switch (dealType) {
            case PRIVATE -> {
                return determineAdditionalTaxForPrivate(vehiclePrice);
            }
            case DEALERSHIP -> {
                return determineAdditionalTaxForCommercial(vehiclePrice);
            }
        }
        return 0;
    }

    private int determineAdditionalTaxForCommercial(long vehiclePrice) {
        if (vehiclePrice < 5500000) {
            return 0;
        }
        if (vehiclePrice < 5600000) {
            return 1;
        }
        if (vehiclePrice < 5700000) {
            return 2;
        }
        if (vehiclePrice < 12500000) {
            return 3;
        }
        if (vehiclePrice < 15000000) {
            return 8;
        }
        return 13;
    }

    private int determineAdditionalTaxForPrivate(long vehiclePrice) {
        if (vehiclePrice < 12500000) {
            return 5;
        }
        if (vehiclePrice < 15000000) {
            return 8;
        }
        return 13;
    }

}
