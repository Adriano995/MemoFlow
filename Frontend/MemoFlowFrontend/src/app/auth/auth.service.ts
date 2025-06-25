import { Injectable } from '@angular/core';
import { AxiosService } from '../core/axios.service';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private axiosService: AxiosService,
    private tokenService: TokenService
  ) {}

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await this.axiosService.post<any>('/auth/login', {
        email,
        password
      });

      const token = response.token;
      //this.tokenService.saveToken(token);
      return true;
    } catch (error) {
      console.error('Errore login:', error);
      return false;
    }
  }

  logout() {
    this.tokenService.clearToken();
  }

  isAuthenticated(): boolean {
    return !!this.tokenService.getToken();
  }
}
