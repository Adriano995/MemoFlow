import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService // <<-- inject del servizio
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

onSubmit(): void {
  console.log('Form valido?', this.loginForm.valid);
  console.log('Valori form:', this.loginForm.value);
  if (this.loginForm.valid) {
    const { email, password } = this.loginForm.value;
    const success = this.authService.login(email, password);

    if (success) {
      console.log('Login riuscito!');
    } else {
      console.log('Credenziali errate!');
    }
  } else {
    console.log('Form non valido');
  }
}

}
