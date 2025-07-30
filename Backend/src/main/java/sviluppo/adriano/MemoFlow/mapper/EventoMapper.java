package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import sviluppo.adriano.MemoFlow.dto.EventoDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.EventoCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.EventoCambiaDTO;
import sviluppo.adriano.MemoFlow.entity.Evento;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.enums.EventoStato;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    EventoMapper INSTANCE = Mappers.getMapper(EventoMapper.class);

    @Mapping(source = "utente", target = "utenteDTO")
    @Mapping(source = "stato", target = "stato", qualifiedByName = "statoToString")
    EventoDTO toDto(Evento entity);

    @Mapping(source = "stato", target = "stato", qualifiedByName = "stringToStato")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utente", ignore = true)
    Evento toEntity(EventoDTO dto); // Questo mapper da DTO a Entity non dovrebbe essere per la creazione, ma per aggiornamenti forse.

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stato", qualifiedByName = "stringToStato")
    //@Mapping(source = "utenteId", target = "utente", qualifiedByName = "idToUtente") // Commentato
    Evento toEntity(EventoCreateDTO dto); // Mappa da EventoCreateDTO a Evento Entity

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utente", ignore = true)
    @Mapping(target = "stato", qualifiedByName = "stringToStato")
    Evento toEntity(EventoCambiaDTO dto);

    @Named("statoToString")
    default String mapStatoToString(EventoStato stato) {
        return stato == null ? null : stato.name();
    }

    @Named("stringToStato")
    default EventoStato mapStringToStato(String stato) {
        if (stato == null) {
            return null;
        }
        try {
            return EventoStato.valueOf(stato);
        } catch (IllegalArgumentException e) {
            return null; // o gestisci l'eccezione come preferisci
        }
    }

    @Named("idToUtente")
    default Utente idToUtente(Long utenteId) {
        if (utenteId == null) return null;
        Utente u = new Utente();
        u.setId(utenteId);
        return u;
    }

}