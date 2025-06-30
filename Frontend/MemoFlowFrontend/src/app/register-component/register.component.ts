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

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ){
    this.registerForm = this.fb.group({
      nome: ['', Validators.required], // Aggiunto 'nome'
      cognome: ['', Validators.required], // Aggiunto 'cognome'
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    })
  }

  async onSubmit(){
    if (this.registerForm.valid) {
      // Estrai nome e cognome oltre a email e password
      const { nome, cognome, email, password } = this.registerForm.value;
      if (!email || !password) return; // Potresti voler aggiungere controlli per nome/cognome qui se sono required

      try {
        // Passa tutti i dati al servizio, che li formatterà come UtenteCreateDTO
        await this.authService.register(nome, cognome, email, password); // Modificato qui
        console.log('Registrazione riuscita!');
        this.router.navigate(['/login']); // Redirect qui
      } catch (error) {
        console.error('Errore durante la registrazione:', error);
        // mostra errore a schermo
      }
    } else {
        console.warn('Form non valido!');
        // Logica per mostrare errori di validazione all'utente
    }
  }
}