package com.finx.domain.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@With
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long databaseId;

    private String name;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<ProductVariation> productVariations;

    @JsonIgnore
    private int profitShareDealershipPercentage;

    @JsonIgnore
    private long dealerMarkup;

    @JsonIgnore
    private long finxMarkup;

}
