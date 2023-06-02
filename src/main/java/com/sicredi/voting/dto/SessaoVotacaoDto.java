package com.sicredi.voting.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SessaoVotacaoDto {

    private String idPauta;
    private LocalDateTime inicio;
    private LocalDateTime fim;

}
