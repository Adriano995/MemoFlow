// nota-creazione.component.ts
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PreviewNotaService } from '../services/preview-nota.service';
import { NotaCreateDTO } from '../preview-nota-component/preview-nota-create.dto';
import { TipoNota } from '../preview-nota-component/tipo-nota.enum';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-nota-creazione',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nota-creazione.component.html',
  styleUrls: ['./nota-creazione.component.css']
})
export class NotaCreazioneComponent implements OnInit, AfterViewInit, OnDestroy {
  newNota: NotaCreateDTO = {
    titolo: '',
    contenutoTesto: '',
    contenutoSVG: '', 
    tipoNota: TipoNota.TESTO,
    utenteId: 1,
    dataNota: this.formatDateToYYYYMMDD(new Date())
  };
  tipiNota = Object.values(TipoNota);
  successMessage: string | null = null;
  errorMessage: string | null = null;

  @ViewChild('drawingCanvas') drawingCanvas!: ElementRef<HTMLDivElement>;
  private svgElement: SVGSVGElement | undefined;
  private isDrawing = false;
  private lastPoint: { x: number, y: number } | null = null;

  constructor(
    private previewNotaService: PreviewNotaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    console.log('NotaCreazioneComponent constructor called.');
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const dateParam = params.get('date');
      const userIdParam = params.get('userId');

      if (dateParam) {
        this.newNota.dataNota = dateParam;
      }

      if (userIdParam) {
        this.newNota.utenteId = +userIdParam;
      } else {
        console.error("ID utente non trovato nella route. Assicurati che l'utente sia loggato e l'ID sia passato correttamente.");
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.newNota.tipoNota === TipoNota.DISEGNO) {
      this.initializeDrawingCanvas();
    }
  }

  ngOnDestroy(): void {
    this.removeDrawingListeners();
  }

  onTipoNotaChange(): void {
    console.log('onTipoNotaChange called. New type:', this.newNota.tipoNota);
    if (this.newNota.tipoNota === TipoNota.DISEGNO) {
      setTimeout(() => {
        this.initializeDrawingCanvas();
      }, 0);
    } else {
      this.removeDrawingListeners();
      if (this.drawingCanvas && this.drawingCanvas.nativeElement) {
        while (this.drawingCanvas.nativeElement.firstChild) {
          this.drawingCanvas.nativeElement.removeChild(this.drawingCanvas.nativeElement.firstChild);
        }
      }
      this.svgElement = undefined;
      this.newNota.contenutoSVG = ''; 
    }
  }

  initializeDrawingCanvas(): void {
    if (this.drawingCanvas) { 
      while (this.drawingCanvas.nativeElement.firstChild) {
        this.drawingCanvas.nativeElement.removeChild(this.drawingCanvas.nativeElement.firstChild);
      }

      this.svgElement = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
      this.svgElement.setAttribute('width', '100%');
      this.svgElement.setAttribute('height', '100%');
      this.drawingCanvas.nativeElement.appendChild(this.svgElement);

      console.log('Inizializzo Drawing Canvas - nuovo svgElement creato:', this.svgElement);
      this.setupDrawingListeners();

      if (this.newNota.contenutoSVG) {
        this.loadSvgContent(this.newNota.contenutoSVG);
      }
    } else {
      console.warn('drawingCanvas non disponibile per inizializzare SVG. Potrebbe esserci un problema di tempismo con *ngIf.');
    }
  }

  setupDrawingListeners(): void {
    if (!this.drawingCanvas || !this.drawingCanvas.nativeElement) {
        console.error('setupDrawingListeners: drawingCanvas ElementRef non disponibile.');
        return;
    }
    const targetElement = this.drawingCanvas.nativeElement;

    targetElement.addEventListener('mousedown', this.onMouseDown.bind(this));
    targetElement.addEventListener('mousemove', this.onMouseMove.bind(this));
    targetElement.addEventListener('mouseup', this.onMouseUp.bind(this));
    targetElement.addEventListener('mouseleave', this.onMouseUp.bind(this));
    console.log('Event listeners setup on:', targetElement);
  }

  removeDrawingListeners(): void {
    if (this.drawingCanvas && this.drawingCanvas.nativeElement) {
      const targetElement = this.drawingCanvas.nativeElement;
      targetElement.removeEventListener('mousedown', this.onMouseDown.bind(this));
      targetElement.removeEventListener('mousemove', this.onMouseMove.bind(this));
      targetElement.removeEventListener('mouseup', this.onMouseUp.bind(this));
      targetElement.removeEventListener('mouseleave', this.onMouseUp.bind(this));
      console.log('Event listeners removed from:', targetElement); 
    }
  }

  onMouseDown(e: MouseEvent): void {
    console.log('onMouseDown - svgElement:', this.svgElement);
    if (!this.svgElement) {
        console.error('onMouseDown: svgElement non disponibile.');
        return;
    }
    e.preventDefault(); 

    this.isDrawing = true;
    const rect = this.svgElement.getBoundingClientRect();
    this.lastPoint = { x: e.clientX - rect.left, y: e.clientY - rect.top };
    console.log('onMouseDown: isDrawing impostato a', this.isDrawing, 'e lastPoint impostato a', this.lastPoint);
  }

  onMouseMove(e: MouseEvent): void {
    console.log('onMouseMove - isDrawing:', this.isDrawing, 'lastPoint:', this.lastPoint, 'svgElement:', this.svgElement);

    if (!this.isDrawing || !this.lastPoint || !this.svgElement) {
        console.log('onMouseMove: Condizioni per disegnare non soddisfatte.');
        return;
    }

    const rect = this.svgElement.getBoundingClientRect(); 
    const newPoint = { x: e.clientX - rect.left, y: e.clientY - rect.top };

    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
    line.setAttribute('x1', this.lastPoint.x.toString());
    line.setAttribute('y1', this.lastPoint.y.toString());
    line.setAttribute('x2', newPoint.x.toString());
    line.setAttribute('y2', newPoint.y.toString());
    line.setAttribute('stroke', 'black');
    line.setAttribute('stroke-width', '2');
    line.setAttribute('stroke-linecap', 'round');

    console.log('Creata linea:', line.outerHTML);

    this.svgElement.appendChild(line); 

    this.lastPoint = newPoint;
  }

  onMouseUp(): void {
    console.log('onMouseUp: Entrata. isDrawing:', this.isDrawing, 'lastPoint:', this.lastPoint);

    this.isDrawing = false;
    this.lastPoint = null;
    if (this.svgElement) {
      console.log('Numero di elementi figli in svgElement prima della serializzazione:', this.svgElement.children.length);
      this.newNota.contenutoSVG = new XMLSerializer().serializeToString(this.svgElement);
      console.log('SVG generato (dopo disegno):', this.newNota.contenutoSVG);
    }
  }

  clearDrawing(): void {
    if (this.svgElement) {
      while (this.svgElement.firstChild) {
        this.svgElement.removeChild(this.svgElement.firstChild);
      }
      this.newNota.contenutoSVG = '';
      console.log('Disegno pulito. SVG children:', this.svgElement.children.length);
    }
  }

  loadSvgContent(svgString: string): void {
    if (this.svgElement) {
      this.clearDrawing();
      const parser = new DOMParser();
      const doc = parser.parseFromString(svgString, 'image/svg+xml');
      const importedSvg = doc.documentElement;

      while (importedSvg.firstChild) {
        this.svgElement.appendChild(importedSvg.firstChild);
      }
    }
  }

  private formatDateToYYYYMMDD(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  async onSubmit(): Promise<void> {
    this.successMessage = null;
    this.errorMessage = null;

    if (!this.newNota.titolo || !this.newNota.utenteId || !this.newNota.dataNota) {
      this.errorMessage = 'Per favore, compila tutti i campi obbligatori (Titolo, Utente ID, Data Nota).';
      return;
    }

    if (this.newNota.tipoNota === TipoNota.TESTO && !this.newNota.contenutoTesto) {
      this.errorMessage = 'Per favore, inserisci il contenuto del testo.';
      return;
    }

    try {
      await this.previewNotaService.createNota(this.newNota);
      this.successMessage = 'Nota creata con successo!';
      this.resetForm();
      this.router.navigate(['/dashboard']);
    } catch (error) {
      console.error('Errore durante la creazione della nota:', error);
      this.errorMessage = 'Errore durante la creazione della nota. Riprova più tardi.';
    }
  }

  resetForm(): void {
    this.newNota = {
      titolo: '',
      contenutoTesto: '',
      contenutoSVG: '', 
      tipoNota: TipoNota.TESTO,
      utenteId: this.newNota.utenteId,
      dataNota: this.formatDateToYYYYMMDD(new Date())
    };
    this.clearDrawing();
  }

  onCancel(): void {
    this.router.navigate(['/dashboard']);
  }
}