import { TipoNota } from './tipo-nota.enum';
export interface NotaCreateDTO {
  titolo: string;
  contenutoTesto?: string;
  contenutoSVG?: string;
  tipoNota: TipoNota;
  utenteId: number; 
  dataNota: string;
}