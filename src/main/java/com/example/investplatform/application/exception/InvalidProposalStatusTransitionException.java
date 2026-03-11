package com.example.investplatform.application.exception;

public class InvalidProposalStatusTransitionException extends RuntimeException {
    public InvalidProposalStatusTransitionException(String message) {
        super(message);
    }
}
