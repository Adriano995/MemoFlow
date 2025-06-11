package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "credential")
public class Credenziali {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "utente_id", referencedColumnName = "id", unique = true)
    private Utente utente;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public Credenziali(){}

    public Credenziali(Long id, Utente utente, String email, String password){
        this.id = id;
        this.utente = utente;
        this.email = email;
        this.password = password;
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
}