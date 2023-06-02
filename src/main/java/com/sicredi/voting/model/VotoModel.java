package com.sicredi.voting.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document
public class VotoModel {

    @Id
    private String id;
    private String idPauta;
    private String cpfAssociado;
    private boolean voto;

    @Override
    public String toString() {
        return "PautaModel{" +
                "Voto='" + voto + '\'' +
                '}';
    }

}