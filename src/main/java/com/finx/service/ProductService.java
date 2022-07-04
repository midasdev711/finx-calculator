package com.finx.service;

import com.finx.domain.cars.Car;
import com.finx.domain.products.Product;
import com.finx.domain.products.ProductChoice;
import com.finx.domain.products.ProductVariation;
import com.finx.persistence.HandlerOfRepository;
import com.finx.web.exceptions.NoSuchProductException;
import com.finx.web.exceptions.NoSuchProductVariationException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    @Autowired
    private final HandlerOfRepository handlerOfRepository;

    public Set<Product> findAllProductsByCar(Car car) {
        return findAllProducts()
                .stream()
                .filter(product -> isValidProductForCar(product, car))
                .collect(Collectors.toSet());
    }

    private boolean isValidProductForCar(Product product, Car car) {
        if (!product.getName().contains("Tire and Rim")) return true;
        if (isOversizeTire(car) && product.getName().contains("*")) return true;
        if (!isOversizeTire(car) && !product.getName().contains("*")) return true;
        return false;
    }

    private boolean isOversizeTire(Car car) {
        //""Specialty Option" is not parsable thus oversize
        return !NumberUtils.isParsable(car.getTireSize())
                || NumberUtils.isParsable(car.getTireSize()) && Integer.parseInt(car.getTireSize()) >= 20;

    }

    public Set<Product> findAllProducts() {
        return handlerOfRepository
                .getProductRepository()
                .findAll();
    }

    public Product findProductByNameOrThrow(String productName) {
        return handlerOfRepository
                .getProductRepository()
                .findByName(productName)
                .orElseThrow(NoSuchProductException::new);
    }

    public long getFinxProductMarkup(ProductChoice productChoice) {
        Product chosenProduct = findProductByNameOrThrow(productChoice.getName());
        return chosenProduct.getFinxMarkup();
    }

    public long getDealerProductMarkup(ProductChoice productChoice) {
        Product chosenProduct = findProductByNameOrThrow(productChoice.getName());
        return chosenProduct.getDealerMarkup();
    }

    public long getInsuranceProfit(ProductChoice productChoice) {
        Product chosenProduct = findProductByNameOrThrow(productChoice.getName());
        ProductVariation chosenProductVariation = getProductVariation(chosenProduct, productChoice);

        return chosenProductVariation.getDealerCostForInsurance();
    }

    public long calculateRetainedProfitForDealer(ProductChoice productChoice) {
        Product chosenProduct = findProductByNameOrThrow(productChoice.getName());
        ProductVariation chosenProductVariation = getProductVariation(chosenProduct, productChoice);

        double rateProfitRetentionForDealer = (double) chosenProduct.getProfitShareDealershipPercentage() / 100;

        return Math.round(rateProfitRetentionForDealer * calculateProfit(chosenProduct, chosenProductVariation));
    }

    public long calculateProfit(ProductChoice productChoice) {
        Product chosenProduct = findProductByNameOrThrow(productChoice.getName());
        ProductVariation chosenProductVariation = getProductVariation(chosenProduct, productChoice);

        return calculateProfit(chosenProduct, chosenProductVariation);
    }

    private long calculateProfit(Product product, ProductVariation productVariation) {
        return productVariation.getRetailPrice()
                - (productVariation.getDealerCostForInsurance()
                + product.getDealerMarkup()
                + product.getFinxMarkup());
    }

    private ProductVariation getProductVariation(Product product, ProductChoice productChoice) {
        return product.getProductVariations()
                .stream()
                .filter(productVariation -> isProductChoiceMatchingProductVariation(productChoice, productVariation))
                .findFirst()
                .orElseThrow(NoSuchProductVariationException::new);
    }

    private boolean isProductChoiceMatchingProductVariation(ProductChoice productChoice, ProductVariation productVariation) {
        return Objects.equals(productChoice.getKm(), productVariation.getKm())
                && Objects.equals(productChoice.getTerm(), productVariation.getTerm())
                && Objects.equals(productChoice.getRetailPrice(), productVariation.getRetailPrice());
    }

}
