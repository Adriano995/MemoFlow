import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router'; 
import { EventoService } from '../services/evento.service'; 
import { EventoCreateDTO } from '../models/evento.model'; 
import { format } from 'date-fns'; 

@Component({
  selector: 'app-evento-creazione',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evento-creazione.html',
  styleUrls: ['./evento-creazione.css']
})
export class EventoCreazioneComponent implements OnInit {
  newEvent: EventoCreateDTO = {
    titolo: '',
    descrizione: '',
    dataInizio: '',
    dataFine: '',
    luogo: ''
  };

  selectedDateInizio: string = '';
  selectedTimeInizio: string = ''; 
  selectedDateFine: string = '';  
  selectedTimeFine: string = '';   

  minDate: string = '';

  constructor(
    private eventoService: EventoService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const now = new Date();
    this.selectedDateInizio = format(now, 'yyyy-MM-dd');
    this.selectedTimeInizio = format(now, 'HH:mm');
    this.selectedDateFine = format(now, 'yyyy-MM-dd');
    this.selectedTimeFine = format(now, 'HH:mm');
    this.minDate = format(now, 'yyyy-MM-dd');
  }
  async onSubmit(): Promise<void> {
    const dataInizioCombined = `${this.selectedDateInizio}T${this.selectedTimeInizio}:00`;
    const dataFineCombined = `${this.selectedDateFine}T${this.selectedTimeFine}:00`;

    this.newEvent.dataInizio = dataInizioCombined;
    this.newEvent.dataFine = dataFineCombined;

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