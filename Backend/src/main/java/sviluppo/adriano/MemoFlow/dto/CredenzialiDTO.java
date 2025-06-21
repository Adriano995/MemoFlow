package sviluppo.adriano.MemoFlow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import sviluppo.adriano.MemoFlow.entity.Credenziali;

public class CredenzialiDTO {

    private Long id;
    private String email;

    //@JsonIgnore   -> Serve a nascondere la password nel json di risposta
    private String password;

    @JsonIgnore
    private Long utenteId;

    public CredenzialiDTO() {}

    public CredenzialiDTO(Long id, String email, String password, Long utenteId){
        this.id = id;
        this.email = email;
        this.password = password;
        this.utenteId = utenteId;
    }

    public CredenzialiDTO(Credenziali credenziali) {
        this.id = credenziali.getId();
        this.email = credenziali.getEmail();
        this.password = credenziali.getPassword();
        this.utenteId = credenziali.getUtente() != null ? credenziali.getUtente().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public Long getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(Long utenteId) {
        this.utenteId = utenteId;
    }
}
