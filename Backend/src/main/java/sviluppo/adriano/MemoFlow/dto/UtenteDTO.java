package sviluppo.adriano.MemoFlow.dto;

import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.entity.Utente;

public class UtenteDTO {

    private Long id;
    private String nome;
    private String cognome;
    private Long credenzialiId;

    public UtenteDTO (){}

    public UtenteDTO(Long id, String nome, String cognome, Long credenzialiId){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.credenzialiId = credenzialiId;
    }

    public UtenteDTO(Utente utente) {
        this.id = utente.getId();
        this.nome = utente.getNome();
        this.cognome = utente.getCognome();
        this.credenzialiId = utente.getCredenziali() != null ? utente.getCredenziali().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Long getCredenzialiId() {
        return credenzialiId;
    }

    public void setCredenzialiId(Long credenzialiId) {
        this.credenzialiId = credenzialiId;
    }
}
