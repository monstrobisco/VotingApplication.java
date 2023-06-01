package com.sicredi.voting.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
public class SessaoVotacao {

    @Id
    private String id;
    private String idPauta;
    private LocalDateTime inicio;
    private LocalDateTime fim;

}