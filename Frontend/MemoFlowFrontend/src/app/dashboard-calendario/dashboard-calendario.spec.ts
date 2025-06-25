import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardCalendario } from './dashboard-calendario';

describe('DashboardCalendario', () => {
  let component: DashboardCalendario;
  let fixture: ComponentFixture<DashboardCalendario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardCalendario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardCalendario);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
