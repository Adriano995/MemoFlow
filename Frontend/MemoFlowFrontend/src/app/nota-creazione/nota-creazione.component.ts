// nota-creazione.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PreviewNotaService } from '../preview-nota-component/preview-nota.service';
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
export class NotaCreazioneComponent implements OnInit {
  newNota: NotaCreateDTO = {
    titolo: '',
    contenutoTesto: '',
    tipoNota: TipoNota.TESTO,
    utenteId: 1, // Sarà sovrascritto
    dataNota: this.formatDateToYYYYMMDD(new Date())// Usa una funzione per ottenere la data formattata localmente
  };
  tipiNota = Object.values(TipoNota);
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private previewNotaService: PreviewNotaService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const dateParam = params.get('date');
      const userIdParam = params.get('userId');

      if (dateParam) {
        // Usa la data passata dal calendario
        this.newNota.dataNota = dateParam;
      }
      // else: newNota.dataNota manterrà il valore di default (oggi)

      if (userIdParam) {
        this.newNota.utenteId = +userIdParam;
      } else {
        console.error("ID utente non trovato nella route. Assicurati che l'utente sia loggato e l'ID sia passato correttamente.");
        // Potresti reindirizzare o mostrare un messaggio di errore
      }
    });
  }

  // Funzione per ottenere la data odierna formattata localmente (YYYY-MM-DD)
  // Questo evita problemi di fuso orario quando la data di default è oggi.
  private formatDateToYYYYMMDD(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }


  async onSubmit(): Promise<void> {
    this.successMessage = null;
    this.errorMessage = null;

    if (!this.newNota.titolo || (!this.newNota.contenutoTesto && this.newNota.tipoNota === TipoNota.TESTO) || !this.newNota.utenteId || !this.newNota.dataNota) {
      this.errorMessage = 'Per favore, compila tutti i campi obbligatori (Titolo, Contenuto Testo, Utente ID, Data Nota).';
      return;
    }

    try {
      await this.previewNotaService.createNota(this.newNota);
      this.successMessage = 'Nota creata con successo!';
      this.resetForm();
      this.router.navigate(['/dashboard']); // Reindirizza alla dashboard
    } catch (error) {
      console.error('Errore durante la creazione della nota:', error);
      this.errorMessage = 'Errore durante la creazione della nota. Riprova più tardi.';
    }
  }

  resetForm(): void {
    this.newNota = {
      titolo: '',
      contenutoTesto: '',
      tipoNota: TipoNota.TESTO,
      utenteId: this.newNota.utenteId,
      dataNota: this.formatDateToYYYYMMDD(new Date()) // Resetta alla data di oggi formattata localmente
    };
  }

  onCancel(): void {
    this.router.navigate(['/dashboard']); // Reindirizza alla dashboard
  }
}