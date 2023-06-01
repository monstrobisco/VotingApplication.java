package com.sicredi.voting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        if(ex.getMessage().contains("Associado já votou nesta pauta")) {

            return new ResponseEntity<>("O associado já votou nesta pauta", HttpStatus.BAD_REQUEST);

        } else if(ex.getMessage().contains("Sessão de votação Expirou, o tempo para conclusão do voto é de um min")) {

            return new ResponseEntity<>("Sessão de votação Expirou, o tempo para conclusão do voto é de um min", HttpStatus.BAD_REQUEST);

        } else if(ex.getMessage().contains("CPF inválido ou não autorizado para votar")) {

        return new ResponseEntity<>("CPF inválido ou não autorizado para votar", HttpStatus.BAD_REQUEST);

    }

        else {

            return new ResponseEntity<>("Erro inesperado", HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }
}
