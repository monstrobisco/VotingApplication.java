package com.sicredi.voting.Service;

import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.repository.PautaRepository;
import com.sicredi.voting.service.PautaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PautaServiceTest {
    /*
        verifica se o método criarPauta retorna corretamente a pauta que foi criada e adicionada ao mock
     */
    @Test
    public void testCriarPauta() {
        PautaRepository pautaRepository = Mockito.mock(PautaRepository.class);
        PautaService pautaService = new PautaService(pautaRepository);

        PautaModel pauta = new PautaModel();

        Mockito.when(pautaRepository.save(pauta)).thenReturn(Mono.just(pauta));

        StepVerifier.create(pautaService.criarPauta(pauta))
                .expectNext(pauta)
                .verifyComplete();

        Mockito.verify(pautaRepository, Mockito.times(1)).save(pauta);
    }
}
