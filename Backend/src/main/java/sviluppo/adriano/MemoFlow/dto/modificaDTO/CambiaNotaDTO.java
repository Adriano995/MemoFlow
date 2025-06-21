package sviluppo.adriano.MemoFlow.dto.modificaDTO;

import sviluppo.adriano.MemoFlow.enums.TipoNota;

public class CambiaNotaDTO {

    private String titolo;
    private String contenutoTesto;
    private TipoNota tipoNota;

    public CambiaNotaDTO(){}

    public CambiaNotaDTO(String titolo, String contenutoTesto, TipoNota tipoNota) {
        this.titolo = titolo;
        this.contenutoTesto = contenutoTesto;
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

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
    }
}
