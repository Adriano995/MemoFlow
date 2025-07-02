// dashboard-calendario.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewNotaComponent } from "../preview-nota-component/preview-nota-component";
import { AuthService } from '../auth/auth.service';
import { PreviewNotaService } from '../preview-nota-component/preview-nota.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard-calendario',
  standalone: true,
  imports: [CommonModule, PreviewNotaComponent, FormsModule],
  providers: [],
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css']
})
export class DashboardCalendarioComponent implements OnInit {
  viewDate: Date = new Date();
  daysInCalendar: Date[] = [];
  selectedDate: Date | null = null;
  giorniConNote: Set<string> = new Set();
  currentUserId: any;

  constructor(
    private authService: AuthService,
    private previewNotaService: PreviewNotaService,
    private cdr: ChangeDetectorRef // Iniettare ChangeDetectorRef
  ) {}

  async ngOnInit(): Promise<void> {
    console.log("DashboardCalendarioComponent ngOnInit start");
    this.currentUserId = this.authService.getUserId();
    console.log("currentUserId", this.currentUserId);

    if (this.currentUserId === null) {
      console.error('ID utente non disponibile. Effettuare il login.');
      return;
    }

    await this.loadNotesAndGenerateCalendar();
  }

  async previousMonth(): Promise<void> {
    this.viewDate = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() - 1, 1);
    await this.loadNotesAndGenerateCalendar();
  }

  async nextMonth(): Promise<void> {
    this.viewDate = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 1);
    await this.loadNotesAndGenerateCalendar();
  }

  private async loadNotesAndGenerateCalendar(): Promise<void> {
    if (!this.currentUserId) return;

    const tutteNote = await this.previewNotaService.getNoteByUser(this.currentUserId);
    console.log("Note raw dal backend:", tutteNote);

    this.giorniConNote = new Set(
      tutteNote
        .map((nota: any) => (nota.data || nota.dataCreazione || '').substring(0, 10)) // Ho aggiunto nota.dataCreazione come fallback
        .filter(dateStr => dateStr !== '')
    );
    console.log("Giorni con note:", this.giorniConNote);

    this.generateCalendarDays();

    // Forza Angular a rilevare il cambiamento
    this.cdr.detectChanges();
  }

  generateCalendarDays(): void {
    const firstDay = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), 1);
    const lastDay = new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, 0);
    const startDay = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1; // Lunedì = 0
    const totalDays = lastDay.getDate();

    const days: Date[] = [];

    // Giorni del mese precedente
    for (let i = startDay - 1; i >= 0; i--) {
      days.push(new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), -i));
    }

    // Giorni del mese corrente
    for (let i = 1; i <= totalDays; i++) {
      days.push(new Date(this.viewDate.getFullYear(), this.viewDate.getMonth(), i));
    }

    // Riempie la riga finale fino a Domenica
    const endPadding = 42 - days.length; // Assicurati che ci siano 6 settimane complete (6*7=42 giorni)
    if (days.length < 42) {
      for (let i = 1; i <= (42 - days.length); i++) {
        days.push(new Date(this.viewDate.getFullYear(), this.viewDate.getMonth() + 1, i));
      }
    }


    this.daysInCalendar = days;
  }

  dayClicked(date: Date): void {
    // Crea una nuova istanza di Date per assicurare che Angular rilevi il cambiamento dell'input
    this.selectedDate = new Date(date);
    // Potrebbe non essere strettamente necessario qui, ma non fa male in casi complessi
    this.cdr.detectChanges();
  }

  isToday(date: Date): boolean {
    const today = new Date();
    return date.getDate() === today.getDate() &&
           date.getMonth() === today.getMonth() &&
           date.getFullYear() === today.getFullYear();
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
    const formatted = `${yyyy}-${mm}-${dd}`;
    return this.giorniConNote.has(formatted);
  }

  // TrackBy function per ottimizzare *ngFor e aiuta Angular nel rendering
  trackByDate(index: number, item: Date): string {
    return item.toISOString();
  }
}