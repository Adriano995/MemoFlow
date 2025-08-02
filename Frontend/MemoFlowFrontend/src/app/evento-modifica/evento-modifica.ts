import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventoService } from '../services/evento.service';
import { EventoCambiaDTO, EventoDTO } from '../models/evento.model';
import { format, parseISO } from 'date-fns';

@Component({
  selector: 'app-evento-modifica',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evento-modifica.component.html',
  styleUrls: ['./evento-modifica.component.css']
})
export class EventoModificaComponent implements OnInit {
  eventoId: number | null = null;
  evento: EventoDTO | null = null;
  modificaEvento: EventoCambiaDTO = {};
  selectedDateInizio: string = '';
  selectedTimeInizio: string = '';
  selectedDateFine: string = '';
  selectedTimeFine: string = '';

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

  loadEvento(id: number): void {
    this.eventoService.getEventoById(id).subscribe(
      (evento) => {
        this.evento = evento;
        this.modificaEvento = { ...evento };
        
        if (evento.dataInizio) {
          const dataInizio = parseISO(evento.dataInizio);
          this.selectedDateInizio = format(dataInizio, 'yyyy-MM-dd');
          this.selectedTimeInizio = format(dataInizio, 'HH:mm');
        }

        if (evento.dataFine) {
          const dataFine = parseISO(evento.dataFine);
          this.selectedDateFine = format(dataFine, 'yyyy-MM-dd');
          this.selectedTimeFine = format(dataFine, 'HH:mm');
        }
      },
      error => {
        console.error('Errore nel caricamento dell\'evento:', error);
      }
    );
  }

  async onSubmit(): Promise<void> {
    if (!this.eventoId) {
      console.error('ID evento non disponibile.');
      return;
    }

      const dataInizioCombined = `${this.selectedDateInizio}T${this.selectedTimeInizio}:00`;
      const dataFineCombined = `${this.selectedDateFine}T${this.selectedTimeFine}:00`;

    if (new Date(dataFineCombined) < new Date(dataInizioCombined)) {
      alert('La data di fine non può essere precedente alla data di inizio.');
      return;
    }

    this.modificaEvento.dataInizio = dataInizioCombined;
    this.modificaEvento.dataFine = dataFineCombined;
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