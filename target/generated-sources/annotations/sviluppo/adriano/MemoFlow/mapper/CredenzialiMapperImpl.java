package sviluppo.adriano.MemoFlow.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-16T15:23:50+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class CredenzialiMapperImpl implements CredenzialiMapper {

    @Override
    public CredenzialiDTO toDto(Credenziali credenziali) {
        if ( credenziali == null ) {
            return null;
        }

        CredenzialiDTO credenzialiDTO = new CredenzialiDTO();

        credenzialiDTO.setId( credenziali.getId() );
        credenzialiDTO.setEmail( credenziali.getEmail() );
        credenzialiDTO.setPassword( credenziali.getPassword() );

        return credenzialiDTO;
    }

    @Override
    public Credenziali toEntity(CredenzialiDTO dto) {
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
