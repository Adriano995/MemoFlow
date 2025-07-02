// nota-dettaglio.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PreviewNotaService } from '../preview-nota-component/preview-nota.service';
import { Nota } from '../preview-nota-component/preview-note.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-nota-dettaglio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nota-dettaglio.component.html',
  styleUrls: ['./nota-dettaglio.component.css']
})
export class NotaDettaglioComponent implements OnInit {
  notaId: number | null = null;
  nota: Nota | null = null;
  loading = true;
  error: string | null = null;

  // Campi per la modifica
  titoloModifica: string = '';
  contenutoTestoModifica: string = '';

  constructor(
    private route: ActivatedRoute, // Per leggere i parametri dell'URL
    private router: Router, // Per la navigazione
    private previewNotaService: PreviewNotaService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.notaId = +idParam; // Converte la stringa in numero
        this.fetchNotaDettaglio(this.notaId);
      } else {
        this.error = 'ID nota non fornito.';
        this.loading = false;
      }
    });
  }

  async fetchNotaDettaglio(id: number): Promise<void> {
    this.loading = true;
    this.error = null;
    try {
      this.nota = await this.previewNotaService.getNotaById(id);
      // Prepopola i campi del form per la modifica
      if (this.nota) {
        this.titoloModifica = this.nota.titolo;
        this.contenutoTestoModifica = this.nota.contenutoTesto || '';
      }
    } catch (err: any) {
      console.error('Errore nel recupero della nota:', err);
      this.error = 'Impossibile caricare i dettagli della nota.';
    } finally {
      this.loading = false;
    }
  }

  async onUpdateNota(): Promise<void> {
    if (!this.notaId || !this.nota) {
      console.error('ID nota o dati nota mancanti per l\'aggiornamento.');
      return;
    }

    this.loading = true; // Mostra lo stato di caricamento durante l'aggiornamento
    this.error = null;

    try {
      const updatedNotaData = {
        titolo: this.titoloModifica,
        contenutoTesto: this.contenutoTestoModifica
        // Non includere tipoNota, utenteId, dataNota a meno che non siano modificabili.
        // Se il backend si aspetta solo i campi modificati, Partial<NotaCreateDTO> è perfetto.
      };
      const result = await this.previewNotaService.updateNota(this.notaId, updatedNotaData);
      console.log('Nota aggiornata con successo:', result);
      alert('Nota aggiornata con successo!');
      this.router.navigate(['/dashboard']); // Torna alla dashboard dopo l'aggiornamento
    } catch (err: any) {
      console.error('Errore durante l\'aggiornamento della nota:', err);
      this.error = 'Errore durante l\'aggiornamento della nota.';
    } finally {
      this.loading = false;
    }
  }

  async onDeleteNotaFromDetail(): Promise<void> {
    if (!this.notaId) {
      console.error('ID nota mancante per l\'eliminazione.');
      return;
    }

    if (!confirm('Sei sicuro di voler eliminare definitivamente questa nota?')) {
      return;
    }

    try {
      await this.previewNotaService.deleteNota(this.notaId);
      console.log(`Nota ${this.notaId} eliminata dalla pagina di dettaglio.`);
      alert('Nota eliminata con successo!');
      this.router.navigate(['/dashboard']); // Torna alla dashboard dopo l'eliminazione
    } catch (error) {
      console.error(`Errore nell'eliminazione della nota ${this.notaId}:`, error);
      this.error = 'Errore durante l\'eliminazione della nota.';
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']); // O router.back() se preferisci tornare indietro nella cronologia
  }
}