package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.programs.Program;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DealValidationService {

    @Autowired
    private final ProgramService programService;

    public boolean isValidProgram(Deal deal) {
        Program chosenProgram = programService.findProgramOrThrow(deal.getChosenProgram());
        if (!isCreditScoreInProgramRange(deal, chosenProgram)) {
            return false;
        }

        return programService.doesTierExistForDealAndProgram(deal, chosenProgram);
    }

    private boolean isCreditScoreInProgramRange(Deal deal, Program program) {
        return deal.getClientCreditScore() >= program.getLowerCreditScoreBoundary()
                && deal.getClientCreditScore() <= program.getUpperCreditScoreBoundary();
    }
}
