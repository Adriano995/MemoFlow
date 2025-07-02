// preview-nota.service.ts
import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { Nota } from './preview-note.model';
import { NotaCreateDTO } from './preview-nota-create.dto';
// Non è più necessaria l'importazione di AxiosResponse qui,
// perché AxiosService gestisce già l'estrazione del 'data'.

@Injectable({ providedIn: 'root' })
export class PreviewNotaService {
  constructor(private axios: AxiosService) {}

  async getNoteByDateAndUser(date: string, userId: number): Promise<Nota[]> {
    try {
      // Il tuo AxiosService restituisce già Promise<Nota[]>, non Promise<AxiosResponse<Nota[]>>
      const response = await this.axios.get<Nota[]>(`/nota/perDataEUtente?data=${date}&utenteId=${userId}`);
      return response; // Restituiamo direttamente la risposta (che è già Nota[])
    } catch (error) {
      console.error('Errore nel recupero delle note per data e utente:', error);
      throw error;
    }
  }

  async getNoteByUser(userId: number): Promise<Nota[]> {
    try {
      const response = await this.axios.get<Nota[]>(`/nota/perUtente?utenteId=${userId}`);
      return response; // Restituiamo direttamente la risposta (che è già Nota[])
    } catch (error) {
      console.error('Errore nel recupero delle note per utente:', error);
      throw error;
    }
  }

  async createNota(notaData: NotaCreateDTO): Promise<Nota> {
    try {
      const response = await this.axios.post<Nota>('/nota/creaNota', notaData);
      return response; // Restituiamo direttamente la risposta (che è già Nota)
    } catch (error) {
      console.error('Errore nella creazione della nota:', error);
      throw error;
    }
  }

  /**
   * Metodo per eliminare una nota tramite il suo ID.
   * @param notaId L'ID della nota da eliminare.
   */
  async deleteNota(notaId: number): Promise<void> {
    try {
      // Per DELETE, spesso non c'è un corpo di risposta che ti interessa
      // AxiosService.delete restituisce Promise<T>, ma in questo caso il Promise<void> è sufficiente
      await this.axios.delete<void>(`/nota/eliminaNota/${notaId}`);
      console.log(`Nota con ID ${notaId} eliminata con successo dal backend.`);
    } catch (error) {
      console.error(`Errore nell'eliminazione della nota ${notaId}:`, error);
      throw error;
    }
  }

  async getNotaById(notaId: number): Promise<Nota> {
    try {
      const response = await this.axios.get<Nota>(`/nota/${notaId}`);
      return response; // Restituiamo direttamente la risposta (che è già Nota)
    } catch (error: any) {
      if (error.response && error.response.status === 404) {
        console.error(`Nota con ID ${notaId} non trovata.`);
        throw new Error('Nota non trovata.');
      }
      console.error(`Errore nel recupero della nota ${notaId}:`, error);
      throw error;
    }
  }

  // Metodo per aggiornare una nota (PUT o PATCH)
  async updateNota(notaId: number, notaData: Partial<NotaCreateDTO>): Promise<Nota> {
    try {
      const response = await this.axios.put<Nota>(`/nota/aggiornaNota/${notaId}`, notaData);
      return response; // Restituiamo direttamente la risposta (che è già Nota)
    } catch (error) {
      console.error(`Errore nell'aggiornamento della nota ${notaId}:`, error);
      throw error;
    }
  }
}