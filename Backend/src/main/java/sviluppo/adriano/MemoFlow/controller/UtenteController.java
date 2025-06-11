package sviluppo.adriano.MemoFlow.controller;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.service.UtenteService;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

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
}
