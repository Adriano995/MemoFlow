package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.*;

import java.util.Objects;
import sviluppo.adriano.MemoFlow.entity.Authority;

@Entity
@Table(name = "user_authority")
public class UtenteAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_enum", nullable = false) 
    private Authority authority;

    public UtenteAuthority(){}


    public UtenteAuthority(Utente utente, Authority authority){
        this.utente = utente;
        this.authority = authority;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Authority getAuthority() { return authority; }
    public void setAuthority(Authority authority) { this.authority = authority; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtenteAuthority that = (UtenteAuthority) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}