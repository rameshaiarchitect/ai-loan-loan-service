package com.ai.loan.controller;

import com.ai.loan.model.LoanRequest;
import com.ai.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/apply")
    public String apply(@RequestBody LoanRequest request) {
        return loanService.apply(request);
    }
}