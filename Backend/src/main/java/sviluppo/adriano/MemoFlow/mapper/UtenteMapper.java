package sviluppo.adriano.MemoFlow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sviluppo.adriano.MemoFlow.dto.creaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Utente;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    // Mappa l'email da Credenziali a UtenteDTO.email
    @Mapping(source = "entity.credenziali.email", target = "email")
    @Mapping(target = "roles", ignore = true) // I ruoli non sono diretti in Utente, ma in Authority
    // La proprietà 'credenziali' NON dovrebbe più essere in UtenteDTO dopo la modifica del DTO.
    // Se MapStruct dovesse ancora lamentarsi di 'credenziali' in UtenteDTO, aggiungi:
    // @Mapping(target = "credenziali", ignore = true)
    UtenteDTO toDto(Utente entity);

    // Quando mappi da DTO a Entity, ignora id, credenziali e ruoli.
    // L'email nel DTO non deve essere mappata a 'credenziali' qui direttamente.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credenziali", ignore = true) // Credenziali gestite a parte
    @Mapping(target = "userAuthorities", ignore = true) // I ruoli (Authority) gestiti a parte
    Utente toEntity(UtenteDTO dto);

    // Mappatura da UtenteCreateDTO a Utente entity.
    // Specifichiamo come mappare le credenziali annidate e ignoriamo le autorità.
    @Mapping(source = "credenziali.email", target = "credenziali.email")
    @Mapping(source = "credenziali.password", target = "credenziali.password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userAuthorities", ignore = true) // Le autorità sono gestite separatamente
    Utente toEntity(UtenteCreateDTO dto);

    // Metodo per aggiornare un'entità Utente da un DTO.
    // Ignoriamo id, credenziali e ruoli, in quanto questo DTO è per dati anagrafici.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credenziali", ignore = true) // Credenziali gestite a parte
    @Mapping(target = "userAuthorities", ignore = true) // I ruoli (Authority) gestiti a parte
    void updateUtenteFromDto(UtenteDTO dto, @MappingTarget Utente entity);
}