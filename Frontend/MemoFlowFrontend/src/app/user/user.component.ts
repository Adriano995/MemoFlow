import { Component, OnInit, OnDestroy } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from './user.service';
import { User } from './user.model';
import { Subject, take } from 'rxjs'; 
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs'; 


@Component({
  selector: 'app-user',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})

export class UserComponent implements OnInit, OnDestroy { // Implementa OnDestroy
  userForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  loading = false;
  user: User | null = null;

  // Subject per gestire l'unsubscribe in modo pulito
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.userForm = this.fb.group({
      nome: ['', Validators.required],
      cognome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      // La password è opzionale qui, ma richiediamo una lunghezza minima se inserita
      password: ['', [Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  // Metodo per pulire le sottoscrizioni quando il componente viene distrutto
  ngOnDestroy(): void {
    this.destroy$.next(); // Emette un valore per notificare le sottoscrizioni
    this.destroy$.complete(); // Completa il Subject per rilasciare le risorse
  }

  loadCurrentUser(): void {
    this.loading = true;
    this.errorMessage = null;

    this.userService.getCurrentUser().pipe(
      // take(1) è perfetto per le chiamate HTTP che emettono un solo valore e poi completano.
      // Assicura che la sottoscrizione venga chiusa automaticamente dopo la prima emissione.
      take(1),
      // catchError gestisce gli errori dell'Observable
      catchError(error => {
        console.error('Errore nel caricamento dell\'utente:', error);
        this.errorMessage = 'Errore nel caricamento dei dati utente. Riprova.';
        this.loading = false;
        // Ritorna un nuovo Observable (ad esempio, di null) per permettere al flusso di continuare
        // senza bloccare il subscribe.
        return of(null);
      })
    ).subscribe(user => {
      // Questo blocco viene eseguito solo se l'operazione ha successo (o se catchError ha emesso null)
      this.user = user;
      if (this.user) {
        this.userForm.patchValue({
          nome: this.user.nome,
          cognome: this.user.cognome,
          email: this.user.email
        });
      }
      this.loading = false;
    });
  }

onSubmit(): void {
    // 1. Validazione del form all'inizio
    if (this.userForm.invalid) {
      this.errorMessage = 'Per favore, compila correttamente tutti i campi obbligatori.';
      this.userForm.markAllAsTouched(); // Mostra gli errori di validazione
      return; // Ferma l'esecuzione se il form non è valido
    }

    // 2. Imposta gli stati di caricamento e pulisci i messaggi
    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    // 3. Prepara l'oggetto 'updates' una sola volta
    const updates: Partial<User> = {
      nome: this.userForm.get('nome')?.value,
      cognome: this.userForm.get('cognome')?.value,
      email: this.userForm.get('email')?.value
    };

    // 4. Aggiungi la password solo se presente, senza ridichiarare
    const passwordValue = this.userForm.get('password')?.value;
    if (passwordValue) {
      // Questo è il modo corretto, TypeScript non dovrebbe darti errore
      // se 'password?: string;' è definito in User come hai confermato.
      updates.password = passwordValue;
    }

    // 5. Procedi con l'aggiornamento dell'utente
    if (this.user) {
      this.userService.updateUser(this.user.id_user, updates).pipe(
        take(1), // Prende il primo valore e completa la sottoscrizione
        catchError(error => { // Gestisce gli errori della chiamata API
          console.error('Errore durante l\'aggiornamento del profilo:', error);
          this.errorMessage = 'Errore durante l\'aggiornamento del profilo. Riprova.';
          this.loading = false;
          return of(null); // Ritorna un Observable di null per continuare il flusso
        })
      ).subscribe(result => {
        // Questo blocco si esegue solo se la chiamata API ha successo o se catchError ha emesso null
        if (result !== null) { // Controlla che non sia un risultato di errore
          this.successMessage = 'Profilo aggiornato con successo!';
          this.router.navigate(['/dashboard']); // Naviga alla pagina del dashboard o dove preferisci
        }
        this.loading = false; // Ferma lo stato di caricamento
      });
    } else {
      // Caso in cui 'this.user' non è ancora stato caricato o è null
      this.errorMessage = 'Utente non caricato. Impossibile aggiornare il profilo.';
      this.loading = false;
    }
  }

  deleteAccount(): void {
    if (!confirm('Sei sicuro di voler eliminare il tuo account? Questa azione non può essere annullata.')) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.userService.deleteOwnAccount().pipe(
      take(1), // Prende il primo valore e completa
      catchError(error => {
        console.error('Errore durante l\'eliminazione dell\'account:', error);
        this.errorMessage = 'Errore durante l\'eliminazione dell\'account. Riprova.';
        this.loading = false;
        return of(null); // Ritorna un Observable di null
      })
    ).subscribe(result => {
      if (result !== null) { // Solo se l'operazione ha avuto successo
        this.authService.logout();
        this.successMessage = 'Account eliminato con successo.';
        this.router.navigate(['/login']); // Naviga alla pagina di login
      }
      this.loading = false;
    });
  }

}