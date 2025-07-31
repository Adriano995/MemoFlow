export enum EventoStato {
  STATO_PROGRAMMATO = 'STATO_PROGRAMMATO',
  STATO_IN_CORSO = 'STATO_IN_CORSO',
  STATO_CONCLUSO = 'STATO_CONCLUSO',
  STATO_ANNULLATO = 'STATO_ANNULLATO',
}

export interface EventoDTO {
dataInizio: string;
dataFine: string;
  id: number;
  titolo: string;
  descrizione?: string;
  oraInizio?: string;
  oraFine?: string;
  luogo?: string;
  stato: EventoStato;
}

export interface EventoCreateDTO {
  titolo: string;
  descrizione?: string;
  dataInizio: string;
  dataFine: string;
  luogo?: string;
}

export interface EventoCambiaDTO {
  titolo?: string;
  descrizione?: string;
  dataInizio?: string;
  dataFine?: string;
  luogo?: string;
  stato?: EventoStato;
}