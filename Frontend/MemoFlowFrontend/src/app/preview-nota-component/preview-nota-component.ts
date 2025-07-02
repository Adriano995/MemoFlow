// preview-nota-component.ts

import { Component, Input, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewNotaService } from './preview-nota.service';
import { Nota } from './preview-note.model';
import { Router } from '@angular/router'; // Importa Router

@Component({
  selector: 'app-nota-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './preview-nota-component.html',
  styleUrls: ['./preview-nota-component.css']
})
export class PreviewNotaComponent implements OnChanges {
  @Input() date: Date | null = null;
  @Input() userId: number | null = null;

  nota: Nota[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private previewNotaService: PreviewNotaService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    console.log('ngOnChanges triggered in NotaComponent', changes);
    if (this.date && this.userId) {
      console.log('Data and UserId present, fetching notes...');
      this.fetchNota();
    } else if (this.date && !this.userId) {
        console.warn('Data present but userId missing.');
        this.error = "ID utente non disponibile. Effettua il login per visualizzare le note.";
        this.nota = [];
        this.cdr.detectChanges();
    } else {
        console.log('No date selected, clearing notes.');
        this.nota = [];
        this.cdr.detectChanges();
    }
  }

  async fetchNota() {
    if (!this.date || !this.userId) {
      console.warn('Impossibile recuperare le note: data o ID utente mancante.');
      this.nota = [];
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    this.error = null;
    this.nota = [];
    this.cdr.detectChanges();

    try {
      const year = this.date.getFullYear();
      const month = (this.date.getMonth() + 1).toString().padStart(2, '0');
      const day = this.date.getDate().toString().padStart(2, '0');
      const dateStr = `${year}-${month}-${day}`;

      console.log('Chiamo il backend per note di data:', dateStr, 'e utente ID:', this.userId);
      const result = await this.previewNotaService.getNoteByDateAndUser(dateStr, this.userId);
      this.nota = result || [];
      console.log('Note recuperate:', this.nota);
    } catch (err: any) {
      console.error('Errore nel recupero delle note:', err);
      this.error = 'Impossibile caricare le note. Riprova più tardi.';
    } finally {
      this.loading = false;
      this.cdr.detectChanges();
    }
  }

  getPreviewText(text: string | undefined): string {
    if (!text) return '';
    const maxLength = 100;
    const linesToKeep = 2;

    const lines = text.split('\n');
    if (lines.length > linesToKeep) {
      return lines.slice(0, linesToKeep).join('\n') + '...';
    }

    if (text.length > maxLength) {
      return text.substring(0, maxLength) + '...';
    }

    return text;
  }

  onViewOrEditNota(notaId: number | undefined): void {
    if (notaId === undefined) {
      console.error('ID nota non definito per visualizzazione/modifica.');
      return;
    }
    this.router.navigate(['/nota-dettaglio', notaId]);
  }

  /**
   * Gestisce il click sul bottone "Crea Nuova Nota".
   * Reindirizza l'utente alla pagina di creazione della nota, passando data e userId.
   */
  onCreateNewNota(): void {
    if (this.date && this.userId) {
      // Usa la nuova funzione per formattare la data
      const formattedDate = this.formatDateToYYYYMMDD(this.date);
      this.router.navigate(['/crea-nota', { date: formattedDate, userId: this.userId }]);
    } else if (this.userId) {
        // Se non c'è una data selezionata ma c'è un utente, naviga solo con l'utenteId
        this.router.navigate(['/crea-nota', { userId: this.userId }]);
    } else {
        console.error("Impossibile creare una nota: ID utente non disponibile.");
        this.router.navigate(['/login']); // Reindirizza al login se non c'è utente
    }
  }

    private formatDateToYYYYMMDD(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  async onDeleteNota(notaId: number | undefined): Promise<void> {
    if (notaId === undefined) {
      console.error('ID nota non definito per eliminazione.');
      return;
    }

    if (!confirm('Sei sicuro di voler eliminare questa nota?')) {
      return;
    }

    try {
      await this.previewNotaService.deleteNota(notaId);
      console.log(`Nota con ID ${notaId} eliminata con successo.`);
      await this.fetchNota();
    } catch (error) {
      console.error(`Errore durante l'eliminazione della nota ${notaId}:`, error);
      alert('Errore durante l\'eliminazione della nota. Riprova più tardi.');
    }
  }
}