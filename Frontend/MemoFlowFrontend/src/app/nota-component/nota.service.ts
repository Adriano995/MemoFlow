import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';

@Injectable({ providedIn: 'root' })
export class NotaService {
  constructor(private axios: AxiosService) {}

  // Cambia l'URL in base al tuo endpoint reale!
  getNoteByDate(date: string) {
    return this.axios.get<any[]>(`/nota/perData?data=${date}`);
  }
}