import { User } from '../user/user.model';
import { TipoNota } from '../preview-nota-component/tipo-nota.enum';
export interface Nota {
  id?: number;
  titolo: string;
  tipoNota: TipoNota;
  contenutoTesto?: string;
  contenutoSVG?: string;
  dataCreazione?: string;
  ultimaModifica?: string;
  utenteDTO?: User;
}