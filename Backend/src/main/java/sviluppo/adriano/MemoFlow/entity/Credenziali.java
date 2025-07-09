// File: sviluppo/adriano/MemoFlow/entity/Credenziali.java
package sviluppo.adriano.MemoFlow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
// Rinominiamo la tabella per coerenza con il nome della classe al plurale
@Table(name = "credenziali")
public class Credenziali {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Usiamo IDENTITY
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", referencedColumnName = "id", unique = true)
    @JsonBackReference
    private Utente utente;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public Credenziali(){}

    public Credenziali(Utente utente, String email, String password){
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