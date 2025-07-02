import { Routes } from '@angular/router';
import { LoginComponent } from './login.component/login.component';
import { DashboardCalendarioComponent } from './dashboard-calendario-component/dashboard-calendario.component';
import { RegisterComponent } from './register-component/register.component';
import { NotaDettaglioComponent } from './nota-dettaglio-component/nota-dettaglio.component';
import { NotaCreazioneComponent } from './nota-creazione/nota-creazione.component';

export const routes: Routes = [
  
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'dashboard', component: DashboardCalendarioComponent },
  { path: 'crea-nota', component: NotaCreazioneComponent },
  { path: 'nota-dettaglio/:id', component: NotaDettaglioComponent },
  
  // Route wildcard per gestire percorsi non trovati (opzionale, ma consigliato)
  { path: '**', redirectTo: '/login' }

];
