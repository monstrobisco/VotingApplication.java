package com.sicredi.voting.controller;

import com.sicredi.voting.model.SessaoVotacao;
import com.sicredi.voting.repository.SessaoVotacaoRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

//@RestController
@RequestMapping("/api/v1/sessoes")
public class SessaoVotacaoController {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public SessaoVotacaoController(SessaoVotacaoRepository sessaoVotacaoRepository) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    }

    @PostMapping
    public Mono<SessaoVotacao> abrirSessao(@RequestBody SessaoVotacao sessaoVotacao) {
        sessaoVotacao.setInicio(LocalDateTime.now());
        sessaoVotacao.setFim(LocalDateTime.now().plusMinutes(1));
        return sessaoVotacaoRepository.save(sessaoVotacao);
    }
}