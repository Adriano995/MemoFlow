package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import sviluppo.adriano.MemoFlow.dto.creaDTO.NotaCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.CambiaNotaDTO;
import sviluppo.adriano.MemoFlow.dto.NotaDTO;
import sviluppo.adriano.MemoFlow.service.NotaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/nota")
public class NotaController {

    private NotaService notaService;

    public NotaController(NotaService notaService){
        this.notaService = notaService;
    }

    @Operation(
            summary = "Recupera la lista di tutte le note (senza filtro utente)",
            description = "Restituisce una lista di tutte le note salvate nel sistema. Se non ci sono note, restituisce 204 No Content."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista note recuperata con successo"),
            @ApiResponse(responseCode = "204", description = "Nessuna nota trovata")
    })
    @GetMapping("/listaTutte")
    public ResponseEntity<List<NotaDTO>> cercaTutte(){
        List<NotaDTO> note = notaService.cercaTutte();
        if( note.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(note);
    }

    @Operation(
            summary = "Recupera le note di un utente per una data specifica",
            description = "Restituisce tutte le note create dall'utente specificato in una data specifica (formato: YYYY-MM-DD)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note recuperate con successo"),
            @ApiResponse(responseCode = "204", description = "Nessuna nota trovata per la data e l'utente specificati"),
            @ApiResponse(responseCode = "400", description = "Formato data o ID utente non valido"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @GetMapping("/perDataEUtente") // Ho cambiato il path per chiarezza
    public ResponseEntity<List<NotaDTO>> cercaTuttePerDataEUtente(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, //
            @RequestParam("utenteId") Long utenteId) { // Aggiungi l'ID utente come parametro
        try {
            // NotaService.cercaTuttePerDataEUtente richiede un LocalDateTime
            List<NotaDTO> note = notaService.cercaTuttePerDataEUtente(data.atStartOfDay(), utenteId); //
            if (note.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(note);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Utente non trovato
        } catch (Exception e) {
            System.err.println("Errore durante la ricerca delle note per data e utente: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Crea una nuova nota",
            description = "Crea una nuova nota associata a un utente esistente, specificato da utenteId nel DTO. L'utenteId è fondamentale per associare la nota al proprietario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi o utente non trovato (verificare che utenteId esista)"),
            @ApiResponse(responseCode = "404", description = "Utente specificato da utenteId non trovato")
    })
    @PostMapping("/creaNota")
    public ResponseEntity<NotaDTO> creaNota(@RequestBody NotaCreateDTO nuovaNota) {
        try {
            NotaDTO nota = notaService.creaNota(nuovaNota);
            return ResponseEntity.status(HttpStatus.CREATED).body(nota);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(
            summary = "Aggiorna una nota con i dati forniti",
            description = "Aggiorna i campi titolo, contenutoTesto e tipoNota della nota specificata dall'ID. L'utente deve essere il proprietario della nota."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota aggiornata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: l'utente non è autorizzato a modificare questa nota"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata")
    })
    @PutMapping("/aggiornaNota/{id}")
    public ResponseEntity<?> aggiornaNota(@PathVariable Long id, @RequestBody CambiaNotaDTO modificaDto) {
        try {
            NotaDTO updatedNota = notaService.aggiornaNota(id, modificaDto);
            return ResponseEntity.ok(updatedNota);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nota non trovata");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Elimina una nota tramite ID",
            description = "Elimina la nota corrispondente all'ID specificato. L'utente deve essere il proprietario della nota."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nota eliminata con successo"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: l'utente non è autorizzato a eliminare questa nota")
    })
    @DeleteMapping("/eliminaNota/{id}")
    public ResponseEntity<String> eliminaNota(@PathVariable Long id) {
        try {
            notaService.eliminaNota(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}