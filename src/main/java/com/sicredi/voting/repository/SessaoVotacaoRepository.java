package com.sicredi.voting.repository;

import com.sicredi.voting.model.SessaoVotacao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SessaoVotacaoRepository extends ReactiveMongoRepository<SessaoVotacao, String> {
    Mono<SessaoVotacao> findByIdPauta(String idPauta);
}
