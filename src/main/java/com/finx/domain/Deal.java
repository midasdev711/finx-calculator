package com.finx.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finx.domain.cars.Car;
import com.finx.domain.enums.DealType;
import com.finx.domain.enums.PaymentFrequency;
import com.finx.domain.enums.Province;
import com.finx.domain.products.ProductChoice;
import com.finx.domain.views.PerDiemView;
import com.finx.domain.views.ProfitView;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@With
@Builder
@NoArgsConstructor
public class Deal {

    @NonNull
    private DealType dealType;

    @NonNull
    private Car car;

    @NonNull
    private String chosenProgram;

    private Deferral deferral;

    @NonNull
    private Province province;

    private int clientCreditScore;


    //region Taxes
    private long pstTax;

    private long gstTax;

    private long luxuryTax;

    private boolean pstActivated;

    private boolean gstActivated;
    //endregion

    //region Financials
    private long vehiclePrice;

    private long tradeInPrice;

    private long lienAmount;

    private long cashDown;

    private long rebateBeforeTaxes;

    private long rebateAfterTaxes;
    //endregion

    //region Fees
    private long dealershipFee;

    private long adminFee;

    private long finxFee;

    private long ppsaFee;

    private long licenseFee;
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

    private long costOfBorrowing;

    private long buyDownAmount;

    private long principleOutstanding;

    private long paymentPerFrequency;

    private long monthlyPayment;

    private long breakEvenPoint;

    //region Buy Down Points
    private int chosenBuyDownPoint;

    private int buyDownPointsAvailable;

    private long retailValueBuyDownPoints;
    //endregion

    private long dealerReserve;

    private long customerInterestSaving;

    private String lenderNote;

    private long retailValueProducts;

    private long totalAmountFinanced;

    private Set<ProductChoice> chosenProducts;

    private List<Double> interestRates;

    @JsonIgnore
    private Set<BuyDownPoint> buyDownPoints;

    private boolean buyDownActivated;

    private PerDiemView perDiemView;

    private ProfitView profitView;

    //For Lease
    private long residual;

}
