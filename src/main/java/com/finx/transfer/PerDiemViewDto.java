package com.finx.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PerDiemViewDto {

    private LocalDate lastPaymentDate;

    private long periodsPaid;

    private LocalDate payOutDate;

    private double perDiemRate;

    private double principal;

    private double total;

}
