import { Routes } from '@angular/router';
import { LoginComponent } from './login.component/login.component';
import { DashboardCalendarioComponent } from './dashboard-calendario-component/dashboard-calendario.component';
import { NotaCreazioneComponent } from './nota-creazione/nota-creazione.component';
import { NotaModificaComponent } from './nota-modifica/nota-modifica.component'; // Importa il nuovo componente
import { RegisterComponent } from './register-component/register.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardCalendarioComponent },
  { path: 'crea-nota', component: NotaCreazioneComponent },
  { path: 'crea-nota/:date/:userId', component: NotaCreazioneComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'modifica-nota/:id', component: NotaModificaComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  { path: '**', redirectTo: '/login' } // Gestisce percorsi non trovati
];