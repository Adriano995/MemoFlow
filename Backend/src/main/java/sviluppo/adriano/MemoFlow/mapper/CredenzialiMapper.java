package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;

@Mapper(componentModel = "spring")
public interface CredenzialiMapper {

    @Mapping(target = "utenteId", ignore = true)
    CredenzialiDTO toDto(Credenziali entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utente", ignore = true)
    Credenziali toEntity(CredenzialiCreateDTO dto);

    @Mapping(target = "utente", ignore = true)
    void updateCredenzialiFromDto(CredenzialiDTO dto, @MappingTarget Credenziali entity);
}