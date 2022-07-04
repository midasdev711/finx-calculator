package com.finx.persistence;

import com.finx.domain.cars.CarOption;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CarOptionRepository extends CrudRepository<CarOption, Long> {

    @Override
    Set<CarOption> findAll();

    Set<CarOption> findByBrand(String brand);

    Set<CarOption> findByBrandAndModel(String brand, String model);

}
