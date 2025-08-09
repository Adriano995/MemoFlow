import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AxiosService } from '../core/axios.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-user-modifica.component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-modifica.component.html',
  styleUrl: './user-modifica.component.css'
})
export class UserModificaComponent implements OnInit {
  form!: FormGroup;
  emailForm!: FormGroup;
  passwordForm!: FormGroup;
  userId!: number;
  activeTab: string = 'anagrafica';

  successMessage: string | null = null;
  errorMessage: string | null = null;
  
  oldPasswordType: string = 'password';
  newPasswordType: string = 'password';

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private router: Router,
    private axiosService: AxiosService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.userId = Number(this.route.snapshot.paramMap.get('id'));

    this.form = this.fb.group({
      nome: ['', Validators.required],
      cognome: ['', Validators.required],
    });

    this.emailForm = this.fb.group({
      vecchiaEmail: ['', [Validators.required, Validators.email]],
      nuovaEmail: ['', [Validators.required, Validators.email]]
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
    
    this.loadUserData();
  }

  loadUserData() {
    this.axiosService.get<any>(`/utente/cercaSingolo/${this.userId}`).then(user => {
      this.form.patchValue({
        nome: user.nome,
        cognome: user.cognome
      });
    }).catch(error => {
      this.errorMessage = 'Non è stato possibile caricare i dati utente. Controlla il server.';
      console.error('Errore durante il caricamento dei dati utente:', error);
      setTimeout(() => this.router.navigate(['/']), 3000); 
    });
  }

  clearMessages(): void {
    this.successMessage = null;
    this.errorMessage = null;
  }
  
  onSaveUser(): void {
    this.clearMessages();
    if (this.form.valid) {
      const { nome, cognome } = this.form.value;

      this.axiosService.put(`/utente/aggiornaDati/${this.userId}`, { nome, cognome })
        .then(() => {
          this.successMessage = 'Dati anagrafici aggiornati con successo!';
          setTimeout(() => this.router.navigate(['/dashboard']), 3000); 
        })
        .catch(error => {
          this.errorMessage = 'Errore durante il salvataggio dei dati anagrafici.';
          console.error(error);
        });
    }
  }

  OnChangeEmail(): void {
    this.clearMessages();
    if (this.emailForm.valid) {
      const { vecchiaEmail, nuovaEmail } = this.emailForm.value;

      this.axiosService.post(`/credenziali/cambiaEmail`, {
        vecchiaEmail: vecchiaEmail,
        nuovaEmail: nuovaEmail
      }).then(() => {
        this.successMessage = 'Email aggiornata con successo! Effettua di nuovo il login con la nuova email.';
        this.emailForm.reset();
        setTimeout(() => this.router.navigate(['/login']), 3000); 
      }).catch(error => {
        this.errorMessage = 'Errore: l\'email attuale non è corretta o la nuova email è già in uso.';
        console.error(error);
      });
    }
  }

  onChangePassword(): void {
    this.clearMessages();
    if (this.passwordForm.valid) {
      const { oldPassword, newPassword } = this.passwordForm.value;

      this.axiosService.post(`/credenziali/cambiaPassword`, {
        vecchiaPassword: oldPassword,
        nuovaPassword: newPassword
      }).then(() => {
        this.successMessage = 'Password aggiornata con successo! Effettua di nuovo il login con la nuova password.';
        this.passwordForm.reset();
        setTimeout(() => this.router.navigate(['/login']), 3000);
      }).catch(error => {
        this.errorMessage = 'Errore: password attuale errata o altro problema';
        console.error(error);
      });
    }
  }
  
  toggleOldPasswordVisibility(): void {
    this.oldPasswordType = this.oldPasswordType === 'password' ? 'text' : 'password';
  }
  
  toggleNewPasswordVisibility(): void {
    this.newPasswordType = this.newPasswordType === 'password' ? 'text' : 'password';
  }
}