package com.sicredi.voting.Service;

import com.sicredi.voting.exception.VotacaoException;
import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.model.SessaoVotacao;
import com.sicredi.voting.model.VotoModel;
import com.sicredi.voting.repository.PautaRepository;
import com.sicredi.voting.repository.SessaoVotacaoRepository;
import com.sicredi.voting.repository.VotoRepository;
import com.sicredi.voting.service.CpfService;
import com.sicredi.voting.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import static org.mockito.Mockito.when;



@SpringBootTest
public class VotoServiceTest {
    private VotoService votoService;
    @MockBean
    private VotoRepository votoRepository;
    @MockBean
    private PautaRepository pautaRepository;
    @MockBean
    private SessaoVotacaoRepository sessaoVotacaoRepository;
    @MockBean
    private CpfService cpfService;
/*
    Verifica se um erro é lançado quando o status do CPF é UNABLE_TO_VOTE.
    Verifica se um erro é lançado quando o tempo da sessão de votação expirou.
    Verifica se um erro é lançado quando um associado já votou.
    Verifica se o voto é salvo corretamente quando todas as condições são atendidas.*/
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        votoService = new VotoService(votoRepository, null, null, null);
    }
    @Test
    public void testVotar() {
        VotoService votoService = new VotoService(votoRepository, pautaRepository, sessaoVotacaoRepository, cpfService);

        VotoModel voto = new VotoModel();

        Mockito.when(cpfService.verificarStatusCpf(voto.getCpfAssociado())).thenReturn(Mono.just("UNABLE_TO_VOTE"));
        StepVerifier.create(votoService.votar(voto))
                .expectError(VotacaoException.class)
                .verify();

        SessaoVotacao sessaoVotacaoExpirada = new SessaoVotacao();
        sessaoVotacaoExpirada.setInicio(LocalDateTime.now().minusHours(2));
        sessaoVotacaoExpirada.setFim(LocalDateTime.now().minusHours(1));

        SessaoVotacao sessaoVotacaoValida = new SessaoVotacao();
        sessaoVotacaoValida.setInicio(LocalDateTime.now().minusMinutes(10));
        sessaoVotacaoValida.setFim(LocalDateTime.now().plusMinutes(10));

        PautaModel pauta = new PautaModel();

        Mockito.when(cpfService.verificarStatusCpf(voto.getCpfAssociado())).thenReturn(Mono.just("ABLE_TO_VOTE"));
        Mockito.when(pautaRepository.findById(voto.getIdPauta())).thenReturn(Mono.just(pauta));
        Mockito.when(sessaoVotacaoRepository.findByIdPauta(voto.getIdPauta())).thenReturn(Mono.just(sessaoVotacaoExpirada));
        StepVerifier.create(votoService.votar(voto))
                .expectError(Exception.class)
                .verify();

        Mockito.when(sessaoVotacaoRepository.findByIdPauta(voto.getIdPauta())).thenReturn(Mono.just(sessaoVotacaoValida));
        Mockito.when(votoRepository.findByCpfAssociadoAndIdPauta(voto.getCpfAssociado(), voto.getIdPauta())).thenReturn(Mono.just(new VotoModel()));
        StepVerifier.create(votoService.votar(voto))
                .expectError(Exception.class)
                .verify();

        Mockito.when(votoRepository.findByCpfAssociadoAndIdPauta(voto.getCpfAssociado(), voto.getIdPauta())).thenReturn(Mono.empty());
        Mockito.when(votoRepository.save(voto)).thenReturn(Mono.just(voto));
        StepVerifier.create(votoService.votar(voto))
                .expectNext(voto)
                .verifyComplete();
    }

    /*
     verifica se o método getVotosPauta retorna corretamente os votos que foram criados e adicionados ao mock
    */
    @Test
    public void testGetVotosPauta() {
        VotoService votoService = new VotoService(votoRepository, pautaRepository, sessaoVotacaoRepository, cpfService);

        String idPauta = "pauta1";
        VotoModel voto1 = new VotoModel();
        VotoModel voto2 = new VotoModel();
        VotoModel voto3 = new VotoModel();

        Mockito.when(votoRepository.findByIdPauta(idPauta)).thenReturn(Flux.just(voto1, voto2, voto3));

        StepVerifier.create(votoService.getVotosPauta(idPauta))
                .expectNext(voto1, voto2, voto3)
                .verifyComplete();
    }

    /*
        Verifica se o método contarVotos retorna corretamente a contagem dos votos "sim" e "não"
    */

    @Test
    public void contarVotosTest() {
        VotoModel voto1 = new VotoModel();
        voto1.setVoto(true);
        VotoModel voto2 = new VotoModel();
        voto2.setVoto(true);
        VotoModel voto3 = new VotoModel();
        voto3.setVoto(false);

        when(votoRepository.findByIdPauta("idPauta"))
                .thenReturn(Flux.just(voto1, voto2, voto3));

        StepVerifier.create(votoService.contarVotos("idPauta"))
                .assertNext(contagemVotos -> {
                    assert contagemVotos.get("sim") == 2;
                    assert contagemVotos.get("nao") == 1;
                })
                .verifyComplete();
    }

}

