package sviluppo.adriano.MemoFlow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.modificaCredenzialiDTO.CambiaEmailDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.modificaCredenzialiDTO.CambiaPasswordDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;

@Service
public class CredenzialiService {

    private CredenzialiRepository credenzialiRepository;

    @Autowired
    public CredenzialiService(CredenzialiRepository credenzialiRepository){
        this.credenzialiRepository = credenzialiRepository;
    }

    // Cambia password, verifica vecchia password
    public CredenzialiDTO cambiaPassword(CambiaPasswordDTO dto) {
        Credenziali cred = credenzialiRepository.findByUtenteId(dto.getUtenteId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (!cred.getPassword().equals(dto.getVecchiaPassword())) {
            throw new IllegalArgumentException("Password vecchia non corretta");
        }

        cred.setPassword(dto.getNuovaPassword());
        Credenziali aggiornata = credenzialiRepository.save(cred);

        return new CredenzialiDTO(aggiornata); // Risposta "pulita"
    }

    public CredenzialiDTO cambiaEmail(Long utenteId, CambiaEmailDTO dto) {
        Credenziali cred = credenzialiRepository.findByUtenteId(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Verifica che la vecchia email corrisponda
        if (!cred.getEmail().equals(dto.getVecchiaEmail())) {
            throw new IllegalArgumentException("Email attuale non corretta");
        }

        // Verifica che la nuova email non sia già in uso
        if (credenzialiRepository.findByEmail(dto.getNuovaEmail()).isPresent()) {
            throw new IllegalArgumentException("La nuova email è già in uso");
        }

        cred.setEmail(dto.getNuovaEmail());
        Credenziali aggiornata = credenzialiRepository.save(cred);

        return new CredenzialiDTO(aggiornata);
    }
}
