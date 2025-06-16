package sviluppo.adriano.MemoFlow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;

@Service
public class CredenzialiService {

    @Autowired
    private CredenzialiRepository credenzialiRepository;

    public Credenziali creaCredenziali(CredenzialiDTO dto) {
        if (credenzialiRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già esistente");
        }
        Credenziali cred = new Credenziali();
        cred.setEmail(dto.getEmail());
        cred.setPassword(dto.getPassword()); // idealmente qui fai hashing della password!
        return credenzialiRepository.save(cred);
    }
}
