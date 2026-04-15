package com.ai.loan.model;

import lombok.Data;

@Data
public class LoanRequest {
    private String applicationId;
    private double amount;
    private double salary;
}