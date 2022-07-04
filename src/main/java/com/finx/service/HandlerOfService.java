package com.finx.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
public class HandlerOfService {

    @Autowired
    private final BuyDownPointCalculationService buyDownPointCalculationService;

    @Autowired
    private final DealCalculationService dealCalculationService;

    @Autowired
    private final LoanCalculationService loanCalculationService;

    @Autowired
    private final PpsaService ppsaService;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final ProgramService programService;

    @Autowired
    private final CarOptionService carOptionService;

    @Autowired
    private final DealValidationService dealValidationService;

    @Autowired
    private final InterestRateCalculationService interestRateCalculationService;

    @Autowired
    private final DeferralCalculationService deferralCalculationService;

}
