package com.finx.service;

import com.finx.domain.*;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import com.finx.domain.programs.InterestRate;
import com.finx.domain.programs.Program;
import com.finx.domain.programs.Tier;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class BuyDownPointCalculationServiceTest {

    @Mock
    private ProgramService programServiceMock;

    @InjectMocks
    private BuyDownPointCalculationService buyDownPointCalculationService;

    @ParameterizedTest
    @MethodSource("chosenRatesAndExpectedCountBuyDownPoints")
    void testCalculateBuyDownPoints(double chosenRate, int expected) {
        Deal deal = Deal.builder()
                .car(Car.builder().build())
                .chosenProgram("RBC")
                .province(Province.BRITISH_COLUMBIA)
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .interestRateAvailable(chosenRate)
                .build();
        Program program = Program.builder().id(1L).build();
        Tier tier = Tier.builder()
                .interestRates(interestRates())
                .build();

        given(programServiceMock.findProgramOrThrow(deal.getChosenProgram()))
                .willReturn(program);
        given(programServiceMock.getRelevantTierFromProgramOrThrow(deal, program))
                .willReturn(tier);

        assertThat(buyDownPointCalculationService.calculateCountBuyDownPoints(deal))
                .isEqualTo(expected);
    }

    private static Stream<Arguments> chosenRatesAndExpectedCountBuyDownPoints() {
        return Stream.of(
                arguments(4.49, 0),
                arguments(7.74, 7),
                arguments(6.49, 5),
                arguments(4.99, 1)
        );
    }

    private Set<InterestRate> interestRates() {
        InterestRate interestRate1 = InterestRate.builder()
                .rate(4.49)
                .build();
        InterestRate interestRate2 = InterestRate.builder()
                .rate(4.99)
                .build();
        InterestRate interestRate3 = InterestRate.builder()
                .rate(5.24)
                .build();
        InterestRate interestRate4 = InterestRate.builder()
                .rate(5.49)
                .build();
        InterestRate interestRate5 = InterestRate.builder()
                .rate(5.99)
                .build();
        InterestRate interestRate6 = InterestRate.builder()
                .rate(6.49)
                .build();
        InterestRate interestRate7 = InterestRate.builder()
                .rate(6.99)
                .build();
        InterestRate interestRate8 = InterestRate.builder()
                .rate(7.74)
                .build();

        return Set.of(interestRate1, interestRate2, interestRate3, interestRate4, interestRate5, interestRate6, interestRate7, interestRate8);
    }

}