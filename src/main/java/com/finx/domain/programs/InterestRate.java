package com.finx.domain.programs;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@EqualsAndHashCode
@Getter
@Builder
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class InterestRate implements Comparable<InterestRate> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long databaseId;

    private double rate;

    private long dollarDealerReserve;

    private double percentageDealerReserve;

    @Override
    public int compareTo(InterestRate o) {
        return (int) (this.getRate() * 100 - o.getRate() * 100);
    }
}
