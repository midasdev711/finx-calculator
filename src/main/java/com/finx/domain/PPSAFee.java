package com.finx.domain;

import com.finx.domain.enums.Province;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PPSAFee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long databaseId;

    private Province province;

    private int financingTerm;

    private long ppsaFee;

}
