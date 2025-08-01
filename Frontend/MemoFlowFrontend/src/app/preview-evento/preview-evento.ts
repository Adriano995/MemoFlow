import { Component, OnInit, Input, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common'; 
import { Router } from '@angular/router'; 
import { EventoService } from '../services/evento.service'; 
import { EventoDTO, EventoStato } from '../models/evento.model'
import { interval, Subject, of } from 'rxjs';
import { takeUntil, startWith, catchError } from 'rxjs/operators';
import { isAfter, isBefore, isEqual, parseISO } from 'date-fns'; 

@Component({
  selector: 'app-preview-evento',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './preview-evento.html',
  styleUrls: ['./preview-evento.css'],
  providers: [DatePipe] 
})
export class PreviewEventoComponent implements OnInit, OnChanges, OnDestroy {
  @Input() date: Date | null = null; 
  @Input() userId: number | null = null; 

  eventi: EventoDTO[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  private destroy$ = new Subject<void>();
  private readonly AUTO_UPDATE_INTERVAL_MS = 60 * 1000; 

  constructor(
    private eventoService: EventoService,
    private router: Router,
    private datePipe: DatePipe 
  ) { }

 ngOnInit(): void {
  this.loadEventsForSelectedDate();

  interval(this.AUTO_UPDATE_INTERVAL_MS)
    .pipe(
     startWith(0), 
     takeUntil(this.destroy$)
    )
    .subscribe(() => this.updateEventStates());
  }

 ngOnChanges(changes: SimpleChanges): void {
  if (changes['date'] && this.date) {
    console.log('PreviewEventoComponent - date input changed:', this.date);
    this.loadEventsForSelectedDate();
  }
  if (changes['userId'] && this.userId) {
    console.log('PreviewEventoComponent - userId input changed:', this.userId); 
    this.loadEventsForSelectedDate();
  }
}

  ngOnDestroy(): void {
   this.destroy$.next(); 
   this.destroy$.complete(); 
  }

  private formatLocalDateForBackend(date: Date): string {
   const year = date.getFullYear();
   const month = (date.getMonth() + 1).toString().padStart(2, '0');
   const day = date.getDate().toString().padStart(2, '0');
   const hour = date.getHours().toString().padStart(2, '0');
   const minute = date.getMinutes().toString().padStart(2, '0');
   const second = date.getSeconds().toString().padStart(2, '0');
   return `${year}-${month}-${day}T${hour}:${minute}:${second}`;
}

loadEventsForSelectedDate(): void {
    if (!this.date || this.userId === null) {
      console.log('Data o UserId non presenti, skip del caricamento.');
      this.eventi = [];
      this.errorMessage = 'Nessuna data selezionata o ID utente non disponibile.';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const startOfDay = new Date(this.date.getFullYear(), this.date.getMonth(), this.date.getDate(), 0, 0, 0, 0);
    const endOfDay = new Date(this.date.getFullYear(), this.date.getMonth(), this.date.getDate(), 23, 59, 59, 999);

    const inizio = this.formatLocalDateForBackend(startOfDay);
    const fine = this.formatLocalDateForBackend(endOfDay);
    
    console.log(`Tentativo di caricare eventi per l'intervallo: inizio ${inizio}, fine ${fine} e userId: ${this.userId}`);

    this.eventoService.getEventiBetweenDates(inizio, fine, this.userId)
      .pipe(
        takeUntil(this.destroy$),
        catchError(error => {
          this.errorMessage = 'Errore durante il caricamento degli eventi. Controlla la console per i dettagli.';
          this.isLoading = false;
          console.error('Errore nel caricamento eventi:', error);
          if (error.status === 404) {
            this.errorMessage = 'Nessun evento trovato per questa data o endpoint non esistente.';
          } else if (error.status === 401 || error.status === 403) {
            this.errorMessage = 'Non autorizzato a visualizzare gli eventi. Effettua il login.';
          }
          return of([]);
        })
      )
      .subscribe((data: EventoDTO[]) => {
          this.eventi = data;
          this.isLoading = false;
          this.updateEventStates(); 
          console.log(`Caricati ${data.length} eventi per la data: ${this.date}`, data); 
      });
  }


updateEventStates(): void {
  const now = new Date();
  this.eventi.forEach(evento => {
    if (evento.stato === EventoStato.STATO_ANNULLATO) {
      return;
    }

    // Qui gestiamo i casi in cui i valori della data sono nulli
    const dataInizio = evento.dataInizio ? parseISO(evento.dataInizio) : null;
    const dataFine = evento.dataFine ? parseISO(evento.dataFine) : null;

    if (!dataInizio) {
      // Se non c'è una data di inizio valida, l'evento viene ignorato
      return;
    }

    // Le condizioni di stato sono state aggiornate per gestire i valori nulli
    if (dataFine && isAfter(now, dataFine)) {
      evento.stato = EventoStato.STATO_CONCLUSO;
    } else if (isAfter(now, dataInizio) && (!dataFine || isBefore(now, dataFine))) {
      evento.stato = EventoStato.STATO_IN_CORSO;
    } else if (isBefore(now, dataInizio)) {
      evento.stato = EventoStato.STATO_PROGRAMMATO;
    } else if (isEqual(now, dataInizio) || (dataFine && isEqual(now, dataFine))) {
      evento.stato = EventoStato.STATO_IN_CORSO;
    }
  });
}

 getStatoClass(stato: EventoStato): string {
    switch (stato) {
    case EventoStato.STATO_PROGRAMMATO:
      return 'status-programmato';
    case EventoStato.STATO_IN_CORSO:
      return 'status-in-corso';
    case EventoStato.STATO_CONCLUSO:
      return 'status-concluso';
    case EventoStato.STATO_ANNULLATO:
      return 'status-annullato';
    default:
      return '';
    }
 }

  deleteEvento(eventId: number): void {
    if (confirm('Sei sicuro di voler eliminare questo evento?')) {
    this.eventoService.deleteEvento(eventId).subscribe({
      next: () => {
      alert('Evento eliminato con successo!');
      this.loadEventsForSelectedDate(); 
      },
      error: (error) => {
      console.error('Errore durante l\'eliminazione dell\'evento:', error);
      alert('Si è verificato un errore durante l\'eliminazione dell\'evento.');
      }
    });
  }
}

  editEvento(eventId: number): void {
   this.router.navigate(['/modifica-evento', eventId]);
  }

  formatDateTime(dateTimeString: string): string | null {
    return this.datePipe.transform(dateTimeString, 'dd/MM/yyyy HH:mm');
  }

  showCreateButton(): boolean {
    return this.date !== null;
  }
   
  goToCreateEvento(): void {
    if (this.date) {
      const formattedDate = this.datePipe.transform(this.date, 'yyyy-MM-dd');
      this.router.navigate(['/crea-evento'], { queryParams: { date: formattedDate } });
    } else {
      this.router.navigate(['/crea-evento']);
    }
  }
}