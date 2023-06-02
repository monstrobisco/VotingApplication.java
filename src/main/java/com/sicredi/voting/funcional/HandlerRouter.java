package com.sicredi.voting.funcional;


import com.sicredi.voting.dto.PautaDto;
import com.sicredi.voting.dto.SessaoVotacaoDto;
import com.sicredi.voting.dto.VotoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HandlerRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/pautas",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = PautaHandler.class,
                    beanMethod = "criarPauta",
                    operation = @Operation(
                            operationId = "criarPauta",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Objeto JSON contendo os dados da pauta",
                                    required = true,
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PautaDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Operação realizada com sucesso",
                                            content = @Content(schema = @Schema(implementation = PautaDto.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/sessoes",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = SessaoVotacaoHandler.class,
                    beanMethod = "abrirSessao",
                    operation = @Operation(
                            operationId = "abrirSessao",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Objeto JSON contendo os dados da Sessão",
                                    required = true,
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SessaoVotacaoDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Operação realizada com sucesso",
                                            content = @Content(schema = @Schema(implementation = SessaoVotacaoDto.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/pautas/votos",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = VotoHandler.class,
                    beanMethod = "votar",
                    operation = @Operation(
                            operationId = "votar",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Objeto JSON contendo os dados do voto",
                                    required = true,
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VotoDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Operação realizada com sucesso",
                                            content = @Content(schema = @Schema(implementation = VotoDto.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
            path = "/api/v1/pautas/pauta/{idPauta}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET,
            beanClass = VotoHandler.class,
            beanMethod = "getVotosPauta",
            operation = @Operation(
                    operationId = "getVotosPauta",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "successful operation",
                                    content = @Content(schema = @Schema(
                                            implementation = PautaDto.class
                                    ))
                            ),
                            @ApiResponse(responseCode = "404",description = "Pauta não encontrada!")
                    },
                    parameters = {
                            @Parameter(in = ParameterIn.PATH,name = "idPauta")
                    }
            )
    ),
            @RouterOperation(
                    path = "/api/v1/pautas/{idPauta}/votos",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    method = RequestMethod.GET,
                    beanClass = VotoHandler.class,
                    beanMethod = "getVotosPorPauta",
                    operation = @Operation(
                            operationId = "getVotosPorPauta",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = PautaDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404",description = "Pauta não encontrada!")
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH,name = "idPauta")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/pautas/{idPauta}/votosPostandoNaFila",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    method = RequestMethod.GET,
                    beanClass = VotoHandler.class,
                    beanMethod = "getVotosPorPautaPostandoNaFila",
                    operation = @Operation(
                            operationId = "getVotosPorPautaPostandoNaFila",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = PautaDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404",description = "Pauta não encontrada!")
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH,name = "idPauta")
                            }
                    )
            )

    })
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

