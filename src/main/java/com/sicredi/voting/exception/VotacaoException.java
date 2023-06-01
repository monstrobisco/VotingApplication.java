package com.sicredi.voting.exception;
public class VotacaoException extends RuntimeException {

    public VotacaoException(String message) {
        super(message);
    }

    public VotacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
