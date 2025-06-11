package sviluppo.adriano.MemoFlow.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;

@Service
@Transactional
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository){
        this.utenteRepository = utenteRepository;
    }

    public Utente creaUtente(Utente utenteInput) {
        Credenziali cred = utenteInput.getCredenziali();

        if (cred == null || cred.getEmail() == null || cred.getPassword() == null) {
            throw new IllegalArgumentException("Email e password sono obbligatorie.");
        }

        // Associazioni bidirezionali
        cred.setUtente(utenteInput);
        utenteInput.setCredenziali(cred);

        return utenteRepository.save(utenteInput);
    }

}