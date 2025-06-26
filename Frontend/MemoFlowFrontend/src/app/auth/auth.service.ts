import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { TokenService } from './token.service'; // Il tuo servizio per il token (che non useremo per l'auth qui)

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Chiave per salvare l'ID utente nel localStorage
  private readonly userIdKey = 'user_id'; 
  
  constructor(
    private axiosService: AxiosService,
    private tokenService: TokenService // Lo manteniamo anche se il token è dummy
  ) {}

  async login(email: string, password: string): Promise<boolean> {
    try {
      // La risposta del backend è del tipo { token: string, user: { id: number, nome: string, email: string } }
      const response = await this.axiosService.post<any>('/auth/login', {
        email,
        password
      });

      // Estrai l'ID utente dalla risposta e salvalo
      if (response && response.user && response.user.id) {
        this.saveUserId(response.user.id);
        console.log('Login riuscito. ID utente salvato:', response.user.id);
      } else {
        console.warn('Login riuscito, ma ID utente non trovato nella risposta.');
        // Potresti voler gestire questo come un errore se l'ID è mandatorio
      }

      // Il token rimane "dummy-token" dal backend, lo puoi salvare o ignorare per ora
      const token = response.token;
      this.tokenService.saveToken(token); // Salva il dummy token se lo vuoi mantenere
      
      return true; // Login riuscito
    } catch (error) {
      console.error('Errore login:', error);
      this.clearUserId(); // Pulisci l'ID utente se il login fallisce
      return false; // Login fallito
    }
  }

  // Metodo per salvare l'ID utente nel localStorage
  private saveUserId(userId: number): void {
    localStorage.setItem(this.userIdKey, userId.toString());
  }

  // Metodo per recuperare l'ID utente dal localStorage
  getUserId(): number | null {
    const userId = localStorage.getItem(this.userIdKey);
    return userId ? parseInt(userId, 10) : null;
  }

  // Metodo per rimuovere l'ID utente dal localStorage
  clearUserId(): void {
    localStorage.removeItem(this.userIdKey);
  }

  logout() {
    this.tokenService.clearToken();
    this.clearUserId(); // Pulisci anche l'ID utente al logout
  }

  isAuthenticated(): boolean {
    // Ora l'autenticazione è considerata valida se esiste un ID utente
    // (e/o un token, a seconda della tua logica)
    return !!this.getUserId(); // O this.tokenService.getToken()
  }
}