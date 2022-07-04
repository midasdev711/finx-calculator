package com.finx.service;

import com.finx.domain.cars.Car;
import com.finx.domain.Deal;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TaxCalculationServiceTest {


    @ParameterizedTest
    @MethodSource()
    void taxRateCalculation(Province province, long vehiclePrice, DealType dealType, long expected) {

        Deal deal = Deal.builder()
                .car(Car.builder().build())
                .chosenProgram("chosen Program")
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .province(province)
                .vehiclePrice(vehiclePrice)
                .dealType(dealType)
                .build();

        long result = new TaxCalculationService().calculatePstTaxValue(deal);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> taxRateCalculation() {
        return Stream.of(
          Arguments.of(Province.BRITISH_COLUMBIA, 10_000_00, DealType.PRIVATE, 1_200_00),
          Arguments.of(Province.BRITISH_COLUMBIA, 120_000_00, DealType.PRIVATE, 14_400_00),
          Arguments.of(Province.BRITISH_COLUMBIA, 160_000_00, DealType.PRIVATE, 32_000_00),
          Arguments.of(Province.BRITISH_COLUMBIA, 130_000_00, DealType.PRIVATE, 19_500_00),
          Arguments.of(Province.BRITISH_COLUMBIA, 130_000_00, DealType.DEALERSHIP, 9_100_00),
          Arguments.of(Province.BRITISH_COLUMBIA, 160_000_00, DealType.DEALERSHIP, 11_200_00)
        );
    }

}