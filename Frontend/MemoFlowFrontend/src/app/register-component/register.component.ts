import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  registerForm: FormGroup;
  passwordType: string = 'password'; // Nuova proprietà per il tipo di input

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ){
    this.registerForm = this.fb.group({
      nome: ['', Validators.required],
      cognome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    })
  }

  async onSubmit(){
    if (this.registerForm.valid) {
      const { nome, cognome, email, password } = this.registerForm.value;
      if (!email || !password) return;

      try {
        await this.authService.register(nome, cognome, email, password);
        console.log('Registrazione riuscita!');
        this.router.navigate(['/login']);
      } catch (error) {
        console.error('Errore durante la registrazione:', error);
      }
    } else {
        console.warn('Form non valido!');
    }
  }

  togglePasswordVisibility(): void {
    this.passwordType = this.passwordType === 'password' ? 'text' : 'password';
  }
}