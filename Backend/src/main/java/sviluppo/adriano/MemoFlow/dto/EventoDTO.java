package sviluppo.adriano.MemoFlow.dto;

import java.time.LocalDateTime;

public class EventoDTO {
    
    private Long id;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    //private LocalDateTime oraInizio;
    //private LocalDateTime oraFine;
    private String luogo;
    private String stato;

    private UtenteDTO utenteDTO;

    public EventoDTO() {}

    /*public EventoDTO(Long id, String titolo, String descrizione, LocalDateTime dataInizio, LocalDateTime dataFine,
            LocalDateTime oraInizio, LocalDateTime oraFine, String luogo, String stato, UtenteDTO utenteDTO) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.luogo = luogo;
        this.stato = stato;
        this.utenteDTO = utenteDTO;
    }*/

    public EventoDTO(Long id, String titolo, String descrizione, LocalDateTime dataInizio, LocalDateTime dataFine,
            String luogo, String stato, UtenteDTO utenteDTO) { // Modifica il costruttore
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.stato = stato;
        this.utenteDTO = utenteDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    /*public LocalDateTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalDateTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public LocalDateTime getOraFine() {
        return oraFine;
    }

    public void setOraFine(LocalDateTime oraFine) {
        this.oraFine = oraFine;
    }*/
    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public UtenteDTO getUtenteDTO() {
        return utenteDTO;
    }

    public void setUtenteDTO(UtenteDTO utenteDTO) {
        this.utenteDTO = utenteDTO;
    }


}
