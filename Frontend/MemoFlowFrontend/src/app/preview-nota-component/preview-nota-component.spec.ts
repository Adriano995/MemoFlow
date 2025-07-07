import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewNotaComponent } from './preview-nota-component';

describe('NotaComponent', () => {
  let component: PreviewNotaComponent;
  let fixture: ComponentFixture<PreviewNotaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewNotaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewNotaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
