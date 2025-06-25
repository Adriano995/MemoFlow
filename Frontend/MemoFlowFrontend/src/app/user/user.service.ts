import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { AxiosService } from '../core/axios.service';
import { User } from './user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = '/api/utenti';

  constructor(private axiosService: AxiosService) {}

  getCurrentUser(): Observable<User> {
    return from(this.axiosService.get<User>(`${this.baseUrl}/current`));
  }

  updateUser(id: number, updates: Partial<User>): Observable<User> {
    return from(this.axiosService.put<User>(`${this.baseUrl}/aggiorna/${id}`, updates));
  }

  deleteUser(id: number): Observable<void> {
    return from(this.axiosService.delete<void>(`${this.baseUrl}/elimina/${id}`));
  }

  deleteOwnAccount(): Observable<void> {
    return from(this.axiosService.delete<void>(`${this.baseUrl}/elimina-account`));
  }
}
