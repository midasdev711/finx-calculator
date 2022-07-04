package com.finx.web;

import com.finx.domain.Deal;
import com.finx.service.HandlerOfService;
import com.finx.transfer.DealDto;
import com.finx.transfer.mapper.MainMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class DealController {

    @Autowired
    private final HandlerOfService handlerOfService;

    @Autowired
    private final MainMapper mainMapper;

    @PostMapping("/deal")
    public DealDto calculateDeal(@RequestBody DealDto dealDto) {
        Deal deal = mainMapper.toDeal(dealDto);
         deal = handlerOfService
                .getDealCalculationService()
                .dealWithAllFieldsCalculated(deal);
        return mainMapper.toDto(deal);
    }

}
