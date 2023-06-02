package com.sicredi.voting.funcional;

import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.service.PautaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PautaHandler {

    private final PautaService pautaService;

    public PautaHandler(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    public Mono<ServerResponse> criarPauta(ServerRequest request) {
        log.info("Iniciando criação de Pauta");
        return request.bodyToMono(PautaModel.class)
                .doOnNext(pauta -> log.info("Pauta recebida para criação: {}", pauta))
                .flatMap(pautaService::criarPauta)
                .doOnNext(pauta -> log.info("Pauta criada com sucesso: {}", pauta))
                .flatMap(pauta -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(pauta))
                .doOnError(e -> log.error("Erro ao criar Pauta", e))
                .onErrorResume(e -> ServerResponse.badRequest().build());
    }
}
