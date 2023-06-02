package com.sicredi.voting.service;
import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.repository.PautaRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PautaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PautaService.class);
    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
        LOGGER.info("PautaService inicializado com sucesso!");
    }

    public Mono<PautaModel> criarPauta(PautaModel pauta) {
        LOGGER.info("Criando pauta: {}", pauta.getTitulo());
        return pautaRepository.save(pauta)
                .doOnSuccess(p -> LOGGER.info("Pauta criada com sucesso: {}", p.getTitulo()))
                .doOnError(e -> LOGGER.error("Erro ao criar pauta: {}", pauta.getTitulo(), e));
    }
}
