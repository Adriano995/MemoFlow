package sviluppo.adriano.MemoFlow.dto.creaDTO;

import sviluppo.adriano.MemoFlow.enums.TipoNota;

public class NotaCreateDTO {

    private String titolo;
    private String contenutoTesto;
    private String contenutoSVG;
    private TipoNota tipoNota;
    private Long utenteId;

    public NotaCreateDTO(){}

    public NotaCreateDTO(String titolo, String contenutoTesto, TipoNota tipoNota, String contenutoSVG, Long utenteId) {
        this.titolo = titolo;
        this.contenutoTesto = contenutoTesto;
        this.tipoNota = tipoNota;
        this.contenutoSVG = contenutoSVG;
        this.utenteId = utenteId;
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
}
