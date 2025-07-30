import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ThemeService } from './theme';
import { UserComponent } from "./user/user.component";
import { RouterModule, Router } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule,
    RouterModule,
    UserComponent
],
  providers: [ThemeService], 
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'MemoFlowFrontend';

  utenteId = 1;

  constructor(private themeService: ThemeService,private authService: AuthService,private router: Router) {}
  
  ngOnInit(): void {
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  isLightTheme(): boolean {
    return this.themeService.getCurrentTheme() === 'light';
  }

  isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  get userId(): number | null {
    return this.authService.getUserId();
  }

  shouldShowNavbar(): boolean {
    const hiddenRoutes = ['/login', '/register'];
    return this.isLoggedIn() && !hiddenRoutes.includes(this.router.url);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}