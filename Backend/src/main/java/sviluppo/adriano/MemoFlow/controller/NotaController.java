package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import sviluppo.adriano.MemoFlow.dto.UtenteDTO;
import sviluppo.adriano.MemoFlow.entity.Nota;
import sviluppo.adriano.MemoFlow.service.NotaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nota")
public class NotaController {

    private NotaService notaService;

    public NotaController(NotaService notaService){
        this.notaService = notaService;
    }

    @Operation(summary = "Recupera la lista di tutte le note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista note recuperata con successo"),
            @ApiResponse(responseCode = "204", description = "Nessuna nota trovato")
    })
    @GetMapping("/listaNote")
    public ResponseEntity<List<Nota>> cercaTutto(){
        List<Nota> note = notaService.cercaTutto();

        if( note.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(note);
    }

    @Operation(summary = "Crea una nuova nota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota creata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    @PostMapping("/creaNota")
    public ResponseEntity<Nota> creaNota(@RequestBody Nota nuovaNota){
        Nota nota = notaService.creaNota(nuovaNota);

        return ResponseEntity.ok(nota);
    }

    @Operation(summary = "Aggiorna una nota con i dati forniti")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota aggiornata con successo"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Accesso negato")
    })
    @PutMapping("/aggiornaNota/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Nota updatdeNota = notaService.aggiornaNota(id, updates);
            return ResponseEntity.ok(updatdeNota);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nota non trovata");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Elimina una nota tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nota eliminata con successo"),
            @ApiResponse(responseCode = "404", description = "Nota non trovata")
    })
    @DeleteMapping("/eliminaNota/{id}")
    public ResponseEntity<String> eliminaUtente(@PathVariable Long id) {
        {
            try {
                notaService.eliminaNota(id);
                return ResponseEntity.noContent().build();
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

    }
}
