package com.sicredi.voting.controller;

import com.sicredi.voting.exception.VotacaoException;
import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.model.VotoModel;
import com.sicredi.voting.repository.VotoRepository;
import com.sicredi.voting.service.PautaService;
import com.sicredi.voting.service.VotoService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

//@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {
    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final VotoService votoService;
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;
    public PautaController(PautaService pautaService, VotoService votoService, VotoRepository votoRepository, RabbitTemplate rabbitTemplate, Queue queue) {
        this.pautaService = pautaService;
        this.votoService = votoService;
        this.votoRepository = votoRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    @PostMapping
    public Mono<PautaModel> criarPauta(@RequestBody PautaModel pauta) {
        return pautaService.criarPauta(pauta);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PostMapping("/votos")
    public Mono<VotoModel> votar(@RequestBody VotoModel voto) {
        try {
            return votoService.votar(voto);
        } catch (VotacaoException e) {
            throw new VotacaoException("Ocorreu um erro ao votar: " + e.getMessage(), e);
        }
    }


    @GetMapping("/pauta/{idPauta}")
    public Flux<VotoModel> getVotosPauta(@PathVariable String idPauta) {
        return votoService.getVotosPauta(idPauta);
    }

    @GetMapping("/{idPauta}/votos")
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

                    rabbitTemplate.convertAndSend(queue.getName(), response);

                    return ServerResponse.ok().bodyValue(response);
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }



}

