package com.sicredi.voting.Service;

import com.sicredi.voting.model.CpfResponse;
import com.sicredi.voting.service.CpfService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;


public class CpfServiceTest {

    @Test
    public void testVerificarStatusCpf() {
        WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient webClient = Mockito.mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);

        String cpf = "12345678901"; // CPF de teste

        CpfResponse cpfResponse = new CpfResponse();
        cpfResponse.setStatus("ABLE_TO_VOTE"); // Definir o status do CPF de teste

        Mockito.when(webClientBuilder.baseUrl(Mockito.anyString())).thenReturn(webClientBuilder);
        Mockito.when(webClientBuilder.build()).thenReturn(webClient);
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri("/{cpf}", cpf)).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.onStatus(Mockito.any(), Mockito.any())).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(CpfResponse.class)).thenReturn(Mono.just(cpfResponse));

        CpfService cpfService = new CpfService(webClientBuilder, "https://api.example.com");

        StepVerifier.create(cpfService.verificarStatusCpf(cpf))
                .expectNext("ABLE_TO_VOTE")
                .verifyComplete();
    }
    /*
        Verifica se o método formatarCpf retorna corretamente o CPF formatado
     */
    @Test
    public void testFormatarCpf() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        CpfService cpfService = new CpfService(webClientBuilder, "https://api.example.com");

        String cpf = "123.456.789-01";
        String cpfFormatado = cpfService.formatarCpf(cpf);

        assertEquals("12345678901", cpfFormatado);
    }


}

