package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.CreaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Utente;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    @Mapping(target = "credenziali", source = "credenziali")
    UtenteDTO toDto(Utente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credenziali", ignore = true)
    Utente toEntity(UtenteDTO dto);

    Utente toEntity(UtenteCreateDTO dto);

    void updateUtenteFromDto(UtenteDTO dto, @MappingTarget Utente entity);
}
