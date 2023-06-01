package com.sicredi.voting.service;
import com.sicredi.voting.model.PautaModel;
import com.sicredi.voting.repository.PautaRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PautaService {
    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    public Mono<PautaModel> criarPauta(PautaModel pauta) {
        return pautaRepository.save(pauta);
    }
}
