package com.finx.randombuilder;

import com.finx.domain.Deal;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import com.finx.transfer.DealDto;

import java.util.Random;

public class TestRandomDeal {

    public Deal getRandom() {
        return Deal.builder()
                .dealType(DealType.PRIVATE)
                .paymentFrequency(randomEnum(PaymentFrequency.class))
                .vehiclePrice(15_000_00)
                .totalAmountFinanced(19_000_00)
                .chosenProgram("RBC")
                .province(Province.BRITISH_COLUMBIA)
                .car(Car.builder().year(2020).build())
                .financingTerm(24)
                .amortizationTerm(24)
                .build();
    }
    public DealDto getRandomDto() {
        return DealDto.builder()
                .dealType(DealType.PRIVATE)
                .paymentFrequency(randomEnum(PaymentFrequency.class))
                .vehiclePrice(15_000)
                .totalAmountFinanced(19_000)
                .chosenProgram("RBC")
                .province(Province.BRITISH_COLUMBIA)
                .car(Car.builder().year(2020).build())
                .financingTerm(24)
                .amortizationTerm(24)
                .build();
    }

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
