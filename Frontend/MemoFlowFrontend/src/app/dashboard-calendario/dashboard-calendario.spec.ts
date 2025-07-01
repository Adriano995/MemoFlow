import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardCalendarioComponent } from './dashboard-calendario.component';

describe('DashboardCalendario', () => {
  let component: DashboardCalendarioComponent;
  let fixture: ComponentFixture<DashboardCalendarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardCalendarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardCalendarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
