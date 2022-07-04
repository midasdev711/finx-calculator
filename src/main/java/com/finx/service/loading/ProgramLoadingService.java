package com.finx.service.loading;

import com.finx.domain.programs.InterestRate;
import com.finx.domain.programs.Program;
import com.finx.domain.programs.SpecialAccessCase;
import com.finx.domain.programs.Tier;
import com.finx.persistence.HandlerOfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProgramLoadingService implements CommandLineRunner {

    @Value("classpath:data/rbcProgramTiers.csv")
    private Resource rbcRessource;

    @Value("classpath:data/bmoFixedProgramTiers.txt")
    private Resource bmoRessource;

    @Value("classpath:data/newSubventedProgramTiers.txt")
    private Resource newSubventedRessource;

    @Value("classpath:data/leaseProgramTiers.txt")
    private Resource leaseProgramRessource;

    @Autowired
    private HandlerOfRepository handlerOfRepository;

    @Override
    public void run(String... args) throws Exception {
        saveProgramsToDatabase();
    }

    @Transactional
    private void saveProgramsToDatabase() throws IOException {
        Program rbc = retrieveProgram("RBC", rbcRessource);
        Program bmo = retrieveProgram("BMO Fixed Rate", bmoRessource);
        Program newSubvented = retrieveProgram("New Subvented", newSubventedRessource);
        newSubvented.setSpecialAccessCase(SpecialAccessCase.AVAILABLE_TO_NEW_CARS);

        Program lease = retrieveProgram("Lease", leaseProgramRessource);
        lease.setSpecialAccessCase(SpecialAccessCase.AVAILABLE_FOR_LEASE);

        handlerOfRepository.getProgramRepository().saveAll(List.of(rbc, bmo, newSubvented, lease));
    }


    private Program retrieveProgram(String programName, Resource resource) throws IOException {
        Program program = Program.builder()
                .name(programName)
                .subvented(false)
                .startDate(LocalDate.of(2000,1,1))
                .endDate(LocalDate.of(2100,12,31))
                .lowerCreditScoreBoundary(0)
                .upperCreditScoreBoundary(1000)
                .finxFee(50_00)
                .adminFee(50_00)
                .build();

        Set<Tier> tiers = new HashSet<>();
        InputStream inputStream = resource.getInputStream();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Tier tier = Tier.builder().build();
                String[] values = line.split(",");
                tier.setNumber(Long.parseLong(values[0]));
                tier.setLowerTermRange(Long.parseLong(values[1]));
                tier.setUpperTermRange(Long.parseLong(values[2]));
                tier.setLowerAmortRange(Long.parseLong(values[3]));
                tier.setUpperAmortRange(Long.parseLong(values[4]));
                tier.setLowerPriceRange(Long.parseLong(values[5]));
                tier.setUpperPriceRange(Long.parseLong(values[6]));
                tier.setLowerVehicleYearRange(Long.parseLong(values[7]));
                tier.setUpperVehicleYearRange(Long.parseLong(values[8]));

                Set<InterestRate> interestRates = new HashSet<>();

                for (int i = 9; i < values.length; i = i + 3) {
                    interestRates.add(
                            InterestRate.builder()
                                    .rate(Double.parseDouble(values[i]))
                                    .dollarDealerReserve(Long.parseLong(values[i+1]))
                                    .percentageDealerReserve(Double.parseDouble(values[i+2]))
                                    .build());
                }

                tier.setInterestRates(interestRates);
                tiers.add(tier);
            }
        }

        program.setTiers(tiers);
        return program;
    }

}
