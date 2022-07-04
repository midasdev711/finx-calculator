package com.finx.service;

import com.finx.domain.cars.Car;
import com.finx.domain.Deal;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealCalculationIT {

    @Autowired
    DealCalculationService dealCalculationService;

    @Test
    void dealWithAllFieldsCalculated() {
        Deal deal = Deal.builder()
                .car(Car.builder().year(2021).build())
                .chosenProgram("RBC")
                .province(Province.BRITISH_COLUMBIA)
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .vehiclePrice(750001L)
                .financingTerm(24)
                .amortizationTerm(24)
                .actualInterestRate(4.99)
                .build();

        System.out.println(dealCalculationService.dealWithAllFieldsCalculated(deal));

    }
}
