package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.UtenteCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.UtenteCambiaDatiDTO;
import sviluppo.adriano.MemoFlow.service.UtenteService;

import java.util.List;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {

        this.utenteService = utenteService;
    }

    @Operation(
            summary = "Recupera la lista di tutti gli utenti",
            description = "Restituisce una lista completa di tutti gli utenti registrati. Se non ci sono utenti, restituisce 204 No Content."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista utenti recuperata con successo"),
            @ApiResponse(responseCode = "204", description = "Nessun utente trovato")
    })
    @GetMapping("/listaUtenti")
    public ResponseEntity<List<UtenteDTO>> cercaTutti() {
        List<UtenteDTO> utenti = utenteService.cercaTutti();
        if (utenti.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        return ResponseEntity.ok(utenti);
    }

    @Operation(
            summary = "Recupera un singolo utente tramite ID",
            description = "Restituisce i dettagli di un utente specifico tramite il suo ID. Se l'utente non viene trovato, restituisce 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente recuperato con successo"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @GetMapping("/cercaSingolo/{id}")
    public ResponseEntity<UtenteDTO> cercaSingolo(@PathVariable Long id){
        UtenteDTO utente = utenteService.cercaSingolo(id);
        if (utente == null){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(utente);
    }

    /*@GetMapping("/current")
    public ResponseEntity<UtenteDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UtenteDTO user = utenteService.findByEmail(email);
        return ResponseEntity.ok(user);
    }*/


    @Operation(
            summary = "Crea un nuovo utente",
            description = "Registra un nuovo utente con credenziali associate. Email deve essere unica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi o email già esistente")
    })
    @Transactional
    @PostMapping("/creaUtente")
    public ResponseEntity<UtenteDTO> creaUtente(@RequestBody UtenteCreateDTO dto) {
        UtenteDTO creato = utenteService.creaUtente(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(creato);
    }

    @Operation(
            summary = "Aggiorna dati anagrafici dell'utente",
            description = "Aggiorna il nome e cognome di un utente specificato dall'ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato"),
            @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    @PutMapping("/aggiornaDati/{id}")
    public ResponseEntity<UtenteDTO> aggiornaDatiUtente(
            @PathVariable Long id,
            @RequestBody @Valid UtenteCambiaDatiDTO dto) {
        UtenteDTO utenteAggiornato = utenteService.aggiornaUtente(id, dto);
        return ResponseEntity.ok(utenteAggiornato);
    }


    @Operation(
            summary = "Elimina un utente tramite ID",
            description = "Elimina l'utente corrispondente all'ID specificato. Operazione irreversibile."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utente eliminato con successo"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @DeleteMapping("/eliminaUtente/{id}")
    public ResponseEntity<String> eliminaUtente(@PathVariable Long id) {
        {
            try {
                utenteService.eliminaUser(id);
                return ResponseEntity.noContent().build();
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

    }
}
