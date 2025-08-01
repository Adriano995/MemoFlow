import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewNotaComponent } from "../preview-nota-component/preview-nota-component";
import { AuthService } from '../auth/auth.service';
import { PreviewNotaService } from '../services/preview-nota.service';
import { FormsModule } from '@angular/forms';
import { UserComponent } from "../user/user.component";
import { PreviewEventoComponent } from '../preview-evento/preview-evento';
import { EventoService } from '../services/evento.service'; // Importa il servizio eventi
import { EventoDTO } from '../models/evento.model'; // Importa il modello EventoDTO
import { parseISO, add, format } from 'date-fns'; // Importa le funzioni di data

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
  providers: [],
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css']
})
export class DashboardCalendarioComponent implements OnInit {
  viewDate: Date = new Date();
  daysInCalendar: Date[] = [];
  selectedDate: Date | null = null;
  giorniConNote: Set<string> = new Set();
  giorniConEventi: Set<string> = new Set(); // Nuovo Set per gli eventi
  currentUserId: number | null = null;
  weekdays: string[] = ['Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab', 'Dom'];

  constructor(
    private authService: AuthService,
    private previewNotaService: PreviewNotaService,
    private eventoService: EventoService, // Inietta il servizio eventi
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit(): Promise<void> {
    console.log("DashboardCalendarioComponent ngOnInit start");
    this.currentUserId = this.authService.getUserId();
    console.log("currentUserId", this.currentUserId);

    if (this.currentUserId === null) {
      console.error("User ID not available. Cannot load data.");
      return;
    }

    this.calculateDaysInMonth();
    await this.fetchNotesForMonth();
    await this.fetchEventsForMonth(); // Chiama il nuovo metodo per gli eventi
    console.log("DashboardCalendarioComponent ngOnInit end");
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
    await this.fetchEventsForMonth(); // Aggiorna gli eventi
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

  // Nuovo metodo per caricare gli eventi del mese
  async fetchEventsForMonth(): Promise<void> {
    this.giorniConEventi.clear();

    if (this.currentUserId === null) {
      console.error('User ID non disponibile. Impossibile caricare gli eventi.');
      return;
    }

    // Calcola l'inizio e la fine del mese per l'API
    const inizioMese = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), 1);
    const fineMese = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 0, 23, 59, 59);

    const inizioMeseISO = format(inizioMese, "yyyy-MM-dd'T'HH:mm:ss");
    const fineMeseISO = format(fineMese, "yyyy-MM-dd'T'HH:mm:ss");

    try {
        const eventi = await this.eventoService.getEventiBetweenDates(inizioMeseISO, fineMeseISO, this.currentUserId).toPromise();
        if (eventi && Array.isArray(eventi)) {
            eventi.forEach(evento => {
                const dataInizio = parseISO(evento.dataInizio);
                const dataFine = evento.dataFine ? parseISO(evento.dataFine) : null;

                // Aggiungiamo i giorni al set, gestendo gli eventi multi-giorno
                if (dataFine === null || dataFine.getTime() === dataInizio.getTime()) {
                    // Evento di un giorno (con o senza dataFine)
                    this.giorniConEventi.add(format(dataInizio, 'yyyy-MM-dd'));
                } else {
                    // Evento di più giorni
                    let dataCorrente = dataInizio;
                    while (dataCorrente <= dataFine) {
                        this.giorniConEventi.add(format(dataCorrente, 'yyyy-MM-dd'));
                        dataCorrente = add(dataCorrente, { days: 1 });
                    }
                }
            });
        }
        this.cdr.detectChanges();
    } catch (error) {
        console.error("Errore nel caricamento degli eventi del mese:", error);
    }
  }


  private calculateDaysInMonth(): void {
    // ... (metodo esistente) ...
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
    this.selectedDate = new Date(date);
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

  // Nuovo metodo per verificare se un giorno ha un evento
  hasEvent(date: Date): boolean {
    const yyyy = date.getFullYear();
    const mm = (date.getMonth() + 1).toString().padStart(2, '0');
    const dd = date.getDate().toString().padStart(2, '0');
    return this.giorniConEventi.has(`${yyyy}-${mm}-${dd}`);
  }

  trackByDate(index: number, day: Date): number {
    return day.getTime();
  }
}