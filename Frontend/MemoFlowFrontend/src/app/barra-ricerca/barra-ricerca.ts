import { Component } from '@angular/core';
import { EventoDTO } from '../models/evento.model';
import { AuthService } from '../auth/auth.service';
import { EventoService } from '../services/evento.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-barra-ricerca',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './barra-ricerca.html',
  styleUrls: ['./barra-ricerca.css']
})
export class BarraRicerca {
  searchTermTitolo: string = '';
  searchTermKeywords: string = '';
  searchResults: EventoDTO[] = [];
  isLoadingSearch: boolean = false;

  constructor(
    private eventoService: EventoService,
    private authService: AuthService
  ) { }

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
