package sviluppo.adriano.MemoFlow.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import sviluppo.adriano.MemoFlow.dto.EventoDTO;
import sviluppo.adriano.MemoFlow.dto.creaDTO.EventoCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.EventoCambiaDTO;
import sviluppo.adriano.MemoFlow.enums.EventoStato;
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

    @Operation(summary = "Trova eventi per stato")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eventi trovati")
    })
    @GetMapping("/stato")
    public ResponseEntity<List<EventoDTO>> findByStato(@RequestParam EventoStato stato) {
        List<EventoDTO> eventi = eventoService.findAllByStato(stato);
        return ResponseEntity.ok(eventi);
    }

    @Operation(summary = "Trova eventi per stato e utente corrente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eventi trovati")
    })
    @GetMapping("/stato/utente")
    public ResponseEntity<List<EventoDTO>> findByStatoAndUtente(@RequestParam String stato) {
        Long utenteId = eventoService.getCurrentUserId();
        List<EventoDTO> eventi = eventoService.findAllByStatoAndUtenteId(stato, utenteId);
        return ResponseEntity.ok(eventi);
    }

    @Operation(summary = "Trova eventi nell'intervallo di date (dataInizio)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eventi trovati")
    })
    @GetMapping("/intervallo-inizio")
    public ResponseEntity<List<EventoDTO>> findByIntervalloInizio(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EventoDTO> eventi = eventoService.findAllByDataInizioBetween(start, end);
        return ResponseEntity.ok(eventi);
    }

    @Operation(summary = "Trova eventi con dataInizio > X per utente corrente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eventi trovati")
    })
    @GetMapping("/dopo-inizio")
    public ResponseEntity<List<EventoDTO>> findByInizioAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInizio) {
        Long utenteId = eventoService.getCurrentUserId();
        List<EventoDTO> eventi = eventoService.findAllByDataInizioAfterAndUtenteId(dataInizio, utenteId);
        return ResponseEntity.ok(eventi);
    }

    @Operation(summary = "Trova eventi con dataFine < X per utente corrente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eventi trovati")
    })
    @GetMapping("/prima-fine")
    public ResponseEntity<List<EventoDTO>> findByFineBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFine) {
        Long utenteId = eventoService.getCurrentUserId();
        List<EventoDTO> eventi = eventoService.findAllByDataFineBeforeAndUtenteId(dataFine, utenteId);
        return ResponseEntity.ok(eventi);
    }

    @GetMapping("/tra-due-date")
    public ResponseEntity<List<EventoDTO>> getEventiBetweenDates(
            @RequestParam("inizio") String inizioStr,
            @RequestParam("fine") String fineStr,
            @RequestParam("userId") Long userId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            LocalDateTime inizio = LocalDateTime.parse(inizioStr, formatter);
            LocalDateTime fine = LocalDateTime.parse(fineStr, formatter);

            List<EventoDTO> eventi = eventoService.getEventiInDateRange(inizio, fine, userId);
            return ResponseEntity.ok(eventi);
        } catch (Exception e) {
            System.err.println("Errore nel recupero eventi tra due date: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }


    // --- NUOVO ENDPOINT PER LA RICERCA AVANZATA ---
    @Operation(
            summary = "Ricerca avanzata eventi per utente corrente",
            description = "Cerca eventi per titolo o parole chiave (descrizione) per l'utente autenticato. I parametri sono opzionali."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventi trovati con successo"),
            @ApiResponse(responseCode = "204", description = "Nessun evento trovato con i criteri specificati"),
            @ApiResponse(responseCode = "403", description = "Utente non autenticato o non autorizzato")
    })
    @GetMapping("/ricercaAvanzata")
    public ResponseEntity<List<EventoDTO>> ricercaAvanzata(
            @RequestParam(required = false) String titolo,
            @RequestParam(required = false) String keywords) {
        try {
            List<EventoDTO> risultati = eventoService.ricercaEventiAvanzata(titolo, keywords);
            if (risultati.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(risultati);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Gestisce il caso di utente non autenticato
        } catch (Exception e) {
            System.err.println("Errore durante la ricerca avanzata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    @GetMapping("/eventi-del-mese")
    public ResponseEntity<List<EventoDTO>> getMonthlyEvents(
            @RequestParam("inizioMese") String inizioMeseStr,
            @RequestParam("fineMese") String fineMeseStr) {

        Long userId = eventoService.getCurrentUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        LocalDateTime inizioMese = LocalDateTime.parse(inizioMeseStr, formatter);
        LocalDateTime fineMese = LocalDateTime.parse(fineMeseStr, formatter);

        List<EventoDTO> eventi = eventoService.getMonthlyEvents(inizioMese, fineMese, userId);
        return ResponseEntity.ok(eventi);

    }
}