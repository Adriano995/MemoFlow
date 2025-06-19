package sviluppo.adriano.MemoFlow.dto.CreaDTO;

public class UtenteCreateDTO {

    private String nome;
    private String cognome;
    private CredenzialiCreateDTO credenziali; // ✅ nested DTO

    public UtenteCreateDTO() {}

    public UtenteCreateDTO(String nome, String cognome, CredenzialiCreateDTO credenziali) {
        this.nome = nome;
        this.cognome = cognome;
        this.credenziali = credenziali;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public CredenzialiCreateDTO getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(CredenzialiCreateDTO credenziali) {
        this.credenziali = credenziali;
    }

}