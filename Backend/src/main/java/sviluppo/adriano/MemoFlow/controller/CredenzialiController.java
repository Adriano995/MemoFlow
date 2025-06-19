package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.dto.credenzialiDTO.CambiaEmailDTO;
import sviluppo.adriano.MemoFlow.dto.credenzialiDTO.CambiaPasswordDTO;
import sviluppo.adriano.MemoFlow.response.MessageResponse;
import sviluppo.adriano.MemoFlow.service.CredenzialiService;

@RestController
@RequestMapping("/credenziali")
public class CredenzialiController {

    @Autowired
    private CredenzialiService credenzialiService;

    @Operation(summary = "Cambia la password dell'utente loggato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password aggiornata con successo"),
            @ApiResponse(responseCode = "400", description = "Password vecchia errata o dati non validi")
    })
    @PostMapping("/cambiaPassword")
    public ResponseEntity<?> cambiaPassword(@RequestBody CambiaPasswordDTO dto) {
        try {
            CredenzialiDTO aggiornata = credenzialiService.cambiaPassword(dto);
            return ResponseEntity.ok(new MessageResponse("Password aggiornata per " + aggiornata.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @Operation(summary = "Cambia l'email dell'utente loggato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email aggiornata con successo"),
            @ApiResponse(responseCode = "400", description = "Email vecchia errata o dati non validi")
    })
    @PostMapping("/cambiaEmail")
    public ResponseEntity<?> cambiaEmail(@RequestBody CambiaEmailDTO dto) {
        try {
            Long utenteId = dto.getUtenteId(); // ✅ lo prendi direttamente dal JSON
            CredenzialiDTO aggiornata = credenzialiService.cambiaEmail(utenteId, dto);
            return ResponseEntity.ok(new MessageResponse("Email aggiornata con successo per " + aggiornata.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
