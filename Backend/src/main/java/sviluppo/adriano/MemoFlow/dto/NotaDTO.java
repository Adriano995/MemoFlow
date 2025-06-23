package sviluppo.adriano.MemoFlow.dto;

import sviluppo.adriano.MemoFlow.enums.TipoNota;

import java.time.LocalDateTime;

public class NotaDTO {

    private Long id;
    private String titolo;
    private TipoNota tipoNota;
    private String contenutoTesto;
    private String contenutoSVG;

    private LocalDateTime dataCreazione;
    private LocalDateTime ultimaModifica;

    //Serve per associare l'utente
    private UtenteDTO utenteDTO;

    public NotaDTO(){}

    public NotaDTO(Long id, String titolo, TipoNota tipoNota, String contenutoTesto, String contenutoSVG, LocalDateTime dataCreazione, LocalDateTime ultimaModifica, UtenteDTO utenteDTO) {
        this.id = id;
        this.titolo = titolo;
        this.tipoNota = tipoNota;
        this.contenutoTesto = contenutoTesto;
        this.contenutoSVG = contenutoSVG;
        this.dataCreazione = dataCreazione;
        this.ultimaModifica = ultimaModifica;
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

    public UtenteDTO getUtenteDTO() {
        return utenteDTO;
    }

    public void setUtenteDTO(UtenteDTO utenteDTO) {
        this.utenteDTO = utenteDTO;
    }
}
