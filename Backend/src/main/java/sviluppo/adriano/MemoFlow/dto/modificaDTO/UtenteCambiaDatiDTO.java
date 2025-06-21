package sviluppo.adriano.MemoFlow.dto.modificaDTO;

import jakarta.validation.constraints.NotBlank;

public class UtenteCambiaDatiDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    public UtenteCambiaDatiDTO(){}

    public UtenteCambiaDatiDTO(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
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
