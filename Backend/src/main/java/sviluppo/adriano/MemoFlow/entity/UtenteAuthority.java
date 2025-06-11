package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_authority")
public class UtenteAuthority {
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Utente utente;

    @ManyToOne
    private Authority authority;

    public UtenteAuthority(){}

    public UtenteAuthority(Long id, Utente utente, Authority authority){
        this.id = id;
        this.utente = utente;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    
}