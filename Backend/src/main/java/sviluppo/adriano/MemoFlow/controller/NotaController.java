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
            description = "Restituisce una lista di tutte le note salvate nel sistema. Se non ci sono note, restituisce una lista vuota."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di note recuperata con successo")
    })
    @GetMapping("/listaTutte")
    public ResponseEntity<List<NotaDTO>> cercaTutte() {
        List<NotaDTO> note = notaService.cercaTutte();
        return ResponseEntity.ok(note);
    }

    @Operation(
            summary = "Recupera una nota tramite ID",
            description = "Restituisce i dettagli di una nota specifica tramite il suo ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotaDTO> cercaPerId(@Parameter(description = "ID della nota da recuperare") @PathVariable Long id) {
        try {
            NotaDTO nota = notaService.cercaPerId(id);
            return ResponseEntity.ok(nota);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "Crea una nuova nota",
            description = "Crea una nuova nota con i dettagli forniti. L'utente viene associato tramite utenteId nel DTO."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @PostMapping("/creaNota")
    public ResponseEntity<NotaDTO> creaNota(@RequestBody NotaCreateDTO createDto) {
        try {
            NotaDTO nuovaNota = notaService.creaNota(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovaNota);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // O un messaggio di errore più specifico
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(
            summary = "Recupera le note per data di creazione e ID utente",
            description = "Restituisce una lista di note create in una data specifica per un dato utente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note recuperate con successo"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato o note non trovate per la data specificata")
    })
    @GetMapping("/perDataEUtente")
    public ResponseEntity<List<NotaDTO>> getNoteByDataCreazioneAndUtenteId(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, // LocalDate per ricevere solo la data
            @RequestParam Long utenteId) {
        List<NotaDTO> note = notaService.getNoteByDataCreazioneAndUtenteId(data, utenteId);
        return ResponseEntity.ok(note);
    }

    @Operation(
            summary = "Recupera tutte le note di un utente",
            description = "Restituisce tutte le note associate a un determinato utente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note recuperate con successo"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato o nessuna nota trovata")
    })
    @GetMapping("/perUtente")
    public ResponseEntity<List<NotaDTO>> getNoteByUtenteId(@RequestParam Long utenteId) {
        List<NotaDTO> note = notaService.getNoteByUtenteId(utenteId);
        return ResponseEntity.ok(note);
    }

    @Operation(
            summary = "Aggiorna una nota esistente tramite ID",
            description = "Aggiorna i campi specificati di una nota. Solo i campi presenti nel body della richiesta verranno modificati."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota aggiornata con successo"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: l'utente non è autorizzato a modificare questa nota")
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
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}