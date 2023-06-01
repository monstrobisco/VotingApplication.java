package com.sicredi.voting.repository;

import com.sicredi.voting.model.AssociadoModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AssociadoRepository extends ReactiveMongoRepository<AssociadoModel, String> {}

