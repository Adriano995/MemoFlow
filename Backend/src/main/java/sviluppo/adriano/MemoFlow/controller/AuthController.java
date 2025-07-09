// File: sviluppo/adriano/MemoFlow/controller/AuthController.java
package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sviluppo.adriano.MemoFlow.dto.LoginDTO;
// Non useremo più Credenziali direttamente qui per la risposta del login JWT
// import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.security.jwt.JwtUtil; // Importa il tuo JwtUtil
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl; // O CustomUserDetailsService
import sviluppo.adriano.MemoFlow.response.JwtResponse; // Creeremo questa classe DTO per la risposta JWT

// Potresti non aver più bisogno di AuthService.login se gestisci l'autenticazione qui
// import sviluppo.adriano.MemoFlow.service.AuthService;


import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/auth")
public class AuthController {

    // Non avrai più bisogno di AuthService per il login diretto, Spring Security lo gestisce
    // @Autowired
    // private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager; // Per autenticare l'utente

    @Autowired
    private JwtUtil jwtUtil; // Per generare il token JWT

    @Autowired
    private UserDetailServiceImpl userDetailsService; // Per recuperare i dettagli utente completi (solo se necessario per la risposta, altrimenti non serve iniettarlo qui)


    @Operation(
            summary = "Effettua il login dell'utente",
            description = "Autentica l'utente tramite email e password. Se le credenziali sono corrette, restituisce un token JWT e i dati dell'utente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login effettuato con successo"),
            @ApiResponse(responseCode = "401", description = "Credenziali non valide"),
            @ApiResponse(responseCode = "400", description = "Dati della richiesta non validi")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            // 1. Autentica l'utente tramite AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

            // 2. Imposta l'oggetto Authentication nel SecurityContextHolder
            // Questo è importante per l'autorizzazione successiva nella stessa richiesta
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Genera il token JWT
            String jwt = jwtUtil.generateJwtToken(authentication);

            // 4. Prepara la risposta JWT con il token e i dettagli dell'utente
            // Puoi recuperare i dettagli dell'utente dal Authentication object
            // o ricaricarli con userDetailsService se hai bisogno di più dati non contenuti nell'Authentication
            // Per la risposta, useremo un JwtResponse DTO.
            UserDetailServiceImpl.UserPrincipal userDetails =
                    (UserDetailServiceImpl.UserPrincipal) authentication.getPrincipal(); // Casting all'oggetto UserPrincipal che hai creato nel UserDetailServiceImpl

            // Estrai i ruoli in formato stringa
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Costruisci la risposta usando il nuovo JwtResponse DTO
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),         // Assicurati che il tuo UserPrincipal abbia un metodo getId()
                    userDetails.getUsername(),   // Sarà l'email
                    roles
            ));

        } catch (org.springframework.security.core.AuthenticationException e) {
            // Cattura le eccezioni di autenticazione (es. BadCredentialsException)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali non valide: " + e.getMessage());
        } catch (Exception e) {
            // Cattura altre eccezioni impreviste
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante il login: " + e.getMessage());
        }
    }

    // Qui potresti aggiungere altri endpoint come /register
    /*
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        // Logica per la registrazione di un nuovo utente
        // Utilizza il tuo UtenteService per salvare l'utente e le credenziali con password hashata
        // Returna una MessageResponse o un UtenteDTO
    }
    */
}