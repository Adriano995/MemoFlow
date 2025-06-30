import { Routes } from '@angular/router';
import { LoginComponent } from './login.component/login.component';
import { DashboardCalendarioComponent } from './dashboard-calendario/dashboard-calendario.component';
import { RegisterComponent } from './register-component/register.component';

export const routes: Routes = [
  
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'dashboard', component: DashboardCalendarioComponent },
  // ... altre route interne all'app

  // Route wildcard per gestire percorsi non trovati (opzionale, ma consigliato)
  { path: '**', redirectTo: '/login' }

];
