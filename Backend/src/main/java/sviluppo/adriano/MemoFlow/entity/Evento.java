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
import sviluppo.adriano.MemoFlow.enums.EventoStato;

@Entity
@Table(name = "eventi")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;

    private LocalDateTime oraInizio;
    private LocalDateTime oraFine;

    @Column(columnDefinition = "TEXT")
    private String luogo;

    @Enumerated(EnumType.STRING)
    private EventoStato stato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    public Evento() {}

    public Evento(Long id, String titolo, String descrizione, LocalDateTime dataInizio, LocalDateTime dataFine,
            LocalDateTime oraInizio, LocalDateTime oraFine, String luogo, EventoStato stato, Utente utente) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.luogo = luogo;
        this.stato = stato;
        this.utente = utente;
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

    public LocalDateTime getOraInizio() {
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
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public EventoStato getStato() {
        return stato;
    }

    public void setStato(EventoStato stato) {
        this.stato = stato;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }


}