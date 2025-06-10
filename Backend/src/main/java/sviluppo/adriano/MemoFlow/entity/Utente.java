package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Utente{

    @Id
    private Long id;

    private String nome;

    private String cognome;

    public Utente(){}

    public Utente(Long id, String nome, String cognome){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
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

    
}