package sviluppo.adriano.MemoFlow.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.NotaDTO;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.NotaCreateDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Nota;
import sviluppo.adriano.MemoFlow.entity.Utente;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-22T12:17:29+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class NotaMapperImpl implements NotaMapper {

    @Override
    public NotaDTO toDto(Nota entity) {
        if ( entity == null ) {
            return null;
        }

        NotaDTO notaDTO = new NotaDTO();

        notaDTO.setUtenteDTO( utenteToUtenteDTO( entity.getUtente() ) );
        notaDTO.setId( entity.getId() );
        notaDTO.setTitolo( entity.getTitolo() );
        notaDTO.setTipoNota( entity.getTipoNota() );
        notaDTO.setContenutoTesto( entity.getContenutoTesto() );
        notaDTO.setContenutoSVG( entity.getContenutoSVG() );
        notaDTO.setDataCreazione( entity.getDataCreazione() );
        notaDTO.setUltimaModifica( entity.getUltimaModifica() );

        return notaDTO;
    }

    @Override
    public Nota toEntity(NotaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Nota nota = new Nota();

        nota.setUtente( utenteDTOToUtente( dto.getUtenteDTO() ) );
        nota.setTitolo( dto.getTitolo() );
        nota.setTipoNota( dto.getTipoNota() );
        nota.setContenutoTesto( dto.getContenutoTesto() );
        nota.setContenutoSVG( dto.getContenutoSVG() );
        nota.setDataCreazione( dto.getDataCreazione() );
        nota.setUltimaModifica( dto.getUltimaModifica() );

        return nota;
    }

    @Override
    public Nota toEntity(NotaCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Nota nota = new Nota();

        nota.setTitolo( dto.getTitolo() );
        nota.setTipoNota( dto.getTipoNota() );
        nota.setContenutoTesto( dto.getContenutoTesto() );
        nota.setContenutoSVG( dto.getContenutoSVG() );

        return nota;
    }

    @Override
    public void updateNotaFromDto(NotaDTO dto, Nota entity) {
        if ( dto == null ) {
            return;
        }

        entity.setId( dto.getId() );
        entity.setTitolo( dto.getTitolo() );
        entity.setTipoNota( dto.getTipoNota() );
        entity.setContenutoTesto( dto.getContenutoTesto() );
        entity.setContenutoSVG( dto.getContenutoSVG() );
        entity.setDataCreazione( dto.getDataCreazione() );
        entity.setUltimaModifica( dto.getUltimaModifica() );
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

    protected UtenteDTO utenteToUtenteDTO(Utente utente) {
        if ( utente == null ) {
            return null;
        }

        UtenteDTO utenteDTO = new UtenteDTO();

        utenteDTO.setId( utente.getId() );
        utenteDTO.setNome( utente.getNome() );
        utenteDTO.setCognome( utente.getCognome() );
        utenteDTO.setCredenziali( credenzialiToCredenzialiDTO( utente.getCredenziali() ) );

        return utenteDTO;
    }

    protected Credenziali credenzialiDTOToCredenziali(CredenzialiDTO credenzialiDTO) {
        if ( credenzialiDTO == null ) {
            return null;
        }

        Credenziali credenziali = new Credenziali();

        credenziali.setId( credenzialiDTO.getId() );
        credenziali.setEmail( credenzialiDTO.getEmail() );
        credenziali.setPassword( credenzialiDTO.getPassword() );

        return credenziali;
    }

    protected Utente utenteDTOToUtente(UtenteDTO utenteDTO) {
        if ( utenteDTO == null ) {
            return null;
        }

        Utente utente = new Utente();

        utente.setId( utenteDTO.getId() );
        utente.setNome( utenteDTO.getNome() );
        utente.setCognome( utenteDTO.getCognome() );
        utente.setCredenziali( credenzialiDTOToCredenziali( utenteDTO.getCredenziali() ) );

        return utente;
    }
}
