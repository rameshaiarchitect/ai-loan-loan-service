package com.ai.loan.service;

import com.ai.loan.kafka.LoanProducer;
import com.ai.loan.model.LoanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanProducer producer;

    public String apply(LoanRequest request) {
        producer.publishLoanApplication(request.toString());
        return "Application Submitted";
    }
}