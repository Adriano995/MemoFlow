package sviluppo.adriano.MemoFlow.dto.modificaDTO;

import sviluppo.adriano.MemoFlow.enums.TipoNota;

public class CambiaNotaDTO {

    private String titolo;
    private String contenutoTesto;
    private TipoNota tipoNota;
    private String contenutoSVG;

    public CambiaNotaDTO(){}

    public CambiaNotaDTO(String titolo, String contenutoTesto, String contenutoSVG, TipoNota tipoNota) {
        this.titolo = titolo;
        this.contenutoTesto = contenutoTesto;
        this.contenutoSVG = contenutoSVG;
        this.tipoNota = tipoNota;
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

    public String getContenutoSVG() {
        return contenutoSVG;
    }
    
    public void setContenutoSVG(String contenutoSVG) {
        this.contenutoSVG = contenutoSVG;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
    }
}