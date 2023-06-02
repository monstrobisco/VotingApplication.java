package com.sicredi.voting.service;

import com.sicredi.voting.model.CpfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CpfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpfService.class);
    private final WebClient webClient;

    public CpfService(WebClient.Builder webClientBuilder, @Value("${cpf.api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        LOGGER.info("Inicializado com a URL base: {}", baseUrl);
    }

    public Mono<String> verificarStatusCpf(String cpf) {
        String cpfFormatado = formatarCpf(cpf);
        LOGGER.info("Verificando status do CPF: {}", cpfFormatado);
        return webClient.get()
                .uri("/{cpf}", cpfFormatado)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> {
                            LOGGER.error("Erro ao verificar o status do CPF. Código de resposta: {}", clientResponse.statusCode());
                            return Mono.error(new Exception("Erro ao verificar o status do CPF"));
                        })
                .bodyToMono(CpfResponse.class)
                .doOnSuccess(response -> LOGGER.info("Resposta recebida para CPF: {}. Status: {}", cpfFormatado, response.getStatus()))
                .doOnError(e -> LOGGER.error("Exceção ao processar o CPF: {}", cpfFormatado, e))
                .map(CpfResponse::getStatus);
    }


    public String formatarCpf(String cpf) {
        String cpfFormatado = cpf.replaceAll("\\D", "");
        LOGGER.debug("CPF formatado: {} -> {}", cpf, cpfFormatado);
        return cpfFormatado;
    }


}
