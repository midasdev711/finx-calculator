package com.finx.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
public class HandlerOfRepository {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ProgramRepository programRepository;

    @Autowired
    private final PpsaFeesRepository ppsaFeesRepository;

    @Autowired
    private final CarOptionRepository carOptionRepository;
}
