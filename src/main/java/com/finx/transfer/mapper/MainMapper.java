package com.finx.transfer.mapper;

import com.finx.domain.Deal;
import com.finx.domain.products.*;
import com.finx.domain.views.PerDiemView;
import com.finx.domain.views.ProfitView;
import com.finx.transfer.DealDto;
import com.finx.transfer.PerDiemViewDto;
import com.finx.transfer.ProfitViewDto;
import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class MainMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        Converter<Long, Double> longDoubleConverter = mappingContext -> BigDecimal.valueOf(mappingContext.getSource()).movePointLeft(2).doubleValue();

        Converter<Double, Long> doubleLongConverter = mappingContext -> BigDecimal.valueOf(mappingContext.getSource()).movePointRight(2).longValue();

        PropertyMap<Deal, DealDto> dealDealDtoPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(longDoubleConverter).map(source.getPstTax()).setPstTax(0);
                using(longDoubleConverter).map(source.getGstTax()).setGstTax(0);
                using(longDoubleConverter).map(source.getLuxuryTax()).setLuxuryTax(0);
                using(longDoubleConverter).map(source.getVehiclePrice()).setVehiclePrice(0);
                using(longDoubleConverter).map(source.getTradeInPrice()).setTradeInPrice(0);
                using(longDoubleConverter).map(source.getLienAmount()).setLienAmount(0);
                using(longDoubleConverter).map(source.getCashDown()).setCashDown(0);
                using(longDoubleConverter).map(source.getRebateBeforeTaxes()).setRebateBeforeTaxes(0);
                using(longDoubleConverter).map(source.getRebateAfterTaxes()).setRebateAfterTaxes(0);
                using(longDoubleConverter).map(source.getDealershipFee()).setDealershipFee(0);
                using(longDoubleConverter).map(source.getAdminFee()).setAdminFee(0);
                using(longDoubleConverter).map(source.getFinxFee()).setFinxFee(0);
                using(longDoubleConverter).map(source.getPpsaFee()).setPpsaFee(0);
                using(longDoubleConverter).map(source.getLicenseFee()).setLicenseFee(0);
                using(longDoubleConverter).map(source.getCostOfBorrowing()).setCostOfBorrowing(0);
                using(longDoubleConverter).map(source.getBuyDownAmount()).setBuyDownAmount(0);
                using(longDoubleConverter).map(source.getPrincipleOutstanding()).setPrincipleOutstanding(0);
                using(longDoubleConverter).map(source.getPaymentPerFrequency()).setPaymentPerFrequency(0);
                using(longDoubleConverter).map(source.getMonthlyPayment()).setMonthlyPayment(0);
                using(longDoubleConverter).map(source.getRetailValueBuyDownPoints()).setRetailValueBuyDownPoints(0);
                using(longDoubleConverter).map(source.getDealerReserve()).setDealerReserve(0);
                using(longDoubleConverter).map(source.getCustomerInterestSaving()).setCustomerInterestSaving(0);
                using(longDoubleConverter).map(source.getRetailValueProducts()).setRetailValueProducts(0);
                using(longDoubleConverter).map(source.getTotalAmountFinanced()).setTotalAmountFinanced(0);
                using(longDoubleConverter).map(source.getResidual()).setResidual(0);

                using(longDoubleConverter).map(source.getDeferral().getDeferredInterestCost()).getDeferral().setDeferredInterestCost(0);
            }
        };

        PropertyMap<DealDto, Deal> dealDtoDealPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(doubleLongConverter).map(source.getPstTax()).setPstTax(0);
                using(doubleLongConverter).map(source.getGstTax()).setGstTax(0);
                using(doubleLongConverter).map(source.getLuxuryTax()).setLuxuryTax(0);
                using(doubleLongConverter).map(source.getVehiclePrice()).setVehiclePrice(0);
                using(doubleLongConverter).map(source.getTradeInPrice()).setTradeInPrice(0);
                using(doubleLongConverter).map(source.getLienAmount()).setLienAmount(0);
                using(doubleLongConverter).map(source.getCashDown()).setCashDown(0);
                using(doubleLongConverter).map(source.getRebateBeforeTaxes()).setRebateBeforeTaxes(0);
                using(doubleLongConverter).map(source.getRebateAfterTaxes()).setRebateAfterTaxes(0);
                using(doubleLongConverter).map(source.getDealershipFee()).setDealershipFee(0);
                using(doubleLongConverter).map(source.getAdminFee()).setAdminFee(0);
                using(doubleLongConverter).map(source.getFinxFee()).setFinxFee(0);
                using(doubleLongConverter).map(source.getPpsaFee()).setPpsaFee(0);
                using(doubleLongConverter).map(source.getLicenseFee()).setLicenseFee(0);
                using(doubleLongConverter).map(source.getCostOfBorrowing()).setCostOfBorrowing(0);
                using(doubleLongConverter).map(source.getBuyDownAmount()).setBuyDownAmount(0);
                using(doubleLongConverter).map(source.getPrincipleOutstanding()).setPrincipleOutstanding(0);
                using(doubleLongConverter).map(source.getPaymentPerFrequency()).setPaymentPerFrequency(0);
                using(doubleLongConverter).map(source.getMonthlyPayment()).setMonthlyPayment(0);
                using(doubleLongConverter).map(source.getRetailValueBuyDownPoints()).setRetailValueBuyDownPoints(0);
                using(doubleLongConverter).map(source.getDealerReserve()).setDealerReserve(0);
                using(doubleLongConverter).map(source.getCustomerInterestSaving()).setCustomerInterestSaving(0);
                using(doubleLongConverter).map(source.getRetailValueProducts()).setRetailValueProducts(0);
                using(doubleLongConverter).map(source.getTotalAmountFinanced()).setTotalAmountFinanced(0);
                using(doubleLongConverter).map(source.getResidual()).setResidual(0);

                using(doubleLongConverter).map(source.getDeferral().getDeferredInterestCost()).getDeferral().setDeferredInterestCost(0);
            }
        };

        PropertyMap<ProductVariationDto, ProductVariation> productVariationDtoProductVariationPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(doubleLongConverter).map(source.getRetailPrice()).setRetailPrice(0);
            }
        };

        PropertyMap<ProductVariation, ProductVariationDto> productVariationProductVariationDtoPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(longDoubleConverter).map(source.getRetailPrice()).setRetailPrice(0);
            }
        };

        PropertyMap<ProductChoiceDto, ProductChoice> productChoiceDtoProductChoicePropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(doubleLongConverter).map(source.getRetailPrice()).setRetailPrice(0);
            }
        };

        PropertyMap<ProductChoice, ProductChoiceDto> productChoiceProductChoiceDtoPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(longDoubleConverter).map(source.getRetailPrice()).setRetailPrice(0);
            }
        };

        PropertyMap<ProfitViewDto, ProfitView> profitViewDtoProfitViewPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(doubleLongConverter).map(source.getBank()).setBank(0);
                using(doubleLongConverter).map(source.getInsurance()).setInsurance(0);
                using(doubleLongConverter).map(source.getDealership()).setDealership(0);
                using(doubleLongConverter).map(source.getFinx()).setFinx(0);
                using(doubleLongConverter).map(source.getTotal()).setTotal(0);
            }
        };

        PropertyMap<ProfitView, ProfitViewDto> profitViewProfitViewDtoPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(longDoubleConverter).map(source.getBank()).setBank(0);
                using(longDoubleConverter).map(source.getInsurance()).setInsurance(0);
                using(longDoubleConverter).map(source.getDealership()).setDealership(0);
                using(longDoubleConverter).map(source.getFinx()).setFinx(0);
                using(longDoubleConverter).map(source.getTotal()).setTotal(0);
            }
        };

        PropertyMap<PerDiemViewDto, PerDiemView> perDiemViewDtoPerDiemViewPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(doubleLongConverter).map(source.getPerDiemRate()).setPerDiemRate(0);
                using(doubleLongConverter).map(source.getPrincipal()).setPrincipal(0);
                using(doubleLongConverter).map(source.getTotal()).setTotal(0);
            }
        };

        PropertyMap<PerDiemView, PerDiemViewDto> perDiemViewPerDiemViewDtoPropertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(longDoubleConverter).map(source.getPerDiemRate()).setPerDiemRate(0);
                using(longDoubleConverter).map(source.getPrincipal()).setPrincipal(0);
                using(longDoubleConverter).map(source.getTotal()).setTotal(0);
            }
        };

        //The order of adding the property maps is relevant!-> https://github.com/modelmapper/modelmapper/issues/281
        modelMapper.addMappings(productVariationDtoProductVariationPropertyMap);
        modelMapper.addMappings(productVariationProductVariationDtoPropertyMap);
        modelMapper.addMappings(productChoiceDtoProductChoicePropertyMap);
        modelMapper.addMappings(productChoiceProductChoiceDtoPropertyMap);
        modelMapper.addMappings(profitViewDtoProfitViewPropertyMap);
        modelMapper.addMappings(profitViewProfitViewDtoPropertyMap);
        modelMapper.addMappings(perDiemViewDtoPerDiemViewPropertyMap);
        modelMapper.addMappings(perDiemViewPerDiemViewDtoPropertyMap);
        modelMapper.addMappings(dealDealDtoPropertyMap);
        modelMapper.addMappings(dealDtoDealPropertyMap);
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public DealDto toDto(Deal deal) {
        return modelMapper.map(deal, DealDto.class);
    }

    public Deal toDeal(DealDto dealDto) {
        return modelMapper.map(dealDto, Deal.class);
    }

    public ProductDto toProductDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

}
