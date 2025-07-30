import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewEvento } from './preview-evento';

describe('PreviewEvento', () => {
  let component: PreviewEvento;
  let fixture: ComponentFixture<PreviewEvento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewEvento]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewEvento);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
