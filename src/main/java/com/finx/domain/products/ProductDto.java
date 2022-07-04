package com.finx.domain.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Data
public class ProductDto {

    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<ProductVariationDto> productVariations;

}
