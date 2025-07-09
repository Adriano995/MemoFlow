package sviluppo.adriano.MemoFlow.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import sviluppo.adriano.MemoFlow.enums.TipoNota;

@Entity
@Table(name = "note")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titolo;

    @Enumerated(EnumType.STRING)
    private TipoNota tipoNota; 

    @Column(columnDefinition = "TEXT")
    private String contenutoTesto;

    @Column(columnDefinition = "TEXT")
    private String contenutoSVG;

    private LocalDateTime dataCreazione;
    private LocalDateTime ultimaModifica;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    public Nota(){};

    public Nota(Long id, String titolo, TipoNota tipoNota, String contenutoTesto, String contenutoSVG, LocalDateTime dataCreazione, LocalDateTime ultimaModifica){
        this.id = id;
        this.titolo = titolo;
        this.tipoNota = tipoNota;
        this.contenutoTesto = contenutoTesto;
        this.contenutoSVG = contenutoSVG;
        this.dataCreazione = dataCreazione;
        this.ultimaModifica = ultimaModifica;
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

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    
}
