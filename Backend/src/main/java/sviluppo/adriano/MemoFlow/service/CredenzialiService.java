package sviluppo.adriano.MemoFlow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.modificaCredenzialiDTO.CambiaEmailDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.modificaCredenzialiDTO.CambiaPasswordDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;

@Service
public class CredenzialiService {

    private CredenzialiRepository credenzialiRepository;
    private PasswordEncoder passwordEncoder; // Dichiarazione del PasswordEncoder

    @Autowired
    public CredenzialiService(CredenzialiRepository credenzialiRepository, PasswordEncoder passwordEncoder){ // Iniezione nel costruttore
        this.credenzialiRepository = credenzialiRepository;
        this.passwordEncoder = passwordEncoder; // Inizializzazione
    }

    /*public CredenzialiDTO cambiaPassword(CambiaPasswordDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetailServiceImpl.UserPrincipal userDetails)) {
            throw new SecurityException("Utente non autenticato");
        }
        Long userId = userDetails.getId();

        Credenziali cred = credenzialiRepository.findByUtenteId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (!cred.getPassword().equals(dto.getVecchiaPassword())) {
            throw new IllegalArgumentException("Password vecchia non corretta");
        }

        cred.setPassword(dto.getNuovaPassword());
        Credenziali aggiornata = credenzialiRepository.save(cred);

        return new CredenzialiDTO(aggiornata);
    }*/


   public CredenzialiDTO cambiaPassword(CambiaPasswordDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetailServiceImpl.UserPrincipal userDetails)) {
            throw new SecurityException("Utente non autenticato");
        }
        Long userId = userDetails.getId();

        Credenziali cred = credenzialiRepository.findByUtenteId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // *** Modifica qui: usa passwordEncoder.matches() per confrontare la vecchia password ***
        // Questo metodo confronta la password in chiaro fornita dal DTO con l'hash salvato nel database.
        if (!passwordEncoder.matches(dto.getVecchiaPassword(), cred.getPassword())) {
            throw new IllegalArgumentException("Password vecchia non corretta");
        }

        // *** Modifica qui: cripta la nuova password prima di salvarla ***
        // È fondamentale salvare l'hash della nuova password, non la password in chiaro.
        cred.setPassword(passwordEncoder.encode(dto.getNuovaPassword()));
        Credenziali aggiornata = credenzialiRepository.save(cred);

        return new CredenzialiDTO(aggiornata);
    }

    public CredenzialiDTO cambiaEmail(CambiaEmailDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetailServiceImpl.UserPrincipal userDetails)) {
            throw new SecurityException("Utente non autenticato");
        }
        Long userId = userDetails.getId();

        Credenziali cred = credenzialiRepository.findByUtenteId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (!cred.getEmail().equals(dto.getVecchiaEmail())) {
            throw new IllegalArgumentException("Email attuale non corretta");
        }

        if (credenzialiRepository.findByEmail(dto.getNuovaEmail()).isPresent()) {
            throw new IllegalArgumentException("La nuova email è già in uso");
        }

        cred.setEmail(dto.getNuovaEmail());
        Credenziali aggiornata = credenzialiRepository.save(cred);

        return new CredenzialiDTO(aggiornata);
    }
}
