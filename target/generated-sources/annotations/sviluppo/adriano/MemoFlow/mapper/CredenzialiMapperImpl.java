package sviluppo.adriano.MemoFlow.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-25T11:11:36+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class CredenzialiMapperImpl implements CredenzialiMapper {

    @Override
    public CredenzialiDTO toDto(Credenziali entity) {
        if ( entity == null ) {
            return null;
        }

        CredenzialiDTO credenzialiDTO = new CredenzialiDTO();

        credenzialiDTO.setId( entity.getId() );
        credenzialiDTO.setEmail( entity.getEmail() );
        credenzialiDTO.setPassword( entity.getPassword() );

        return credenzialiDTO;
    }

    @Override
    public Credenziali toEntity(CredenzialiCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Credenziali credenziali = new Credenziali();

        credenziali.setEmail( dto.getEmail() );
        credenziali.setPassword( dto.getPassword() );

        return credenziali;
    }

    @Override
    public void updateCredenzialiFromDto(CredenzialiDTO dto, Credenziali entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setEmail( dto.getEmail() );
        entity.setPassword( dto.getPassword() );
    }
}
