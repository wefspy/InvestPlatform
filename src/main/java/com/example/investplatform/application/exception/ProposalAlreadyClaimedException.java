package com.example.investplatform.application.exception;

public class ProposalAlreadyClaimedException extends RuntimeException {
    public ProposalAlreadyClaimedException(String message) {
        super(message);
    }
}
