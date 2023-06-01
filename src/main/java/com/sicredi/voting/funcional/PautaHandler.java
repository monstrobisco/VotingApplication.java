package com.sicredi.voting.funcional;

import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.service.PautaService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class PautaHandler {

    private final PautaService pautaService;

    public PautaHandler(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    public Mono<ServerResponse> criarPauta(ServerRequest request) {
        return request.bodyToMono(PautaModel.class)
                .flatMap(pautaService::criarPauta)
                .flatMap(pauta -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(pauta))
                .onErrorResume(e -> ServerResponse.badRequest().build());
    }

}

