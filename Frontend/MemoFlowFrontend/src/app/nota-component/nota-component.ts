import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotaService } from './nota.service';

@Component({
  selector: 'app-nota-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './nota-component.html',
  styleUrls: ['./nota-component.css']
})
export class NotaComponent implements OnChanges {
  @Input() date: Date | null = null;
  nota: any[] = [];
  loading = false;
  error: string | null = null;

  constructor(private notaService: NotaService) {}

  ngOnChanges(changes: SimpleChanges) {
    if (this.date) {
      this.fetchNota();
    }
  }

  async fetchNota() {
    this.loading = true;
    this.error = null;
    this.nota = [];
    try {
      const dateStr = this.date!.toISOString().slice(0, 10); // "YYYY-MM-DD"
      console.log('Chiamo il backend per:', dateStr);
      const result = await this.notaService.getNoteByDate(dateStr);
      this.nota = result || [];
      console.log('Risposta:', this.nota);
    } catch (err: any) {
      console.error('Errore:', err);
      this.error = 'Nessuna nota trovata o errore di rete';
    } finally {
      this.loading = false;
    }
  }
}
