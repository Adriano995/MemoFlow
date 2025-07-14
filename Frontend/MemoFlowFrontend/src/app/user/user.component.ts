import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent {
    userForm: FormGroup;
    
    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.userForm = this.fb.group({
            nome: ['', Validators.required],
            cognome: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],       
            password: ['', [Validators.required, Validators.minLength(6)]],
        });
    
}
}