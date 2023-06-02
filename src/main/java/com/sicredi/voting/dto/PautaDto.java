package com.sicredi.voting.dto;

import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.model.VotoModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PautaDto {

    private String titulo;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private List<VotoModel> votos;



}
