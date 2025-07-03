import { Routes } from '@angular/router';
import { LoginComponent } from './login.component/login.component';
import { DashboardCalendarioComponent } from './dashboard-calendario-component/dashboard-calendario.component';
import { NotaCreazioneComponent } from './nota-creazione/nota-creazione.component';
import { NotaModificaComponent } from './nota-modifica/nota-modifica.component'; // Importa il nuovo componente

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardCalendarioComponent },
  { path: 'crea-nota', component: NotaCreazioneComponent },
  { path: 'crea-nota/:date/:userId', component: NotaCreazioneComponent }, // Route con parametri
  { path: 'modifica-nota/:id', component: NotaModificaComponent }, // *** NUOVA ROUTE ***
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  { path: '**', redirectTo: '/login' } // Gestisce percorsi non trovati
];