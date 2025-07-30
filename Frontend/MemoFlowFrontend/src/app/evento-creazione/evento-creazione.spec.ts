import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventoCreazione } from './evento-creazione';

describe('EventoCreazione', () => {
  let component: EventoCreazione;
  let fixture: ComponentFixture<EventoCreazione>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventoCreazione]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventoCreazione);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
