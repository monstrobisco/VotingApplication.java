package com.sicredi.voting.service;

import com.sicredi.voting.exception.VotacaoException;
import com.sicredi.voting.model.VotoModel;
import com.sicredi.voting.repository.PautaRepository;
import com.sicredi.voting.repository.SessaoVotacaoRepository;
import com.sicredi.voting.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final CpfService cpfService;

    public VotoService(VotoRepository votoRepository, PautaRepository pautaRepository, SessaoVotacaoRepository sessaoVotacaoRepository, CpfService cpfService) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.cpfService = cpfService;
    }

    public Mono<VotoModel> votar(VotoModel voto) {
        return cpfService.verificarStatusCpf(voto.getCpfAssociado())
                .doOnNext(cpfStatus -> log.info("CPF Status: " + cpfStatus)) // log para verificar o status do CPF
                .flatMap(cpfStatus -> {
                    if ("UNABLE_TO_VOTE".equals(cpfStatus)) {
                        return Mono.error(new VotacaoException("CPF inválido ou não autorizado para votar"));
                    }
                    return pautaRepository.findById(voto.getIdPauta())
                            .doOnNext(pauta -> log.info("Pauta: {}" + pauta)) // log para verificar a Pauta
                            .flatMap(pauta -> sessaoVotacaoRepository.findByIdPauta(voto.getIdPauta())
                                    .doOnNext(sessaoVotacao -> log.info("Sessão de Votação: {}" + sessaoVotacao)) // log para verificar a Sessão de Votação
                                    .flatMap(sessaoVotacao -> {
                                        LocalDateTime now = LocalDateTime.now();
                                        if (now.isAfter(sessaoVotacao.getInicio()) && now.isBefore(sessaoVotacao.getFim())) {
                                            return votoRepository.findByCpfAssociadoAndIdPauta(voto.getCpfAssociado(), voto.getIdPauta())
                                                    .hasElement()
                                                    .flatMap(hasVoted -> {
                                                        if (hasVoted) {
                                                            return Mono.error(new Exception("Associado já votou nesta pauta"));
                                                        } else {
                                                            return votoRepository.save(voto)
                                                                    .doOnNext(savedVoto -> log.info("Voto salvo: {}" + savedVoto)); // log para verificar o Voto salvo
                                                        }
                                                    });
                                        } else {
                                            return Mono.error(new Exception("Sessão de votação Expirou, o tempo para conclusão do voto é de um min"));
                                        }
                                    }));
                });
    }



    public Flux<VotoModel> getVotosPauta(String idPauta) {
        return votoRepository.findByIdPauta(idPauta);
    }

    public Mono<Map<String, Long>> contarVotos(String idPauta) {
        return votoRepository.findByIdPauta(idPauta)
                .collectList()
                .map(votos -> votos.stream()
                        .collect(Collectors.groupingBy(voto -> voto.isVoto() ? "sim" : "nao", Collectors.counting())));
    }

}
