package com.finx.domain.views;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
@With
@Builder
public class PerDiemView {

    private LocalDate lastPaymentDate;

    private long periodsPaid;

    private LocalDate payOutDate;

    private long perDiemRate;

    private long principal;

    private long total;

}
