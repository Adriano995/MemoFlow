package sviluppo.adriano.MemoFlow.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Importa HashSet

import jakarta.persistence.CascadeType;     // Importa Set
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Utente{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;

    @OneToOne(mappedBy = "utente", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Credenziali credenziali;

    // AGGIUNGI QUESTO BLOCCO PER LA RELAZIONE CON UtenteAuthority
    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<UtenteAuthority> userAuthorities = new HashSet<>(); // <-- INIZIALIZZA QUI!

    public Utente(){}

    public Utente(Long id, String nome, String cognome){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        // Se non inizializzato sopra alla dichiarazione, inizializza anche qui:
        // this.userAuthorities = new HashSet<>();
    }

    public Utente(String nome, String cognome, Credenziali credenziali) {
        this.nome = nome;
        this.cognome = cognome;
        this.credenziali = credenziali;
        // Se non inizializzato sopra alla dichiarazione, inizializza anche qui:
        // this.userAuthorities = new HashSet<>();
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

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
        // Assicurati l'associazione bidirezionale
        if (credenziali != null && credenziali.getUtente() != this) {
            credenziali.setUtente(this);
        }
    }

    // NUOVI GETTER/SETTER PER userAuthorities
    public Set<UtenteAuthority> getUserAuthorities() {
        return userAuthorities;
    }

    public void setUserAuthorities(Set<UtenteAuthority> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }

    // Metodo addUtenteAuthority che causava l'errore
    public void addUtenteAuthority(UtenteAuthority utenteAuthority) {
        // Questa riga ora non dovrebbe più lanciare NPE
        this.userAuthorities.add(utenteAuthority);
        utenteAuthority.setUtente(this); // Imposta anche il lato proprietario della relazione
    }

    // Metodo per rimuovere l'associazione se necessario
    public void removeUtenteAuthority(UtenteAuthority utenteAuthority) {
        this.userAuthorities.remove(utenteAuthority);
        utenteAuthority.setUtente(null);
    }
}