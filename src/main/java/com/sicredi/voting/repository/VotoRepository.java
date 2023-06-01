package com.sicredi.voting.repository;

import com.sicredi.voting.model.VotoModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VotoRepository extends ReactiveMongoRepository<VotoModel, String> {
    Mono<VotoModel> findByCpfAssociadoAndIdPauta(String cpfAssociado, String idPauta);
    Flux<VotoModel> findByIdPauta(String idPauta);
}
