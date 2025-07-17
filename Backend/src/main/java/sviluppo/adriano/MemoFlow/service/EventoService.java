package sviluppo.adriano.MemoFlow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

  /*  @Override
    public List<EventoDTO> findAll() {
        Long currentUserId = getCurrentUserId();
        return repository.findByUtenteId(currentUserId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }*/

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

        if (updateDto.getOraInizio() != null) {
            entity.setOraInizio(updateDto.getOraInizio());
        }

        if (updateDto.getOraFine() != null) {
            entity.setOraFine(updateDto.getOraFine());
        }

        if (updateDto.getLuogo() != null) {
            entity.setLuogo(updateDto.getLuogo());
        }

        if (updateDto.getStato() != null) {
            try {
                entity.setStato(Enum.valueOf(EventoStato.class, updateDto.getStato()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valore di stato non valido: " + updateDto.getStato());
            }
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


    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailServiceImpl.UserPrincipal userDetails) {
            return userDetails.getId();
        }
        throw new SecurityException("Utente non autenticato");
    }

}
