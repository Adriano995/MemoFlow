import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { PreviewNotaComponent } from "../preview-nota-component/preview-nota-component";
import { AuthService } from '../auth/auth.service';
import { PreviewNotaService } from '../services/preview-nota.service';
import { FormsModule } from '@angular/forms';
import { UserComponent } from "../user/user.component";
import { PreviewEventoComponent } from '../preview-evento/preview-evento';
import { EventoService } from '../services/evento.service';
import { EventoDTO, EventoStato } from '../models/evento.model';
import { parseISO, add, format, isAfter, isBefore, isEqual } from 'date-fns';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-dashboard-calendario',
  standalone: true,
  imports: [
    CommonModule,
    PreviewNotaComponent,
    FormsModule,
    UserComponent,
    PreviewEventoComponent
  ],
  providers: [DatePipe],
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css']
})
export class DashboardCalendarioComponent implements OnInit {
  viewDate: Date = new Date();
  daysInCalendar: Date[] = [];
  selectedDate: Date | null = null;
  giorniConNote: Set<string> = new Set();
  giorniConEventi: Map<string, string> = new Map();
  currentUserId: number | null = null;
  weekdays: string[] = ['Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab', 'Dom'];

  eventiDelMese: EventoDTO[] = [];

  isExpanded: boolean = false; 

  constructor(
    private authService: AuthService,
    private previewNotaService: PreviewNotaService,
    private eventoService: EventoService,
    private cdr: ChangeDetectorRef,
    private datePipe: DatePipe
  ) {}

  async ngOnInit(): Promise<void> {
    this.currentUserId = this.authService.getUserId();
    if (this.currentUserId === null) {
      console.error("User ID not available. Cannot load data.");
      return;
    }
    this.calculateDaysInMonth();
    await this.fetchNotesForMonth();
    await this.fetchEventsForMonth();
  }

  previousMonth(): void {
    this.viewDate = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() - 1, 1);
    this.updateCalendarAndData();
  }

  nextMonth(): void {
    this.viewDate = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 1);
    this.updateCalendarAndData();
  }

  private async updateCalendarAndData(): Promise<void> {
    this.calculateDaysInMonth();
    await this.fetchNotesForMonth();
    await this.fetchEventsForMonth();
    this.selectedDate = null;
  }

  async fetchNotesForMonth(): Promise<void> {
    this.giorniConNote.clear();
    if (this.currentUserId === null) {
      console.error("User ID is null, cannot fetch notes.");
      return;
    }
    try {
      const allNotes = await this.previewNotaService.getNoteByUser(this.currentUserId);
      const currentMonth = this.viewDate.getMonth();
      const currentYear = this.viewDate.getFullYear();
      if (allNotes && Array.isArray(allNotes)) {
        allNotes.forEach((nota: any) => {
          const notaDate = new Date(nota.dataCreazione);
          if (notaDate.getMonth() === currentMonth && notaDate.getFullYear() === currentYear) {
            const yyyy = notaDate.getFullYear();
            const mm = (notaDate.getMonth() + 1).toString().padStart(2, '0');
            const dd = notaDate.getDate().toString().padStart(2, '0');
            this.giorniConNote.add(`${yyyy}-${mm}-${dd}`);
          }
        });
      } else {
        console.warn("Struttura di risposta inaspettata per tutte le note dell'utente:", allNotes);
      }
      this.cdr.detectChanges();
    } catch (error) {
      console.error("Errore nel caricamento di tutte le note dell'utente:", error);
    }
  }

  async fetchEventsForMonth(): Promise<void> {
    this.giorniConEventi.clear();

    if (this.currentUserId === null) {
      console.error('User ID non disponibile. Impossibile caricare gli eventi.');
      return;
    }

    const inizioMese = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), 1);
    const fineMese = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 0, 23, 59, 59);

    const inizioMeseISO = this.datePipe.transform(inizioMese, "yyyy-MM-dd'T'HH:mm:ss", 'UTC') || '';
    const fineMeseISO = this.datePipe.transform(fineMese, "yyyy-MM-dd'T'HH:mm:ss", 'UTC') || '';
    
    console.log(`Chiamo il backend per gli eventi tra ${inizioMeseISO} e ${fineMeseISO}`);

    try {
        const eventi = await firstValueFrom(this.eventoService.getEventiBetweenDates(inizioMeseISO, fineMeseISO, this.currentUserId));
        
        console.log('Eventi ricevuti dal backend:', eventi);
        
        this.eventiDelMese = eventi;
        if (this.eventiDelMese && this.eventiDelMese.length > 0) {
            this.updateEventStates(); 
            this.giorniConEventi.clear(); 
            this.eventiDelMese.forEach(evento => {
                const dataInizio = parseISO(evento.dataInizio);
                const dataFine = evento.dataFine ? parseISO(evento.dataFine) : null;
                const stato = evento.stato;
                if (dataFine === null || dataFine.getTime() === dataInizio.getTime()) {
                  this.giorniConEventi.set(format(dataInizio, 'yyyy-MM-dd'), stato);
                } else {
                  let dataCorrente = dataInizio;
                  while (dataCorrente <= dataFine) {
                    this.giorniConEventi.set(format(dataCorrente, 'yyyy-MM-dd'), stato);
                    dataCorrente = add(dataCorrente, { days: 1 });
                  }
                }
            });
        } else {
             this.giorniConEventi.clear();
        }

        console.log('Mappa giorniConEventi dopo il fetch:', this.giorniConEventi);
        this.cdr.detectChanges();
    } catch (error) {
      console.error("Errore nel caricamento degli eventi del mese:", error);
      this.giorniConEventi.clear();
      this.cdr.detectChanges();
    }
  }

  updateEventStates(): void {
    const now = new Date();
    this.eventiDelMese.forEach(evento => {
        if (evento.stato === EventoStato.STATO_ANNULLATO) {
            return;
        }
        const dataInizio = evento.dataInizio ? parseISO(evento.dataInizio) : null;
        const dataFine = evento.dataFine ? parseISO(evento.dataFine) : null;

        if (!dataInizio) {
            return;
        }
        
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

  private calculateDaysInMonth(): void {
    const year = this.viewDate.getFullYear();
    const month = this.viewDate.getMonth();
    const days: Date[] = [];
    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);
    const firstDayOfWeek = firstDayOfMonth.getDay() === 0 ? 6 : firstDayOfMonth.getDay() - 1;
    for (let i = firstDayOfWeek; i > 0; i--) {
      days.unshift(new Date(year, month, 1 - i));
    }
    for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
      days.push(new Date(year, month, i));
    }
    const totalDaysToShow = 42;
    if (days.length < totalDaysToShow) {
      for (let i = 1; days.length < totalDaysToShow; i++) {
        days.push(new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, i));
      }
    }
    this.daysInCalendar = days;
  }

  dayClicked(date: Date): void {
    if (this.selectedDate && this.isSameDay(date, this.selectedDate)) {
      this.selectedDate = null; 
      this.isExpanded = false; 
    } else {
      this.selectedDate = new Date(date);
      this.isExpanded = true; 
    }
    this.cdr.detectChanges();
  }

  isToday(date: Date): boolean {
    const today = new Date();
    return date.getDate() === today.getDate() &&
             today.getMonth() === date.getMonth() &&
             today.getFullYear() === today.getFullYear();
  }

  isSameDay(date1: Date, date2: Date): boolean {
    return date1.getDate() === date2.getDate() &&
             date1.getMonth() === date2.getMonth() &&
             date1.getFullYear() === date2.getFullYear();
  }

  isWeekend(date: Date): boolean {
    const day = date.getDay();
    return day === 0 || day === 6;
  }

  hasNote(date: Date): boolean {
    const yyyy = date.getFullYear();
    const mm = (date.getMonth() + 1).toString().padStart(2, '0');
    const dd = date.getDate().toString().padStart(2, '0');
    return this.giorniConNote.has(`${yyyy}-${mm}-${dd}`);
  }

  hasEvent(date: Date): boolean {
    const dateKey = format(date, 'yyyy-MM-dd');
    return this.giorniConEventi.has(dateKey);
  }

// ... (il resto del tuo codice)

getEventStatus(date: Date): string {
  const dateKey = format(date, 'yyyy-MM-dd');
  const status = this.giorniConEventi.get(dateKey);
  if (!status) {
    return 'sconosciuto';
  }
  
  // Questa riga corregge il problema
  // Rimuove 'STATO_' e converte il resto in minuscolo
  return status.replace('STATO_', '').toLowerCase();
}

// ... (il resto del tuo codice)

  trackByDate(index: number, day: Date): number {
    return day.getTime();
  }
}