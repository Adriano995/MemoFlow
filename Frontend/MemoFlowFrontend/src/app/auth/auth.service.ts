import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { TokenService } from '../auth/token.service';

interface CredenzialiCreateDTO {
  email: string;
  password: string;
}

interface UtenteCreateDTO {
  nome: string;
  cognome: string; 
  credenziali: CredenzialiCreateDTO;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private readonly userIdKey = 'user_id';

  constructor(
    private axiosService: AxiosService,
    private tokenService: TokenService
  ) { }

  async register(nome: string, cognome: string, email: string, password: string): Promise<boolean> {
    try {
      const registrationData: UtenteCreateDTO = {
        nome: nome,        
        cognome: cognome,  
        credenziali: {
          email: email,
          password: password
        }
      };
      const response = await this.axiosService.post<any>('/utente/creaUtente', registrationData);
      
      if (response && typeof response.id === 'number') { 
        this.saveUserId(response.id); 
        console.log('Registrazione riuscita. ID utente salvato:', response.id);
      } else {
        console.warn('Registrazione riuscita, ma ID utente non trovato nella risposta.');
      }

      return true;
    } catch (error) {
      console.error('Errore durante la registrazione:', error);
      this.clearUserId();
      return false;
    }
  }

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await this.axiosService.post<any>('/auth/login', {
        email,
        password
      });

      if (response && response.user && typeof response.user.id === 'number') {
        this.saveUserId(response.user.id);
        console.log('Login riuscito. ID utente salvato:', response.user.id);
      } else {
        console.warn('Login riuscito, ma ID utente non trovato nella risposta.');
      }

      const token = response.token;
      this.tokenService.saveToken(token);

      return true;
    } catch (error) {
      console.error('Errore login:', error);
      this.clearUserId();
      return false;
    }
  }

  private saveUserId(userId: number): void {
    localStorage.setItem(this.userIdKey, userId.toString());
  }

  getUserId(): number | null {
    const userId = localStorage.getItem(this.userIdKey);
    return userId ? parseInt(userId, 10) : null;
  }

  clearUserId(): void {
    localStorage.removeItem(this.userIdKey);
  }

  logout() {
    this.tokenService.clearToken();
    this.clearUserId();
  }

  isAuthenticated(): boolean {
    return !!this.getUserId();
  }
}