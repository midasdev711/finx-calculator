package com.finx.service.loading;

import com.finx.domain.products.Product;
import com.finx.domain.products.ProductVariation;
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
import java.util.HashSet;

@Service
public class ProductLoadingService implements CommandLineRunner {

    @Autowired
    private HandlerOfRepository handlerOfRepository;

    @Value("classpath:data/products_import.txt")
    private Resource products;

    @Override
    public void run(String... args) throws Exception {
        loadProductToDatabase();
    }

    private void loadProductToDatabase() throws IOException {
        InputStream inputStream = products.getInputStream();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String currentProductName = "";
            Product currentProduct = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(",");

                if (!splitLine[0].equals(currentProductName)) {
                    if (currentProduct != null) {
                        handlerOfRepository.getProductRepository().save(currentProduct);
                    }
                    if (splitLine[0].equals("-finish-")) {
                        break;
                    }
                    currentProduct = Product.builder()
                            .name(splitLine[0])
                            .productVariations(new HashSet<>())
                            .finxMarkup(50_00)
                            .dealerMarkup(50_00)
                            .profitShareDealershipPercentage(30)
                            .build();
                    currentProductName = splitLine[0];
                }

                ProductVariation productVariation = ProductVariation.builder()
                        .term(getTermOrKm(splitLine[1]))
                        .km(getTermOrKm(splitLine[2]))
                        .retailPrice(Long.parseLong(splitLine[3]))
                        .dealerCostForInsurance(Long.parseLong(splitLine[4]))
                        .build();

                currentProduct.getProductVariations().add(productVariation);
            }
        }

    }

    private Long getTermOrKm(String termString) {
        if (termString.equals("~")) {
            return null;
        } else {
            return Long.parseLong(termString);
        }
    }
}

