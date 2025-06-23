package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.creaDTO.NotaCreateDTO;
import sviluppo.adriano.MemoFlow.dto.NotaDTO;
import sviluppo.adriano.MemoFlow.entity.Nota;

@Mapper(componentModel = "spring")
public interface NotaMapper {

    @Mapping(source = "utente", target = "utenteDTO")
    NotaDTO toDto(Nota entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "utenteDTO", target = "utente")
    Nota toEntity(NotaDTO dto);

    Nota toEntity(NotaCreateDTO dto);

    void updateNotaFromDto(NotaDTO dto, @MappingTarget Nota entity);
}
