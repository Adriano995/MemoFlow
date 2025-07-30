// src/app/services/evento.service.ts

import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { Observable, from, throwError } from 'rxjs';
import { EventoDTO, EventoCreateDTO, EventoCambiaDTO, EventoStato } from '../models/evento.model'; 
import { AuthService } from '../auth/auth.service'; 

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
    return from(this.axiosService.put<EventoDTO>(`${this.BASE_URL}/modificaEvento/${id}`, evento)); // Modificato qui: modificato /modifica a /modificaEvento
  }

  deleteEvento(id: number): Observable<void> {
    return from(this.axiosService.delete<void>(`${this.BASE_URL}/eliminaEvento/${id}`)); // Modificato qui: modificato /elimina a /eliminaEvento
  }

  // **** MODIFICA QUI ****
  getEventiByDate(data: string, userId: number | null): Observable<EventoDTO[]> {
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi per data.');
      return throwError(() => new Error('User ID is null. Cannot fetch events by date.'));
    }

    // Formatta la data per coprire l'intero giorno
    const inizioGiorno = `${data}T00:00:00`; // Esempio: "2025-07-29T00:00:00"
    const fineGiorno = `${data}T23:59:59`;   // Esempio: "2025-07-29T23:59:59"

    console.log(`Chiamata a getEventiBetweenDates con inizio: ${inizioGiorno}, fine: ${fineGiorno}, userId: ${userId}`);
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/tra-due-date`, { params: { inizio: inizioGiorno, fine: fineGiorno, userId: userId } }));
  }
  // **** FINE MODIFICA ****

  getEventiByStatoAndUtente(stato: EventoStato): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi per stato.');
      return throwError(() => new Error('User ID is null. Cannot fetch events by status.'));
    }
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/stato/utente`, { params: { stato: stato.toString(), userId } }));
  }

  getEventiAfterDataInizio(dataInizio: string): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi dopo la data di inizio.');
      return throwError(() => new Error('User ID is null. Cannot fetch events after start date.'));
    }
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/dopo-inizio`, { params: { dataInizio, userId } }));
  }

  getEventiBeforeDataFine(dataFine: string): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi prima della data di fine.');
      return throwError(() => new Error('User ID is null. Cannot fetch events before end date.'));
    }
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/prima-fine`, { params: { dataFine, userId } }));
  }

  getEventiBetweenDates(inizio: string, fine: string, currentUserId: number): Observable<EventoDTO[]> {
    const userId = this.authService.getUserId();
    if (userId === null) {
      console.error('Errore: User ID è null. Impossibile recuperare eventi tra due date.');
      return throwError(() => new Error('User ID is null. Cannot fetch events between dates.'));
    }
    return from(this.axiosService.get<EventoDTO[]>(`${this.BASE_URL}/tra-due-date`, { params: { inizio, fine, userId } }));
  }
}