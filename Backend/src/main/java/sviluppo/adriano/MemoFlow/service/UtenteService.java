package sviluppo.adriano.MemoFlow.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.UtenteCambiaDatiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.dto.creaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;
import sviluppo.adriano.MemoFlow.mapper.UtenteMapper;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CredenzialiRepository credenzialiRepository;

    @Autowired
    private UtenteMapper utenteMapper;

    public UtenteService(UtenteRepository utenteRepository, CredenzialiRepository credenzialiRepository, UtenteMapper utenteMapper) {
        this.utenteRepository = utenteRepository;
        this.credenzialiRepository = credenzialiRepository;
        this.utenteMapper = utenteMapper;
    }

    public List<UtenteDTO> cercaTutti() {
        List<Utente> utenti = utenteRepository.findAll();
        return utenti.stream()
                .map(utenteMapper::toDto)
                .toList();
    }

    public UtenteDTO cercaSingolo(Long id){
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        return utenteMapper.toDto(utente);
    }

    public UtenteDTO creaUtente(UtenteCreateDTO utenteDto) {

        // Prendi le credenziali dal DTO di creazione
        CredenzialiCreateDTO credDto = utenteDto.getCredenziali();

        if (credDto == null || credDto.getEmail() == null || credDto.getPassword() == null) {
            throw new IllegalArgumentException("Credenziali mancanti");
        }

        if (credenzialiRepository.findByEmail(credDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già esistente");
        }

        // Mappa il DTO di creazione (UtenteCreateDTO) all'entità Utente
        Utente utente = utenteMapper.toEntity(utenteDto);

        // Associa bidirezionalmente le credenziali
        Credenziali credenziali = utente.getCredenziali();
        credenziali.setUtente(utente);

        // Salva l'utente (cascade salva anche le credenziali)
        Utente salvato = utenteRepository.save(utente);

        // Mappa l'entità salvata a DTO di lettura (UtenteDTO)
        return utenteMapper.toDto(salvato);
    }

    public UtenteDTO findByEmail(String email) {
        Credenziali cred = credenzialiRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email non trovata"));

        return utenteMapper.toDto(cred.getUtente());
    }

    public UtenteDTO aggiornaUtente(Long id, UtenteCambiaDatiDTO dto) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        // Aggiorna solo i dati anagrafici previsti dal DTO
        utente.setNome(dto.getNome());
        utente.setCognome(dto.getCognome());

        Utente utenteAggiornato = utenteRepository.save(utente);

        return utenteMapper.toDto(utenteAggiornato);
    }


    public void eliminaUser(Long id) {
        if (!utenteRepository.existsById(id)) {
            throw new EntityNotFoundException("Utente con ID " + id + " non trovato");
        }
        utenteRepository.deleteById(id);
    }
}
