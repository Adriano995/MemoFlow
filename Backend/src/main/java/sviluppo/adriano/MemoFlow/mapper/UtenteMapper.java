package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Utente;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    @Mapping(target = "credenzialiId", source = "credenziali.id")
    UtenteDTO toDto(Utente utente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credenziali", ignore = true)
    Utente toEntity(UtenteDTO dto);

    void updateUtenteFromDto(UtenteDTO dto, @MappingTarget Utente entity);
}
