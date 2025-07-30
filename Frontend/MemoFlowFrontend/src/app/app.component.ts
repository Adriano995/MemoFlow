import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ThemeService } from './theme';
import { UserComponent } from "./user/user.component";
import { RouterModule, Router } from '@angular/router';
import { AuthService } from './auth/auth.service';
import { EventoCreazioneComponent } from './evento-creazione/evento-creazione';

@Component({
  selector: 'app-root',
  standalone: true,
imports: [
  RouterOutlet,
  CommonModule,
  RouterModule,
  UserComponent,
  EventoCreazioneComponent
],

  providers: [ThemeService], 
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'MemoFlowFrontend';

  constructor(private themeService: ThemeService) {}

  ngOnInit(): void {
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  isLightTheme(): boolean {
    return this.themeService.getCurrentTheme() === 'light';
  }
}