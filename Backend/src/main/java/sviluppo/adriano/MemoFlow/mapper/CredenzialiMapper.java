package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;

@Mapper(componentModel = "spring")
public interface CredenzialiMapper {

    @Mapping(target = "utenteId", ignore = true) // mappiamo utente solo nel service
    CredenzialiDTO toDto(Credenziali credenziali);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utente", ignore = true) // da settare nel service, non qui
    Credenziali toEntity(CredenzialiDTO dto);

    void updateCredenzialiFromDto(CredenzialiDTO dto, @MappingTarget Credenziali entity);
}