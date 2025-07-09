package sviluppo.adriano.MemoFlow.dto;

import sviluppo.adriano.MemoFlow.entity.Authority;
import sviluppo.adriano.MemoFlow.entity.Utente;

import java.util.HashSet;
import java.util.Set;

public class UtenteDTO {

    private Long id;
    private String nome;
    private String cognome;
    // Rimuovi questa riga: private CredenzialiDTO credenziali;
    private Set<String> roles;
    private String email; // Questa riga deve esserci!

    public UtenteDTO() {}

    public UtenteDTO(Long id, String nome, String cognome, String email) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.roles = new HashSet<>();
    }

    public UtenteDTO(Long id, String nome, String cognome, String email, Set<String> roles) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}