import { Component, Input, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core'; // Importa ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { NotaService } from './nota.service';
import { Nota } from './note.model'; // Assicurati che questo path sia corretto

@Component({
  selector: 'app-nota-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './nota-component.html',
  styleUrls: ['./nota-component.css']
})
export class NotaComponent implements OnChanges {
  @Input() date: Date | null = null;
  @Input() userId: number | null = null; // NUOVO: L'ID utente come Input

  nota: Nota[] = [];
  loading = false;
  error: string | null = null;

  constructor(private notaService: NotaService, private cdr: ChangeDetectorRef) {} // Iniettare ChangeDetectorRef

  ngOnChanges(changes: SimpleChanges) {
    console.log('ngOnChanges triggered in NotaComponent', changes); // Debugging
    // Ora il fetch delle note dipende sia dalla data che dall'ID utente
    if (this.date && this.userId) {
      console.log('Data and UserId present, fetching notes...'); // Debugging
      this.fetchNota();
    } else if (this.date && !this.userId) {
        console.warn('Data present but userId missing.'); // Debugging
        this.error = "ID utente non disponibile. Effettua il login per visualizzare le note.";
        this.nota = []; // Pulisci le note
        this.cdr.detectChanges(); // Forza l'aggiornamento della vista
    } else {
        console.log('No date selected, clearing notes.'); // Debugging
        this.nota = []; // Se non c'è data selezionata, non mostrare note
        this.cdr.detectChanges(); // Forza l'aggiornamento della vista
    }
  }

  async fetchNota() {
    if (!this.date || !this.userId) {
      console.warn('Impossibile recuperare le note: data o ID utente mancante.');
      this.nota = [];
      this.cdr.detectChanges(); // Forza l'aggiornamento della vista
      return;
    }

    this.loading = true;
    this.error = null;
    this.nota = [];
    this.cdr.detectChanges(); // Forza l'aggiornamento della vista per mostrare "Caricamento..."

    try {
      const dateStr = this.date.toISOString().slice(0, 10); // "YYYY-MM-DD"
      console.log('Chiamo il backend per note di data:', dateStr, 'e utente ID:', this.userId);
      const result = await this.notaService.getNoteByDateAndUser(dateStr, this.userId); // Passa l'ID utente
      this.nota = result || [];
      console.log('Note recuperate:', this.nota);
    } catch (err: any) {
      console.error('Errore nel recupero note:', err);
      this.error = 'Errore nel caricamento delle note.';
    } finally {
      this.loading = false;
      this.cdr.detectChanges(); // Forza l'aggiornamento della vista con i dati o l'errore finale
    }
  }
}