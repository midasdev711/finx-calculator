package com.finx.web;

import com.finx.domain.cars.Car;
import com.finx.domain.products.ProductDto;
import com.finx.service.HandlerOfService;
import com.finx.transfer.mapper.MainMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductController {

    @Autowired
    private final HandlerOfService handlerOfService;

    @Autowired
    private final MainMapper mainMapper;

    @GetMapping("/products")
    public Set<ProductDto> getAllProducts() {
        return handlerOfService
                .getProductService()
                .findAllProducts()
                .stream()
                .map(mainMapper::toProductDto)
                .collect(Collectors.toSet());
    }

    @PostMapping("/products")
    public Set<ProductDto> getProductsByCar(@RequestBody @Valid Car car) {
        return handlerOfService
                .getProductService()
                .findAllProductsByCar(car)
                .stream()
                .map(mainMapper::toProductDto)
                .collect(Collectors.toSet());
    }
}
