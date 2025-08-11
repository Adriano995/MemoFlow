import { Component, ChangeDetectorRef } from '@angular/core';
import { EventoDTO } from '../models/evento.model';
import { AuthService } from '../auth/auth.service';
import { EventoService } from '../services/evento.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RicercaEventiService } from '../services/ricerca-eventi.service';
import { of, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { ChangeDetectionStrategy } from '@angular/core';
import { NgZone } from '@angular/core';

@Component({
  selector: 'app-barra-ricerca',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  templateUrl: './barra-ricerca.html',
  styleUrls: ['./barra-ricerca.css']
})
export class BarraRicerca {
  searchTermTitolo: string = '';
  searchTermKeywords: string = '';
  searchResults: EventoDTO[] = [];
  isLoadingSearch: boolean = false;
  searchTermSubject = new Subject<string>();
  hasSearched = false;

  constructor(
    private eventoService: EventoService,
    private authService: AuthService,
    private ricercaService: RicercaEventiService,
    private router: Router,
    private cd: ChangeDetectorRef,
    private ngZone: NgZone, 
  ) {
    this.ricercaService.risultati$.subscribe(results => {
      this.ngZone.run(() => {
        this.searchResults = results;
        this.cd.markForCheck();
      });
    });

    this.searchTermSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if(term.trim().length === 0) {
          this.hasSearched = false;
          return of([]); 
        }
        this.isLoadingSearch = true;
        this.hasSearched = true;
        return this.eventoService.ricercaEventiAvanzata(term.trim(), '');
      })
    ).subscribe({
      next: data => {
        this.ricercaService.setRisultati(data);
        this.isLoadingSearch = false;
      },
      error: () => {
        this.ricercaService.setRisultati([]);
        this.isLoadingSearch = false;
      }
    });
  }

  onSearchTermChange(value: string): void {
    this.searchTermSubject.next(value);
  }

  selectSuggestion(titolo: string) {
    this.searchTermTitolo = titolo;
    this.ricercaService.setRisultati([]);
    this.hasSearched = false;
  }

  eseguiRicerca(): void {
    this.isLoadingSearch = true;
    this.searchResults = [];  
    this.ricercaService.setRisultati([]); 
    this.eventoService.ricercaEventiAvanzata(this.searchTermTitolo.trim(), '').subscribe({
      next: data => {
        this.ricercaService.setRisultati(data);
        this.isLoadingSearch = false;
        this.cd.detectChanges();
      },
      error: () => {
        this.ricercaService.setRisultati([]);
        this.isLoadingSearch = false;
        this.cd.detectChanges();
      }
    });
  }

  resetRicerca(): void {
    this.searchTermTitolo = '';
    this.searchTermKeywords = '';
    this.searchResults = [];
    this.ricercaService.setRisultati([]);
    console.log("Ricerca resettata.");
  }

  apriEvento(id: number): void {
    this.router.navigate(['/modifica-evento', id]);
  }

  trackById(index: number, item: EventoDTO): number {
    return item.id;
  }

}
