package com.finx.domain.cars;

import com.finx.domain.enums.CarType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    private CarType carType;

    private long year;

    private String brand;

    private String model;

    private String series;

    private String bodyStyle;

    private String odometer;

    @NotNull
    private String tireSize;

}
