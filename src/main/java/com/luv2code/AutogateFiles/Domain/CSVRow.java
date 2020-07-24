package com.luv2code.AutogateFiles.Domain;

import java.util.ArrayList;
import java.util.List;

public class CSVRow {


   private String inputPIN;
    private String inputPAN;


    private String expiryMonth;
    private String expiryYear;

    private String amount;

   private String sourceAccountNumber;
    private  String sourceAccountType;
    private String  sourceBankCbnCode;

    public CSVRow(){

    }

    public String getInputPIN() {
        return inputPIN;
    }

    public void setInputPIN(String inputPIN) {
        this.inputPIN = inputPIN;
    }

    public String getInputPAN() {
        return inputPAN;
    }

    public void setInputPAN(String inputPAN) {
        this.inputPAN = inputPAN;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getSourceAccountType() {
        return sourceAccountType;
    }

    public void setSourceAccountType(String sourceAccountType) {
        this.sourceAccountType = sourceAccountType;
    }

    public String getSourceBankCbnCode() {
        return sourceBankCbnCode;
    }

    public void setSourceBankCbnCode(String sourceBankCbnCode) {
        this.sourceBankCbnCode = sourceBankCbnCode;
    }
}
