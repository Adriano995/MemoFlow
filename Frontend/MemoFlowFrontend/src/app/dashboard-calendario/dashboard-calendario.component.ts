import { Component, OnInit } from '@angular/core'; // Aggiunto OnInit
import { CalendarEvent, CalendarModule, CalendarUtils } from 'angular-calendar';
import { CommonModule } from '@angular/common';
import { NotaComponent } from "../nota-component/nota-component";
import { AuthService } from '../auth/auth.service'; // Importa AuthService
import { NotaService } from '../nota-component/nota.service'; // Importa NotaService per la creazione
import { NotaCreateDTO } from '../nota-component/nota-create.dto'; // Importa il DTO di creazione
import { TipoNota } from '../nota-component/tipo-nota.enum'; // Importa l'enum
import { FormsModule } from '@angular/forms'; // Per i moduli del form di creazione

@Component({
  selector: 'app-dashboard-calendario',
  standalone: true,
  // Aggiungi FormsModule qui se aggiungi il form di creazione in questo HTML
  imports: [CommonModule, CalendarModule, NotaComponent, FormsModule], 
  providers: [CalendarUtils],
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css']
})
export class DashboardCalendarioComponent implements OnInit { // Implementa OnInit

  viewDate: Date = new Date();
  events: CalendarEvent[] = []; // Inizializza vuoto o con eventi di esempio
  selectedDate: Date | null = null;
  currentUserId: number | null = null; // Per memorizzare l'ID utente

  // Variabili per il form di creazione della nota (spostate qui)
  newNotaTitolo: string = '';
  newNotaContenuto: string = '';
  newNotaTipo: TipoNota = TipoNota.TESTO; 
  tipiNota = Object.values(TipoNota);

  constructor(
    private authService: AuthService, // Inietta AuthService
    private notaService: NotaService // Inietta NotaService per creare note
  ) {}

  ngOnInit(): void {
    // Recupera l'ID utente all'avvio del componente
    this.currentUserId = this.authService.getUserId();
    if (!this.currentUserId) {
      console.warn('Utente non loggato, impossibile recuperare ID utente.');
      // Potresti reindirizzare al login o mostrare un messaggio
    }
  }

  dayClicked(event: { day: { date: Date; events: CalendarEvent[] }; sourceEvent: MouseEvent | KeyboardEvent }): void {
    this.selectedDate = event.day.date;
    // Quando la data cambia, assicurati che il form di creazione sia resettato
    this.newNotaTitolo = '';
    this.newNotaContenuto = '';
    this.newNotaTipo = TipoNota.TESTO;
  }

  handleEvent(event: CalendarEvent): void {
    console.log('Evento cliccato:', event);
  }

  // Metodo per la creazione di una nota, ora qui
  async onCreateNota() {
    if (!this.selectedDate || !this.currentUserId) {
      console.error('Impossibile creare nota: data selezionata o ID utente mancante.');
      // Mostra un errore all'utente
      return;
    }

    if (!this.newNotaTitolo || !this.newNotaContenuto) {
      console.error("Titolo e contenuto della nota non possono essere vuoti.");
      // Mostra un errore all'utente
      return;
    }

    const notaToCreate: NotaCreateDTO = {
      titolo: this.newNotaTitolo,
      contenutoTesto: this.newNotaContenuto,
      tipoNota: this.newNotaTipo,
      contenutoSVG: '', // Gestisci se hai input per SVG
      utenteId: this.currentUserId // L'ID utente è qui disponibile
    };

    try {
      const createdNota = await this.notaService.createNota(notaToCreate);
      console.log('Nota creata con successo:', createdNota);
      // Dopo aver creato la nota, ricarica le note per la data selezionata nel NotaComponent
      // Per fare questo, devi "attivare" un cambiamento nella proprietà "date" o "userId"
      // del NotaComponent. Il modo più semplice è forzare un aggiornamento:
      // Se il tuo NotaComponent ha un metodo per ricaricare, potresti chiamarlo direttamente
      // (ma è meno "Angular-way"). L'ideale è riassegnare la data selezionata.
      const tempDate = this.selectedDate;
      this.selectedDate = null; // Force change detection
      setTimeout(() => {
        this.selectedDate = tempDate; // Restore value to re-trigger ngOnChanges in child
      }, 0);
      
      // Resetta il form
      this.newNotaTitolo = '';
      this.newNotaContenuto = '';
      this.newNotaTipo = TipoNota.TESTO;

    } catch (error) {
      console.error('Errore durante la creazione della nota:', error);
      // Mostra un errore all'utente
    }
  }
}