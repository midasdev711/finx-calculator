package com.finx.domain.views;

import lombok.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@With
@Builder
public class ProfitView {

    private long bank;

    private long insurance;

    private long dealership;

    private long finx;

    private long total;
}
