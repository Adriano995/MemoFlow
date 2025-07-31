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
  passwordForm!: FormGroup;
  userId!: number;

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
      nome: [''],
      cognome: [''],
      email: ['', [Validators.required, Validators.email]]
    });
    
    this.axiosService.get<any>(`/utente/${this.userId}`).then(user => {
      this.form.setValue({
        nome: user.nome,
        cognome: user.cognome,
        email: user.email,
      });
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
    this.loadUserData();
  }

  loadUserData() {
    this.axiosService.get<any>(`/utente/${this.userId}`).then(user => {
      this.form.patchValue({
        nome: user.nome,
        cognome: user.cognome,
        email: user.email
      });
    });
  }

  onSaveUser(): void {
    if (this.form.valid) {
      const { nome, cognome, email } = this.form.value;

      this.axiosService.put(`/utente/${this.userId}`, { nome, cognome })
        .then(() => {
          return this.axiosService.post(`/credenziali/cambiaEmail`, {
            userId: this.userId,
            nuovaEmail: email
          });
        })
        .then(() => {
          alert('Dati aggiornati con successo!');
        })
        .catch(() => {
          alert('Errore durante il salvataggio dei dati.');
        });
    }
  }

  onChangePassword(): void {
    if (this.passwordForm.valid) {
      const { oldPassword, newPassword } = this.passwordForm.value;

      this.axiosService.post(`/credenziali/cambiaPassword`, {
        userId: this.userId,
        vecchiaPassword: oldPassword,
        nuovaPassword: newPassword
      }).then(() => {
        alert('Password aggiornata con successo!');
        this.passwordForm.reset();
      }).catch(() => {
        alert('Errore: password attuale errata o altro problema');
      });
    }
  }
}