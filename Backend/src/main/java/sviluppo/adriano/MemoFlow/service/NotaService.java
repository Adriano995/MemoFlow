package sviluppo.adriano.MemoFlow.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import sviluppo.adriano.MemoFlow.dto.NotaDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.NotaCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.CambiaNotaDTO;
import sviluppo.adriano.MemoFlow.entity.Nota;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.mapper.NotaMapper; 
import sviluppo.adriano.MemoFlow.repository.NotaRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;

@Service
@Transactional
public class NotaService {

    private NotaRepository notaRepository;
    private UtenteRepository utenteRepository;
    private NotaMapper notaMapper;

    @Autowired
    public NotaService(NotaRepository notaRepository, UtenteRepository utenteRepository, NotaMapper notaMapper){
        this.notaRepository = notaRepository;
        this.utenteRepository = utenteRepository;
        this.notaMapper = notaMapper;
    }

    public List<NotaDTO> cercaTutte() {
        List<Nota> note = notaRepository.findAll();
        return note.stream()
                .map(notaMapper::toDto)
                .collect(Collectors.toList());
    }

    public NotaDTO cercaPerId(Long id) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nota con ID " + id + " non trovata"));
        return notaMapper.toDto(nota);
    }

    public NotaDTO creaNota(NotaCreateDTO createDto) {
        Utente utente = utenteRepository.findById(createDto.getUtenteId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));

        Nota nota = notaMapper.toEntity(createDto);
        nota.setUtente(utente);
        nota.setDataCreazione(createDto.getDataNota().atStartOfDay());
        nota.setUltimaModifica(createDto.getDataNota().atStartOfDay());

        Nota salvata = notaRepository.save(nota);

        return notaMapper.toDto(salvata);
    }

    public NotaDTO aggiornaNota(Long id, CambiaNotaDTO modifica) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nota con ID " + id + " non trovata"));

        Long currentUserId = getCurrentUserId();
        if (!nota.getUtente().getId().equals(currentUserId)) {
            throw new SecurityException("Non puoi modificare una nota che non ti appartiene!");
        }

        if (modifica.getTitolo() != null) {
            nota.setTitolo(modifica.getTitolo());
        }

        if (modifica.getContenutoTesto() != null) {
            nota.setContenutoTesto(modifica.getContenutoTesto());
        }
        
        if (modifica.getContenutoSVG() != null) {
            nota.setContenutoSVG(modifica.getContenutoSVG());
        }

        if (modifica.getTipoNota() != null) {
            nota.setTipoNota(modifica.getTipoNota());
        }

        nota.setUltimaModifica(LocalDateTime.now());

        Nota salvata = notaRepository.save(nota);
        return notaMapper.toDto(salvata);
    }

    public void eliminaNota(Long id) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nota con ID " + id + " non trovata per l'eliminazione."));

        Long currentUserId = getCurrentUserId();
        if (!nota.getUtente().getId().equals(currentUserId)) {
            throw new SecurityException("Non puoi eliminare una nota che non ti appartiene!");
        }

        notaRepository.deleteById(id);
    }

    public List<NotaDTO> getNoteByDataCreazioneAndUtenteId(LocalDate data, Long utenteId) {
        LocalDateTime startOfDay = data.atStartOfDay();
        LocalDateTime endOfDay = data.atTime(23, 59, 59, 999_999_999);

        List<Nota> note = notaRepository.findAllByDataCreazioneBetweenAndUtenteId(startOfDay, endOfDay, utenteId);
        return note.stream()
                .map(notaMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<NotaDTO> getNoteByUtenteId(Long utenteId) {
        List<Nota> note = notaRepository.findAllByUtenteId(utenteId);
        return note.stream()
                .map(notaMapper::toDto)
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailServiceImpl.UserPrincipal userDetails) {
            return userDetails.getId();
        }
        throw new SecurityException("Utente non autenticato");
    }
}