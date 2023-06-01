package com.sicredi.voting.repository;

import com.sicredi.voting.model.PautaModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PautaRepository extends ReactiveMongoRepository<PautaModel, String> {}
