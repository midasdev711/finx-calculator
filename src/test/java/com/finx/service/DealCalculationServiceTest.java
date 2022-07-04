package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import com.finx.domain.programs.InterestRate;
import com.finx.domain.programs.Program;
import com.finx.domain.programs.Tier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DealCalculationServiceTest {

    @Mock
    private ProgramService programServiceMock;

    @InjectMocks
    private DealCalculationService dealCalculationService;

    @Test
    void calculateDealerReserve() {

        long totalAmountFinanced = 46714_45;
        InterestRate interestRate1 = InterestRate.builder().rate(3.99).dollarDealerReserve(3500_00).percentageDealerReserve(1.75).build();
        InterestRate interestRate2 = InterestRate.builder().rate(4.29).dollarDealerReserve(3500_00).percentageDealerReserve(2.75).build();
        InterestRate interestRate3 = InterestRate.builder().rate(5.29).dollarDealerReserve(3500_00).percentageDealerReserve(3.9).build();
        InterestRate interestRate4 = InterestRate.builder().rate(6.59).dollarDealerReserve(3500_00).percentageDealerReserve(5.1).build();

        Set<InterestRate> interestRates = Set.of(interestRate1, interestRate2, interestRate3, interestRate4);
        Tier tier = Tier.builder()
                .interestRates(interestRates)
                .lowerAmortRange(12)
                .upperAmortRange(48)
                .lowerTermRange(12)
                .upperTermRange(48)
                .lowerPriceRange(40000_00)
                .upperPriceRange(125000_00)
                .lowerVehicleYearRange(2010)
                .upperVehicleYearRange(2021)
                .build();

        Program program = Program.builder()
                .tiers(Set.of(tier))
                .build();

        Deal deal = Deal.builder()
                .totalAmountFinanced(totalAmountFinanced)
                .financingTerm(30)
                .amortizationTerm(30)
                .finxInterestRateGenerated(5.29)
                .car(Car.builder().year(2015).build())
                .build();

        long expectedDealerReserve = dealCalculationService.dealWithDealerReserveCalculated(deal).getDealerReserve();

        assertThat(expectedDealerReserve)
                .isEqualTo(1821_86);
    }

//    @Test
//    void calculateRetailValuesProducts() {
//        ProductVariation productVariation = ProductVariation.builder()
//                .retailPrice(100_00)
//                .build();
//
//        Product product1 = Product.builder()
//                .chosenProductVariation(productVariation)
//                .build();
//        Product product2 = Product.builder().build();
//
//        Deal deal = Deal.builder()
//                .chosenProducts(Set.of(product1, product2))
//                .build();
//
//        assertThat(dealCalculationService.dealWithRetailValueProductsCalculated(deal).getRetailValueProducts())
//                .isEqualTo(100_00);
//
//    }


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
                .build();

        System.out.println(dealCalculationService.dealWithAllFieldsCalculated(deal));

    }

    @Test
    void getInterestRatesForChosenProgram() {
    }

    @Test
    void dealWithTotalAmountFinancedCalculated() {
    }

    @Test
    void dealWithDealerReserveCalculated() {
    }

    @Test
    void calculateDealerReserveFromTierWithCertainInterestRate() {
    }

    @Test
    void dealWithLoanInformationCalculated() {
    }

    @Test
    void calculateRetailValueProducts() {
    }

    @Test
    void calculateAdminFinxPPSAFees() {
    }

    @Test
    void testDealWithAllFieldsCalculated() {
    }

    @Test
    void testGetInterestRatesForChosenProgram() {
    }

    @Test
    void testDealWithTotalAmountFinancedCalculated() {
    }

    @Test
    void testDealWithDealerReserveCalculated() {
    }

    @Test
    void testCalculateDealerReserveFromTierWithCertainInterestRate() {
    }

    @Test
    void testDealWithLoanInformationCalculated() {
    }

    @Test
    void dealWithRetailValueProductsCalculated() {
    }

    @Test
    void dealWithAdminFinxPPSAFeesCalculated() {
    }
}