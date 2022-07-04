package com.finx.transfer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import com.finx.domain.products.ProductChoiceDto;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@With
@Builder
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
public class DealDto {

    @NonNull
    private DealType dealType;

    @NonNull
    private Car car;

    @NonNull
    private String chosenProgram;

    private DeferralDto deferral;

    @NonNull
    private Province province;

    private int clientCreditScore;


    //region Taxes
    private double pstTax;

    private double gstTax;

    private double luxuryTax;

    private boolean pstActivated;

    private boolean gstActivated;
    //endregion

    //region Financials
    private double vehiclePrice;

    private double tradeInPrice;

    private double lienAmount;

    private double cashDown;

    private double rebateBeforeTaxes;

    private double rebateAfterTaxes;
    //endregion

    //region Fees
    private double dealershipFee;

    private double adminFee;

    private double finxFee;

    private double ppsaFee;

    private double licenseFee;
    //endregion

    private int financingTerm;

    private int amortizationTerm;

    @NonNull
    private PaymentFrequency paymentFrequency;

    private double interestRateAvailable;

    private double loyaltyInterestRateDeduction;

    private double finxInterestRateGenerated;

    private double customInterestRate;

    private boolean manualCustomInterestRate;

    private double actualInterestRate;

    private double costOfBorrowing;

    private double buyDownAmount;

    private double principleOutstanding;

    private double paymentPerFrequency;

    private double monthlyPayment;

    private int breakEvenPoint;

    //region Buy Down Points
    private int chosenBuyDownPoint;

    private int buyDownPointsAvailable;

    private double retailValueBuyDownPoints;
    //endregion

    private double dealerReserve;

    private double customerInterestSaving;

    private String lenderNote;

    private double retailValueProducts;

    private double totalAmountFinanced;

    private List<Double> interestRates;

    private Set<ProductChoiceDto> chosenProducts;

    private boolean buyDownActivated;

    private PerDiemViewDto perDiemView;

    private ProfitViewDto profitView;

    private double residual;

}
