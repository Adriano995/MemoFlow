ok, fatte le entity, vediamole

nota:
package sviluppo.adriano.MemoFlow.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import sviluppo.adriano.MemoFlow.enums.TipoNota;

@Entity
public class Nota {
    
    @Id
    private UUID id;

    private String titolo;

    @Enumerated(EnumType.STRING)
    private TipoNota tipoNota; // TESTO o DISEGNO

    @Column(columnDefinition = "TEXT")
    private String contenutoTesto;

    @Column(columnDefinition = "TEXT")
    private String contenutoSVG;

    private LocalDateTime dataCreazione;
    private LocalDateTime ultimaModifica;

    @ManyToOne
    private Utente utente;

    public Nota(){};

    public Nota(UUID id, String titolo, TipoNota tipoNota, String contenutoTesto, String contenutoSVG, LocalDateTime dataCreazione, LocalDateTime ultimaModifica){
        this.id = id;
        this.titolo = titolo;
        this.tipoNota = tipoNota;
        this.contenutoTesto = contenutoTesto;
        this.contenutoSVG = contenutoSVG;
        this.dataCreazione = dataCreazione;
        this.ultimaModifica = ultimaModifica;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
    }

    public String getContenutoTesto() {
        return contenutoTesto;
    }

    public void setContenutoTesto(String contenutoTesto) {
        this.contenutoTesto = contenutoTesto;
    }

    public String getContenutoSVG() {
        return contenutoSVG;
    }

    public void setContenutoSVG(String contenutoSVG) {
        this.contenutoSVG = contenutoSVG;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getUltimaModifica() {
        return ultimaModifica;
    }

    public void setUltimaModifica(LocalDateTime ultimaModifica) {
        this.ultimaModifica = ultimaModifica;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    
}

Authority:
package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import sviluppo.adriano.MemoFlow.enums.AuthorityEnum;

@Entity
public class Authority {

    @Enumerated(EnumType.STRING)
    private AuthorityEnum authorityEnum; // es: ROLE_USER, ROLE_ADMIN, ROLE_PROPRIETARIO_GRUPPO, ecc.

    public Authority(){}

    public Authority(AuthorityEnum authorityEnum){
        this.authorityEnum = authorityEnum;
    }

    public AuthorityEnum getAuthorityEnum() {
        return authorityEnum;
    }

    public void setAuthorityEnum(AuthorityEnum authorityEnum) {
        this.authorityEnum = authorityEnum;
    }

}

UtenteAuthority:
package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
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

PasswordresetToken:
package sviluppo.adriano.MemoFlow.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class PasswordResetToken {
    
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token; // UUID o hash

    private LocalDateTime dataCreazione;
    private LocalDateTime dataScadenza;

    private boolean usato;

    @OneToOne
    private Credenziali credenziali;

    public PasswordResetToken(){}

    public PasswordResetToken(UUID id, String token, LocalDateTime dataCreazione, LocalDateTime dataScadenza){
        this.id = id;
        this.token = token;
        this.dataCreazione = dataCreazione;
        this.dataScadenza = dataScadenza;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDateTime dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public boolean isUsato() {
        return usato;
    }

    public void setUsato(boolean usato) {
        this.usato = usato;
    }

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
    }

    
}

Utente:
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

Credenziali:
package sviluppo.adriano.MemoFlow.entity;

import jakarta.persistence.Entity;

@Entity
public class Credenziali {
 
    private Utente utente;

    private String email;

    private String password;

    public Credenziali(){}

    public Credenziali(Utente utente, String email, String password){
        this.utente = utente;
        this.email = email;
        this.password = password;
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

ed ecco gli enum,
authorityEnum:
package sviluppo.adriano.MemoFlow.enums;

public enum  AuthorityEnum {
    
     ROLE_USER, 
     ROLE_ADMIN, 
     ROLE_PROPRIETARIO_GRUPPO

}

TipoNota:
package sviluppo.adriano.MemoFlow.enums;

public enum TipoNota {

    TESTO,
    DISEGNO

}


mancano le relazioni tra utente e credenziali questo si, ma non so se farle 1-1 o 1-n