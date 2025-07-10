import { Routes } from '@angular/router';
import { LoginComponent } from './login.component/login.component';
import { DashboardCalendarioComponent } from './dashboard-calendario-component/dashboard-calendario.component';
import { NotaCreazioneComponent } from './nota-creazione/nota-creazione.component';
import { NotaModificaComponent } from './nota-modifica/nota-modifica.component'; 
import { RegisterComponent } from './register-component/register.component';
import { UserModificaComponent } from './user-modifica.component/user-modifica.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardCalendarioComponent },
  { path: 'crea-nota', component: NotaCreazioneComponent },
  { path: 'crea-nota/:date/:userId', component: NotaCreazioneComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'modifica-nota/:id', component: NotaModificaComponent },
  { path: 'modifica-utente/:id', component: UserModificaComponent},
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  { path: '**', redirectTo: '/login' }
];