package sviluppo.adriano.MemoFlow.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.dto.cambiaRuoliDTO.UtenteCambiaRuoliDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.UtenteCambiaDatiDTO;
import sviluppo.adriano.MemoFlow.entity.Authority;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.entity.UtenteAuthority;
import sviluppo.adriano.MemoFlow.enums.AuthorityEnum;
import sviluppo.adriano.MemoFlow.mapper.UtenteMapper;
import sviluppo.adriano.MemoFlow.repository.AuthorityRepository;
import sviluppo.adriano.MemoFlow.repository.CredenzialiRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;

@Service
@Transactional
public class UtenteService {

    private UtenteRepository utenteRepository;
    private CredenzialiRepository credenzialiRepository;
    private UtenteMapper utenteMapper;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UtenteService(UtenteRepository utenteRepository, CredenzialiRepository credenzialiRepository, UtenteMapper utenteMapper, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.credenzialiRepository = credenzialiRepository;
        this.utenteMapper = utenteMapper;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public List<UtenteDTO> cercaTutti() {
        List<Utente> utenti = utenteRepository.findAll();
        return utenti.stream()
                .map(utenteMapper::toDto)
                .toList();
    }

    @Transactional
    public UtenteDTO cercaSingolo(Long id){
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        UtenteDTO utenteDTO = utenteMapper.toDto(utente);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName().equals(utente.getCredenziali().getEmail())) {
            Set<String> userRoles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            utenteDTO.setRoles(userRoles);
        }

         return utenteDTO;
    }

    @Transactional
    public UtenteDTO creaUtente(UtenteCreateDTO utenteDto) {

        CredenzialiCreateDTO credDto = utenteDto.getCredenziali();
        if (credDto == null || credDto.getEmail() == null || credDto.getPassword() == null) {
            throw new IllegalArgumentException("Dati delle credenziali mancanti o incompleti.");
        }

        if (credenzialiRepository.findByEmail(credDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già esistente. Si prega di usare un'altra email.");
        }

        Utente utenteToSave = utenteMapper.toEntity(utenteDto);

        Credenziali credenzialiFromMapper = utenteToSave.getCredenziali();
        if (credenzialiFromMapper == null) {
            throw new IllegalStateException("L'utenteMapper non ha creato le credenziali associate.");
        }
        credenzialiFromMapper.setPassword(passwordEncoder.encode(credDto.getPassword()));
        credenzialiFromMapper.setUtente(utenteToSave);

        Authority userRole = authorityRepository.findById(AuthorityEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Errore: Ruolo 'ROLE_USER' non trovato nel database. Assicurati che DataInitializer sia stato eseguito correttamente."));

        UtenteAuthority utenteAuthority = new UtenteAuthority(utenteToSave, userRole);

        utenteToSave.addUtenteAuthority(utenteAuthority);

        Utente salvatoUtente = utenteRepository.save(utenteToSave);

        Set<String> roles = salvatoUtente.getUserAuthorities().stream()
                .map(ua -> ua.getAuthority().getAuthorityEnum().name())
                .collect(Collectors.toSet());

        return new UtenteDTO(salvatoUtente.getId(), salvatoUtente.getNome(), salvatoUtente.getCognome(), salvatoUtente.getCredenziali().getEmail(), roles);
    }

    public UtenteDTO findByEmail(String email) {
        Credenziali cred = credenzialiRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email non trovata"));

        return utenteMapper.toDto(cred.getUtente());
    }

    @Transactional
    public UtenteDTO aggiornaUtente(Long id, UtenteCambiaDatiDTO dto) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        utente.setNome(dto.getNome());
        utente.setCognome(dto.getCognome());

        Utente utenteAggiornato = utenteRepository.save(utente);

        return utenteMapper.toDto(utenteAggiornato);
    }

    @Transactional
    public UtenteDTO aggiornaRuoliUtente(Long id, UtenteCambiaRuoliDTO dto) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente con ID " + id + " non trovato"));

        utente.getUserAuthorities().clear(); 

        for (String roleName : dto.getRoles()) {
            AuthorityEnum authorityEnum;
            try {
                authorityEnum = AuthorityEnum.valueOf(roleName);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ruolo non valido: " + roleName);
            }
            Authority authority = authorityRepository.findById(authorityEnum)
                    .orElseThrow(() -> new RuntimeException("Errore: Ruolo '" + roleName + "' non trovato nel database."));

            UtenteAuthority utenteAuthority = new UtenteAuthority(utente, authority);
            utente.getUserAuthorities().add(utenteAuthority); 
        }

        Utente utenteAggiornato = utenteRepository.save(utente);

        Set<String> roles = utenteAggiornato.getUserAuthorities().stream()
                .map(ua -> ua.getAuthority().getAuthorityEnum().name())
                .collect(Collectors.toSet());

        return new UtenteDTO(utenteAggiornato.getId(), utenteAggiornato.getNome(), utenteAggiornato.getCognome(), utenteAggiornato.getCredenziali().getEmail(), roles);
    }


    @Transactional
    public void eliminaUser(Long id) {
        if (!utenteRepository.existsById(id)) {
            throw new EntityNotFoundException("Utente con ID " + id + " non trovato");
        }
        utenteRepository.deleteById(id);
    }
}
