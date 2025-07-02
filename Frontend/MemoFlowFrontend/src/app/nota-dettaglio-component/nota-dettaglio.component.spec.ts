import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotaDettaglioComponent } from './nota-dettaglio.component';

describe('NotaDettaglio', () => {
  let component: NotaDettaglioComponent;
  let fixture: ComponentFixture<NotaDettaglioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotaDettaglioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotaDettaglioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
