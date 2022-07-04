package com.finx.web;

import com.finx.service.HandlerOfService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CarController {

    @Autowired
    private final HandlerOfService handlerOfService;

    @GetMapping("/brands")
    public Set<String> getBrands() {
        return handlerOfService
                .getCarOptionService()
                .findAllBrands();
    }

    @GetMapping("/brands/{brand}")
    public Set<String> getModels(@PathVariable String brand) {
        return handlerOfService
                .getCarOptionService()
                .findAllModelByBrand(brand);
    }

    @GetMapping("/brands/{brand}/{model}")
    public Set<String> getSeries(@PathVariable String brand, @PathVariable String model) {
        return handlerOfService
                .getCarOptionService()
                .findAllSeriesByBrandAndModel(brand, model);
    }

    @GetMapping("/tiresizes")
    public List<String> findAllTireSizes() {
        return handlerOfService
                .getCarOptionService()
                .findAllTireSizes();
    }

}
