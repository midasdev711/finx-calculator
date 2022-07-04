package com.finx.service;

import com.finx.domain.Deal;
import com.finx.domain.PPSAFee;
import com.finx.persistence.HandlerOfRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PpsaService {

    @Autowired
    private final HandlerOfRepository handlerOfRepository;

    public long findPpsaFee(Deal deal) {
        Optional<PPSAFee> ppsaFee = handlerOfRepository.getPpsaFeesRepository().findPpsaFeeForTermAndProvince(deal.getFinancingTerm(), deal.getProvince());

        if (ppsaFee.isEmpty()) {
            return 0;
        }

        return ppsaFee.get().getPpsaFee();
    }
}
