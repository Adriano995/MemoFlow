package sviluppo.adriano.MemoFlow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import sviluppo.adriano.MemoFlow.abstractions.AbstractCrudService;
import sviluppo.adriano.MemoFlow.dto.EventoDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.EventoCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.EventoCambiaDTO;
import sviluppo.adriano.MemoFlow.entity.Evento;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.enums.EventoStato;
import sviluppo.adriano.MemoFlow.mapper.EventoMapper;
import sviluppo.adriano.MemoFlow.repository.EventoRepository;
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;


@Service
@Transactional
public class EventoService extends AbstractCrudService<
        Evento,
        Long,
        EventoDTO,
        EventoCreateDTO,
        EventoCambiaDTO,
        EventoRepository> {

    private final EventoMapper eventoMapper;

    @Autowired
    public EventoService(EventoRepository repository, EventoMapper eventoMapper) {
        super(repository);
        this.eventoMapper = eventoMapper;
    }

    @Override
    protected EventoDTO toDto(Evento entity) {
        return eventoMapper.toDto(entity);
    }

    @Override
    protected Evento toEntity(EventoCreateDTO createDto) {
        return eventoMapper.toEntity(createDto);
    }

    @Override
    public EventoDTO create(EventoCreateDTO createDto) {
        Evento evento = toEntity(createDto);
        Utente utente = new Utente();
        utente.setId(getCurrentUserId());
        evento.setUtente(utente);
        Evento salvato = repository.save(evento);
        return toDto(salvato);
    }

    @Override
    protected void updateEntity(Evento entity, EventoCambiaDTO updateDto) {
        if (updateDto.getTitolo() != null) {
            entity.setTitolo(updateDto.getTitolo());
        }
        if (updateDto.getDescrizione() != null) {
            entity.setDescrizione(updateDto.getDescrizione());
        }
        if (updateDto.getDataInizio() != null) {
            entity.setDataInizio(updateDto.getDataInizio());
        }
        if (updateDto.getDataFine() != null) {
            entity.setDataFine(updateDto.getDataFine());
        }
        /*if (updateDto.getOraInizio() != null) {
            entity.setOraInizio(updateDto.getOraInizio());
        }
        if (updateDto.getOraFine() != null) {
            entity.setOraFine(updateDto.getOraFine());
        }*/
        if (updateDto.getLuogo() != null) {
            entity.setLuogo(updateDto.getLuogo());
        }
        if (updateDto.getStato() != null) {
            entity.setStato(Enum.valueOf(EventoStato.class, updateDto.getStato()));
        }
    }

    @Override
    public void delete(Long id) {
        Long currentUserId = getCurrentUserId();

        Evento evento = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Evento con ID " + id + " non trovato per l'eliminazione."));

        if (!evento.getUtente().getId().equals(currentUserId)) {
            throw new SecurityException("Non sei autorizzato a eliminare questo evento.");
        }

        repository.deleteById(id);
    }

    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailServiceImpl.UserPrincipal userDetails) {
            return userDetails.getId();
        }
        throw new SecurityException("Utente non autenticato");
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> findAllByUtenteId(Long utenteId) {
        return repository.findAllByUtenteId(utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> findAllByDataInizioBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findAllByDataInizioBetween(start, end)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> findAllByStato(EventoStato stato) {
        return repository.findAllByStato(stato)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> findAllByStatoAndUtenteId(String stato, Long utenteId) {
        return repository.findAllByStatoAndUtenteId(stato, utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> findAllByDataInizioAfterAndUtenteId(LocalDateTime dataInizio, Long utenteId) {
        return repository.findAllByDataInizioAfterAndUtenteId(dataInizio, utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> findAllByDataFineBeforeAndUtenteId(LocalDateTime dataFine, Long utenteId) {
        return repository.findAllByDataFineBeforeAndUtenteId(dataFine, utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    /*@Transactional(readOnly = true)
    public List<EventoDTO> findAllByDataInizioAfterAndDataFineBeforeAndUtenteId(LocalDateTime dataInizio, LocalDateTime dataFine, Long utenteId) {
        return repository.findAllByDataInizioBetweenAndDataFineBetweenAndUtenteId(dataInizio, dataFine, utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }*/

    @Transactional(readOnly = true)
    public List<EventoDTO> getEventiInDateRange(LocalDateTime dataInizio, LocalDateTime dataFine, Long utenteId) {
        return repository.findEventsInDateRange(dataInizio, dataFine, utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    /*public List<EventoDTO> getEventoByDataCreazioneAndUtenteId(LocalDate data, Long utenteId) {
        LocalDateTime startOfDay = data.atStartOfDay();
        LocalDateTime endOfDay = data.atTime(23, 59, 59, 999_999_999);

        List<Evento> eventi = repository.findAllByDataInizioAfterAndDataFineBeforeAndUtenteId(startOfDay, endOfDay, utenteId);
        return eventi.stream()
            .map(eventoMapper::toDto)
            .collect(Collectors.toList());
    }*/

    // --- NUOVO METODO PER LA RICERCA AVANZATA ---
    @Transactional(readOnly = true)
    public List<EventoDTO> ricercaEventiAvanzata(String titolo, String keywords) {
        Long currentUserId = getCurrentUserId();
        List<Evento> eventi;

        // Logica per determinare quale metodo del repository chiamare, includendo l'ID utente
        boolean hasTitolo = titolo != null && !titolo.trim().isEmpty();
        boolean hasKeywords = keywords != null && !keywords.trim().isEmpty();

        if (hasTitolo && hasKeywords) {
            // Ricerca per titolo E parole chiave
            eventi = repository.findByTitoloContainingIgnoreCaseAndDescrizioneContainingIgnoreCaseAndUtenteId(titolo, keywords, currentUserId);
        } else if (hasTitolo) {
            // Ricerca solo per titolo
            eventi = repository.findByTitoloContainingIgnoreCaseAndUtenteId(titolo, currentUserId);
        } else if (hasKeywords) {
            // Ricerca solo per parole chiave
            eventi = repository.findByDescrizioneContainingIgnoreCaseAndUtenteId(keywords, currentUserId);
        } else {
            // Se nessun parametro di ricerca, restituisci tutti gli eventi dell'utente
            eventi = repository.findAllByUtenteId(currentUserId);
        }

        return eventi.stream()
                     .map(eventoMapper::toDto)
                     .collect(Collectors.toList());
    }
}