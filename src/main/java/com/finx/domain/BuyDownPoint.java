package com.finx.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BuyDownPoint implements Comparable<BuyDownPoint>{

    private double interestRate;

    private long retailPrice;

    private long pstTax;

    private long gstTax;

    private long luxuryTax;

    @Override
    public int compareTo(BuyDownPoint o) {
        return (int) (this.getInterestRate() * 100 - o.getInterestRate() * 100);
    }

    public long getTotalPayableValue() {
        return retailPrice + pstTax + gstTax + luxuryTax;
    }
}
