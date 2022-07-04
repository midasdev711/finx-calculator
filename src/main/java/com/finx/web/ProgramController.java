package com.finx.web;

import com.finx.domain.Deal;
import com.finx.domain.cars.Car;
import com.finx.service.HandlerOfService;
import com.finx.transfer.DealDto;
import com.finx.transfer.mapper.MainMapper;
import com.finx.web.exceptions.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ProgramController {

    @Autowired
    private final HandlerOfService handlerOfService;

    @Autowired
    private final MainMapper mainMapper;

    @PostMapping("/programs")
    public Set<String> provideAvailablePrograms(@RequestBody Car car) {
        return handlerOfService
                .getProgramService()
                .findAvailablePrograms(car);
    }

    @PostMapping("/validProgram")
    public boolean isValid(@RequestBody DealDto dealDto) {
        Deal deal = mainMapper.toDeal(dealDto);
        try {
            handlerOfService
                    .getDealCalculationService()
                    .dealWithAllFieldsCalculated(deal);
            return true;
        } catch (NoTierAvailableException |
                InsufficientCreditScoreException |
                NoSuchProductException |
                NoSuchProductVariationException |
                NoSuchProgramException |
                TermSettingException exception) {
            return false;
        }
    }

}
