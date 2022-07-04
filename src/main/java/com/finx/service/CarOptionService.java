package com.finx.service;

import com.finx.domain.cars.CarOption;
import com.finx.persistence.HandlerOfRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CarOptionService {

    @Autowired
    private final HandlerOfRepository handlerOfRepository;

    public Set<String> findAllBrands() {
        return handlerOfRepository.getCarOptionRepository().findAll()
                .stream()
                .map(CarOption::getBrand)
                .collect(Collectors.toSet());
    }

    public Set<String> findAllModelByBrand(String brand) {
        return handlerOfRepository.getCarOptionRepository().findByBrand(brand)
                .stream()
                .map(CarOption::getModel)
                .collect(Collectors.toSet());
    }

    public Set<String> findAllSeriesByBrandAndModel(String brand, String model) {
        return handlerOfRepository.getCarOptionRepository().findByBrandAndModel(brand, model)
                .stream()
                .map(CarOption::getSeries)
                .collect(Collectors.toSet());
    }

    public List<String> findAllTireSizes() {
        return List.of(
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20",
                "21",
                "22",
                "23",
                "24",
                "25",
                "Specialty Option"
        );
    }



}
