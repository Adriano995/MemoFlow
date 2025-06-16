package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.service.UtenteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping("/utenti")
    public ResponseEntity<List<Utente>> cercaTutti() {
        List<Utente> utenti = utenteService.cercaTutti();
        if (utenti.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        return ResponseEntity.ok(utenti);
    }


    /*@GetMapping("/current")
    public ResponseEntity<Utente> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Utente user = utenteService.findByEmail(email);
        return ResponseEntity.ok(user);
    }*/

    @Operation(summary = "Crea un nuovo utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente creato con successo"),
    })
    @Transactional
    @PostMapping("/crea")
    public ResponseEntity<?> creaUtente(@RequestBody Utente utenteInput) {
//        utenteService.creaUtente(null); // Ignora input per ora
//
        try{
            Utente nuovoUtente = utenteService.creaUtente(utenteInput);
            return  ResponseEntity.ok(nuovoUtente);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Aggiorna un utente con i dati forniti")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato"),
            @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    @PutMapping("/aggiorna/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Utente updatedUser = utenteService.aggiornaUtente(id, updates);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
