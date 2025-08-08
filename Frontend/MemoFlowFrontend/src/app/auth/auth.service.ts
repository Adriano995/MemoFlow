import { Injectable, PLATFORM_ID, Inject } from '@angular/core'; // Aggiungi Inject e PLATFORM_ID
import { AxiosService } from '../core/axios.service';
import { TokenService } from '../auth/token.service';
import { isPlatformBrowser } from '@angular/common'; // Importa isPlatformBrowser

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
  private isBrowser: boolean; // Aggiungi una proprietà per lo stato del browser

  constructor(
    private axiosService: AxiosService,
    private tokenService: TokenService,
    @Inject(PLATFORM_ID) private platformId: Object // Inietta PLATFORM_ID
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId); // Controlla se siamo nel browser
  }

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
    if (this.isBrowser) { // Esegui solo se sei nel browser
      localStorage.setItem(this.userIdKey, userId.toString());
    }
  }

  getUserId(): number | null {
    if (this.isBrowser) { // Esegui solo se sei nel browser
      const userId = localStorage.getItem(this.userIdKey);
      return userId ? parseInt(userId, 10) : null;
    }
    return null; // Ritorna null se non sei nel browser
  }

  clearUserId(): void {
    if (this.isBrowser) { // Esegui solo se sei nel browser
      localStorage.removeItem(this.userIdKey);
    }
  }

  logout(): void {
    localStorage.removeItem('user_id')
    this.tokenService.clearToken();
    this.clearUserId();
  }

  isAuthenticated(): boolean {
    if (this.isBrowser) { // Esegui solo se sei nel browser
        return !!this.getUserId();
    }
    return false; // Non autenticato se non sei nel browser (o finché non sei nel browser)
  }
}