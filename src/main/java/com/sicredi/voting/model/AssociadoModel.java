package com.sicredi.voting.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class AssociadoModel {

    @Id
    private String cpf;
    private String nome;

}
