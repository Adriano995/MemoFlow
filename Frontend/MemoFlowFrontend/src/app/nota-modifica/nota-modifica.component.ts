// nota-modifica.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PreviewNotaService } from '../preview-nota-component/preview-nota.service'; // Assicurati che il percorso sia corretto
import { Nota } from '../preview-nota-component/preview-note.model'; // Assicurati che il percorso sia corretto
import { TipoNota } from '../preview-nota-component/tipo-nota.enum'; // Assicurati che il percorso sia corretto

@Component({
  selector: 'app-nota-modifica',
  standalone: true,
  imports: [CommonModule, FormsModule], // FormsModule è indispensabile per [(ngModel)]
  templateUrl: './nota-modifica.component.html',
  styleUrls: ['./nota-modifica.component.css']
})
export class NotaModificaComponent implements OnInit {
  notaId: number | null = null;
  nota: Nota | null = null; // La nota completa recuperata
  loading = true;
  error: string | null = null;
  successMessage: string | null = null;

  // Campi per la modifica (legati a ngModel)
  titoloModifica: string = '';
  contenutoTestoModifica: string = '';
  contenutoSVGModifica: string = ''; // Se la nota è di tipo DISEGNO e modificabile
  tipoNotaModifica: TipoNota | null = null; // Se il tipo nota è modificabile

  // Per il dropdown di TipoNota se modificabile
  tipiNota = Object.values(TipoNota);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private previewNotaService: PreviewNotaService
  ) { }

  ngOnInit(): void {
    // Recupera l'ID della nota dalla route
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.notaId = +idParam; // Converte la stringa in numero
        this.fetchNotaDettaglio(this.notaId);
      } else {
        this.error = 'ID nota non fornito nell\'URL.';
        this.loading = false;
      }
    });
  }

  async fetchNotaDettaglio(id: number): Promise<void> {
    this.loading = true;
    this.error = null;
    this.successMessage = null; // Resetta messaggi
    try {
      this.nota = await this.previewNotaService.getNotaById(id);
      if (this.nota) {
        // Popola i campi del form per la modifica con i valori attuali della nota
        this.titoloModifica = this.nota.titolo;
        this.contenutoTestoModifica = this.nota.contenutoTesto || '';
        this.contenutoSVGModifica = this.nota.contenutoSVG || '';
        this.tipoNotaModifica = this.nota.tipoNota; // Inizializza anche il tipo
      }
    } catch (err: any) {
      console.error('Errore nel recupero della nota:', err);
      this.error = 'Impossibile caricare i dettagli della nota. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  async onUpdateNota(): Promise<void> {
    if (!this.notaId || !this.nota) {
      this.error = 'Dati nota mancanti per l\'aggiornamento.';
      return;
    }

    this.loading = true;
    this.error = null;
    this.successMessage = null;

    try {
      // Prepara i dati da inviare al backend
      const updatedNotaData: Partial<Nota> = {
        titolo: this.titoloModifica,
        tipoNota: this.tipoNotaModifica || TipoNota.TESTO // Usa il valore del form o un default
      };

      if (this.tipoNotaModifica === TipoNota.TESTO) {
        updatedNotaData.contenutoTesto = this.contenutoTestoModifica;
        updatedNotaData.contenutoSVG = undefined; // Assicurati che l'altro campo sia vuoto se cambi tipo
      } else if (this.tipoNotaModifica === TipoNota.DISEGNO) {
        updatedNotaData.contenutoSVG = this.contenutoSVGModifica;
        updatedNotaData.contenutoTesto = undefined; // Assicurati che l'altro campo sia vuoto
      }

      await this.previewNotaService.updateNota(this.notaId, updatedNotaData);
      this.successMessage = 'Nota aggiornata con successo!';
      // Opzionalmente ricarica la nota per mostrare le modifiche persistenti dal backend
      // await this.fetchNotaDettaglio(this.notaId);
      this.router.navigate(['/dashboard']); // Reindirizza alla dashboard
    } catch (err: any) {
      console.error('Errore durante l\'aggiornamento della nota:', err);
      this.error = 'Errore durante l\'aggiornamento della nota. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  async onDeleteNota(): Promise<void> {
    if (!this.notaId) {
      this.error = 'ID nota mancante per l\'eliminazione.';
      return;
    }

    if (!confirm('Sei sicuro di voler eliminare definitivamente questa nota? Questa azione non può essere annullata.')) {
      return;
    }

    this.loading = true;
    this.error = null;
    this.successMessage = null;

    try {
      await this.previewNotaService.deleteNota(this.notaId);
      this.successMessage = 'Nota eliminata con successo!';
      // Naviga via dopo l'eliminazione
      this.router.navigate(['/dashboard']);
    } catch (err: any) {
      console.error('Errore durante l\'eliminazione della nota:', err);
      this.error = 'Errore durante l\'eliminazione della nota. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  // Torna alla dashboard
  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}