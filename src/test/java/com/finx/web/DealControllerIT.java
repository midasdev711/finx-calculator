package com.finx.web;

import com.finx.domain.Deal;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import com.finx.transfer.DealDto;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DealControllerIT {

    @LocalServerPort
    int localPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DealController dealController;

    @Test
    void testController() {

        String url = String.format("http://localhost:%s/deal", localPort);

        DealDto deal = DealDto.builder()
                .car(Car.builder().year(2021).build())
                .dealType(DealType.DEALERSHIP)
                .chosenProgram("BMO Fixed Rate")
                .province(Province.BRITISH_COLUMBIA)
                .paymentFrequency(PaymentFrequency.WEEKLY)
                .vehiclePrice(25000.00)
                .financingTerm(24)
                .amortizationTerm(48)
                .chosenBuyDownPoint(1)
                .build();

//        ResponseEntity<DealDto> responseEntity = testRestTemplate.postForEntity(url, deal, DealDto.class);

        DealDto result = dealController.calculateDeal(deal);

        System.out.println(result);
//
//        DealDto result = responseEntity.getBody();
//
//        assertThat(result.getTotalAmountFinanced())
//                .isEqualTo(15643_12);
    }

    @Test
    @Disabled("Currently there is a randomized deal return")
    void testControllerWithInsufficientData() throws JSONException {

        String dealAsJson = "{\"vehiclePrice\":300000,\"cashDown\":12500}";

        Deal deal = Deal.builder()
                .vehiclePrice(3000_00)
                .cashDown(125_00)
                .pstTax(972_93)
                .gstTax(694_95)
                .retailValueProducts(10899_00)
                .adminFee(50_00)
                .finxFee(100_00)
                .ppsaFee(51_24)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(dealAsJson, headers);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/api/deal", entity, String.class);

//        Deal result = responseEntity.getBody();

        System.out.println(responseEntity);
    }



}