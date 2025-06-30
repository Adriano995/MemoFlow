// nota-create.dto.ts
import { TipoNota } from './tipo-nota.enum'; // Assicurati che il percorso sia corretto

export interface NotaCreateDTO {
  titolo: string;
  contenutoTesto?: string;
  contenutoSVG?: string;
  tipoNota: TipoNota;
  utenteId: number; // Questo è l'ID dell'utente a cui associare la nota
  dataNota: string;
}