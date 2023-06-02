package com.sicredi.voting.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
@Getter
@Setter
public class PautaModel {

    @Id
    private String id;
    private String titulo;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private List<VotoModel> votos;

    @Override
    public String toString() {
        return "PautaModel{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                '}';
    }

}
