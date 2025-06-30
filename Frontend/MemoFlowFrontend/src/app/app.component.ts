import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login.component/login.component';
import { DashboardCalendarioComponent } from './dashboard-calendario/dashboard-calendario.component';
import { RegisterComponent } from './register-component/register.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'MemoFlowFrontend';
}
