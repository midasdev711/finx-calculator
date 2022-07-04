package com.finx.service.loading;

import com.finx.domain.cars.CarOption;
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
import java.util.Arrays;

@Service
public class CarLoadingService implements CommandLineRunner {

    @Autowired
    private HandlerOfRepository handlerOfRepository;

    @Value("classpath:data/cars_import.txt")
    private Resource cars;

    @Override
    public void run(String... args) throws Exception {
        loadCarsToDatabase();
    }

    private void loadCarsToDatabase() throws IOException {
        InputStream inputStream = cars.getInputStream();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] carLine = line.split(",");
                String[] series = carLine[2].split(";");

                Arrays.stream(series)
                        .map(serie -> buildCarOption(carLine[0], carLine[1], serie))
                        .forEach(carOption -> handlerOfRepository.getCarOptionRepository().save(carOption));
            }
        }

    }

    private CarOption buildCarOption(String brand, String model, String series) {
        return CarOption.builder()
                .brand(brand)
                .model(model)
                .series(series)
                .build();
    }
}
