package com.finx.domain.programs;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@With
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long databaseId;

    private long number;

    private long lowerTermRange;

    private long upperTermRange;

    private long lowerAmortRange;

    private long upperAmortRange;

    private long lowerPriceRange;

    private long upperPriceRange;

    private long lowerVehicleYearRange;

    private long upperVehicleYearRange;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<InterestRate> interestRates;
}
