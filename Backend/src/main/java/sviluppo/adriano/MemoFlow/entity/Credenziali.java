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