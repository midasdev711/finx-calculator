package com.finx.web;

import com.finx.randombuilder.TestRandomDeal;
import com.finx.service.HandlerOfService;
import com.finx.transfer.DealDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private HandlerOfService handlerOfServiceMock;

    @InjectMocks
    private DealController dealController;

    @Test
    void testGet() {
    }

    @Test
    void calculateDeal() {
        DealDto deal = new TestRandomDeal().getRandomDto()
                .withVehiclePrice(3000)
                .withCashDown(125)
                .withPstTax(972.93)
                .withGstTax(694.95)
                .withRetailValueProducts(10899.00)
                .withAdminFee(50.00)
                .withFinxFee(100)
                .withPpsaFee(51.24);

        for (int i = 0; i < 50; i++) {
            System.out.println(dealController.calculateDeal(deal.withChosenBuyDownPoint(Math.random() > 0.5 ? 3 : 0)));
        }

    }

    @Test
    void getPrograms() {
    }

    @Test
    void getPerDiem() {
    }

    @Test
    void getProfitView() {
    }


    @Test
    void getBuyDownPoints() {
        DealDto deal = new TestRandomDeal().getRandomDto()
                .withVehiclePrice(3000_00)
                .withCashDown(125_00)
                .withPstTax(972_93)
                .withGstTax(694_95)
                .withRetailValueProducts(10899_00)
                .withAdminFee(50_00)
                .withFinxFee(100_00)
                .withPpsaFee(51_24);
        for (int i = 0; i < 50; i++) {
//            System.out.println(dealController.getBuyDownPoints(deal.withChosenBuyDownPoint(Math.random() > 0.5 ? 3 : 0)));
        }
    }

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    @Test
    void testCalculateDeal() {
    }

    @Test
    void testGetPrograms() {
    }

    @Test
    void testGetPerDiem() {
    }

    @Test
    void testGetProfitView() {
    }

    @Test
    void testGetInterestRates() {
    }

    @Test
    void testGetBuyDownPoints() {
    }
}