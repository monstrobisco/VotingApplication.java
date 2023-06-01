package com.sicredi.voting.funcional;

import com.sicredi.voting.model.VotoModel;
import com.sicredi.voting.service.VotoService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

// Handler para Voto
@Service
public class VotoHandler {

    private final VotoService votoService;
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public VotoHandler(VotoService votoService, RabbitTemplate rabbitTemplate, Queue queue) {
        this.votoService = votoService;
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    public Mono<ServerResponse> votar(ServerRequest request) {
        return request.bodyToMono(VotoModel.class)
                .flatMap(votoService::votar)
                .flatMap(voto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(voto))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Ocorreu um erro ao votar: " + e.getMessage()));
    }

    public Mono<ServerResponse> getVotosPauta(ServerRequest request) {
        String idPauta = request.pathVariable("idPauta");
        return votoService.getVotosPauta(idPauta)
                .collectList()
                .flatMap(votos -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(votos))
                .onErrorResume(e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getVotosPorPauta(ServerRequest request) {
        String idPauta = request.pathVariable("idPauta");
        return this.votoService.contarVotos(idPauta)
                .flatMap(contagemVotos -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("contagemVotos", contagemVotos);
                    Long votosSim = contagemVotos.getOrDefault("Sim", 0L);
                    Long votosNao = contagemVotos.getOrDefault("Nao", 0L);
                    String resultado;
                    if (votosSim > votosNao) {
                        resultado = "Aprovado";
                    } else if (votosNao > votosSim) {
                        resultado = "Rejeitado";
                    } else {
                        resultado = "Empate";
                    }
                    response.put("resultado", resultado);
                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getVotosPorPautaPostandoNaFila(ServerRequest request) {
        String idPauta = request.pathVariable("idPauta");
        return this.votoService.contarVotos(idPauta)
                .flatMap(contagemVotos -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("contagemVotos", contagemVotos);
                    Long votosSim = contagemVotos.getOrDefault("Sim", 0L);
                    Long votosNao = contagemVotos.getOrDefault("Nao", 0L);
                    String resultado;
                    if (votosSim > votosNao) {
                        resultado = "Aprovado";
                    } else if (votosNao > votosSim) {
                        resultado = "Rejeitado";
                    } else {
                        resultado = "Empate";
                    }
                    response.put("resultado", resultado);

                    rabbitTemplate.convertAndSend(queue.getName(), response);

                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }


}

