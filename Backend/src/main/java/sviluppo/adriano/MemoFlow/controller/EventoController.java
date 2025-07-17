package sviluppo.adriano.MemoFlow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import sviluppo.adriano.MemoFlow.dto.EventoDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.EventoCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.EventoCambiaDTO;
import sviluppo.adriano.MemoFlow.service.EventoService;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @Operation(
        summary = "Lista di tutti gli eventi", 
        description = "Restituisce la lista completa degli eventi salvati."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista eventi restituita"),
            @ApiResponse(responseCode = "204", description = "Nessun evento trovato")
    })
    @GetMapping("/listaTutti")
    public ResponseEntity<List<EventoDTO>> getAll() {
        List<EventoDTO> eventi = eventoService.findAll();
        return eventi.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(eventi);
    }

    @Operation(
        summary = "Singolo evento", 
        description = "Restituisce un evento dato il suo ID."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento trovato"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato")
    })
    @GetMapping("/eventoSingolo/{id}")
    public ResponseEntity<EventoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.findById(id));
    }

    @Operation(
        summary = "Crea nuovo evento", 
        description = "Crea un nuovo evento a partire dai dati forniti."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    @PostMapping("/creaEvento")
    public ResponseEntity<EventoDTO> create(@RequestBody EventoCreateDTO dto) {
        EventoDTO creato = eventoService.create(dto);
        return new ResponseEntity<>(creato, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Aggiorna un evento", 
        description = "Aggiorna un evento esistente a partire dal suo ID."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento aggiornato con successo"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato")
    })
    @PutMapping("/modificaEvento/{id}")
    public ResponseEntity<EventoDTO> update(@PathVariable Long id, @RequestBody EventoCambiaDTO dto) {
        return ResponseEntity.ok(eventoService.update(id, dto));
    }

    @Operation(
        summary = "Elimina un evento", 
        description = "Elimina un evento dato il suo ID."
        )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento eliminato"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato")
    })
    @DeleteMapping("/eliminaEvento/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
