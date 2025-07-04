import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ThemeService } from './theme';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule
  ],
  providers: [ThemeService], // Se ThemeService è providedIn: 'root', questo non è strettamente necessario qui, ma non causa problemi.
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'MemoFlowFrontend';

  constructor(private themeService: ThemeService) {}

  ngOnInit(): void {
    // Non è necessario chiamare nulla qui, il ThemeService si inizializza da solo.
  }

  toggleTheme(): void {
    this.themeService.toggleTheme(); // Ora questo metodo esiste nel ThemeService
  }

  isLightTheme(): boolean {
    return this.themeService.getCurrentTheme() === 'light';
  }
}