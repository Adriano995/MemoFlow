package sviluppo.adriano.MemoFlow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sviluppo.adriano.MemoFlow.dto.CredenzialiDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.service.CredenzialiService;

@RestController
@RequestMapping("/credenziali")
public class CredenzialiController {

    @Autowired
    private CredenzialiService credenzialiService;

    @PostMapping("/crea")
    public ResponseEntity<?> creaCredenziali(@RequestBody CredenzialiDTO dto) {
        try {
            Credenziali cred = credenzialiService.creaCredenziali(dto);
            return ResponseEntity.ok(cred);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

