package com.sicredi.voting.funcional;

import com.sicredi.voting.model.SessaoVotacao;
import com.sicredi.voting.repository.SessaoVotacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class SessaoVotacaoHandler {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public SessaoVotacaoHandler(SessaoVotacaoRepository sessaoVotacaoRepository) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    }

    public Mono<ServerResponse> abrirSessao(ServerRequest request) {
        return request.bodyToMono(SessaoVotacao.class)
                .doOnNext(sessaoVotacao -> {
                    sessaoVotacao.setInicio(LocalDateTime.now());
                    sessaoVotacao.setFim(LocalDateTime.now().plusMinutes(1));
                })
                .flatMap(sessaoVotacao -> sessaoVotacaoRepository.save(sessaoVotacao))
                .flatMap(saved -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(saved))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage()));
    }
}
