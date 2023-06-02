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
        log.info("VotoService inicializado com sucesso!");
    }

    public Mono<VotoModel> votar(VotoModel voto) {
        log.info("Votação iniciada para CPF: {} e Pauta: {}", voto.getCpfAssociado(), voto.getIdPauta());
        return cpfService.verificarStatusCpf(voto.getCpfAssociado())
                .doOnNext(cpfStatus -> log.info("Status do CPF: {}", cpfStatus))
                .flatMap(cpfStatus -> {
                    if ("UNABLE_TO_VOTE".equals(cpfStatus)) {
                        log.warn("CPF inválido ou não autorizado para votar: {}", voto.getCpfAssociado());
                        return Mono.error(new VotacaoException("CPF inválido ou não autorizado para votar"));
                    }
                    return pautaRepository.findById(voto.getIdPauta())
                            .doOnNext(pauta -> log.info("Pauta encontrada: {}", pauta))
                            .flatMap(pauta -> sessaoVotacaoRepository.findByIdPauta(voto.getIdPauta())
                                    .doOnNext(sessaoVotacao -> log.info("Sessão de Votação encontrada: {}", sessaoVotacao))
                                    .flatMap(sessaoVotacao -> {
                                        LocalDateTime now = LocalDateTime.now();
                                        if (now.isAfter(sessaoVotacao.getInicio()) && now.isBefore(sessaoVotacao.getFim())) {
                                            return votoRepository.findByCpfAssociadoAndIdPauta(voto.getCpfAssociado(), voto.getIdPauta())
                                                    .hasElement()
                                                    .flatMap(hasVoted -> {
                                                        if (hasVoted) {
                                                            log.warn("Associado já votou nesta pauta. CPF: {} Pauta: {}", voto.getCpfAssociado(), voto.getIdPauta());
                                                            return Mono.error(new Exception("Associado já votou nesta pauta"));
                                                        } else {
                                                            return votoRepository.save(voto)
                                                                    .doOnNext(savedVoto -> log.info("Voto salvo com sucesso: {}", savedVoto));
                                                        }
                                                    });
                                        } else {
                                            log.warn("Sessão de votação expirou para a Pauta: {}", voto.getIdPauta());
                                            return Mono.error(new Exception("Sessão de votação Expirou, o tempo para conclusão do voto é de um min"));
                                        }
                                    }));
                });
    }

    public Flux<VotoModel> getVotosPauta(String idPauta) {
        log.info("Buscando votos para a Pauta: {}", idPauta);
        return votoRepository.findByIdPauta(idPauta)
                .doOnNext(voto -> log.info("Voto encontrado: {}", voto));
    }

    public Mono<Map<String, Long>> contarVotos(String idPauta) {
        log.info("Contando votos para a Pauta: {}", idPauta);
        return votoRepository.findByIdPauta(idPauta)
                .collectList()
                .map(votos -> {
                    Map<String, Long> contagemVotos = votos.stream()
                            .collect(Collectors.groupingBy(voto -> voto.isVoto() ? "sim" : "nao", Collectors.counting()));
                    log.info("Contagem de votos para a Pauta: {}. Resultado: {}", idPauta, contagemVotos);
                    return contagemVotos;
                });
    }
}
