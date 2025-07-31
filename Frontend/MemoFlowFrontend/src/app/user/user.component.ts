import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { UserService } from '../user.service';
import { User } from '../user.model';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  userForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  loading = false;
  user: User | null = null;

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
      password: ['', [Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  async loadCurrentUser(): Promise<void> {
    this.loading = true;
    this.errorMessage = null;
    try {
      this.user = await this.userService.getCurrentUser().toPromise();
      if (this.user) {
        this.userForm.patchValue({
          nome: this.user.nome,
          cognome: this.user.cognome,
          email: this.user.email
        });
      }
    } catch (error) {
      console.error('Errore nel caricamento dell\'utente:', error);
      this.errorMessage = 'Errore nel caricamento dei dati utente. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  async onSubmit(): Promise<void> {
    if (this.userForm.invalid) {
      this.errorMessage = 'Per favore, compila correttamente tutti i campi obbligatori.';
      this.userForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    try {
      const updates: Partial<User> = {
        nome: this.userForm.get('nome')?.value,
        cognome: this.userForm.get('cognome')?.value,
        email: this.userForm.get('email')?.value
      };
      if (this.userForm.get('password')?.value) {
        updates['password'] = this.userForm.get('password')?.value;
      }
      if (this.user) {
        await this.userService.updateUser(this.user.id_user, updates).toPromise();
        this.successMessage = 'Profilo aggiornato con successo!';
        this.router.navigate(['/dashboard']);
      }
    } catch (error) {
      console.error('Errore durante l\'aggiornamento del profilo:', error);
      this.errorMessage = 'Errore durante l\'aggiornamento del profilo. Riprova.';
    } finally {
      this.loading = false;
    }
  }

  async deleteAccount(): Promise<void> {
    if (!confirm('Sei sicuro di voler eliminare il tuo account? Questa azione non può essere annullata.')) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    try {
      await this.userService.deleteOwnAccount().toPromise();
      this.authService.logout();
      this.successMessage = 'Account eliminato con successo.';
      this.router.navigate(['/login']);
    } catch (error) {
      console.error('Errore durante l\'eliminazione dell\'account:', error);
      this.errorMessage = 'Errore durante l\'eliminazione dell\'account. Riprova.';
    } finally {
      this.loading = false;
    }
  }
}