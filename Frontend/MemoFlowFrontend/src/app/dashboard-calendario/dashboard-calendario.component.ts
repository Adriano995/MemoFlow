import { Component, OnInit } from '@angular/core';
import { CalendarEvent, CalendarModule, CalendarUtils } from 'angular-calendar';
import { CommonModule } from '@angular/common';
import { PreviewNotaComponent } from "../preview-nota-component/preview-nota-component";
import { AuthService } from '../auth/auth.service';
import { PreviewNotaService } from '../preview-nota-component/preview-nota.service';
import { NotaCreateDTO } from '../preview-nota-component/preview-nota-create.dto';
import { TipoNota } from '../preview-nota-component/tipo-nota.enum';
import { FormsModule } from '@angular/forms';

// Importa MonthViewDay per un tipaggio più preciso, anche se non strettamente necessario per la fix
import { CalendarMonthViewDay } from 'angular-calendar'; // <<< AGGIUNGI QUESTO IMPORT


@Component({
  selector: 'app-dashboard-calendario',
  standalone: true,
  imports: [CommonModule, CalendarModule, PreviewNotaComponent, FormsModule],
  providers: [CalendarUtils],
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css']
})
export class DashboardCalendarioComponent implements OnInit {

  viewDate: Date = new Date();
  events: CalendarEvent[] = [];
  selectedDate: Date | null = null;
  currentUserId: number | null = null;

  newNotaTitolo: string = '';
  newNotaContenuto: string = '';
  newNotaTipo: TipoNota = TipoNota.TESTO;
  tipiNota: TipoNota[] = Object.values(TipoNota);

  constructor(
    private authService: AuthService,
    private previewNotaService: PreviewNotaService
  ) {}

  ngOnInit(): void {
    this.currentUserId = this.authService.getUserId();
    if (this.currentUserId === null) {
      console.error('ID utente non disponibile. Effettuare il login.');
    }
  }

  // MODIFICA QUI: Allinea la firma del metodo con l'output di angular-calendar
  dayClicked({ day }: { day: CalendarMonthViewDay }): void {
    this.selectedDate = day.date; // Accedi alla data tramite la proprietà 'day.date'
    console.log('Data selezionata:', this.selectedDate);
    // Puoi anche accedere a day.events se ti servono gli eventi del giorno cliccato
    // console.log('Eventi del giorno cliccato:', day.events);
  }

  async onCreateNota() {
    if (!this.newNotaTitolo || !this.newNotaTipo || !this.selectedDate || this.currentUserId === null) {
      console.error('Errore: Titolo, Tipo Nota, Data o ID Utente non possono essere vuoti.');
      return;
    }

    const formattedDate = this.selectedDate.toISOString().split('T')[0];

    const notaToCreate: NotaCreateDTO = {
      titolo: this.newNotaTitolo,
      contenutoTesto: this.newNotaContenuto,
      tipoNota: this.newNotaTipo,
      contenutoSVG: '',
      utenteId: this.currentUserId,
      dataNota: formattedDate
    };

    try {
      const createdNota = await this.previewNotaService.createNota(notaToCreate);
      console.log('Nota creata con successo:', createdNota);

      const tempDate = this.selectedDate;
      this.selectedDate = null;
      setTimeout(() => {
        this.selectedDate = tempDate;
      }, 0);

      this.newNotaTitolo = '';
      this.newNotaContenuto = '';
      this.newNotaTipo = TipoNota.TESTO;
    } catch (error) {
      console.error('Errore durante la creazione della nota:', error);
    }
  }
}