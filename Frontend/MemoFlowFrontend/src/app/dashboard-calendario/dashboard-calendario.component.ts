import { Component } from '@angular/core';
import { CalendarEvent, CalendarModule, CalendarUtils } from 'angular-calendar';
import { CommonModule } from '@angular/common';
import { NotaComponent } from "../nota-component/nota-component";
@Component({
  selector: 'app-dashboard-calendario',
  standalone: true,
  imports: [CommonModule, CalendarModule, NotaComponent],
  providers: [CalendarUtils],
  templateUrl: './dashboard-calendario.component.html',
  styleUrls: ['./dashboard-calendario.component.css']
})
export class DashboardCalendarioComponent {

    viewDate: Date = new Date();
  events: CalendarEvent[] = [
    {
      start: new Date(),
      title: 'Evento di prova',
      color: {
        primary: '#ad2121',
        secondary: '#FAE3E3'
      }
    }
  ];
selectedDate: Date | null = null;

  dayClicked(event: { day: { date: Date; events: CalendarEvent[] }; sourceEvent: MouseEvent | KeyboardEvent }): void {
    this.selectedDate = event.day.date;
  }

  handleEvent(event: CalendarEvent): void {
    console.log('Evento cliccato:', event);
  }
}
