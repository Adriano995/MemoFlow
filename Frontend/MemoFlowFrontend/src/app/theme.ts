// src/app/theme.ts
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private currentTheme: string = 'light'; // O il tema di default che preferisci

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.loadTheme();
  }

  loadTheme(): void {
    if (isPlatformBrowser(this.platformId)) {
      const savedTheme = localStorage.getItem('theme');
      if (savedTheme) {
        this.currentTheme = savedTheme;
      }
    }
    this.applyTheme(this.currentTheme);
  }

  setTheme(theme: string): void {
    this.currentTheme = theme;
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('theme', theme);
    }
    this.applyTheme(theme);
  }

  // AGGIUNGI QUESTO METODO PER CAMBIARE IL TEMA
  toggleTheme(): void {
    const newTheme = this.currentTheme === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
  }

  private applyTheme(theme: string): void {
    if (isPlatformBrowser(this.platformId)) {
      document.body.className = '';
      document.body.classList.add(theme + '-theme');
    }
  }

  getCurrentTheme(): string {
    return this.currentTheme;
  }
}