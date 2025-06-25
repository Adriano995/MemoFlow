package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import sviluppo.adriano.MemoFlow.dto.creaDTO.NotaCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.CambiaNotaDTO;
import sviluppo.adriano.MemoFlow.dto.NotaDTO;
import sviluppo.adriano.MemoFlow.service.NotaService;

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
            summary = "Recupera la lista di tutte le note",
            description = "Restituisce una lista di tutte le note salvate nel sistema. Se non ci sono note, restituisce 204 No Content."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista note recuperata con successo"),
            @ApiResponse(responseCode = "204", description = "Nessuna nota trovata")
    })
    public ResponseEntity<List<NotaDTO>> cercaTutte(){
        List<NotaDTO> note = notaService.cercaTutte();

        if( note.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(note);
    }

    @Operation(
            summary = "Recupera la nota per una data specifica",
            description = "Restituisce la nota creata in una data specifica (YYYY-MM-DD)."
    )
    @GetMapping("/perData")
    public ResponseEntity<List<NotaDTO>> cercaTuttePerData(@RequestParam("data") String data) {
        try {
            LocalDateTime giorno = LocalDateTime.parse(data + "T00:00:00");
            List<NotaDTO> note = notaService.cercaTuttePerData(giorno);
            if (note.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Crea una nuova nota",
            description = "Crea una nuova nota associata a un utente esistente, specificato da utenteId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi o utente non trovato")
    })
    @PostMapping("/creaNota")
    public ResponseEntity<NotaDTO> creaNota(@RequestBody NotaCreateDTO nuovaNota) {
        NotaDTO nota = notaService.creaNota(nuovaNota);
        return ResponseEntity.status(HttpStatus.CREATED).body(nota);
    }

    @Operation(
            summary = "Aggiorna una nota con i dati forniti",
            description = "Aggiorna i campi titolo, contenutoTesto e tipoNota della nota specificata dall'ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota aggiornata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Accesso negato"),
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Elimina una nota tramite ID",
            description = "Elimina la nota corrispondente all'ID specificato."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nota eliminata con successo"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata")
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
