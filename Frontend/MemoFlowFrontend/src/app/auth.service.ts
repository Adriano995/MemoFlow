import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly validEmail = 'admin@memoflow.com';
  private readonly validPassword = '1234';

  login(email: string, password: string): boolean {
    return email === this.validEmail && password === this.validPassword;
  }
}
