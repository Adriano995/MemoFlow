package sviluppo.adriano.MemoFlow.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UtenteService {

    @Autowired
    private final UtenteRepository utenteRepository;

    @Autowired
    private CredenzialiRepository credenzialiRepository;

    public UtenteService(UtenteRepository utenteRepository, CredenzialiRepository credenzialiRepository) {
        this.utenteRepository = utenteRepository;
        this.credenzialiRepository = credenzialiRepository;
    }

    /**
     * Recupera tutti gli utenti dal database come AdminDTO (per utenti con
     * ruolo ADMIN)
     *
     * @return Lista di AdminDTO
     */
    public List<Utente> cercaTutti() {
        List<Utente> usersList = utenteRepository.findAll();
        return usersList;
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

    /**
     * Recupera un utente tramite email come AdminDTO (per utenti con ruolo
     * ADMIN)
     *
     * @return AdminDTO
     */
    public Utente findByEmail(String email) {
        Credenziali cred = credenzialiRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Credenziali con email " + email + " non trovate"));
        return cred.getUtente();
    }


    public Utente aggiornaUtente(Long id, Map<String, Object> updates) {
        Utente existingUser = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        Credenziali credenziali = existingUser.getCredenziali();

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

        // Aggiorna i campi dell'utente
        updates.forEach((key, value) -> {
            switch (key) {
                case "nome":
                    existingUser.setNome((String) value);
                    break;
                case "cognome":
                    existingUser.setCognome((String) value);
                    break;
                case "currentPassword":
                case "password":
                case "email":
                    // Già gestiti sopra
                    break;
                default:
                    throw new IllegalArgumentException("Campo non valido per l'aggiornamento: " + key);
            }
        });

        // Aggiorna anche l'email se presente
        if (updates.containsKey("email")) {
            credenziali.setEmail((String) updates.get("email"));
        }

        // Salva entrambi
        credenzialiRepository.save(credenziali);
        Utente utenteAggiornato = utenteRepository.save(existingUser);

        return utenteAggiornato;
    }


    /**
     * Elimina un utente dal sistema
     *
     * @param id ID dell'utente da eliminare
     * @throws EntityNotFoundException se l'utente non esiste
     */
    public void eliminaUser(Long id) {
        if (!utenteRepository.existsById(id)) {
            throw new EntityNotFoundException("Utente con ID " + id + " non trovato");
        }
        utenteRepository.deleteById(id);
    }

}