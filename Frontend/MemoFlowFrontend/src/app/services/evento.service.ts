// src/app/services/evento.service.ts

import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { Observable, from, throwError } from 'rxjs';
import { EventoDTO, EventoCreateDTO, EventoCambiaDTO, EventoStato } from '../models/evento.model';
import { AuthService } from '../auth/auth.service';
import { HttpParams } from '@angular/common/http'; 
import { EventoDTO, EventoCreateDTO, EventoCambiaDTO, EventoStato } from '../models/evento.model'; 
import { AuthService } from '../auth/auth.service'; 
import { AxiosRequestConfig } from 'axios';


@Injectable({
  providedIn: 'root'
})
export class EventoService {
  private readonly BASE_URL = '/eventi';

  constructor(
    private axiosService: AxiosService,
    private authService: AuthService
  ) { }

  getAllEventi(): Observable<EventoDTO[]> {
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/listaTutti`));
  }

  getEventoById(id: number): Observable<EventoDTO> {
    return from(this.axiosService.get<EventoDTO>(`${this.BASE_URL}/eventoSingolo/${id}`));
  }

  createEvento(evento: EventoCreateDTO): Observable<EventoDTO> {
    return from(this.axiosService.post<EventoDTO>(`${this.BASE_URL}/creaEvento`, evento));
  }

  updateEvento(id: number, evento: EventoCambiaDTO): Observable<EventoDTO> {
    return from(this.axiosService.put<EventoDTO>(`${this.BASE_URL}/modificaEvento/${id}`, evento));
  }

  deleteEvento(id: number): Observable<void> {
    return from(this.axiosService.delete<void>(`${this.BASE_URL}/eliminaEvento/${id}`));
  }

  getEventiByDate(data: string, userId: number | null): Observable<EventoDTO[]> {
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi per data.');
      return throwError(() => new Error('User ID is null. Cannot fetch events by date.'));
    }

    const inizioGiorno = `${data}T00:00:00`;
    const fineGiorno = `${data}T23:59:59`;

    const inizioGiorno = `${data}T00:00:00`; 
    const fineGiorno = `${data}T23:59:59`; 


    console.log(`Chiamata a getEventiBetweenDates con inizio: ${inizioGiorno}, fine: ${fineGiorno}, userId: ${userId}`);
    // Nota: L'endpoint /tra-due-date nel tuo controller Spring Boot non usa più il userId dal frontend,
    // ma lo ottiene internamente. Qui lo passiamo lo stesso, ma il backend lo ignorerà se autenticato.
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/tra-due-date`, { params: { inizio: inizioGiorno, fine: fineGiorno, userId: userId } }));
  }

  getEventiByStatoAndUtente(stato: EventoStato): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi per stato.');
      return throwError(() => new Error('User ID is null. Cannot fetch events by status.'));
    }
    // Nota: Anche qui, il backend ottiene userId internamente per questo endpoint.
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/stato/utente`, { params: { stato: stato.toString() } }));
  }

  getEventiAfterDataInizio(dataInizio: string): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi dopo la data di inizio.');
      return throwError(() => new Error('User ID is null. Cannot fetch events after start date.'));
    }
    // Nota: Anche qui, il backend ottiene userId internamente per questo endpoint.
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/dopo-inizio`, { params: { dataInizio } }));
  }

  getEventiBeforeDataFine(dataFine: string): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi prima della data di fine.');
      return throwError(() => new Error('User ID is null. Cannot fetch events before end date.'));
    }
    // Nota: Anche qui, il backend ottiene userId internamente per questo endpoint.
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/prima-fine`, { params: { dataFine } }));
  }

  getEventiBetweenDates(inizio: string, fine: string, currentUserId: number): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi tra due date.');
      return throwError(() => new Error('User ID is null. Cannot fetch events between dates.'));
    }
    // Nota: Questo metodo potrebbe essere duplicato con getEventiByDate, considera di unificarli.
    // L'endpoint /tra-due-date nel backend ora prende userId dal token JWT e lo ignora se passato come param.
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/tra-due-date`, { params: { inizio, fine, userId } }));
  }

  ricercaEventiAvanzata(titolo?: string, keywords?: string): Observable<EventoDTO[]> {
    const params: any = {};
    if (titolo) {
      params.titolo = titolo;
   }
    if (keywords) {
      params.keywords = keywords;
    }

    // Passa un oggetto JavaScript a Axios
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/ricercaAvanzata`, { params: params }));
 }
}
    getEventsBetweenDates(inizio: string, fine: string, currentUserId: number): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null || userId !== currentUserId) {
        console.error('Errore: User ID non corrispondente o nullo.');
        return throwError(() => new Error('User ID is null or mismatch. Cannot fetch events.'));
    }

    const config: AxiosRequestConfig = {
        params: {
            inizioMese: inizio,
            fineMese: fine
        }
    };
    
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/eventi-del-mese`, config));
  }
}