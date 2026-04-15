package com.ai.loan.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishLoanApplication(String event) {
        kafkaTemplate.send("loan.application.submitted", event);
    }
}