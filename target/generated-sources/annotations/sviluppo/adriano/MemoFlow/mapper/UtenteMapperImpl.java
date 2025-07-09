package sviluppo.adriano.MemoFlow.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.CredenzialiCreateDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-08T11:48:01+0200",
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

        utenteDTO.setEmail( entityCredenzialiEmail( entity ) );
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

        utente.setCredenziali( credenzialiCreateDTOToCredenziali( dto.getCredenziali() ) );
        utente.setNome( dto.getNome() );
        utente.setCognome( dto.getCognome() );

        return utente;
    }

    @Override
    public void updateUtenteFromDto(UtenteDTO dto, Utente entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNome( dto.getNome() );
        entity.setCognome( dto.getCognome() );
    }

    private String entityCredenzialiEmail(Utente utente) {
        if ( utente == null ) {
            return null;
        }
        Credenziali credenziali = utente.getCredenziali();
        if ( credenziali == null ) {
            return null;
        }
        String email = credenziali.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
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
}
