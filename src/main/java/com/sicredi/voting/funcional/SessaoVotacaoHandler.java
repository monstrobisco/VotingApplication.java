package com.sicredi.voting.funcional;

import com.sicredi.voting.model.SessaoVotacao;
import com.sicredi.voting.repository.SessaoVotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class SessaoVotacaoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessaoVotacaoHandler.class);

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public SessaoVotacaoHandler(SessaoVotacaoRepository sessaoVotacaoRepository) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        LOGGER.info("Sessao Handler Inicializado com sucesso!");
    }

    public Mono<ServerResponse> abrirSessao(ServerRequest request) {
        LOGGER.info("Iniciando o processo de abertura de sessão...");
        return request.bodyToMono(SessaoVotacao.class)
                .doOnNext(sessaoVotacao -> {
                    sessaoVotacao.setInicio(LocalDateTime.now());
                    sessaoVotacao.setFim(LocalDateTime.now().plusMinutes(1));
                    LOGGER.info("Sessão de votação para pauta {} foi definida para iniciar em {} e terminar em {}", sessaoVotacao.getIdPauta(), sessaoVotacao.getInicio(), sessaoVotacao.getFim());
                })
                .flatMap(sessaoVotacaoRepository::save)
                .doOnNext(saved -> LOGGER.info("Sessão de votação salva com sucesso para a pauta {}", saved.getIdPauta()))
                .flatMap(saved -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(saved))
                .onErrorResume(e -> {
                    LOGGER.error("Erro ocorrido ao tentar abrir a sessão de votação: {}", e.getMessage());
                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(e.getMessage());
                });
    }
}
