package sviluppo.adriano.MemoFlow.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
    
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token; // UUID o hash

    private LocalDateTime dataCreazione;
    private LocalDateTime dataScadenza;

    private boolean usato;

    @OneToOne
    private Credenziali credenziali;

    public PasswordResetToken(){}

    public PasswordResetToken(UUID id, String token, LocalDateTime dataCreazione, LocalDateTime dataScadenza){
        this.id = id;
        this.token = token;
        this.dataCreazione = dataCreazione;
        this.dataScadenza = dataScadenza;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDateTime dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public boolean isUsato() {
        return usato;
    }

    public void setUsato(boolean usato) {
        this.usato = usato;
    }

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
    }

    
}
