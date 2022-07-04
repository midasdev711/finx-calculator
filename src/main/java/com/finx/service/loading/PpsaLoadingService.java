package com.finx.service.loading;

import com.finx.domain.PPSAFee;
import com.finx.domain.enums.Province;
import com.finx.persistence.HandlerOfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

@Service
public class PpsaLoadingService implements CommandLineRunner {

    @Autowired
    private HandlerOfRepository handlerOfRepository;

    @Value("classpath:data/ppsaFees.txt")
    private Resource ppsaFees;

    @Override
    public void run(String... args) throws Exception {
        loadPpsaFeesToDatabase();
    }

    private void loadPpsaFeesToDatabase() throws IOException {
        InputStream inputStream = ppsaFees.getInputStream();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] termPricePair = line.split(",");
                Stream.of(Province.values())
                        .forEach(province -> {
                            PPSAFee currentFee = PPSAFee.builder()
                                    .financingTerm(Integer.parseInt(termPricePair[0]))
                                    .ppsaFee(Long.parseLong(termPricePair[1]))
                                    .province(province)
                                    .build();
                            handlerOfRepository.getPpsaFeesRepository().save(currentFee);
                        });
            }
        }

    }


}
