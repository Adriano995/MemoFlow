// src/app/dashboard-calendario-component/dashboard-calendario.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventoService } from '../services/evento.service';
import { EventoDTO } from '../models/evento.model';
import { AuthService } from '../auth/auth.service';

// --- MODIFICHE QUI: Percorso e nome della classe per 'preview-evento' ---
// Il percorso è '../preview-evento/preview-evento' perché il file si chiama preview-evento.ts.
// Il nome della classe è 'PreviewEventoComponent' come indicato nel tuo file.
import { PreviewEventoComponent } from '../preview-evento/preview-evento';
import { NotaCreazioneComponent } from '../nota-creazione/nota-creazione.component';

@Component({
  selector: 'app-dashboard-calendario-component',
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NotaCreazioneComponent,
    PreviewEventoComponent // <-- Qui usiamo il nome della classe corretto
  ]
})
export class DashboardCalendarioComponent implements OnInit {

  viewDate: Date = new Date();
  daysInCalendar: Date[] = [];
  weekdays: string[] = ['Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab'];
  selectedDate: Date | null = null;
  currentUserId: number | null = null;

  searchTermTitolo: string = '';
  searchTermKeywords: string = '';
  searchResults: EventoDTO[] = [];
  isLoadingSearch: boolean = false;

  constructor(
    private eventoService: EventoService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.currentUserId = this.authService.getUserId();
    this.generateCalendar();
  }

  // --- Metodi del calendario ---
  generateCalendar(): void {
    this.daysInCalendar = [];
    const firstDayOfMonth = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), 1);
    const lastDayOfMonth = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 0);
    const startDayOfWeek = firstDayOfMonth.getDay();
    for (let i = 0; i < startDayOfWeek; i++) {
        const prevDay = new Date(firstDayOfMonth);
        prevDay.setDate(firstDayOfMonth.getDate() - (startDayOfWeek - i));
        this.daysInCalendar.push(prevDay);
    }
    for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
        this.daysInCalendar.push(new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), i));
    }
    const totalDaysInGrid = 42;
    let currentDay = 1;
    while (this.daysInCalendar.length < totalDaysInGrid) {
        const nextDay = new Date(lastDayOfMonth);
        nextDay.setDate(lastDayOfMonth.getDate() + currentDay);
        this.daysInCalendar.push(nextDay);
        currentDay++;
    }
  }

  previousMonth(): void {
    this.viewDate.setMonth(this.viewDate.getMonth() - 1);
    this.generateCalendar();
  }

  nextMonth(): void {
    this.viewDate.setMonth(this.viewDate.getMonth() + 1);
    this.generateCalendar();
  }

  dayClicked(day: Date): void {
    this.selectedDate = day;
  }

  isToday(date: Date): boolean {
    const today = new Date();
    return date.getFullYear() === today.getFullYear() &&
           date.getMonth() === today.getMonth() &&
           date.getDate() === today.getDate();
  }

  isSameDay(date1: Date, date2: Date): boolean {
    return date1.getFullYear() === date2.getFullYear() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getDate() === date2.getDate();
  }

  isWeekend(date: Date): boolean {
    const dayOfWeek = date.getDay();
    return dayOfWeek === 0 || dayOfWeek === 6;
  }

  hasNote(day: Date): boolean {
    return false;
  }

  trackByDate(index: number, day: Date): number {
    return day.getTime();
  }

  eseguiRicerca(): void {
    this.isLoadingSearch = true;
    const titolo = this.searchTermTitolo.trim();
    const keywords = this.searchTermKeywords.trim();
    if (!titolo && !keywords) {
      this.searchResults = [];
      this.isLoadingSearch = false;
      console.warn("Nessun criterio di ricerca fornito.");
      return;
    }
    this.eventoService.ricercaEventiAvanzata(titolo, keywords).subscribe(
      (data: EventoDTO[]) => {
        this.searchResults = data;
        this.isLoadingSearch = false;
        console.log('Risultati della ricerca:', this.searchResults);
      },
      (error) => {
        this.isLoadingSearch = false;
        console.error('Errore durante la ricerca:', error);
        this.searchResults = [];
        if (error.status === 204) {
          console.log('Nessun evento trovato.');
        } else if (error.status === 403) {
            alert('Non sei autorizzato a effettuare questa ricerca. Effettua il login.');
        } else {
          alert('Si è verificato un errore durante la ricerca. Riprova più tardi.');
        }
      }
    );
  }

  resetRicerca(): void {
    this.searchTermTitolo = '';
    this.searchTermKeywords = '';
    this.searchResults = [];
    console.log("Ricerca resettata.");
  }
}