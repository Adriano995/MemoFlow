// ... (le importazioni e le proprietà restano le stesse)
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router'; 
import { EventoService } from '../services/evento.service'; 
import { EventoCreateDTO } from '../models/evento.model'; 
import flatpickr from 'flatpickr';
import { format } from 'date-fns'; // Aggiungi format da date-fns

@Component({
  selector: 'app-evento-creazione',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evento-creazione.html',
  styleUrls: ['./evento-creazione.css']
})
export class EventoCreazioneComponent implements OnInit, AfterViewInit {
  newEvent: EventoCreateDTO = {
    titolo: '',
    descrizione: '',
    dataInizio: '',
    dataFine: '',
    luogo: ''
  };

  isAllDay: boolean = false; 

  @ViewChild('dataInizioInput') dataInizioInput!: ElementRef;
  @ViewChild('dataFineInput') dataFineInput!: ElementRef;

  private flatpickrInizio: any;
  private flatpickrFine: any;

  constructor(
    private eventoService: EventoService,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.flatpickrInizio = flatpickr(this.dataInizioInput.nativeElement, {
      enableTime: true,
      dateFormat: "Y-m-d H:i",
      time_24hr: true,
      minuteIncrement: 15,
      minDate: "today",
      onChange: (selectedDates: Date[]) => {
        if (selectedDates.length > 0) {
          this.newEvent.dataInizio = selectedDates[0].toISOString();
        }
      }
    });

    this.flatpickrFine = flatpickr(this.dataFineInput.nativeElement, {
      enableTime: true,
      dateFormat: "Y-m-d H:i",
      time_24hr: true,
      minuteIncrement: 15,
      minDate: "today",
      onChange: (selectedDates: Date[]) => {
        if (selectedDates.length > 0) {
          this.newEvent.dataFine = selectedDates[0].toISOString();
        }
      }
    });
  }

  onAllDayChange(): void {
    const dataInizioSelezionata = this.flatpickrInizio.selectedDates[0] || new Date();

    if (this.isAllDay) {
      if (this.flatpickrInizio) {
        this.flatpickrInizio.set('enableTime', false);
        
        const dataInizio = new Date(dataInizioSelezionata);
        dataInizio.setHours(0, 0, 0, 0);
        this.newEvent.dataInizio = format(dataInizio, "yyyy-MM-dd'T'HH:mm:ss");

        const dataFine = new Date(dataInizioSelezionata);
        dataFine.setHours(23, 59, 59, 999);
        this.newEvent.dataFine = format(dataFine, "yyyy-MM-dd'T'HH:mm:ss.SSS");
      }
    } else {
      // Riabilita il selettore dell'ora e ripristina la formattazione ISO
      if (this.flatpickrInizio) {
        this.flatpickrInizio.set('enableTime', true);
        const dataInizio = this.flatpickrInizio.selectedDates[0] || new Date();
        this.newEvent.dataInizio = dataInizio.toISOString();
      }
      const dataFineSelezionata = this.flatpickrFine.selectedDates[0] || dataInizioSelezionata;
      if (this.flatpickrFine) {
        this.flatpickrFine.set('enableTime', true);
        this.newEvent.dataFine = dataFineSelezionata.toISOString();
      }
    }
  }

  async onSubmit(): Promise<void> {
    try {
      await this.eventoService.createEvento(this.newEvent).toPromise();
      alert('Evento creato con successo!');
      this.router.navigate(['/dashboard']);
    } catch (error) {
      console.error('Errore durante la creazione dell\'evento:', error);
      alert('Si è verificato un errore durante la creazione dell\'evento.');
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}