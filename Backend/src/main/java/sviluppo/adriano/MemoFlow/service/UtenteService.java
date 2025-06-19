package sviluppo.adriano.MemoFlow.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.dto.CreaDTO.CredenzialiCreateDTO;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.dto.CreaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;
import sviluppo.adriano.MemoFlow.mapper.UtenteMapper;

import java.util.List;
import java.util.Map;

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

    public UtenteDTO aggiornaUtente(Long id, Map<String, Object> updates) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        Credenziali credenziali = utente.getCredenziali();

        // Verifica email duplicata
        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            credenzialiRepository.findByEmail(newEmail).ifPresent(existingCredenziali -> {
                Long idUtenteAssociato = existingCredenziali.getUtente().getId();
                if (!idUtenteAssociato.equals(id)) {
                    throw new IllegalArgumentException("Email già in uso da un altro utente");
                }
            });
        }

        // Verifica e aggiorna password (in chiaro per ora)
        if (updates.containsKey("password")) {
            String currentPassword = (String) updates.get("currentPassword");
            String newPassword = (String) updates.get("password");

            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("La password attuale è richiesta per modificare la password");
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("La nuova password non può essere vuota");
            }

            if (!currentPassword.equals(credenziali.getPassword())) {
                throw new IllegalArgumentException("Password attuale non corretta");
            }

            credenziali.setPassword(newPassword);
        }

        // Aggiorna i campi dell’utente
        updates.forEach((key, value) -> {
            switch (key) {
                case "nome" -> utente.setNome((String) value);
                case "cognome" -> utente.setCognome((String) value);
                case "currentPassword", "password", "email" -> {
                    // già gestiti
                }
                default -> throw new IllegalArgumentException("Campo non valido: " + key);
            }
        });

        // Aggiorna email se presente
        if (updates.containsKey("email")) {
            credenziali.setEmail((String) updates.get("email"));
        }

        credenzialiRepository.save(credenziali);
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
