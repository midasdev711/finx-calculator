package com.finx.domain.programs;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@With
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private boolean subvented;

    private LocalDate startDate;

    private LocalDate endDate;

    private long lowerCreditScoreBoundary;

    private long upperCreditScoreBoundary;

    private long lowerDeferralPeriod;

    private long upperDeferralPeriod;

    private long adminFee;

    private long finxFee;

    private SpecialAccessCase specialAccessCase;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Tier> tiers;


}
