import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-evento',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './evento.component.html',
  styleUrl: './evento.component.css'
})
export class Evento {

}
