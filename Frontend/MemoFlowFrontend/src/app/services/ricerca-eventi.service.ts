import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { EventoDTO } from '../models/evento.model';

@Injectable({ providedIn: 'root' })
export class RicercaEventiService {
  private risultatiSource = new BehaviorSubject<EventoDTO[]>([]);
  risultati$ = this.risultatiSource.asObservable();

  setRisultati(risultati: EventoDTO[]) {
    this.risultatiSource.next(risultati);
  }
}
