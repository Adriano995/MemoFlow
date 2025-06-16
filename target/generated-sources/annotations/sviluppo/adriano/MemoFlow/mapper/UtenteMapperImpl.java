package sviluppo.adriano.MemoFlow.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-16T15:43:56+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class UtenteMapperImpl implements UtenteMapper {

    @Override
    public UtenteDTO toDto(Utente utente) {
        if ( utente == null ) {
            return null;
        }

        UtenteDTO utenteDTO = new UtenteDTO();

        utenteDTO.setCredenzialiId( utenteCredenzialiId( utente ) );
        utenteDTO.setId( utente.getId() );
        utenteDTO.setNome( utente.getNome() );
        utenteDTO.setCognome( utente.getCognome() );

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
    public void updateUtenteFromDto(UtenteDTO dto, Utente entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setNome( dto.getNome() );
        entity.setCognome( dto.getCognome() );
    }

    private Long utenteCredenzialiId(Utente utente) {
        if ( utente == null ) {
            return null;
        }
        Credenziali credenziali = utente.getCredenziali();
        if ( credenziali == null ) {
            return null;
        }
        Long id = credenziali.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
