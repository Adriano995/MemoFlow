import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { Nota } from '../models/preview-note.model';
import { NotaCreateDTO } from '../preview-nota-component/preview-nota-create.dto';

@Injectable({ providedIn: 'root' })
export class PreviewNotaService {
  constructor(private axios: AxiosService) {}

  async getNoteByDateAndUser(date: string, userId: number): Promise<Nota[]> {
    try {
    const response = await this.axios.get<Nota[]>(`/nota/perDataEUtente?data=${date}&utenteId=${userId}`);
    return response;
    } catch (error) {
    console.error('Errore nel recupero delle note per data e utente:', error);
    throw error;
    }
  }

  async getNoteByUser(userId: number): Promise<Nota[]> {
    try {
    const response = await this.axios.get<Nota[]>(`/nota/perUtente?utenteId=${userId}`);
    return response;
    } catch (error) {
    console.error('Errore nel recupero delle note per utente:', error);
    throw error;
    }
  }

  async createNota(notaData: NotaCreateDTO): Promise<Nota> {
    try {
    const response = await this.axios.post<Nota>('/nota/creaNota', notaData);
    return response;
    } catch (error) {
    console.error('Errore nella creazione della nota:', error);
    throw error;
    }
  }

  async deleteNota(notaId: number): Promise<void> {
    try {
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
    return response;
    } catch (error: any) {
    if (error.response && error.response.status === 404) {
      console.error(`Nota con ID ${notaId} non trovata.`);
      throw new Error('Nota non trovata.');
    }
    console.error(`Errore nel recupero della nota ${notaId}:`, error);
    throw error;
    }
  }

   async updateNota(notaId: number, notaData: Partial<NotaCreateDTO>): Promise<Nota> {
   try {
     const response = await this.axios.put<Nota>(`/nota/aggiornaNota/${notaId}`, notaData);
     return response;
   } catch (error) {
     console.error(`Errore nell'aggiornamento della nota ${notaId}:`, error);
     throw error;
    }
  }

}