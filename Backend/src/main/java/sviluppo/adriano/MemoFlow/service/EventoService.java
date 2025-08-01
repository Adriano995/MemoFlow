package sviluppo.adriano.MemoFlow.service;

import java.time.LocalDateTime;
import java.util.List;

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
        if (createDto.getDataFine() != null && createDto.getDataFine().isBefore(createDto.getDataInizio())) {
            throw new IllegalArgumentException("La data di fine non può essere precedente alla data di inizio.");
        }
        
        Evento evento = toEntity(createDto);
        Utente utente = new Utente();
        utente.setId(getCurrentUserId());
        evento.setUtente(utente);
        Evento salvato = repository.save(evento);
        return toDto(salvato);
    }

    @Override
    protected void updateEntity(Evento entity, EventoCambiaDTO updateDto) {
        LocalDateTime dataInizio = entity.getDataInizio();
        LocalDateTime dataFine = entity.getDataFine();

        if (updateDto.getDataInizio() != null) {
            dataInizio = updateDto.getDataInizio();
        }
        if (updateDto.getDataFine() != null) {
            dataFine = updateDto.getDataFine();
        }

        if (dataFine != null && dataFine.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data di fine non può essere precedente alla data di inizio.");
        }
        
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

    @Transactional(readOnly = true)
    public List<EventoDTO> getEventiInDateRange(LocalDateTime dataInizio, LocalDateTime dataFine, Long utenteId) {
        return repository.findEventsInDateRange(dataInizio, dataFine, utenteId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> getMonthlyEvents(LocalDateTime inizioMese, LocalDateTime fineMese, Long utenteId) {
        return repository.findMonthlyEvents(inizioMese, fineMese, utenteId).stream()
                .map(eventoMapper::toDto)
                .toList();
    }
}
