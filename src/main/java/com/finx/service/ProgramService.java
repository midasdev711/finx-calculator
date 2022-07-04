package com.finx.service;

import com.finx.domain.cars.Car;
import com.finx.domain.Deal;
import com.finx.domain.enums.CarType;
import com.finx.domain.enums.DealType;
import com.finx.domain.programs.Program;
import com.finx.domain.programs.Tier;
import com.finx.persistence.HandlerOfRepository;
import com.finx.web.exceptions.NoSuchProgramException;
import com.finx.web.exceptions.NoTierAvailableException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProgramService {

    @Autowired
    private final HandlerOfRepository handlerOfRepository;

    public Set<String> findAvailablePrograms(Car car) {
        return handlerOfRepository.getProgramRepository().findAll()
                .stream()
                .filter(program -> doesCarFitToProgram(car, program))
                .map(Program::getName)
                .collect(Collectors.toSet());
    }

    private boolean doesCarFitToProgram(Car car, Program program) {
        return program.getTiers()
                .stream()
                .anyMatch(tier -> isCarYearInTierRange(car, tier) || isSpecialCase(car, program));
    }

    private boolean isSpecialCase(Car car, Program program) {
        if (isSpecialAccessCase(program)) return false;

        return switch (program.getSpecialAccessCase()) {
            case AVAILABLE_TO_NEW_CARS -> Objects.equals(car.getCarType(), CarType.NEW);
            //no special condition for lease, just the car year
            case AVAILABLE_FOR_LEASE -> false;
        };
    }

    private boolean isCarYearInTierRange(Car car, Tier tier) {
        return tier.getLowerVehicleYearRange() <= car.getYear()
                && tier.getUpperVehicleYearRange() >= car.getYear();
    }

    public Program findProgramOrThrow(String programName) {
        return handlerOfRepository
                .getProgramRepository()
                .findByName(programName)
                .orElseThrow(NoSuchProgramException::new);
    }

    public boolean doesTierExistForDealAndProgram(Deal deal, Program program) {
        return program.getTiers()
                .stream()
                .anyMatch(tier -> doesTierMatchToDealAndProgram(tier, deal, program));
    }

    public Tier getRelevantTierFromProgramOrThrow(Deal deal, Program program) {
        return program.getTiers()
                .stream()
                .filter(tier -> doesTierMatchToDealAndProgram(tier, deal, program))
                .findFirst()
                .orElseThrow(NoTierAvailableException::new);
    }

    private boolean doesTierMatchToDealAndProgram(Tier tier, Deal deal, Program program) {
        return  (isFinancingAmortPriceAndYearInRange(tier, deal) && !isLeaseDeal(deal) && !isSpecialAccessCase(program)) || isFittingDealSpecialAccessCase(deal.getCar(), program, tier, deal);
    }

    private boolean isFittingDealSpecialAccessCase(Car car, Program program, Tier tier, Deal deal) {
        if (!isSpecialAccessCase(program)) return false;

        return switch (program.getSpecialAccessCase()) {
            case AVAILABLE_TO_NEW_CARS -> isFinancingAmortAndPriceInRange(tier, deal)
                    && Objects.equals(car.getCarType(), CarType.NEW);
            case AVAILABLE_FOR_LEASE -> isFinancingPriceAndYearInRange(tier, deal)
                    && isLeaseDeal(deal);
        };

    }

    private boolean isSpecialAccessCase(Program program) {
        return Objects.nonNull(program.getSpecialAccessCase());
    }

    private boolean isLeaseDeal(Deal deal) {
        return Objects.equals(deal.getDealType(), DealType.LEASE);
    }

    private boolean isFinancingPriceAndYearInRange(Tier tier, Deal deal) {
        return isFinancingTermInRange(tier, deal)
                && isPriceInRange(tier, deal)
                && isVehicleYearInRange(tier, deal);
    }

    private boolean isFinancingAmortPriceAndYearInRange(Tier tier, Deal deal) {
        return isFinancingAmortAndPriceInRange(tier, deal)
                && isVehicleYearInRange(tier, deal);
    }

    private boolean isFinancingAmortAndPriceInRange(Tier tier, Deal deal) {
        return isFinancingTermInRange(tier, deal)
                && isAmortTermInRange(tier, deal)
                && isPriceInRange(tier, deal);
    }

    private boolean isFinancingTermInRange(Tier tier, Deal deal) {
        return tier.getLowerTermRange() <= deal.getFinancingTerm()
                && tier.getUpperTermRange() >= deal.getFinancingTerm();
    }

    private boolean isAmortTermInRange(Tier tier, Deal deal) {
        return tier.getLowerAmortRange() <= deal.getAmortizationTerm()
                && tier.getUpperAmortRange() >= deal.getAmortizationTerm();
    }

    private boolean isPriceInRange(Tier tier, Deal deal) {
        return tier.getLowerPriceRange() <= deal.getTotalAmountFinanced()
                && tier.getUpperPriceRange() >= deal.getTotalAmountFinanced();
    }

    private boolean isVehicleYearInRange(Tier tier, Deal deal) {
        return tier.getLowerVehicleYearRange() <= deal.getCar().getYear()
                && tier.getUpperVehicleYearRange() >= deal.getCar().getYear();
    }

}
