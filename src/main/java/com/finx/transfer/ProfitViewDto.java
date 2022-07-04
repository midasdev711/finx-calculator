package com.finx.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProfitViewDto {

    private double bank;

    private double insurance;

    private double dealership;

    private double finx;

    private double total;
}
