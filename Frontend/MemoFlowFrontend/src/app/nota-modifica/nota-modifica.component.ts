// nota-modifica.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PreviewNotaService } from '../preview-nota-component/preview-nota.service'; 
import { Nota } from '../preview-nota-component/preview-note.model'; 
import { TipoNota } from '../preview-nota-component/tipo-nota.enum'; 
import { SafeHtmlPipe } from './safe-html.pipe'; // <-- AGGIUNGI QUESTA RIGA: Assicurati che il percorso sia corretto

@Component({
  selector: 'app-nota-modifica',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeHtmlPipe], // <-- AGGIUNGI SafeHtmlPipe QUI
  templateUrl: './nota-modifica.component.html',
  styleUrls: ['./nota-modifica.component.css']
})
export class NotaModificaComponent implements OnInit {
  notaId: number | null = null;
  nota: Nota | null = null; 
  loading = true;
  error: string | null = null;
  successMessage: string | null = null;

  titoloModifica: string = '';
  contenutoTestoModifica: string = '';
  contenutoSVGModifica: string = ''; 
  tipoNotaModifica: TipoNota | null = null; 

  tipiNota = Object.values(TipoNota);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private previewNotaService: PreviewNotaService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.notaId = +idParam; 
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
    this.successMessage = null;
    try {
      this.nota = await this.previewNotaService.getNotaById(id);
      console.log('Nota recuperata in NotaModificaComponent:', this.nota);

      if (this.nota) {
        this.titoloModifica = this.nota.titolo;
        this.contenutoTestoModifica = this.nota.contenutoTesto || '';
        this.contenutoSVGModifica = this.nota.contenutoSVG || '';
        this.tipoNotaModifica = this.nota.tipoNota; 
        console.log('Campi del form popolati:', {
          titolo: this.titoloModifica,
          contenutoTesto: this.contenutoTestoModifica,
          contenutoSVG: this.contenutoSVGModifica,
          tipoNota: this.tipoNotaModifica
        });

        this.cdr.detectChanges(); 
      }
    } catch (err: any) {
      console.error('Errore nel recupero della nota:', err);
      this.error = 'Impossibile caricare i dettagli della nota. Riprova.';
    } finally {
      this.loading = false;
      console.log('Stato finale loading in NotaModificaComponent:', this.loading);
      this.cdr.detectChanges(); 
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
      const updatedNotaData: Partial<Nota> = {
        titolo: this.titoloModifica,
        tipoNota: this.tipoNotaModifica || TipoNota.TESTO 
      };

      if (this.tipoNotaModifica === TipoNota.TESTO) {
        updatedNotaData.contenutoTesto = this.contenutoTestoModifica;
        updatedNotaData.contenutoSVG = undefined; 
      } else if (this.tipoNotaModifica === TipoNota.DISEGNO) {
        updatedNotaData.contenutoSVG = this.contenutoSVGModifica;
        updatedNotaData.contenutoTesto = undefined; 
      }

      await this.previewNotaService.updateNota(this.notaId, updatedNotaData);
      this.successMessage = 'Nota aggiornata con successo!';
      this.router.navigate(['/dashboard']); 
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
      this.router.navigate(['/dashboard']);
    } catch (err: any) {
      console.error('Errore durante l\'eliminazione della nota:', err);
      this.error = 'Errore durante l\'eliminazione della nota. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}