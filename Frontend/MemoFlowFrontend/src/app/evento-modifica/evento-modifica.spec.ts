import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventoModifica } from './evento-modifica';

describe('EventoModifica', () => {
  let component: EventoModifica;
  let fixture: ComponentFixture<EventoModifica>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventoModifica]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventoModifica);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
