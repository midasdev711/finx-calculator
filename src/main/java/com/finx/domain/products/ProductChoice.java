package com.finx.domain.products;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductChoice {

    private String name;

    private Long term;

    private Long km;

    private long retailPrice;
}
