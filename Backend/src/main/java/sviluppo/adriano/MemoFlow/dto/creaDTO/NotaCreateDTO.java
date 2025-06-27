package sviluppo.adriano.MemoFlow.dto.creaDTO;

import sviluppo.adriano.MemoFlow.enums.TipoNota;

import java.time.LocalDate;

public class NotaCreateDTO {

    private String titolo;
    private String contenutoTesto;
    private String contenutoSVG;
    private TipoNota tipoNota;
    private Long utenteId;
    private LocalDate dataNota; // <-- Per gestire note con giorni diversi

    public NotaCreateDTO(){}

    public NotaCreateDTO(String titolo, String contenutoTesto, TipoNota tipoNota, String contenutoSVG, Long utenteId, LocalDate dataNota) {
        this.titolo = titolo;
        this.contenutoTesto = contenutoTesto;
        this.tipoNota = tipoNota;
        this.contenutoSVG = contenutoSVG;
        this.utenteId = utenteId;
        this.dataNota = dataNota;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getContenutoTesto() {
        return contenutoTesto;
    }

    public void setContenutoTesto(String contenutoTesto) {
        this.contenutoTesto = contenutoTesto;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
    }

    public String getContenutoSVG() {
        return contenutoSVG;
    }

    public void setContenutoSVG(String contenutoSVG) {
        this.contenutoSVG = contenutoSVG;
    }

    public Long getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(Long utenteId) {
        this.utenteId = utenteId;
    }

    public LocalDate getDataNota() {
        return dataNota;
    }

    public void setDataNota(LocalDate dataNota) {
        this.dataNota = dataNota;
    }
}
