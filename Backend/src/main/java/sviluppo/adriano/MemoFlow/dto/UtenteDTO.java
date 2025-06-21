package sviluppo.adriano.MemoFlow.dto;

import sviluppo.adriano.MemoFlow.entity.Utente;

public class UtenteDTO {

    private Long id;
    private String nome;
    private String cognome;
    private CredenzialiDTO credenziali;

    public UtenteDTO() {}

    public UtenteDTO(Long id, String nome, String cognome, CredenzialiDTO credenziali){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.credenziali = credenziali;
    }

    public UtenteDTO(Utente utente) {
        this.id = utente.getId();
        this.nome = utente.getNome();
        this.cognome = utente.getCognome();
        if (utente.getCredenziali() != null) {
            this.credenziali = new CredenzialiDTO(utente.getCredenziali());
        }
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

    public CredenzialiDTO getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(CredenzialiDTO credenziali) {
        this.credenziali = credenziali;
    }
}