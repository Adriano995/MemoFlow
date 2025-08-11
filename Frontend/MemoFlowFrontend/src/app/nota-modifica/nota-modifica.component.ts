// nota-modifica.component.ts
import { Component, OnInit, ChangeDetectorRef, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PreviewNotaService } from '../services/preview-nota.service';
import { Nota } from '../models/preview-note.model';
import { TipoNota } from '../preview-nota-component/tipo-nota.enum';
import { SafeHtmlPipe } from './safe-html.pipe';
import { NgZone } from '@angular/core';

@Component({
  selector: 'app-nota-modifica',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeHtmlPipe],
  templateUrl: './nota-modifica.component.html',
  styleUrls: ['./nota-modifica.component.css']
})
export class NotaModificaComponent implements OnInit, OnDestroy {
  @ViewChild('drawingCanvas', { static: false }) drawingCanvas!: ElementRef<HTMLDivElement>;

  notaId: number | null = null;
  nota: Nota | null = null;
  loading = true;
  error: string | null = null;
  successMessage: string | null = null;

  titoloModifica = '';
  contenutoTestoModifica = '';
  contenutoSVGModifica = '';
  tipoNotaModifica: TipoNota = TipoNota.TESTO;

  TipoNota = TipoNota; 
  
  tipiNota = Object.values(TipoNota);

  isEditingDrawing = false;    
  isDrawing = false;
  isEraserMode = false;
  private svgElement?: SVGSVGElement;
  private lastPoint: { x: number, y: number } | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private previewNotaService: PreviewNotaService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
  ) {}

  async ngOnInit() {
    this.notaId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.notaId) {
      await this.fetchNotaDettaglio(this.notaId);
    } else {
      console.error('ID nota non valido');
      this.error = 'Impossibile caricare la nota, ID non valido.';
      this.loading = false; 
    }
  }

  ngOnDestroy(): void {
    this.removeDrawingListeners();
  }

  async fetchNotaDettaglio(id: number): Promise<void> {
    this.loading = true;
    this.error = null;
    this.successMessage = null;
    this.isEditingDrawing = false;

    try {
      this.nota = await this.previewNotaService.getNotaById(id);
      
      if (this.nota) {
        this.titoloModifica = this.nota.titolo;
        this.contenutoTestoModifica = this.nota.contenutoTesto || '';
        this.contenutoSVGModifica = this.nota.contenutoSVG || '';
        this.tipoNotaModifica = this.nota.tipoNota;
        
        this.isEditingDrawing = this.tipoNotaModifica === TipoNota.DISEGNO;
        if (this.isEditingDrawing) {
           setTimeout(() => this.initializeDrawingCanvas(), 0);
        }
      }
     } catch (err: any) {
      console.error('Errore recupero nota:', err);
      this.error = 'Impossibile caricare i dettagli della nota. Riprova.';
    } finally {
      this.loading = false;
      this.ngZone.run(() => {
        this.cdr.detectChanges();
      });
    }
  }

  onTipoNotaSelectChange(): void {
    this.isEditingDrawing = this.tipoNotaModifica === TipoNota.DISEGNO;
    if (this.tipoNotaModifica !== TipoNota.DISEGNO) {
      this.removeDrawingListeners();
      if (this.drawingCanvas?.nativeElement) {
        while (this.drawingCanvas.nativeElement.firstChild) {
          this.drawingCanvas.nativeElement.removeChild(this.drawingCanvas.nativeElement.firstChild);
        }
      }
      this.svgElement = undefined;
      this.contenutoSVGModifica = '';
    }
  }

  async onUpdateNota(): Promise<void> {
    if (!this.notaId || !this.nota) {
      this.error = 'Dati nota mancanti per l\'aggiornamento.';
      return;
    }

    // Qui serializziamo il disegno prima di inviarlo al backend,
    // senza modificare lo stato dell'editor.
    if (this.tipoNotaModifica === TipoNota.DISEGNO) {
      this._serializeDrawing(); // Nuovo metodo per serializzare il disegno
      console.log('SVG serializzato prima dell\'invio:', this.contenutoSVGModifica);
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
        updatedNotaData.contenutoSVG = this.contenutoSVGModifica || undefined;
        updatedNotaData.contenutoTesto = undefined;
      }

      await this.previewNotaService.updateNota(this.notaId, updatedNotaData);
      this.successMessage = 'Nota aggiornata con successo!';
      
      setTimeout(() => this.router.navigate(['/dashboard']), 800);
    } catch (err: any) {
      console.error('Errore aggiornamento nota:', err);
      this.error = 'Errore durante l\'aggiornamento. Riprova.';
    } finally {
      this.loading = false;
    }
  }


  async onDeleteNota(): Promise<void> {
    if (!this.notaId) {
      this.error = 'ID nota mancante per l\'eliminazione.';
      return;
    }
    if (!confirm('Sei sicuro di voler eliminare definitivamente questa nota?')) return;

    this.loading = true;
    this.error = null;
    this.successMessage = null;

    try {
      await this.previewNotaService.deleteNota(this.notaId);
      this.successMessage = 'Nota eliminata con successo!';
      this.router.navigate(['/dashboard']);
    } catch (err: any) {
      console.error('Errore eliminazione nota:', err);
      this.error = 'Errore durante l\'eliminazione. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  cancelEditingDrawing(): void {
    this.isEditingDrawing = false;
    this.removeDrawingListeners();
    if (this.drawingCanvas?.nativeElement) {
      while (this.drawingCanvas.nativeElement.firstChild) {
        this.drawingCanvas.nativeElement.removeChild(this.drawingCanvas.nativeElement.firstChild);
      }
    }
    this.svgElement = undefined;
  }

  finishEditingDrawing(): void {
    // Questo metodo serve solo per uscire dalla modalità di modifica
    // e per informare l'utente che le modifiche sono state salvate localmente.
    // La serializzazione viene ora gestita da _serializeDrawing().
    this.isEditingDrawing = false;
    this.removeDrawingListeners();
    this.successMessage = 'Disegno aggiornato in locale. Salva la nota per inviare al server.';
    // Lasciamo il canvas e svgElement in memoria per l'eventuale salvataggio,
    // in modo da poterlo serializzare.
  }
  
  // Nuovo metodo privato per serializzare l'SVG
  private _serializeDrawing(): void {
    if (this.svgElement) {
      this.contenutoSVGModifica = new XMLSerializer().serializeToString(this.svgElement);
    } else {
      this.contenutoSVGModifica = '';
    }
  }


  downloadSvg(): void {
    if (!this.contenutoSVGModifica) return;
    const blob = new Blob([this.contenutoSVGModifica], { type: 'image/svg+xml' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = (this.titoloModifica || 'disegno') + '.svg';
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  }

  deleteDrawing(): void {
    if (!confirm('Eliminare il disegno associato a questa nota?')) return;
    this.contenutoSVGModifica = '';
    this.successMessage = 'Disegno rimosso (salva per confermare).';
  }

  startEditingDrawing(): void {
    this.isEditingDrawing = true;
    this.isEraserMode = false;

    this.ngZone.run(() => {
      this.cdr.detectChanges();
      setTimeout(() => this.initializeDrawingCanvas(), 0);
    });
  }

  // Metodo per inizializzare il canvas
  initializeDrawingCanvas(): void {
    console.log('Inizializzo canvas con SVG:', this.contenutoSVGModifica);
    if (!this.drawingCanvas) {
      console.warn('Canvas di disegno non disponibile.');
      return; 
    }
    const container = this.drawingCanvas.nativeElement as HTMLDivElement;
    
    // Controlla che l'elemento sia visibile e abbia dimensioni prima di procedere
    if (container.clientWidth === 0 || container.clientHeight === 0) {
      console.warn('L\'elemento canvas non ha dimensioni. Riprovo...');
      setTimeout(() => this.initializeDrawingCanvas(), 50);
      return;
    }

    // Assicura che l'SVG non venga ricreato se già esiste
    if (this.svgElement) {
        return;
    }

    // Rimuovi eventuali elementi preesistenti nel container
    while (container.firstChild) container.removeChild(container.firstChild);

    this.svgElement = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    this.svgElement.setAttribute('width', String(container.clientWidth || 600));
    this.svgElement.setAttribute('height', String(container.clientHeight || 300));
    this.svgElement.setAttribute('class', 'drawing-svg');
    this.svgElement.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
    this.svgElement.style.background = 'transparent';

    container.appendChild(this.svgElement);

    if (this.contenutoSVGModifica) {
      try {
        const parser = new DOMParser();
        const doc = parser.parseFromString(this.contenutoSVGModifica, 'image/svg+xml');
        const imported = doc.documentElement;
        while (imported.firstChild) {
          this.svgElement.appendChild(imported.firstChild);
        }
      } catch (e) {
        console.warn('Impossibile importare SVG preesistente nel canvas editor.', e);
      }
    }

    this.setupDrawingListeners();
  }

  onMouseDown = (e: MouseEvent | TouchEvent): void => {
    e.preventDefault();
    if (!this.svgElement) return;
    this.isDrawing = true;
    this.lastPoint = this.getSvgMousePoint(e);
  };

  onMouseMove = (e: MouseEvent | TouchEvent): void => {
    if (!this.isDrawing || !this.svgElement) return;
    e.preventDefault();
    const newPoint = this.getSvgMousePoint(e);
    if (!this.lastPoint) {
      this.lastPoint = newPoint;
      return;
    }

    if (this.isEraserMode) {
      this.eraseLines(newPoint);
    } else {
      const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
      line.setAttribute('x1', String(this.lastPoint.x));
      line.setAttribute('y1', String(this.lastPoint.y));
      line.setAttribute('x2', String(newPoint.x));
      line.setAttribute('y2', String(newPoint.y));
      line.setAttribute('stroke', 'black');
      line.setAttribute('stroke-width', '2');
      line.setAttribute('stroke-linecap', 'round');
      this.svgElement.appendChild(line);
    }
    this.lastPoint = newPoint;
  };

  onMouseUp = (): void => {
    this.isDrawing = false;
    this.lastPoint = null;
  };

  setupDrawingListeners(): void {
    if (!this.drawingCanvas) return;
    const el = this.drawingCanvas.nativeElement;
    el.addEventListener('mousedown', this.onMouseDown);
    el.addEventListener('mousemove', this.onMouseMove);
    el.addEventListener('mouseup', this.onMouseUp);
    el.addEventListener('mouseleave', this.onMouseUp);
    el.addEventListener('touchstart', this.onMouseDown, { passive: false });
    el.addEventListener('touchmove', this.onMouseMove, { passive: false });
    el.addEventListener('touchend', this.onMouseUp);
    el.addEventListener('touchcancel', this.onMouseUp);
  }

  removeDrawingListeners(): void {
    if (!this.drawingCanvas) return;
    const el = this.drawingCanvas.nativeElement;
    el.removeEventListener('mousedown', this.onMouseDown);
    el.removeEventListener('mousemove', this.onMouseMove);
    el.removeEventListener('mouseup', this.onMouseUp);
    el.removeEventListener('mouseleave', this.onMouseUp);
    el.removeEventListener('touchstart', this.onMouseDown);
    el.removeEventListener('touchmove', this.onMouseMove);
    el.removeEventListener('touchend', this.onMouseUp);
    el.removeEventListener('touchcancel', this.onMouseUp);
  }

  clearDrawing(): void {
    if (!this.svgElement) return;
    while (this.svgElement.firstChild) this.svgElement.removeChild(this.svgElement.firstChild);
  }

  toggleEraserMode(): void {
    this.isEraserMode = !this.isEraserMode;
  }

  private eraseLines(point: { x: number, y: number }): void {
    if (!this.svgElement) return;
    const tolerance = 8;
    const lines = Array.from(this.svgElement.querySelectorAll('line'));
    lines.forEach(line => {
      const x1 = parseFloat(line.getAttribute('x1') || '0');
      const y1 = parseFloat(line.getAttribute('y1') || '0');
      const x2 = parseFloat(line.getAttribute('x2') || '0');
      const y2 = parseFloat(line.getAttribute('y2') || '0');
      if (this.isPointNearLine(point.x, point.y, x1, y1, x2, y2, tolerance)) {
        this.svgElement?.removeChild(line);
      }
    });
  }

  private isPointNearLine(px: number, py: number, x1: number, y1: number, x2: number, y2: number, tolerance: number): boolean {
    const dx = x2 - x1;
    const dy = y2 - y1;
    const lenSq = dx * dx + dy * dy;
    let t = 0;
    if (lenSq !== 0) {
      t = ((px - x1) * dx + (py - y1) * dy) / lenSq;
    }
    t = Math.max(0, Math.min(1, t));
    const closestX = x1 + t * dx;
    const closestY = y1 + t * dy;
    const distSq = (px - closestX) ** 2 + (py - closestY) ** 2;
    return Math.sqrt(distSq) < tolerance;
  }

  private getSvgMousePoint(e: MouseEvent | TouchEvent): { x: number; y: number } {
    if (!this.svgElement) return { x: 0, y: 0 };
    const rect = this.svgElement.getBoundingClientRect();
    let clientX: number;
    let clientY: number;
    if (e instanceof MouseEvent) {
      clientX = e.clientX;
      clientY = e.clientY;
    } else {
      clientX = e.touches[0].clientX;
      clientY = e.touches[0].clientY;
    }
    return { x: clientX - rect.left, y: clientY - rect.top };
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}