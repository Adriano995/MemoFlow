package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;

@Mapper(componentModel = "spring")
public interface CredenzialiMapper {

    @Mapping(target = "utenteId", ignore = true) // mappiamo utente solo nel service
    CredenzialiDTO toDto(Credenziali entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utente", ignore = true) // da settare nel service, non qui
    Credenziali toEntity(CredenzialiCreateDTO dto);

    void updateCredenzialiFromDto(CredenzialiDTO dto, @MappingTarget Credenziali entity);
}