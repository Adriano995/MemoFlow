import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';
import { provideRouter } from '@angular/router';

const bootstrap = () => bootstrapApplication(AppComponent, config);

export default bootstrap;
