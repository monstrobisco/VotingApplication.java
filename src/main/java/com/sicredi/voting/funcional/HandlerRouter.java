package com.sicredi.voting.funcional;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HandlerRouter {

    @Bean

    public RouterFunction<ServerResponse> route(PautaHandler pautaHandler, VotoHandler votoHandler, SessaoVotacaoHandler sessaoVotacaoHandler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/api/v1/pautas").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), pautaHandler::criarPauta)
                .andRoute(RequestPredicates.POST("/api/v1/pautas/votos").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), votoHandler::votar)
                .andRoute(RequestPredicates.GET("/api/v1/pautas/pauta/{idPauta}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), votoHandler::getVotosPauta)
                .andRoute(RequestPredicates.GET("/api/v1/pautas/{idPauta}/votos").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), votoHandler::getVotosPorPauta)
                .andRoute(RequestPredicates.POST("/api/v1/sessoes").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), sessaoVotacaoHandler::abrirSessao)
                .andRoute(RequestPredicates.GET("/api/v1/pautas/{idPauta}/votosPostandoNaFila").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), votoHandler::getVotosPorPautaPostandoNaFila);
    }

}

