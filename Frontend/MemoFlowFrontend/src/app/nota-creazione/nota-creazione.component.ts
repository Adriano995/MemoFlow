// nota-creazione.component.ts
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy, Input } from '@angular/core'; // <-- Ho aggiunto Input qui

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

  @Input() date: Date | null = null;
  @Input() userId: number | null = null;
  // --------------------

  newNota: NotaCreateDTO = {
    titolo: '',
    contenutoTesto: '',
    contenutoSVG: '', 
    tipoNota: TipoNota.TESTO,
    utenteId: 1, // Questo valore sarà sovrascritto dall'Input userId se fornito
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
   // Aggiungo logica per usare gli @Input() se sono disponibili
   if (this.date) {
     this.newNota.dataNota = this.formatDateToYYYYMMDD(this.date);
     console.log('NotaCreazioneComponent - data ricevuta via Input:', this.date);
   }

   if (this.userId !== null) { // userId può essere 0, quindi !== null è meglio di if(this.userId)
     this.newNota.utenteId = this.userId;
     console.log('NotaCreazioneComponent - userId ricevuto via Input:', this.userId);
   } else {
     // Se userId non è passato via Input, cerchiamo ancora nella route (per retro-compatibilità o scenari specifici)
     this.route.paramMap.subscribe(params => {
       const dateParam = params.get('date'); // Puoi rimuovere questi se ti affidi sempre agli @Input()
       const userIdParam = params.get('userId');

       if (dateParam && !this.date) { // Sovrascrive solo se l'input 'date' non è stato fornito
         this.newNota.dataNota = dateParam;
       }

       if (userIdParam && this.userId === null) { // Sovrascrive solo se l'input 'userId' non è stato fornito
         this.newNota.utenteId = +userIdParam;
       } else if (this.userId === null) {
         console.error("ID utente non trovato nella route e non fornito come Input. Assicurati che l'utente sia loggato e l'ID sia passato correttamente.");
       }
     });
   }
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
  isDrawingMode: boolean = false;
  isEraserMode: boolean = false;

  @ViewChild('drawingCanvas', { static: false }) drawingCanvas!: ElementRef<HTMLDivElement>;

  private svgElement?: SVGSVGElement;
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
    if (this.isDrawingMode) {
      this.initializeDrawingCanvas();
    }
  }

  ngOnDestroy(): void {
    this.removeDrawingListeners();
  }

  onTipoNotaChange(): void {
    console.log('onTipoNotaChange called. New type:', this.newNota.tipoNota);
    
    this.isDrawingMode = this.newNota.tipoNota === TipoNota.DISEGNO;
    this.isEraserMode = false; // Reset gomma

    if (this.isDrawingMode) {
      setTimeout(() => this.initializeDrawingCanvas(), 0);
    } else {
      this.removeDrawingListeners();
      if (this.drawingCanvas?.nativeElement) {
        while (this.drawingCanvas.nativeElement.firstChild) {
          this.drawingCanvas.nativeElement.removeChild(this.drawingCanvas.nativeElement.firstChild);
        }
      }
      this.svgElement = undefined;
      
      this.newNota.contenutoSVG = '';
    }
  }

  initializeDrawingCanvas(): void {
    if (!this.drawingCanvas) {
      console.warn('drawingCanvas non disponibile');
      return;
    }

    const canvas = this.drawingCanvas.nativeElement as HTMLDivElement;

    // Pulisco il contenitore
    while (canvas.firstChild) {
      canvas.removeChild(canvas.firstChild);
    }

      // Creo nuovo SVG
      this.svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg");
      this.svgElement.setAttribute('width', canvas.offsetWidth.toString());
      this.svgElement.setAttribute('height', canvas.offsetHeight.toString());
      this.svgElement.setAttribute('class', 'drawing-svg');

    // Aggiungo l'SVG nel DOM
    canvas.appendChild(this.svgElement);

    console.log('Drawing canvas inizializzato con nuovo SVG:', this.svgElement);

    this.setupDrawingListeners();

    if (this.newNota.contenutoSVG) {
      this.loadSvgContent(this.newNota.contenutoSVG);
    }
  }


  // Event handlers come arrow functions per poterli aggiungere/rimuovere correttamente
  onMouseDown = (e: MouseEvent | TouchEvent): void => {
    e.preventDefault();
    if (!this.svgElement) return;
    this.isDrawing = true;
    this.lastPoint = this.getSvgMousePoint(e);
  }

  onMouseMove = (e: MouseEvent | TouchEvent): void => {
    if (!this.isDrawing || !this.svgElement) return;
    e.preventDefault();
    const newPoint = this.getSvgMousePoint(e);

    if (this.isEraserMode) {
      this.eraseLines(newPoint);
    } else {
      if (this.lastPoint) {
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', this.lastPoint.x.toString());
        line.setAttribute('y1', this.lastPoint.y.toString());
        line.setAttribute('x2', newPoint.x.toString());
        line.setAttribute('y2', newPoint.y.toString());
        line.setAttribute('stroke', 'black');
        line.setAttribute('stroke-width', '2');
        line.setAttribute('stroke-linecap', 'round');
        this.svgElement.appendChild(line);
      }
      this.lastPoint = newPoint;
    }
  }

  onMouseUp = (): void => {
    this.isDrawing = false;
    this.lastPoint = null;
    if (this.svgElement) {
      this.newNota.contenutoSVG = new XMLSerializer().serializeToString(this.svgElement);
    }
  }

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

    console.log('Event listeners per disegno aggiunti');
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

    console.log('Event listeners per disegno rimossi');
  }

  clearDrawing(): void {
    if (this.svgElement) {
      while (this.svgElement.firstChild) {
        this.svgElement.removeChild(this.svgElement.firstChild);
      }
      this.newNota.contenutoSVG = '';
      console.log('Disegno pulito');
    }
  }

  toggleEraserMode(): void {
    this.isEraserMode = !this.isEraserMode;
    console.log(`Modalità gomma: ${this.isEraserMode ? 'Attivata' : 'Disattivata'}`);
  }

  loadSvgContent(svgString: string): void {
    if (!this.svgElement) return;
    this.clearDrawing();
    const parser = new DOMParser();
    const doc = parser.parseFromString(svgString, 'image/svg+xml');
    const importedSvg = doc.documentElement;

    while (importedSvg.firstChild) {
      this.svgElement.appendChild(importedSvg.firstChild);
    }
  }

  private eraseLines(point: { x: number, y: number }): void {
    const tolerance = 5;
    const lines = this.svgElement?.querySelectorAll('line');
    lines?.forEach(line => {
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

  private getSvgMousePoint(e: MouseEvent | TouchEvent): { x: number, y: number } {
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

    return {
      x: clientX - rect.left,
      y: clientY - rect.top
    };
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