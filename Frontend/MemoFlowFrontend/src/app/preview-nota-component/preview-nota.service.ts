import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { Nota } from './preview-note.model'; // Assicurati di avere un modello/interfaccia Nota (vedi punto 5)
import { NotaCreateDTO } from './preview-nota-create.dto'; // Modello per la creazione (vedi punto 5)

@Injectable({ providedIn: 'root' })
export class PreviewNotaService {
  constructor(private axios: AxiosService) {}

  // Modifica per accettare l'ID utente
  async getNoteByDateAndUser(date: string, userId: number): Promise<Nota[]> {
    try {
      // Il backend si aspetta /nota/perDataEUtente?data=YYYY-MM-DD&utenteId=X
      const response = await this.axios.get<Nota[]>(`/nota/perDataEUtente?data=${date}&utenteId=${userId}`);
      return response; // Axios restituisce direttamente il body della risposta
    } catch (error) {
      console.error('Errore nel recupero delle note per data e utente:', error);
      throw error; // Rilancia l'errore per gestirlo nel componente
    }
  }

  // Nuovo metodo per creare una nota
  async createNota(notaData: NotaCreateDTO): Promise<Nota> {
    try {
      const response = await this.axios.post<Nota>('/nota/creaNota', notaData);
      return response;
    } catch (error) {
      console.error('Errore nella creazione della nota:', error);
      throw error;
    }
  }

  // Potresti voler aggiungere metodi per aggiornare/eliminare che usano l'ID utente
  // (per coerenza, anche se il backend non lo forza ancora)

  // Metodo esistente, potresti volerlo rimuovere o modificarlo per non esistere più nel backend
  // getNoteByDate(date: string) {
  //   return this.axios.get<any[]>(`/nota/perData?data=${date}`);
  // }
}