import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventoService } from '../services/evento.service';
import { EventoCambiaDTO, EventoDTO } from '../models/evento.model';
import { format, parseISO } from 'date-fns';
import flatpickr from 'flatpickr';

@Component({
  selector: 'app-evento-modifica',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evento-modifica.component.html',
  styleUrls: ['./evento-modifica.component.css']
})
export class EventoModificaComponent implements OnInit, AfterViewInit {
  eventoId: number | null = null;
  evento: EventoDTO | null = null;
  modificaEvento: EventoCambiaDTO = {};

  @ViewChild('dataInizioInput') dataInizioInput!: ElementRef;
  @ViewChild('dataFineInput') dataFineInput!: ElementRef;

  private flatpickrInizio: any;
  private flatpickrFine: any;
  isAllDay: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private eventoService: EventoService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.eventoId = +id;
        this.loadEvento(this.eventoId);
      }
    });
  }

  ngAfterViewInit(): void {
    this.flatpickrInizio = flatpickr(this.dataInizioInput.nativeElement, {
      enableTime: true,
      dateFormat: "Y-m-d H:i",
      time_24hr: true,
      minuteIncrement: 15,
      onChange: (selectedDates: Date[]) => {
        if (selectedDates.length > 0) {
          this.modificaEvento.dataInizio = format(selectedDates[0], "yyyy-MM-dd'T'HH:mm:ss");
        }
      }
    });

    this.flatpickrFine = flatpickr(this.dataFineInput.nativeElement, {
      enableTime: true,
      dateFormat: "Y-m-d H:i",
      time_24hr: true,
      minuteIncrement: 15,
      onChange: (selectedDates: Date[]) => {
        if (selectedDates.length > 0) {
          this.modificaEvento.dataFine = format(selectedDates[0], "yyyy-MM-dd'T'HH:mm:ss");
        }
      }
    });
  }

  loadEvento(id: number): void {
    this.eventoService.getEventoById(id).subscribe(
      (evento) => {
        this.evento = evento;
        this.modificaEvento = { ...evento };
        
        if (this.evento.dataInizio && this.evento.dataFine) {
          const inizio = parseISO(this.evento.dataInizio);
          const fine = parseISO(this.evento.dataFine);
          
          const isFullDayEvent = inizio.getHours() === 0 && inizio.getMinutes() === 0 &&
                                 fine.getHours() === 23 && fine.getMinutes() === 59;
          
          this.isAllDay = isFullDayEvent;
        }

        if (this.flatpickrInizio && this.evento.dataInizio) {
          this.flatpickrInizio.setDate(parseISO(this.evento.dataInizio), false);
          this.flatpickrInizio.set('enableTime', !this.isAllDay);
        }
        if (this.flatpickrFine && this.evento.dataFine) {
          this.flatpickrFine.setDate(parseISO(this.evento.dataFine), false);
          this.flatpickrFine.set('enableTime', !this.isAllDay);
        }
      },
      error => {
        console.error('Errore nel caricamento dell\'evento:', error);
      }
    );
  }

  onAllDayChange(): void {
    const dataInizioSelezionata = this.flatpickrInizio.selectedDates[0] || new Date();

    if (this.isAllDay) {
      if (this.flatpickrInizio) {
        this.flatpickrInizio.set('enableTime', false);
        
        const dataInizio = new Date(dataInizioSelezionata);
        dataInizio.setHours(0, 0, 0, 0);
        this.modificaEvento.dataInizio = format(dataInizio, "yyyy-MM-dd'T'HH:mm:ss");

        const dataFine = new Date(dataInizioSelezionata);
        dataFine.setHours(23, 59, 59, 999);
        this.modificaEvento.dataFine = format(dataFine, "yyyy-MM-dd'T'HH:mm:ss.SSS");
      }
    } else {
      if (this.flatpickrInizio) {
        this.flatpickrInizio.set('enableTime', true);
        const dataInizio = this.flatpickrInizio.selectedDates[0] || new Date();
        this.modificaEvento.dataInizio = format(dataInizio, "yyyy-MM-dd'T'HH:mm:ss");
      }
      const dataFineSelezionata = this.flatpickrFine.selectedDates[0] || dataInizioSelezionata;
      if (this.flatpickrFine) {
        this.flatpickrFine.set('enableTime', true);
        this.modificaEvento.dataFine = format(dataFineSelezionata, "yyyy-MM-dd'T'HH:mm:ss");
      }
    }
  }

  async onSubmit(): Promise<void> {
    if (!this.eventoId) {
      console.error('ID evento non disponibile.');
      return;
    }
    
    try {
      await this.eventoService.updateEvento(this.eventoId, this.modificaEvento).toPromise();
      alert('Evento aggiornato con successo!');
      this.router.navigate(['/dashboard']);
    } 
    catch (error) {
      console.error('Errore durante l\'aggiornamento dell\'evento:', error);
      alert('Si è verificato un errore durante l\'aggiornamento dell\'evento.');
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}