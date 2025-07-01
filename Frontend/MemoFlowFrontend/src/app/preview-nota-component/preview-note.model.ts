import { User } from '../user/user.model';
import { TipoNota } from './tipo-nota.enum'; 

export interface Nota {
  id?: number; // Optional perché potrebbe non esserci per le nuove note
  titolo: string;
  tipoNota: TipoNota;
  contenutoTesto?: string;
  contenutoSVG?: string;
  dataCreazione?: string; // Puoi usare string per ISO date o Date
  ultimaModifica?: string; // Puoi usare string per ISO date o Date
  utenteDTO?: User; // Corrisponde a utenteDTO in NotaDTO.java
}