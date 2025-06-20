package sviluppo.adriano.MemoFlow.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Nota;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.enums.TipoNota;
import sviluppo.adriano.MemoFlow.repository.NotaRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NotaService {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    public NotaService(NotaRepository notaRepository, UtenteRepository utenteRepository){
        this.notaRepository = notaRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<Nota> cercaTutto(){
        List<Nota> note = notaRepository.findAll();

        return note.stream()
                .toList();
    }

    public Nota creaNota(Nota nota) {
        // Verifica che l'utente sia valorizzato e abbia un ID
        if (nota.getUtente() == null || nota.getUtente().getId() == null) {
            throw new IllegalArgumentException("Utente mancante o ID non specificato");
        }

        // Recupera l'utente dal DB
        Utente utente = utenteRepository.findById(nota.getUtente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));
        nota.setUtente(utente);

        // Associa l'utente esistente alla nota
        nota.setUtente(utente);
        nota.setDataCreazione(LocalDateTime.now());
        nota.setUltimaModifica(LocalDateTime.now());

        return notaRepository.save(nota);
    }



    public Nota aggiornaNota(Long id, Map<String, Object> updates) {
        Nota nota  = notaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nota con ID " + id + " non trovata"));

        //Credenziali credenziali = utente.getCredenziali();

        // Verifica email duplicata
        /*if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            credenzialiRepository.findByEmail(newEmail).ifPresent(existingCredenziali -> {
                Long idUtenteAssociato = existingCredenziali.getUtente().getId();
                if (!idUtenteAssociato.equals(id)) {
                    throw new IllegalArgumentException("Email già in uso da un altro utente");
                }
            });
        }*/

        // Verifica e aggiorna password (in chiaro per ora)
        /*if (updates.containsKey("password")) {
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
        }*/

        // Aggiorna i campi della nota
        updates.forEach((key, value) -> {
            switch (key) {
                case "titolo" -> nota.setTitolo((String) value);
                case "contenutoTesto" -> nota.setContenutoTesto((String) value);
                case "contenutoSVG", "password", "email" -> {
                }
                case "tipoNota" -> nota.setTipoNota(TipoNota.valueOf((String) value));
                default -> throw new IllegalArgumentException("Campo non valido: " + key);
            }
        });

        // Aggiorna email se presente
        /*if (updates.containsKey("email")) {
            credenziali.setEmail((String) updates.get("email"));
        }*/

        //credenzialiRepository.save(credenziali);
        Nota notaAggiornata = notaRepository.save(nota);

        return notaAggiornata;
    }

    public void eliminaNota(Long id){
        if (!notaRepository.existsById(id)){
            throw new EntityNotFoundException("Nota con ID " + id + " non trovata");
        }
        notaRepository.deleteById(id);
    }
}