package sviluppo.adriano.MemoFlow.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-25T11:11:36+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class UtenteMapperImpl implements UtenteMapper {

    @Override
    public UtenteDTO toDto(Utente entity) {
        if ( entity == null ) {
            return null;
        }

        UtenteDTO utenteDTO = new UtenteDTO();

        utenteDTO.setCredenziali( credenzialiToCredenzialiDTO( entity.getCredenziali() ) );
        utenteDTO.setId( entity.getId() );
        utenteDTO.setNome( entity.getNome() );
        utenteDTO.setCognome( entity.getCognome() );

        return utenteDTO;
    }

    @Override
    public Utente toEntity(UtenteDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Utente utente = new Utente();

        utente.setNome( dto.getNome() );
        utente.setCognome( dto.getCognome() );

        return utente;
    }

    @Override
    public Utente toEntity(UtenteCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Utente utente = new Utente();

        utente.setNome( dto.getNome() );
        utente.setCognome( dto.getCognome() );
        utente.setCredenziali( credenzialiCreateDTOToCredenziali( dto.getCredenziali() ) );

        return utente;
    }

    @Override
    public void updateUtenteFromDto(UtenteDTO dto, Utente entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setNome( dto.getNome() );
        entity.setCognome( dto.getCognome() );
        if ( dto.getCredenziali() != null ) {
            if ( entity.getCredenziali() == null ) {
                entity.setCredenziali( new Credenziali() );
            }
            credenzialiDTOToCredenziali( dto.getCredenziali(), entity.getCredenziali() );
        }
        else {
            entity.setCredenziali( null );
        }
    }

    protected CredenzialiDTO credenzialiToCredenzialiDTO(Credenziali credenziali) {
        if ( credenziali == null ) {
            return null;
        }

        CredenzialiDTO credenzialiDTO = new CredenzialiDTO();

        credenzialiDTO.setId( credenziali.getId() );
        credenzialiDTO.setEmail( credenziali.getEmail() );
        credenzialiDTO.setPassword( credenziali.getPassword() );

        return credenzialiDTO;
    }

    protected Credenziali credenzialiCreateDTOToCredenziali(CredenzialiCreateDTO credenzialiCreateDTO) {
        if ( credenzialiCreateDTO == null ) {
            return null;
        }

        Credenziali credenziali = new Credenziali();

        credenziali.setEmail( credenzialiCreateDTO.getEmail() );
        credenziali.setPassword( credenzialiCreateDTO.getPassword() );

        return credenziali;
    }

    protected void credenzialiDTOToCredenziali(CredenzialiDTO credenzialiDTO, Credenziali mappingTarget) {
        if ( credenzialiDTO == null ) {
            return;
        }

        mappingTarget.setId( credenzialiDTO.getId() );
        mappingTarget.setEmail( credenzialiDTO.getEmail() );
        mappingTarget.setPassword( credenzialiDTO.getPassword() );
    }
}
