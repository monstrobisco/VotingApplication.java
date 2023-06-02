package com.sicredi.voting.funcional;

import com.sicredi.voting.model.VotoModel;
import com.sicredi.voting.service.VotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(VotoHandler.class);

    private final VotoService votoService;
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    public VotoHandler(VotoService votoService, RabbitTemplate rabbitTemplate, Queue queue) {
        this.votoService = votoService;
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
        LOGGER.info("Voto Handler Inicializado com sucesso!");
    }

    public Mono<ServerResponse> votar(ServerRequest request) {
        LOGGER.info("Recebendo solicitação de voto");
        return request.bodyToMono(VotoModel.class)
                .flatMap(voto -> {
                    LOGGER.info("Votando: {}", voto);
                    return votoService.votar(voto);
                })
                .flatMap(voto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(voto))
                .onErrorResume(e -> {
                    LOGGER.error("Erro ao votar: {}", e.getMessage());
                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Ocorreu um erro ao votar: " + e.getMessage());
                });
    }

    public Mono<ServerResponse> getVotosPauta(ServerRequest request) {
        String idPauta = request.pathVariable("idPauta");
        LOGGER.info("Buscando votos da pauta: {}", idPauta);
        return votoService.getVotosPauta(idPauta)
                .collectList()
                .flatMap(votos -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(votos))
                .onErrorResume(e -> {
                    LOGGER.error("Pauta não encontrada: {}", idPauta);
                    return ServerResponse.notFound().build();
                });
    }

    public Mono<ServerResponse> getVotosPorPauta(ServerRequest request) {
        String idPauta = request.pathVariable("idPauta");
        LOGGER.info("Contando votos para a pauta: {}", idPauta);
        return this.votoService.contarVotos(idPauta)
                .flatMap(contagemVotos -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("contagemVotos", contagemVotos);
                    Long votosSim = contagemVotos.getOrDefault("sim", 0L);
                    Long votosNao = contagemVotos.getOrDefault("nao", 0L);
                    Long totalVotos = votosSim + votosNao;
                    LOGGER.info("Contando votos sim: {}", votosSim);
                    LOGGER.info("Contando votos nao: {}", votosNao);
                    LOGGER.info("Total de votos: {}", totalVotos);
                    String resultado;
                    if (totalVotos == 1) {
                        if (votosSim == 1) {
                            resultado = "Aprovado";
                        } else {
                            resultado = "Rejeitado";
                        }
                    } else {
                        if (votosSim > votosNao) {
                            resultado = "Aprovado";
                        } else if (votosNao > votosSim) {
                            resultado = "Rejeitado";
                        } else {
                            resultado = "Empate";
                        }
                    }
                    response.put("resultado", resultado);
                    LOGGER.info("Resultado para a pauta {}: {}", idPauta, resultado);
                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOGGER.info("Nenhum voto encontrado para a pauta: {}", idPauta);
                    return ServerResponse.notFound().build();
                }));
    }







    public Mono<ServerResponse> getVotosPorPautaPostandoNaFila(ServerRequest request) {
        String idPauta = request.pathVariable("idPauta");
        LOGGER.info("Contando votos para a pauta: {}", idPauta);
        return this.votoService.contarVotos(idPauta)
                .flatMap(contagemVotos -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("contagemVotos", contagemVotos);
                    Long votosSim = contagemVotos.getOrDefault("sim", 0L);
                    Long votosNao = contagemVotos.getOrDefault("nao", 0L);
                    Long totalVotos = votosSim + votosNao;
                    LOGGER.info("Contando votos sim: {}", votosSim);
                    LOGGER.info("Contando votos nao: {}", votosNao);
                    LOGGER.info("Total de votos: {}", totalVotos);
                    String resultado;
                    if (totalVotos == 1) {
                        if (votosSim == 1) {
                            resultado = "Aprovado";
                        } else {
                            resultado = "Rejeitado";
                        }
                    } else {
                        if (votosSim > votosNao) {
                            resultado = "Aprovado";
                        } else if (votosNao > votosSim) {
                            resultado = "Rejeitado";
                        } else {
                            resultado = "Empate";
                        }
                    }
                    response.put("resultado", resultado);
                    LOGGER.info("Resultado para a pauta {}: {}", idPauta, resultado);
                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOGGER.info("Nenhum voto encontrado para a pauta: {}", idPauta);
                    return ServerResponse.notFound().build();
                }));
    }
}
